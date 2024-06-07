create table if not exists used_limits
(
    id            bigserial primary key,
    user_id       bigint,
    limit_date    date   not null default cast(now() as date),
    daily_limit   bigint not null check ( daily_limit > 0 ),
    current_limit bigint not null check ( current_limit >= 0 )
);
create unique index if not exists used_limits_user_id_timestamp_idx on used_limits (user_id, limit_date);


