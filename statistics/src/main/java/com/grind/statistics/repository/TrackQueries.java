package com.grind.statistics.repository;

public class TrackQueries {
    public static final String Q_TRACK_COMPLETION = """
            SELECT
                track_id,
                round(
                    countIf(task_status = 'COMPLETED') / count() * 100,
                    2
                ) AS completion_percent,
                count() AS total_tasks
            FROM analytics.task_actual_state_v
            WHERE track_id = {track:UUID}
            GROUP BY track_id
            FORMAT JSONEachRow;
            """;
    public static final String Q_TRACK_REMAINING_LOAD = """
            SELECT
                track_id,
                countIf(task_status != 'COMPLETED') AS remaining_tasks
            FROM analytics.task_actual_state_v
            WHERE track_id = {track:UUID}
            GROUP BY track_id
            FORMAT JSONEachRow;
            """;
    public static final String Q_TRACK_OVERDUE_PRESSURE = """
            SELECT
                track_id,
                round(
                    countIf(task_status = 'OVERDUE') / count() * 100,
                    2
                ) AS overdue_percent
            FROM analytics.task_actual_state_v
            WHERE track_id = {track:UUID}
            GROUP BY track_id
            FORMAT JSONEachRow;
            """;
    public static final String Q_TRACK_ACTIVE_TASKS_AGING = """
            SELECT
                track_id,
                avg(
                    dateDiff('day', changed_at, now())
                ) AS avg_active_age_days
            FROM analytics.task_actual_state_v
            WHERE task_status != 'COMPLETED' AND track_id = {track:UUID}
            GROUP BY track_id
            FORMAT JSONEachRow;
            """;
    public static final String Q_TRACK_WORK_IN_PROGRESS = """
            SELECT
                track_id,
                countIf(task_status = 'PLANNED') AS active_wip
            FROM analytics.task_actual_state_v
            WHERE track_id = {track:UUID}
            GROUP BY track_id
            FORMAT JSONEachRow;
            """;
    public static final String Q_TRACK_OVERDUE_AMONG_COMPLETED = """
            SELECT
                track_id,
                round(
                    countIf(task_status = 'OVERDUE')
                    /
                    countIf(task_status != 'COMPLETED')
                    * 100,
                    2
                ) AS overdue_among_active_percent
            FROM analytics.task_actual_state_v
            WHERE track_id = {track:UUID}
            GROUP BY track_id
            FORMAT JSONEachRow;
            """;
    public static final String Q_COMPLETED_LAST_MONTH = """
            SELECT
                track_id,
                count() AS completed_last_30d
            FROM analytics.raw
            WHERE task_status = 'COMPLETED'
              AND changed_at >= now() - INTERVAL 30 DAY
              AND track_id = {track:UUID}
            GROUP BY track_id
            FORMAT JSONEachRow;
            """;
    public static final String Q_COMPLETED_LAST_WEEK = """
            SELECT
                track_id,
                count() AS completed_last_30d
            FROM analytics.raw
            WHERE task_status = 'COMPLETED'
              AND changed_at >= now() - INTERVAL 7 DAY
              AND track_id = {track:UUID}
            GROUP BY track_id
            FORMAT JSONEachRow;
            """;
}
