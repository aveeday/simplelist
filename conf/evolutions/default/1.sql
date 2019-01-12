# --- !Ups

create table "message" (
  "id" bigint generated by default as identity(start with 1) not null primary key,
  "text" varchar not null
);

# --- !Downs

drop table "message" if exists;
