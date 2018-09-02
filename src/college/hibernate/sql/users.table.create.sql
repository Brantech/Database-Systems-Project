create sequence APP.USERS_SEQUENCE start with 1 increment by 1;

create table APP.USERS (
  PK integer not null,

  firstName varchar(20) not null,
  lastName varchar(20) not null,
  username varchar(20) unique not null,
  password varchar(20) not null,

  primary key (PK)
)