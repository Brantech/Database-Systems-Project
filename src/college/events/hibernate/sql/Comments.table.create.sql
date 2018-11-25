create table APP.COMMENT (
  ID varchar(255) not null,

  USER_ID varchar(255) not null,
  TITLE varchar(255) not null,
  MESSAGE varchar(255) not null,
  EVENT_ID varchar(255) not null,

  primary key (ID),
  foreign key (USER_ID) references APP.USERS(USER_ID) on delete cascade,
  foreign key (EVENT_ID) references APP.EVENTS(EVENT_ID) on delete cascade
)
