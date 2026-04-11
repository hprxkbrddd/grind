package com.grind.statistics.config;

import com.grind.statistics.dto.response.ddl.DescribeTableResponse;
import com.grind.statistics.repository.ClickhouseRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Aligns the ClickHouse schema with expected DDL without destroying existing data.
 * It creates missing objects, adds absent columns, updates view queries, and
 * patches materialized view definitions in place when possible.
 */
@Component
@RequiredArgsConstructor
public class ClickhouseSchemaInitializer {

    private static final Logger log = LoggerFactory.getLogger(ClickhouseSchemaInitializer.class);
    private static final Pattern SELECT_BODY_PATTERN = Pattern.compile("(?is)AS\\s+(SELECT\\b.*)");
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

    private final ClickhouseRepository clickhouseRepository;

    @Value("${clickhouse.database}")
    private String database;
    @Value("${clickhouse.table.raw}")
    private String tableRaw;
    @Value("${clickhouse.table.task-as}")
    private String tableActualState;
    @Value("${clickhouse.view.task-as-mv}")
    private String mViewActualState;
    @Value("${clickhouse.view.task-as-v}")
    private String viewActualState;
    @Value("${clickhouse.view.task-visible-v}")
    private String viewVisibleState;

    @PostConstruct
    public void synchronizeSchema() {
        log.info("Ensuring ClickHouse schema is synchronized with in-repo DDL");
        ensureDatabase();

        TableDefinition rawTableDefinition = buildRawTableDefinition();
        TableDefinition actualStateDefinition = buildActualStateTableDefinition();
        MaterializedViewDefinition materializedViewDefinition = buildMaterializedViewDefinition();
        ViewDefinition actualStateViewDefinition = buildActualStateViewDefinition();
        ViewDefinition visibleStateViewDefinition = buildVisibleStateViewDefinition();

        boolean rawTableRebuilt = ensureTableAligned(
                rawTableDefinition,
                materializedViewDefinition,
                actualStateViewDefinition,
                visibleStateViewDefinition
        );
        boolean actualStateRebuilt = ensureTableAligned(
                actualStateDefinition,
                materializedViewDefinition,
                actualStateViewDefinition,
                visibleStateViewDefinition
        );

        ensureMaterializedView(materializedViewDefinition);
        ensureView(actualStateViewDefinition);
        ensureView(visibleStateViewDefinition);

        if (rawTableRebuilt || actualStateRebuilt) {
            backfillActualState(materializedViewDefinition);
        }
    }

    private void ensureDatabase() {
        String statement = "CREATE DATABASE IF NOT EXISTS " + database;
        log.debug("Ensuring database exists: {}", database);
        clickhouseRepository.executeStatement(statement).block();
    }

