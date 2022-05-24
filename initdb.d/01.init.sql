drop table if exists requests;
create table requests (
  id serial primary key,
  city text,
  temperature float,
  created_at  timestamp default now()
);
