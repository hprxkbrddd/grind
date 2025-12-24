DROP MATERIALIZED VIEW IF EXISTS analytics_task_actual_state_mv;
CREATE MATERIALIZED VIEW analytics_task_actual_state_mv
TO analytics.task_actual_state
AS
SELECT
    task_id,
    track_id,
    sprint_id,
    argMaxState(task_status, version) AS task_status_state,
    maxState(changed_at) AS changed_at_state,
    toYYYYMM(changed_at) AS changed_month
FROM analytics_raw
GROUP BY task_id, track_id, sprint_id;

DROP VIEW IF EXISTS analytics_task_actual_state_v;
CREATE VIEW analytics_task_actual_state_v
AS
SELECT
  task_id,
  sprint_id,
  track_id,
  argMaxMerge(task_status_state) AS task_status
FROM analytics_task_actual_state
GROUP BY task_id, sprint_id, track_id;