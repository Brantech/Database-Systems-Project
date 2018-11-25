create sequence APP.RSO_SEQUENCE start with 1 increment by 1;

create table APP.RSO (
  RSO_ID varchar(255) not null,

  ADMIN_ID varchar(255) not null,
  NAME varchar(255) not null,
  DESCRIPTION varchar(255) not null,
  TYPE varchar(255) not null,
  MEMBERS integer not null,
  STATUS varchar(255) not null,
  UNI_ID varchar(255) not null,

  primary key (RSO_ID),
  foreign key (ADMIN_ID) references APP.USERS(USER_ID) on delete cascade,
  foreign key (UNI_ID) references APP.UNIVERSITIES(UNI_ID) on delete cascade
)