    private boolean ensureTableAligned(
            TableDefinition definition,
            MaterializedViewDefinition materializedViewDefinition,
            ViewDefinition actualStateViewDefinition,
            ViewDefinition visibleStateViewDefinition
    ) {
        log.info("Checking table {}", definition.qualifiedName());
        boolean exists = Boolean.TRUE.equals(
                clickhouseRepository.tableExists(definition.database(), definition.name()).block()
        );
        if (!exists) {
            log.warn("Table {} is missing; creating via configured DDL", definition.qualifiedName());
            clickhouseRepository.executeStatement(definition.createStatement()).block();
            return isActualStateTable(definition);
        }

        List<DescribeTableResponse> describe = clickhouseRepository.describeTable(definition.database(), definition.name())
                .collectList()
                .blockOptional()
                .orElseGet(Collections::emptyList);

        Map<String, DescribeTableResponse> actualByName = new LinkedHashMap<>();
        for (DescribeTableResponse column : describe) {
            actualByName.put(column.name(), column);
        }

        String previousColumn = null;
        for (ColumnDefinition expected : definition.columns()) {
            DescribeTableResponse actual = actualByName.get(expected.name());
            if (actual == null) {
                addColumn(definition, expected, previousColumn);
                previousColumn = expected.name();
                continue;
            }

            if (!typesCompatible(expected.type(), actual.type())) {
                if (requiresActualStateRebuild(definition, expected, actual)) {
                    rebuildActualStateStorage(
                            definition,
                            materializedViewDefinition,
                            actualStateViewDefinition,
                            visibleStateViewDefinition
                    );
                    return true;
                } else if (isRawTable(definition)) {
                    try {
                        updateColumnType(definition, expected);
                    } catch (RuntimeException ex) {
                        rebuildRawStorage(
                                definition,
                                actualByName,
                                materializedViewDefinition,
                                actualStateViewDefinition,
                                visibleStateViewDefinition
                        );
                        return true;
                    }
                } else if (canAutoUpdateType(expected.type(), actual.type())) {
                    try {
                        updateColumnType(definition, expected);
                    } catch (RuntimeException ex) {
                        throw ex;
                    }
                } else {
                    log.warn(
                            "Column {}.{} has type '{}' but '{}' is expected. Manual intervention required; skipping automatic change.",
                            definition.qualifiedName(),
                            expected.name(),
                            actual.type(),
                            expected.type()
                    );
                }
            }

            if (expected.defaultExpression() != null
                    && !defaultsCompatible(expected.defaultExpression(), actual.defaultExpression())) {
                updateDefault(definition, expected);
            }

            previousColumn = expected.name();
        }

        return false;
    }

    private void addColumn(TableDefinition definition, ColumnDefinition column, String previousColumn) {
        StringBuilder statement = new StringBuilder()
                .append("ALTER TABLE ")
                .append(definition.qualifiedName())
                .append(" ADD COLUMN IF NOT EXISTS ")
                .append(column.name())
                .append(' ')
                .append(column.type());

        if (column.defaultExpression() != null) {
            statement.append(" DEFAULT ").append(column.defaultExpression());
        }

        if (previousColumn == null) {
            statement.append(" FIRST");
        } else {
            statement.append(" AFTER ").append(previousColumn);
        }

        log.info("Adding missing column {}.{}", definition.qualifiedName(), column.name());
        clickhouseRepository.executeStatement(statement.toString()).block();
    }

    private void updateDefault(TableDefinition definition, ColumnDefinition column) {
        String statement = "ALTER TABLE %s MODIFY COLUMN %s %s DEFAULT %s"
                .formatted(definition.qualifiedName(), column.name(), column.type(), column.defaultExpression());
        log.info("Updating default expression for {}.{}", definition.qualifiedName(), column.name());
        clickhouseRepository.executeStatement(statement).block();
    }

    private void updateColumnType(TableDefinition definition, ColumnDefinition column) {
        String statement = "ALTER TABLE %s MODIFY COLUMN %s %s"
                .formatted(definition.qualifiedName(), column.name(), column.type());
        log.info("Updating column type for {}.{}", definition.qualifiedName(), column.name());
        clickhouseRepository.executeStatement(statement).block();
    }

    private void rebuildActualStateStorage(
            TableDefinition definition,
            MaterializedViewDefinition materializedViewDefinition,
            ViewDefinition actualStateViewDefinition,
            ViewDefinition visibleStateViewDefinition
    ) {
        if (materializedViewDefinition == null || actualStateViewDefinition == null || visibleStateViewDefinition == null) {
            throw new IllegalStateException("Materialized/view definitions must be provided for task_actual_state rebuild");
        }
        log.warn("Rebuilding {} due to incompatible AggregateFunction(Enum8) type change", definition.qualifiedName());
        dropActualStateStorage(materializedViewDefinition, actualStateViewDefinition, visibleStateViewDefinition);
        clickhouseRepository.executeStatement(definition.createStatement()).block();
    }

