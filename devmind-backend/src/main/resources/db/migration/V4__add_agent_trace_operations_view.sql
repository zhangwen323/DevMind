ALTER TABLE agent_traces
    ADD COLUMN trace_key VARCHAR(100);

UPDATE agent_traces
SET trace_key = CONCAT('legacy-trace-', id)
WHERE trace_key IS NULL;

ALTER TABLE agent_traces
    ALTER COLUMN trace_key SET NOT NULL;
