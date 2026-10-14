create table accounts (
    account_id uuid primary key,
    balance bigint not null,
    status varchar(20) not null,
    created_at timestamptz not null default now()
);