    private void rebuildRawStorage(
            TableDefinition definition,
            Map<String, DescribeTableResponse> actualByName,
            MaterializedViewDefinition materializedViewDefinition,
            ViewDefinition actualStateViewDefinition,
            ViewDefinition visibleStateViewDefinition
    ) {
        if (materializedViewDefinition == null || actualStateViewDefinition == null || visibleStateViewDefinition == null) {
            throw new IllegalStateException("Materialized/view definitions must be provided for raw rebuild");
        }
        String rawQualified = definition.qualifiedName();
        String tempName = definition.name() + "__enum_mig_tmp";
        String tempQualified = definition.database() + "." + tempName;

        log.warn("Rebuilding {} due to unsupported enum conversion", rawQualified);
        dropActualStateStorage(materializedViewDefinition, actualStateViewDefinition, visibleStateViewDefinition);

        clickhouseRepository.executeStatement("DROP TABLE IF EXISTS " + tempQualified).block();
        String createTmp = definition.createStatement()
                .replace("CREATE TABLE IF NOT EXISTS " + rawQualified,
                        "CREATE TABLE IF NOT EXISTS " + tempQualified);
        clickhouseRepository.executeStatement(createTmp).block();

        String columnList = definition.columns().stream()
                .map(ColumnDefinition::name)
                .collect(Collectors.joining(", "));
        String selectList = definition.columns().stream()
                .map(column -> buildRawCopyExpression(column, actualByName))
                .collect(Collectors.joining(",\n    "));

        String copy = """
                INSERT INTO %s
                (%s)
                SELECT
                    %s
                FROM %s
                """.formatted(tempQualified, columnList, selectList, rawQualified);
        clickhouseRepository.executeStatement(copy).block();

        clickhouseRepository.executeStatement("DROP TABLE IF EXISTS " + rawQualified).block();
        clickhouseRepository.executeStatement("RENAME TABLE " + tempQualified + " TO " + rawQualified).block();
    }

    private String buildRawCopyExpression(
            ColumnDefinition expected,
            Map<String, DescribeTableResponse> actualByName
    ) {
        DescribeTableResponse actual = actualByName.get(expected.name());
        if (actual == null) {
            if (expected.defaultExpression() != null) {
                return expected.defaultExpression() + " AS " + expected.name();
            }
            return "CAST(NULL AS " + expected.type() + ") AS " + expected.name();
        }

        if ("task_status".equals(expected.name()) && isEnum8Type(expected.type()) && isEnum8Type(actual.type())) {
            return "CAST(toInt8(task_status) AS " + expected.type() + ") AS task_status";
        }

        if (typesCompatible(expected.type(), actual.type())) {
            return expected.name();
        }

        return "CAST(" + expected.name() + " AS " + expected.type() + ") AS " + expected.name();
    }

    private void dropActualStateStorage(
            MaterializedViewDefinition materializedViewDefinition,
            ViewDefinition actualStateViewDefinition,
            ViewDefinition visibleStateViewDefinition
    ) {
        clickhouseRepository.executeStatement("DROP VIEW IF EXISTS " + visibleStateViewDefinition.qualifiedName()).block();
        clickhouseRepository.executeStatement("DROP VIEW IF EXISTS " + actualStateViewDefinition.qualifiedName()).block();
        clickhouseRepository.executeStatement("DROP TABLE IF EXISTS " + materializedViewDefinition.qualifiedName()).block();
        clickhouseRepository.executeStatement("DROP TABLE IF EXISTS " + qualifiedName(tableActualState)).block();
    }

