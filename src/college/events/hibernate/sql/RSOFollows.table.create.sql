create table RSOFollows (
  USER_ID varchar(255),
  RSO_ID varchar(255),

  foreign key (USER_ID) references APP.USERS(USER_ID) on delete cascade,
  foreign key (RSO_ID) references APP.RSO(RSO_ID)  on delete cascade
)