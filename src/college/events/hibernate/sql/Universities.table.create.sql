create table APP.UNIVERSITIES (
  UNI_ID varchar(255) not null,

  NAME varchar(255) not null,
  LOCATION varchar(255),
  DESCRITPION varchar(255),
  STUDENTS INTEGER not null,

  primary key (UNI_ID)
)