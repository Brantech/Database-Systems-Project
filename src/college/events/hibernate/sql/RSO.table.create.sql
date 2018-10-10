create sequence APP.RSO_SEQUENCE start with 1 increment by 1;

create table APP.RSO (
  RSO_ID integer not null,

  ADMIN_ID integer not null,
  MEMBERS integer not null,

  primary key (RSO_ID),
  foreign key (ADMIN_ID) references APP.USERS(USER_ID),

  check (MEMBERS > 4)
)