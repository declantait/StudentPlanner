# --- First database schema

# --- !Ups

create table folder (
  id                        bigint not null,
  name                      varchar(255),
  constraint pk_folder primary key (id))
;

create table task (
  id                        bigint not null,
  name                      varchar(255),
  dueDate                   timestamp,
  folder_id                bigint,
  constraint pk_task primary key (id))
;

create table subtask (
  id                        bigint not null,
  name                      varchar(255),
  task_id                   bigint,
  constraint pk_subtask primary key (id))
;

create sequence folder_seq start with 1000;

create sequence task_seq start with 1000;

create sequence subtask_seq start with 1000;

alter table task add constraint fk_task_folder_1 foreign key (folder_id) references folder (id) on delete restrict on update restrict;
create index ix_task_folder_1 on task (folder_id);

alter table subtask add constraint fk_subtask_task_1 foreign key (task_id) references task (id) on delete restrict on update restrict;
create index ix_subtask_task_1 on subtask (task_id);


# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists folder;

drop table if exists task;

drop table if exists subtask;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists folder_seq;

drop sequence if exists task_seq;

drop sequence if exists subtask_seq;