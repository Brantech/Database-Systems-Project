create sequence APP.RSO_SEQUENCE start with 1 increment by 1;

create table APP.RSO (
  EVENT_ID integer not null,

  NAME varchar(255) not null,
  DESCRIPTION varchar(255),
  TYPE varchar(20) not null,
  UNIVERSITY varchar(255) not null,
  ADMIN_ID varchar(255) not null,

  primary key (EVENT_ID),

  foreign key (ADMIN_ID) references APP.USERS(USER_ID)
)