    private void ensureMaterializedView(MaterializedViewDefinition definition) {
        log.info("Checking materialized view {}", definition.qualifiedName());
        boolean exists = Boolean.TRUE.equals(
                clickhouseRepository.tableExists(definition.database(), definition.name()).block()
        );
        if (!exists) {
            log.warn("Materialized view {} missing; creating", definition.qualifiedName());
            clickhouseRepository.executeStatement(definition.createStatement()).block();
            return;
        }

        String ddl = clickhouseRepository.fetchTableDdl(definition.database(), definition.name()).block();
        if (ddl == null) {
            log.warn("Unable to fetch DDL for materialized view {}", definition.qualifiedName());
            return;
        }

        Optional<String> selectBody = extractSelectBody(ddl);
        if (selectBody.isEmpty()) {
            log.warn("Failed to extract SELECT body for materialized view {}", definition.qualifiedName());
            return;
        }

        if (!normalizeSql(selectBody.get()).equals(normalizeSql(definition.selectQuery()))) {
            log.warn("Materialized view {} definition differs from expected; applying ALTER MODIFY QUERY", definition.qualifiedName());
            String statement = "ALTER TABLE %s MODIFY QUERY\n%s"
                    .formatted(definition.qualifiedName(), definition.selectQuery());
            clickhouseRepository.executeStatement(statement).block();
        }
    }

    private void ensureView(ViewDefinition definition) {
        log.info("Ensuring view {} matches the configured SELECT", definition.qualifiedName());
        clickhouseRepository.executeStatement(definition.createStatement()).block();
    }

    private Optional<String> extractSelectBody(String ddl) {
        return Optional.ofNullable(ddl)
                .flatMap(text -> {
                    var matcher = SELECT_BODY_PATTERN.matcher(text);
                    if (matcher.find()) {
                        return Optional.of(matcher.group(1).trim());
                    }
                    return Optional.empty();
                });
    }

    private boolean typesCompatible(String expected, String actual) {
        return normalizeType(expected).equals(normalizeType(actual));
    }

    private boolean defaultsCompatible(String expected, String actual) {
        return normalizeWhitespace(expected).equals(normalizeWhitespace(actual));
    }

    private boolean canAutoUpdateType(String expected, String actual) {
        String expectedNorm = normalizeType(expected);
        String actualNorm = normalizeType(actual);

        return expectedNorm.startsWith("ENUM8(") && actualNorm.startsWith("ENUM8(");
    }

    private boolean requiresActualStateRebuild(
            TableDefinition definition,
            ColumnDefinition expected,
            DescribeTableResponse actual
    ) {
        return isActualStateTable(definition)
                && "status_state".equals(expected.name())
                && isAggregateEnumType(expected.type())
                && isAggregateEnumType(actual.type());
    }

    private boolean isAggregateEnumType(String type) {
        return normalizeType(type).startsWith("AGGREGATEFUNCTION(ARGMAX,ENUM8(");
    }

    private boolean isEnum8Type(String type) {
        return normalizeType(type).startsWith("ENUM8(");
    }

    private boolean isActualStateTable(TableDefinition definition) {
        return definition.database().equals(extractDatabaseName(tableActualState))
                && definition.name().equals(extractObjectName(tableActualState));
    }

    private boolean isRawTable(TableDefinition definition) {
        return definition.database().equals(extractDatabaseName(tableRaw))
                && definition.name().equals(extractObjectName(tableRaw));
    }

    private void backfillActualState(MaterializedViewDefinition definition) {
        String statement = """
                INSERT INTO %s
                (task_id, track_id, user_id, sprint_state, status_state, planned_date_state, changed_at_state)
                %s
                """.formatted(definition.targetTable(), definition.selectQuery());
        log.info("Backfilling {} from raw events", definition.targetTable());
        clickhouseRepository.executeStatement(statement).block();
    }

    private String normalizeSql(String sql) {
        if (sql == null) {
            return "";
        }
        return WHITESPACE_PATTERN.matcher(sql).replaceAll(" ").trim();
    }

    private String normalizeWhitespace(String value) {
        if (value == null) {
            return "";
        }
        return WHITESPACE_PATTERN.matcher(value).replaceAll(" ").trim();
    }

    private String normalizeType(String type) {
        if (type == null) {
            return "";
        }
        return WHITESPACE_PATTERN.matcher(type).replaceAll("")
                .replace("`", "")
                .toUpperCase(Locale.ROOT);
    }

