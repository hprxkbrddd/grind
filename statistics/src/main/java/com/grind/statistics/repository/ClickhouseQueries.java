package com.grind.statistics.repository;

public class ClickhouseQueries {

    public static final String ANALYTICS_DB = "analytics";

    public static final String DDL_CREATE_DATABASE = """
            CREATE DATABASE IF NOT EXISTS analytics;
            """;

    public static final String DDL_CREATE_TABLES = """
            CREATE TABLE IF NOT EXISTS analytics.raw(
                event_id UUID,
                user_id UUID,
                track_id UUID,
                sprint_id Nullable(UUID),
                task_id UUID,
                planned_date DateTime64(3, 'UTC'),
                version UInt64,
                task_status Enum8(
                        'UNKNOWN' = 0,
                        'CREATED'   = 1,
                        'PLANNED'   = 2,
                        'COMPLETED' = 3,
                        'OVERDUE'  = 4
                    ),
                changed_at DateTime64(3, 'UTC'),
                ingested_at DateTime64(3, 'UTC') DEFAULT now64(3)
            )
            ENGINE = MergeTree
            PARTITION BY toYYYYMM(changed_at)
            ORDER BY (track_id, task_id, version, event_id);

            CREATE TABLE analytics.task_actual_state
            (
              task_id UUID,
              track_id UUID,
              user_id UUID,

              sprint_state AggregateFunction(argMax, UUID, UInt64),
              status_state AggregateFunction(
                  argMax,
                  Enum8(
                      'UNKNOWN' = 0,
                      'CREATED'   = 1,
                      'PLANNED'   = 2,
                      'COMPLETED' = 3,
                      'OVERDUE'   = 4
                  ),
                  UInt64
              ),
              planned_date_state AggregateFunction(max, DateTime64(3, 'UTC')),
              changed_at_state AggregateFunction(max, DateTime64(3, 'UTC'))
            )
            ENGINE = AggregatingMergeTree
            ORDER BY (task_id, track_id, user_id);
            """;

    public static final String DDL_CREATE_VIEWS = """
            CREATE MATERIALIZED VIEW analytics.task_actual_state_mv
            TO analytics.task_actual_state
            AS
            SELECT
                task_id,
                track_id,
                user_id,
                argMaxState(
                    ifNull(sprint_id, toUUID('00000000-0000-0000-0000-000000000000')),
                    version
                ) AS sprint_state,
                argMaxState(task_status, version)  AS status_state,
                maxState(changed_at)               AS changed_at_state,
                maxState(planned_date)             AS planned_date_state
            FROM analytics.raw
            GROUP BY
                task_id,
                track_id,
                user_id;

            CREATE VIEW analytics.task_actual_state_v AS
            SELECT
                task_id,
                track_id,
                user_id,
                argMaxMerge(sprint_state)  AS sprint_id,
                argMaxMerge(status_state)  AS task_status,
                maxMerge(changed_at_state) AS changed_at,
                maxMerge(planned_date_state) AS planned_date,
                toYYYYMM(maxMerge(changed_at_state)) AS changed_month
            FROM analytics.task_actual_state
            GROUP BY
                task_id,
                track_id,
                user_id;
            """;
    public static final String Q_TRACK_STATS_ACTUAL_STATE = """
            SELECT
                track_id,
            
                count() AS total_tasks,
            
                countIf(task_status = 'COMPLETED') AS completed_tasks,
                countIf(task_status != 'COMPLETED') AS remaining_tasks,
                countIf(task_status = 'OVERDUE') AS overdue_tasks,
                countIf(task_status = 'PLANNED') AS active_wip,
            
                round(
                    countIf(task_status = 'COMPLETED') / count() * 100,
                    2
                ) AS completion_percent,
            
                round(
                    countIf(task_status = 'OVERDUE') / count() * 100,
                    2
                ) AS overdue_percent,
            
                round(
                    countIf(task_status = 'OVERDUE')
                    /
                    nullIf(countIf(task_status != 'COMPLETED'), 0)
                    * 100,
                    2
                ) AS overdue_among_active_percent,
            
                avgIf(
                    dateDiff('day', changed_at, now()),
                    task_status != 'COMPLETED'
                ) AS avg_active_age_days
            
            FROM analytics.task_actual_state_v
            WHERE track_id = {track:UUID}
            GROUP BY track_id
            FORMAT JSONEachRow;
            """;

    public static final String Q_STATS_PER_DAY = """
            SELECT
                day,
                sum(planned)   AS planned_tasks,
                sum(completed) AS completed_tasks
            FROM
            (
                SELECT
                    toDate(planned_date) AS day,
                    uniqExact(task_id) AS planned,
                    0 AS completed
                FROM task_actual_state_v
                GROUP BY day

                UNION ALL

                SELECT
                    toDate(changed_at) AS day,
                    0 AS planned,
                    uniqExact(task_id) AS completed
                FROM analytics.raw
                WHERE task_status = 'COMPLETED'
                GROUP BY day
            )
            GROUP BY day
            ORDER BY day
            FORMAT JSONEachRow;
            """;

    public static final String Q_STATS_PER_WEEK = """
            SELECT
                day,
                uniqExactIf(task_id, task_status = 'PLANNED')   AS planned_tasks,
                uniqExactIf(task_id, task_status = 'COMPLETED') AS completed_tasks
            FROM
            (
                SELECT
                    task_id,
                    task_status,
                    toStartOfWeek(toDate(changed_at)) AS day
                FROM analytics.raw
                WHERE track_id = {track:UUID}
            )
            GROUP BY week
            ORDER BY week
            FORMAT JSONEachRow;
            """;

    public static final String Q_TRACK_STATS_RAW = """
            SELECT
                track_id,
            
                countIf(
                    task_status = 'COMPLETED'
                    AND changed_at >= now() - INTERVAL 30 DAY
                ) AS completed_last_30d,
            
                countIf(
                    task_status = 'COMPLETED'
                    AND changed_at >= now() - INTERVAL 7 DAY
                ) AS completed_last_7d
            
            FROM analytics.raw
            WHERE track_id = {track:UUID}
            GROUP BY track_id
            FORMAT JSONEachRow;
            """;

    public static final String Q_SPRINT_STATS = """
            SELECT
                sprint_id,
                count() AS total_tasks,
            
                countIf(task_status = 'COMPLETED') AS completed_tasks,
                countIf(task_status != 'COMPLETED') AS remaining_tasks,
                countIf(task_status = 'OVERDUE') AS overdue_tasks,
                countIf(task_status = 'PLANNED') AS active_wip,
            
                round(
                    countIf(task_status = 'COMPLETED') / count() * 100,
                    2
                ) AS completion_percent,
            
                round(
                    countIf(task_status = 'OVERDUE') / count() * 100,
                    2
                ) AS overdue_percent,
            
                round(
                    countIf(task_status = 'OVERDUE')
                    /
                    countIf(task_status != 'COMPLETED')
                    * 100,
                    2
                ) AS overdue_among_active_percent,
            
                avgIf(
                    dateDiff('day', changed_at, now()),
                    task_status != 'COMPLETED'
                ) AS avg_active_age_days

            FROM analytics.task_actual_state_v
            WHERE sprint_id = {sprint:UUID}
            GROUP BY sprint_id
            FORMAT JSONEachRow;
            """;

    public static final String Q_INGEST_EVENT = """
            INSERT
            INTO analytics.raw
            FORMAT JSONEachRow
            
            """;
}
