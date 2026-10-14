CREATE TABLE sagas (
    saga_id uuid PRIMARY KEY, idempotency_key varchar(200) NOT NULL UNIQUE,
    request jsonb NOT NULL, transfer_id uuid, state varchar(32) NOT NULL,
    attempts integer NOT NULL DEFAULT 0, last_error varchar(256),
    state_history jsonb NOT NULL DEFAULT '[]',
    updated_at timestamptz NOT NULL DEFAULT now()
);