    private TableDefinition buildRawTableDefinition() {
        String db = extractDatabaseName(tableRaw);
        String name = extractObjectName(tableRaw);
        String qualified = db + "." + name;

        String createStatement = ("""
                CREATE TABLE IF NOT EXISTS %s (
                    event_id Int64,
                    user_id UUID,
                    track_id UUID,
                    sprint_id Nullable(UUID),
                    task_id UUID,
                    planned_date Nullable(DateTime64(3, 'UTC')),
                    version UInt64,
                    task_status Enum8(
                            'DELETED'  = 0,
                            'CREATED'  = 1,
                            'PLANNED'  = 2,
                            'COMPLETED' = 3,
                            'OVERDUE'  = 4
                        ),
                    changed_at DateTime64(3, 'UTC'),
                    ingested_at DateTime64(3, 'UTC') DEFAULT now64(3)
                )
                ENGINE = MergeTree
                PARTITION BY toYYYYMM(changed_at)
                ORDER BY (track_id, task_id, version, event_id)
                """
        ).formatted(qualified);

        List<ColumnDefinition> columns = List.of(
                new ColumnDefinition("event_id", "Int64", null),
                new ColumnDefinition("user_id", "UUID", null),
                new ColumnDefinition("track_id", "UUID", null),
                new ColumnDefinition("sprint_id", "Nullable(UUID)", null),
                new ColumnDefinition("task_id", "UUID", null),
                new ColumnDefinition("planned_date", "Nullable(DateTime64(3, 'UTC'))", null),
                new ColumnDefinition("version", "UInt64", null),
                new ColumnDefinition("task_status", "Enum8('DELETED' = 0, 'CREATED' = 1, 'PLANNED' = 2, 'COMPLETED' = 3, 'OVERDUE' = 4)", null),
                new ColumnDefinition("changed_at", "DateTime64(3, 'UTC')", null),
                new ColumnDefinition("ingested_at", "DateTime64(3, 'UTC')", "now64(3)")
        );

        return new TableDefinition(db, name, createStatement, columns);
    }

    private TableDefinition buildActualStateTableDefinition() {
        String db = extractDatabaseName(tableActualState);
        String name = extractObjectName(tableActualState);
        String qualified = db + "." + name;

        String createStatement = ("""
                CREATE TABLE IF NOT EXISTS %s (
                  task_id UUID,
                  track_id UUID,
                  user_id UUID,
                  sprint_state AggregateFunction(argMax, UUID, UInt64),
                  status_state AggregateFunction(
                      argMax,
                      Enum8(
                          'DELETED'  = 0,
                          'CREATED'  = 1,
                          'PLANNED'  = 2,
                          'COMPLETED' = 3,
                          'OVERDUE'   = 4
                      ),
                      UInt64
                  ),
                  planned_date_state AggregateFunction(argMax, Nullable(DateTime64(3, 'UTC')), UInt64),
                  changed_at_state AggregateFunction(max, DateTime64(3, 'UTC'))
                )
                ENGINE = AggregatingMergeTree
                ORDER BY (task_id, track_id, user_id)
                """
        ).formatted(qualified);

        List<ColumnDefinition> columns = List.of(
                new ColumnDefinition("task_id", "UUID", null),
                new ColumnDefinition("track_id", "UUID", null),
                new ColumnDefinition("user_id", "UUID", null),
                new ColumnDefinition("sprint_state", "AggregateFunction(argMax, UUID, UInt64)", null),
                new ColumnDefinition("status_state", "AggregateFunction(argMax, Enum8('DELETED' = 0, 'CREATED' = 1, 'PLANNED' = 2, 'COMPLETED' = 3, 'OVERDUE' = 4), UInt64)", null),
                new ColumnDefinition("planned_date_state", "AggregateFunction(argMax, Nullable(DateTime64(3, 'UTC')), UInt64)", null),
                new ColumnDefinition("changed_at_state", "AggregateFunction(max, DateTime64(3, 'UTC'))", null)
        );

        return new TableDefinition(db, name, createStatement, columns);
    }

