create table task (
  id                            integer auto_increment not null,
  work_day_id                   integer,
  task_id                       varchar(255),
  start_time                    time,
  end_time                      time,
  comment                       varchar(255),
  min_per_task                  integer,
  constraint pk_task primary key (id)
);

create table time_logger (
  id                            integer auto_increment not null,
  constraint pk_time_logger primary key (id)
);

create table work_day (
  id                            integer auto_increment not null,
  work_month_id                 integer,
  required_min_per_day          bigint,
  extra_min_per_day             bigint,
  actual_day                    date,
  sum_per_day                   bigint,
  constraint pk_work_day primary key (id)
);

create table work_month (
  id                            integer auto_increment not null,
  time_logger_id                integer,
  date                          date,
  sum_per_month                 bigint,
  required_min_per_month        bigint,
  extra_min_per_month           bigint,
  is_weekend_enabled            tinyint(1) default 0,
  constraint pk_work_month primary key (id)
);

