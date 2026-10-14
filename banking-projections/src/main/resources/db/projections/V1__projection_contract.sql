CREATE TABLE consumed_events (
    consumer varchar(32) NOT NULL, event_id uuid NOT NULL, transfer_id uuid NOT NULL,
    sequence bigint NOT NULL, effect_count integer NOT NULL, delivery_count integer NOT NULL DEFAULT 1, payload jsonb NOT NULL,
    PRIMARY KEY (consumer, event_id)
);
