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
    maxState(changed_at)               AS changed_at_state
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
    toYYYYMM(maxMerge(changed_at_state)) AS changed_month
FROM analytics.task_actual_state_mv
GROUP BY
    task_id,
    track_id,
    user_id;