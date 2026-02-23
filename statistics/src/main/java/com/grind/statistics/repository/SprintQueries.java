package com.grind.statistics.repository;

public class SprintQueries {
    public static final String Q_SPRINT_COMPLETION = """
            SELECT
                sprint_id,
                round(
                    countIf(task_status = 'COMPLETED') / count() * 100,
                    2
                ) AS completion_percent,
                count() AS total_tasks
            FROM analytics.task_actual_state_v
            WHERE sprint_id = {sprint:UUID}
            GROUP BY sprint_id
            FORMAT JSONEachRow;
            """;
    public static final String Q_SPRINT_REMAINING_LOAD = """
            SELECT
                sprint_id,
                countIf(task_status != 'COMPLETED') AS remaining_tasks
            FROM analytics.task_actual_state_v
            WHERE sprint_id = {sprint:UUID}
            GROUP BY sprint_id
            FORMAT JSONEachRow;
            """;
    public static final String Q_SPRINT_OVERDUE_PRESSURE = """
            SELECT
                sprint_id,
                round(
                    countIf(task_status = 'OVERDUE') / count() * 100,
                    2
                ) AS overdue_percent
            FROM analytics.task_actual_state_v
            WHERE sprint_id = {sprint:UUID}
            GROUP BY sprint_id
            FORMAT JSONEachRow;
            """;
    public static final String Q_SPRINT_ACTIVE_TASKS_AGING = """
            SELECT
                sprint_id,
                avg(
                    dateDiff('day', changed_at, now())
                ) AS avg_active_age_days
            FROM analytics.task_actual_state_v
            WHERE task_status != 'COMPLETED' AND sprint_id = {sprint:UUID}
            GROUP BY sprint_id
            FORMAT JSONEachRow;
            """;
    public static final String Q_SPRINT_WORK_IN_PROGRESS = """
            SELECT
                sprint_id,
                countIf(task_status = 'PLANNED') AS active_wip
            FROM analytics.task_actual_state_v
            WHERE sprint_id = {sprint:UUID}
            GROUP BY sprint_id
            FORMAT JSONEachRow;
            """;
    public static final String Q_SPRINT_OVERDUE_AMONG_COMPLETED = """
            SELECT
                sprint_id,
                round(
                    countIf(task_status = 'OVERDUE')
                    /
                    countIf(task_status != 'COMPLETED')
                    * 100,
                    2
                ) AS overdue_among_active_percent
            FROM analytics.task_actual_state_v
            WHERE sprint_id = {sprint:UUID}
            GROUP BY sprint_id
            FORMAT JSONEachRow;
            """;
}
