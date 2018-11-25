create sequence APP.USERS_SEQUENCE start with 1 increment by 1;

create table APP.USERS (
  USER_ID varchar(255) not null,

  FIRSTNAME varchar(255) not null,
  LASTNAME varchar(255) not null,
  USERNAME varchar(20) unique not null,
  PASSWORD clob(512) not null,
  EMAIL varchar(255) unique not null,
  TYPE varchar(20) not null,

  STUDENT_ID varchar(255) unique,
  UNI_ID varchar(255),

  primary key (USER_ID),
  foreign key (UNI_ID) references APP.UNIVERSITIES(UNI_ID) on delete set null
)
