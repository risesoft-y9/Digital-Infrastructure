alter table Y9_ORG_ORGBASES_ROLES add NEW_ID VARCHAR(38) not null;
UPDATE Y9_ORG_ORGBASES_ROLES SET NEW_ID = CAST(ID AS CHAR);
alter table Y9_ORG_ORGBASES_ROLES drop column ID;
alter table Y9_ORG_ORGBASES_ROLES change NEW_ID ID varchar(38) not null comment '主键id';
alter table Y9_ORG_ORGBASES_ROLES add constraint Y9_ORG_ORGBASES_ROLES_pk primary key (ID);

alter table  Y9_ORG_PERSONS_GROUPS add NEW_ID VARCHAR(38) not null;
UPDATE  Y9_ORG_PERSONS_GROUPS SET NEW_ID = CAST(ID AS CHAR);
alter table  Y9_ORG_PERSONS_GROUPS drop column ID;
alter table  Y9_ORG_PERSONS_GROUPS change NEW_ID ID varchar(38) not null comment '主键id';
alter table  Y9_ORG_PERSONS_GROUPS add constraint  Y9_ORG_PERSONS_GROUPS_pk primary key (ID);

alter table Y9_ORG_PERSONS_POSITIONS add NEW_ID VARCHAR(38) not null;
UPDATE Y9_ORG_PERSONS_POSITIONS SET NEW_ID = CAST(ID AS CHAR);
alter table Y9_ORG_PERSONS_POSITIONS drop column ID;
alter table Y9_ORG_PERSONS_POSITIONS change NEW_ID ID varchar(38) not null comment '主键id';
alter table Y9_ORG_PERSONS_POSITIONS add constraint Y9_ORG_PERSONS_POSITIONS_pk primary key (ID);

alter table Y9_ORG_POSITIONS_GROUPS add NEW_ID VARCHAR(38) not null;
UPDATE Y9_ORG_POSITIONS_GROUPS SET NEW_ID = CAST(ID AS CHAR);
alter table Y9_ORG_POSITIONS_GROUPS drop column ID;
alter table Y9_ORG_POSITIONS_GROUPS change NEW_ID ID varchar(38) not null comment '主键id';
alter table Y9_ORG_POSITIONS_GROUPS add constraint Y9_ORG_POSITIONS_GROUPS_pk primary key (ID);

alter table Y9_ORG_PERSONS_RESOURCES add NEW_ID VARCHAR(38) not null;
UPDATE Y9_ORG_PERSONS_RESOURCES SET NEW_ID = CAST(ID AS CHAR);
alter table Y9_ORG_PERSONS_RESOURCES drop column ID;
alter table Y9_ORG_PERSONS_RESOURCES change NEW_ID ID varchar(38) not null comment '主键id';
alter table Y9_ORG_PERSONS_RESOURCES add constraint Y9_ORG_PERSONS_RESOURCES_pk primary key (ID);

alter table Y9_ORG_PERSONS_ROLES add NEW_ID VARCHAR(38) not null;
UPDATE Y9_ORG_PERSONS_ROLES SET NEW_ID = CAST(ID AS CHAR);
alter table Y9_ORG_PERSONS_ROLES drop column ID;
alter table Y9_ORG_PERSONS_ROLES change NEW_ID ID varchar(38) not null comment '主键id';
alter table Y9_ORG_PERSONS_ROLES add constraint Y9_ORG_PERSONS_ROLES_pk primary key (ID);

alter table Y9_ORG_POSITIONS_RESOURCES add NEW_ID VARCHAR(38) not null;
UPDATE Y9_ORG_POSITIONS_RESOURCES SET NEW_ID = CAST(ID AS CHAR);
alter table Y9_ORG_POSITIONS_RESOURCES drop column ID;
alter table Y9_ORG_POSITIONS_RESOURCES change NEW_ID ID varchar(38) not null comment '主键id';
alter table Y9_ORG_POSITIONS_RESOURCES add constraint Y9_ORG_POSITIONS_RESOURCES_pk primary key (ID);

alter table Y9_ORG_POSITIONS_ROLES add NEW_ID VARCHAR(38) not null;
UPDATE Y9_ORG_POSITIONS_ROLES SET NEW_ID = CAST(ID AS CHAR);
alter table Y9_ORG_POSITIONS_ROLES drop column ID;
alter table Y9_ORG_POSITIONS_ROLES change NEW_ID ID varchar(38) not null comment '主键id';
alter table Y9_ORG_POSITIONS_ROLES add constraint Y9_ORG_POSITIONS_ROLES_pk primary key (ID);

