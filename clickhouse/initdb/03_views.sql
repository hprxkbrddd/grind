CREATE MATERIALIZED VIEW analytics.task_actual_state_mv
TO analytics.task_actual_state
AS
SELECT
    task_id,
    track_id,
    sprint_id,
    argMaxState(task_status, version) AS status_state,
    maxState(changed_at)              AS changed_at_state,
    toYYYYMM(argMax(changed_at, version)) AS changed_month
FROM analytics.raw
GROUP BY
    task_id,
    track_id,
    sprint_id;

CREATE VIEW IF NOT EXISTS analytics.task_actual_state_v
AS
SELECT
    task_id,
    sprint_id,
    track_id,
    argMaxMerge(status_state) AS task_status,
    maxMerge(changed_at_state)     AS changed_at,
    toYYYYMM(maxMerge(changed_at_state)) AS changed_month
FROM analytics.task_actual_state
GROUP BY
    task_id,
    sprint_id,
    track_id;

CREATE VIEW IF NOT EXISTS analytics.per_period_v
AS
SELECT
    task_status,
    count() cnt
FROM analytics.task_actual_state_v
GROUP BY task_status
ORDER BY task_status