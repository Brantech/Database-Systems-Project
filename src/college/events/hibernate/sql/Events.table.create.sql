create sequence APP.EVENTS_SEQUENCE start with 1 increment by 1;

create table APP.EVENTS (
  EVENT_ID integer not null,

  NAME varchar(255) not null,
  TYPE varchar(20) not null,
  CATEGORY varchar(255) not null,
  DESCRIPTION varchar(255),
  TIME varchar(255) not null,
  DATE varchar(255) not null,
  LOCATION varchar(255),
  CONTACT_PHONE varchar(255),
  CONTACT_EMAIL varchar(255),

  primary key (EVENT_ID)
)