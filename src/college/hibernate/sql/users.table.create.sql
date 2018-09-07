create sequence APP.USERS_SEQUENCE start with 1 increment by 1;

create table APP.USERS (
  PK integer not null,

  FIRSTNAME varchar(255) not null,
  LASTNAME varchar(255) not null,
  USERNAME varchar(20) unique not null,
  PASSWORD long varchar not null,
  EMAIL varchar(255) unique not null,
  SALT long varchar not null,

  primary key (PK)
)