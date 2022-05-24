create table if not exists meteodata (
  id serial primary key,
  city text,
  temperature float,
  datetime  timestamp default now()
);
