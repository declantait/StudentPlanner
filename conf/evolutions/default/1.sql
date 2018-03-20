# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table folder (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  constraint pk_folder primary key (id)
);

create table task (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  due_date                      timestamp,
  important                     boolean default false not null,
  folder_id                     bigint,
  constraint pk_task primary key (id)
);

alter table task add constraint fk_task_folder_id foreign key (folder_id) references folder (id) on delete restrict on update restrict;
create index ix_task_folder_id on task (folder_id);


# --- !Downs

alter table task drop constraint if exists fk_task_folder_id;
drop index if exists ix_task_folder_id;

drop table if exists folder;

drop table if exists task;

