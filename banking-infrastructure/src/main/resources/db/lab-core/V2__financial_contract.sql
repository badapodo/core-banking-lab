-- Tables are observable lab contracts; transaction and locking strategy remain learner choices.
ALTER TABLE accounts ADD CONSTRAINT nonnegative_balance CHECK (balance >= 0);
CREATE TABLE transfers (
    transfer_id uuid PRIMARY KEY, source_account_id uuid NOT NULL REFERENCES accounts,
    target_account_id uuid NOT NULL REFERENCES accounts, amount bigint NOT NULL CHECK (amount > 0),
    status varchar(24) NOT NULL, committed_at timestamptz,
    CHECK (source_account_id <> target_account_id)
);
CREATE TABLE funding_baselines (
    account_id uuid PRIMARY KEY REFERENCES accounts, opening_balance bigint NOT NULL CHECK (opening_balance >= 0)
);
CREATE TABLE ledger_entries (
    entry_id uuid PRIMARY KEY, transfer_id uuid NOT NULL REFERENCES transfers,
    account_id uuid NOT NULL REFERENCES accounts, side varchar(6) NOT NULL CHECK (side IN ('DEBIT', 'CREDIT')),
    amount bigint NOT NULL CHECK (amount > 0), created_at timestamptz NOT NULL DEFAULT now()
);
CREATE TABLE idempotency_records (
    idempotency_key varchar(200) PRIMARY KEY, request_hash varchar(128) NOT NULL,
    transfer_id uuid NOT NULL REFERENCES transfers, response_status integer NOT NULL, response_body jsonb NOT NULL
);
CREATE TABLE outbox_events (
    event_id uuid PRIMARY KEY, transfer_id uuid NOT NULL REFERENCES transfers,
    payload jsonb NOT NULL, state varchar(24) NOT NULL DEFAULT 'PENDING',
    attempts integer NOT NULL DEFAULT 0, created_at timestamptz NOT NULL DEFAULT now(), published_at timestamptz
);
