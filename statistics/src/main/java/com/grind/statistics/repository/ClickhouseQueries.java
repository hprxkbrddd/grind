package com.grind.statistics.repository;

public class ClickhouseQueries {
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
