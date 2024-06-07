create table if not exists users
(
    id       bigserial primary key,
    username varchar(255) unique
);

create table if not exists products
(
    id           bigserial primary key,
    user_id      bigint      not null,
    account      varchar(20) not null check ( length(trim(account)) > 0 ),
    balance      bigint,
    product_type smallint,
    constraint fk_products_user foreign key (user_id) references users (id) on delete cascade
);
create index if not exists products_user_id_idx on products (user_id);
create index if not exists products_account_idx on products (account);
