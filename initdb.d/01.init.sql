drop table if exists requests;
create table meteodata (
  id serial primary key,
  city text,
  temperature float,
  datetime  timestamp default now()
);
