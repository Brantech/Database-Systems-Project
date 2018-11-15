create table APP.Messages (
  ID varchar(255),
  SUBJECT varchar(255),
  MESSAGE_TYPE varchar(255),
  MESSAGE clob(512),
  PAYLOAD clob(512),
  SENDER_ID varchar(255),
  UNI_ID varchar(255),
  SEND_DATE varchar(19) not null,

  primary key (ID),
  foreign key (SENDER_ID) references APP.USERS(USER_ID) on delete cascade ,
  foreign key (UNI_ID) references APP.UNIVERSITIES(UNI_ID)
)