    private MaterializedViewDefinition buildMaterializedViewDefinition() {
        String db = extractDatabaseName(mViewActualState);
        String name = extractObjectName(mViewActualState);
        String qualifiedRaw = qualifiedName(tableRaw);
        String qualifiedTarget = qualifiedName(tableActualState);

        String selectQuery = ("""
                SELECT
                    task_id,
                    track_id,
                    user_id,
                    argMaxState(
                        ifNull(sprint_id, toUUID('00000000-0000-0000-0000-000000000000')),
                        version
                    ) AS sprint_state,
                    argMaxState(task_status, version)  AS status_state,
                    argMaxState(planned_date, version) AS planned_date_state,
                    maxState(changed_at)               AS changed_at_state
                FROM %s
                GROUP BY
                    task_id,
                    track_id,
                    user_id
                """
        ).formatted(qualifiedRaw);

        return new MaterializedViewDefinition(db, name, qualifiedTarget, selectQuery);
    }

    private ViewDefinition buildActualStateViewDefinition() {
        String db = extractDatabaseName(viewActualState);
        String name = extractObjectName(viewActualState);
        String qualifiedSource = qualifiedName(tableActualState);

        String selectQuery = ("""
                SELECT
                    task_id,
                    track_id,
                    user_id,
                    argMaxMerge(sprint_state)  AS sprint_id,
                    argMaxMerge(status_state)  AS task_status,
                    maxMerge(changed_at_state) AS changed_at,
                    argMaxMerge(planned_date_state) AS planned_date,
                    toYYYYMM(maxMerge(changed_at_state)) AS changed_month
                FROM %s
                GROUP BY
                    task_id,
                    track_id,
                    user_id
                """
        ).formatted(qualifiedSource);

        return new ViewDefinition(db, name, selectQuery);
    }

    private ViewDefinition buildVisibleStateViewDefinition() {
        String db = extractDatabaseName(viewVisibleState);
        String name = extractObjectName(viewVisibleState);
        String qualifiedSource = qualifiedName(viewActualState);

        String selectQuery = ("""
                SELECT
                    task_id,
                    track_id,
                    user_id,
                    sprint_id,
                    task_status,
                    changed_at,
                    planned_date,
                    changed_month
                FROM %s
                WHERE task_status != 'DELETED'
                """
        ).formatted(qualifiedSource);

        return new ViewDefinition(db, name, selectQuery);
    }

    private String qualifiedName(String configuredName) {
        return extractDatabaseName(configuredName) + "." + extractObjectName(configuredName);
    }

    private String extractDatabaseName(String configuredName) {
        int idx = configuredName.indexOf('.');
        if (idx > 0) {
            return configuredName.substring(0, idx);
        }
        return database;
    }

    private String extractObjectName(String configuredName) {
        int idx = configuredName.indexOf('.');
        if (idx >= 0 && idx + 1 < configuredName.length()) {
            return configuredName.substring(idx + 1);
        }
        return configuredName;
    }

    private record TableDefinition(String database, String name, String createStatement, List<ColumnDefinition> columns) {
        String qualifiedName() {
            return database + "." + name;
        }
    }

    private record ColumnDefinition(String name, String type, String defaultExpression) {
    }

    private record MaterializedViewDefinition(String database, String name, String targetTable, String selectQuery) {
        String qualifiedName() {
            return database + "." + name;
        }

        String createStatement() {
            return "CREATE MATERIALIZED VIEW IF NOT EXISTS %s\nTO %s\nAS\n%s"
                    .formatted(qualifiedName(), targetTable, selectQuery);
        }
    }

    private record ViewDefinition(String database, String name, String selectQuery) {
        String qualifiedName() {
            return database + "." + name;
        }

        String createStatement() {
            return "CREATE OR REPLACE VIEW %s AS\n%s"
                    .formatted(qualifiedName(), selectQuery);
        }
    }
}
