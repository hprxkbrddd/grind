CREATE TABLE IF NOT EXISTS analytics.raw(
    event_id UUID,
    track_id UUID,
    sprint_id UUID,
    task_id UUID,
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
ORDER BY (sprint_id, track_id, task_id, version, event_id);

CREATE TABLE IF NOT EXISTS analytics.task_actual_state
(
  task_id UUID,
  track_id UUID,
  sprint_id UUID,

  status_state AggregateFunction(
    argMax,
    Enum8(
        'UNKNOWN' = 0,
        'CREATED'   = 1,
        'PLANNED'   = 2,
        'COMPLETED' = 3,
        'OVERDUE'  = 4
    ),
    UInt64
  ),
  changed_at_state AggregateFunction(max, DateTime64(3, 'UTC')),

  changed_month UInt32
)
ENGINE = AggregatingMergeTree
ORDER BY (task_id, track_id, sprint_id);
