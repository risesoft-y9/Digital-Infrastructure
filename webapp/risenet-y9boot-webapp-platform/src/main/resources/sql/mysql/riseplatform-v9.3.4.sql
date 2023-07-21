/*
Navicat MySQL Data Transfer

Target Server Type    : MYSQL
Target Server Version : 50725

Date: 2020-05-07 11:09
*/

CREATE TABLE RC8_AC_PERSON_MENU (
	ID VARCHAR ( 255 ) NOT NULL,
	JSON LONGTEXT,
	PERSONID VARCHAR ( 50 ),
	URL VARCHAR ( 200 ),
	PRIMARY KEY ( ID ) 
) ENGINE = INNODB;

CREATE TABLE RC8_AC_PERSONROLE_MAPPING (
	ID VARCHAR ( 38 ) NOT NULL,
	APPID VARCHAR ( 255 ),
	APPNAME VARCHAR ( 255 ),
	DEPARTMENTID VARCHAR ( 255 ) NOT NULL,
	DESCRIPTION VARCHAR ( 255 ),
	PERSONID VARCHAR ( 255 ) NOT NULL,
	ROLEID VARCHAR ( 255 ) NOT NULL,
	ROLENAME VARCHAR ( 255 ),
	SYSTEMCNNAME VARCHAR ( 255 ),
	SYSTEMID VARCHAR ( 255 ),
	SYSTEMNAME VARCHAR ( 255 ),
	TENANTID VARCHAR ( 255 ),
	UPDATETIME datetime ( 6 ),
	PRIMARY KEY ( ID ) 
) ENGINE = INNODB;

CREATE TABLE RC8_AC_ROLE_PERMISSION (
	ID VARCHAR ( 255 ) NOT NULL,
	AUTHORIZER VARCHAR ( 30 ),
	CODE VARCHAR ( 255 ),
	CREATEDATETIME datetime ( 6 ) NOT NULL,
	INHERITID VARCHAR ( 255 ),
	NEGATIVE INTEGER,
	ORGUNITID VARCHAR ( 255 ),
	RESOURCEID VARCHAR ( 255 ) NOT NULL,
	ROLENODEID VARCHAR ( 255 ) NOT NULL,
	TENANTID VARCHAR ( 255 ),
	TYPE INTEGER DEFAULT 0,
	PRIMARY KEY ( ID ) 
) ENGINE = INNODB;

CREATE TABLE RC8_AC_ROLENODE_MAPPING (
	ID INTEGER NOT NULL auto_increment,
	NEGATIVE INTEGER DEFAULT 0,
	ORG_UNIT_ID VARCHAR ( 255 ) NOT NULL,
	ORG_UNIT_PARENTID VARCHAR ( 255 ),
	ORG_UNITS_ORDER INTEGER NOT NULL,
	ROLE_NODE_ID VARCHAR ( 255 ) NOT NULL,
	PRIMARY KEY ( ID ) 
) ENGINE = INNODB;

CREATE TABLE RC8_COMMON_IPADDRESSWHITELIST ( 
	ID VARCHAR ( 38 ) NOT NULL, 
	IPADDRESS VARCHAR ( 255 ), 
	PRIMARY KEY ( ID ) 
) ENGINE = INNODB;

CREATE TABLE RC8_IMPORTXML_LOG ( 
	ID VARCHAR ( 50 ) NOT NULL, 
	ERRORCONTENT LONGTEXT, 
	PRIMARY KEY ( ID )
) ENGINE = INNODB;

CREATE TABLE RC8_IMPORTXML_ORG (
	ID VARCHAR ( 50 ) NOT NULL,
	ERROR bit DEFAULT 0 NOT NULL,
	FILEPATH VARCHAR ( 255 ) NOT NULL,
	TENANTID VARCHAR ( 50 ),
	UPLOADDATE VARCHAR ( 50 ),
	USERID VARCHAR ( 50 ),
	USERNAME VARCHAR ( 50 ),
	XMLNAME VARCHAR ( 255 ),
	XMLSIZE VARCHAR ( 10 ),
	PRIMARY KEY ( ID ) 
) ENGINE = INNODB;

CREATE TABLE RC8_ORG_ADDRESSBOOK (
	ID VARCHAR ( 255 ) NOT NULL,
	DN VARCHAR ( 2000 ),
	EMAIL VARCHAR ( 255 ),
	GUIDPATH VARCHAR ( 800 ),
	MOBILE VARCHAR ( 255 ),
	NAME VARCHAR ( 255 ),
	OFFICEADDRESS VARCHAR ( 255 ),
	OFFICEPHONE VARCHAR ( 255 ),
	ORDEREDPATH VARCHAR ( 500 ),
	ORGTYPE VARCHAR ( 255 ),
	PARENT_ID VARCHAR ( 255 ),
	SEX INTEGER NOT NULL,
	TABINDEX INTEGER NOT NULL,
	TENANTID VARCHAR ( 255 ),
	PRIMARY KEY ( ID ) 
) ENGINE = INNODB;

CREATE TABLE RC8_ORG_DEPARTMENT (
	ID VARCHAR ( 255 ) NOT NULL,
	CREATETIME datetime ( 6 ),
	CUSTOMID VARCHAR ( 255 ),
	DELETED bit DEFAULT 0 NOT NULL,
	DESCRIPTION VARCHAR ( 255 ),
	DISABLED bit DEFAULT 0 NOT NULL,
	DN VARCHAR ( 2000 ),
	GUIDPATH VARCHAR ( 800 ),
	NAME VARCHAR ( 255 ),
	ORGTYPE VARCHAR ( 255 ),
	PROPERTIES VARCHAR ( 500 ),
	SHORTDN VARCHAR ( 255 ),
	TABINDEX INTEGER NOT NULL,
	ALIASNAME VARCHAR ( 255 ),
	BUREAU bit DEFAULT 0 NOT NULL,
	DEPTADDRESS VARCHAR ( 255 ),
	DEPTFAX VARCHAR ( 255 ),
	DEPTGIVENNAME VARCHAR ( 255 ),
	DEPTOFFICE VARCHAR ( 255 ),
	DEPTPHONE VARCHAR ( 255 ),
	DEPTTYPE VARCHAR ( 255 ),
	DEPTTYPENAME VARCHAR ( 255 ),
	DIVISIONSCODE VARCHAR ( 255 ),
	ENNAME VARCHAR ( 255 ),
	ESTABLISHDATE date,
	GRADECODE VARCHAR ( 255 ),
	GRADECODENAME VARCHAR ( 255 ),
	LEADER VARCHAR ( 255 ),
	MANAGER VARCHAR ( 255 ),
	PARENT_ID VARCHAR ( 255 ) NOT NULL,
	POSITION_LEADER VARCHAR ( 255 ),
	POSITION_MANAGER VARCHAR ( 255 ),
	TENANTID VARCHAR ( 255 ),
	VERSION INTEGER NOT NULL,
	ZIPCODE VARCHAR ( 255 ),
	PRIMARY KEY ( ID ) 
) ENGINE = INNODB;

CREATE TABLE RC8_ORG_DEPT_MOVE (
	ID VARCHAR ( 255 ) NOT NULL,
	CREATETIME datetime ( 6 ),
	DEPT_ID VARCHAR ( 255 ) NOT NULL,
	DEPT_NAME VARCHAR ( 200 ),
	PARENT_ID VARCHAR ( 255 ) NOT NULL,
	PERSON_NAME VARCHAR ( 200 ),
	PRIMARY KEY ( ID ) 
) ENGINE = INNODB;

CREATE TABLE RC8_ORG_GROUP (
	ID VARCHAR ( 255 ) NOT NULL,
	CREATETIME datetime ( 6 ),
	CUSTOMID VARCHAR ( 255 ),
	DELETED bit DEFAULT 0 NOT NULL,
	DESCRIPTION VARCHAR ( 255 ),
	DISABLED bit DEFAULT 0 NOT NULL,
	DN VARCHAR ( 2000 ),
	GUIDPATH VARCHAR ( 800 ),
	NAME VARCHAR ( 255 ),
	ORGTYPE VARCHAR ( 255 ),
	PROPERTIES VARCHAR ( 500 ),
	SHORTDN VARCHAR ( 255 ),
	TABINDEX INTEGER NOT NULL,
	PARENT_ID VARCHAR ( 255 ) NOT NULL,
	PRIMARY KEY ( ID ) 
) ENGINE = INNODB;

CREATE TABLE RC8_ORG_GROUP_POSITION (
	ID VARCHAR ( 50 ) NOT NULL,
	ORG_PERSON_ID VARCHAR ( 255 ),
	GROUP_ORDER INTEGER,
	ORG_POSITION_ID VARCHAR ( 255 ),
	POSITIONS_ORDER INTEGER,
	PRIMARY KEY ( ID ) 
) ENGINE = INNODB;

CREATE TABLE RC8_ORG_OPTIONCLASS ( 
	TYPE VARCHAR ( 255 ) NOT NULL, 
	NAME VARCHAR ( 255 ) NOT NULL, 
	PRIMARY KEY ( TYPE ) 
) ENGINE = INNODB;

CREATE TABLE RC8_ORG_OPTIONVALUE (
	ID VARCHAR ( 255 ) NOT NULL,
	CODE VARCHAR ( 255 ),
	NAME VARCHAR ( 255 ) NOT NULL,
	TABINDEX INTEGER NOT NULL,
	TYPE VARCHAR ( 255 ) NOT NULL,
	PRIMARY KEY ( ID ) 
) ENGINE = INNODB;

CREATE TABLE RC8_ORG_ORGANIZATION (
	ID VARCHAR ( 255 ) NOT NULL,
	CREATETIME datetime ( 6 ),
	CUSTOMID VARCHAR ( 255 ),
	DELETED bit DEFAULT 0 NOT NULL,
	DESCRIPTION VARCHAR ( 255 ),
	DISABLED bit DEFAULT 0 NOT NULL,
	DN VARCHAR ( 2000 ),
	GUIDPATH VARCHAR ( 800 ),
	NAME VARCHAR ( 255 ),
	ORGTYPE VARCHAR ( 255 ),
	PROPERTIES VARCHAR ( 500 ),
	SHORTDN VARCHAR ( 255 ),
	TABINDEX INTEGER NOT NULL,
	ENNAME VARCHAR ( 255 ),
	ORGANIZATIONCODE VARCHAR ( 255 ),
	ORGANIZATIONTYPE VARCHAR ( 255 ),
	PRINCIPALIDNUM VARCHAR ( 255 ),
	PRINCIPALIDTYPE VARCHAR ( 255 ),
	PRINCIPALNAME VARCHAR ( 255 ),
	TENANTID VARCHAR ( 255 ),
	TYPE INTEGER,
	VERSION INTEGER NOT NULL,
	PRIMARY KEY ( ID ) 
) ENGINE = INNODB;

CREATE TABLE RC8_ORG_PERSON (
	ID VARCHAR ( 255 ) NOT NULL,
	CREATETIME datetime ( 6 ),
	CUSTOMID VARCHAR ( 255 ),
	DELETED bit DEFAULT 0 NOT NULL,
	DESCRIPTION VARCHAR ( 255 ),
	DISABLED bit DEFAULT 0 NOT NULL,
	DN VARCHAR ( 2000 ),
	GUIDPATH VARCHAR ( 800 ),
	NAME VARCHAR ( 255 ),
	ORGTYPE VARCHAR ( 255 ),
	PROPERTIES VARCHAR ( 500 ),
	SHORTDN VARCHAR ( 255 ),
	TABINDEX INTEGER NOT NULL,
	CAID VARCHAR ( 255 ),
	IDNUM VARCHAR ( 255 ),
	IDTYPE VARCHAR ( 255 ),
	AVATOR VARCHAR ( 100 ),
	BIRTHDAY date,
	CITY VARCHAR ( 255 ),
	COUNTRY VARCHAR ( 255 ),
	DUTY VARCHAR ( 255 ),
	DUTYLEVELNAME VARCHAR ( 255 ),
	DUTYLEVEL INTEGER NOT NULL,
	EDUCATION VARCHAR ( 255 ),
	EMAIL VARCHAR ( 255 ),
	HOMEADDRESS VARCHAR ( 255 ),
	HOMEPHONE VARCHAR ( 255 ),
	INNERROLE VARCHAR ( 255 ),
	LOGINNAME VARCHAR ( 255 ),
	MARITALSTATUS INTEGER NOT NULL,
	MOBILE VARCHAR ( 255 ),
	OFFICEADDRESS VARCHAR ( 255 ),
	OFFICEFAX VARCHAR ( 255 ),
	OFFICEPHONE VARCHAR ( 255 ),
	OFFICIAL INTEGER NOT NULL,
	OFFICIALTYPE VARCHAR ( 255 ),
	ORDEREDPATH VARCHAR ( 500 ),
	ORIGINAL INTEGER DEFAULT 1 NOT NULL,
	ORIGINAL_ID VARCHAR ( 255 ),
	PARENT_ID VARCHAR ( 255 ),
	PASSWORD VARCHAR ( 255 ),
	PERSONTYPE VARCHAR ( 255 ),
	PLAINTEXT VARCHAR ( 255 ),
	POLICITALSTATUS VARCHAR ( 255 ),
	PROFESSIONAL VARCHAR ( 255 ),
	PROVINCE VARCHAR ( 255 ),
	ROLES LONGTEXT,
	SEX INTEGER NOT NULL,
	TENANTID VARCHAR ( 255 ),
	TENANTMANAGER bit DEFAULT 0 NOT NULL,
	UPDATETIME datetime ( 6 ),
	VERSION INTEGER NOT NULL,
	WEIXINID VARCHAR ( 255 ),
	WORKTIME date,
	PRIMARY KEY ( ID ) 
) ENGINE = INNODB;

CREATE TABLE RC8_ORG_PERSON_EXT (
	ID VARCHAR ( 255 ) NOT NULL,
	CREATETIME datetime ( 6 ),
	NAME VARCHAR ( 50 ),
	PARENT_ID VARCHAR ( 100 ) NOT NULL,
	PERSON_ID VARCHAR ( 100 ) NOT NULL,
	PHOTO LONGBLOB,
	SIGN LONGBLOB,
	UPDATETIME datetime ( 6 ),
	PRIMARY KEY ( ID ) 
) ENGINE = INNODB;

CREATE TABLE RC8_ORG_PERSON_MOVE (
	ID VARCHAR ( 255 ) NOT NULL,
	CREATETIME datetime ( 6 ),
	DEPT_ID VARCHAR ( 255 ) NOT NULL,
	DEPT_NAME VARCHAR ( 200 ),
	PERSON_ID VARCHAR ( 255 ) NOT NULL,
	PERSON_NAME VARCHAR ( 200 ),
	PRIMARY KEY ( ID ) 
) ENGINE = INNODB;

CREATE TABLE RC8_ORG_PERSONS_GROUPS (
	ID INTEGER NOT NULL auto_increment,
	GROUPS_ORDER INTEGER NOT NULL,
	ORG_GROUP_ID VARCHAR ( 255 ) NOT NULL,
	ORG_PERSON_ID VARCHAR ( 255 ) NOT NULL,
	PERSONS_ORDER INTEGER NOT NULL,
	PRIMARY KEY ( ID ) 
) ENGINE = INNODB;

CREATE TABLE RC8_ORG_POSITION (
	ID VARCHAR ( 255 ) NOT NULL,
	CREATETIME datetime ( 6 ),
	CUSTOMID VARCHAR ( 255 ),
	DELETED bit DEFAULT 0 NOT NULL,
	DESCRIPTION VARCHAR ( 255 ),
	DISABLED bit DEFAULT 0 NOT NULL,
	DN VARCHAR ( 2000 ),
	GUIDPATH VARCHAR ( 800 ),
	NAME VARCHAR ( 255 ),
	ORGTYPE VARCHAR ( 255 ),
	PROPERTIES VARCHAR ( 500 ),
	SHORTDN VARCHAR ( 255 ),
	TABINDEX INTEGER NOT NULL,
	DEPARTMENT_ID VARCHAR ( 255 ),
	DUTY VARCHAR ( 255 ),
	DUTYLEVEL INTEGER NOT NULL,
	DUTYLEVELNAME VARCHAR ( 255 ),
	DUTYTYPE VARCHAR ( 255 ),
	ORDEREDPATH VARCHAR ( 500 ),
	PARENT_ID VARCHAR ( 255 ),
	TYPE VARCHAR ( 10 ),
	PRIMARY KEY ( ID ) 
) ENGINE = INNODB;

CREATE TABLE RC8_ORG_POSITIONS_PERSONS (
	ID INTEGER NOT NULL auto_increment,
	ORG_PERSON_ID VARCHAR ( 255 ) NOT NULL,
	ORG_POSITION_ID VARCHAR ( 255 ) NOT NULL,
	PERSONS_ORDER INTEGER NOT NULL,
	POSITIONS_ORDER INTEGER NOT NULL,
	PRIMARY KEY ( ID ) 
) ENGINE = INNODB;

CREATE TABLE RC8_PERSON_CUSTOMGROUP (
	ID VARCHAR ( 38 ) NOT NULL,
	CREATETIME datetime ( 6 ) NOT NULL,
	CUSTOMID VARCHAR ( 255 ),
	GROUPNAME VARCHAR ( 50 ),
	MODIFYTIME datetime ( 6 ),
	SHAREID VARCHAR ( 50 ),
	SHARENAME VARCHAR ( 50 ),
	TABINDEX INTEGER,
	TENANTID VARCHAR ( 38 ),
	USERID VARCHAR ( 38 ),
	USERNAME VARCHAR ( 50 ),
	PRIMARY KEY ( ID ) 
) ENGINE = INNODB;

CREATE TABLE RC8_PERSON_CUSTOMGROUP_MEMBER (
	ID VARCHAR ( 38 ) NOT NULL,
	CREATETIME datetime ( 6 ) NOT NULL,
	GROUPID VARCHAR ( 38 ),
	MEMBERID VARCHAR ( 38 ),
	MEMBERNAME VARCHAR ( 20 ),
	MENBERTYPE VARCHAR ( 255 ),
	MODIFYTIME datetime ( 6 ),
	PARENTID VARCHAR ( 100 ) NOT NULL,
	SEX INTEGER,
	TABINDEX INTEGER,
	PRIMARY KEY ( ID ) 
) ENGINE = INNODB;

CREATE INDEX index_personID_Menu ON RC8_AC_PERSON_MENU ( PERSONID ASC );
CREATE INDEX index_url ON RC8_AC_PERSON_MENU ( URL ASC );
CREATE INDEX index_personID_roleMapping ON RC8_AC_PERSONROLE_MAPPING ( PERSONID ASC );
ALTER TABLE RC8_AC_PERSONROLE_MAPPING DROP INDEX UK5ja5kuivh4c866pagjn4tg1dn;
ALTER TABLE RC8_AC_PERSONROLE_MAPPING ADD CONSTRAINT UK5ja5kuivh4c866pagjn4tg1dn UNIQUE ( PERSONID, ROLEID );
CREATE INDEX index_roleNodeID ON RC8_AC_ROLE_PERMISSION ( ROLENODEID ASC );
CREATE INDEX index_resourceID_permission ON RC8_AC_ROLE_PERMISSION ( RESOURCEID ASC );
CREATE INDEX index_orgUnitID ON RC8_AC_ROLE_PERMISSION ( ORGUNITID ASC );
ALTER TABLE RC8_ORG_PERSON_EXT DROP INDEX UK_2sixx2r4bm3oh87gu3ucnydks;
ALTER TABLE RC8_ORG_PERSON_EXT ADD CONSTRAINT UK_2sixx2r4bm3oh87gu3ucnydks UNIQUE ( PERSON_ID );

INSERT INTO RC8_ORG_OPTIONCLASS (TYPE, NAME) VALUES ('duty', '职务');
INSERT INTO RC8_ORG_OPTIONCLASS (TYPE, NAME) VALUES ('dutyLevel', '职级');
INSERT INTO RC8_ORG_OPTIONCLASS (TYPE, NAME) VALUES ('dutyType', '职务类型');
INSERT INTO RC8_ORG_OPTIONCLASS (TYPE, NAME) VALUES ('officialType', '编制类型');
INSERT INTO RC8_ORG_OPTIONCLASS (TYPE, NAME) VALUES ('organizationType', '机构类型');
INSERT INTO RC8_ORG_OPTIONCLASS (TYPE, NAME) VALUES ('principalIDType', '证件类型');

INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('003c4418166d4a6d8c2b88fdbbbcb8a3', '10', '副科级', 9, 'dutyLevel');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('0123acb9531649f093880944644cf55e', '259A', '旗长', 52, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('045573f8162f47259b51440c5e7f260a', '841A', '队长', 77, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('0740d0537d9f448cba1bdad4d00105ab', '418A', '校长', 68, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('0a480f603f6e4412b6355e4a743af2de', '91', '民办非企业单位', 19, 'organizationType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('0e4ddb08818140b6a1482801add08ea2', '39', '其他机关', 12, 'organizationType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('0f874d9b1c694f07bae64887091c9e21', '9', '正科级', 8, 'dutyLevel');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('0fafd3db43c941dcbe793b2f5df455cb', '417A', '院长', 66, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('0ffbb8247eb6409c86ab14814313e9a1', '256A', '盟长', 46, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('130143a76e234602b451c1f9396cf9c0', '262A', '村长', 58, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('1413f597b898490792c45e0c9c3b83de', '11', '公司', 0, 'organizationType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('180e212bb4444532beb3e599a0dbc764', '002K', '常务委员', 3, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('18677b1b91984f35abd23d2ec3486957', '260B', '副镇长', 55, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('1c3425cc2d4a4c56aecdee665783cd9d', NULL, '行政编', 1, 'officialType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('1c710ac2730345a5a5c26c8bc873891e', '011A', '调研员', 7, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('1c8c7ea4a8824d65b223561967cc3bf1', '224A', '办事员', 36, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('1e0510a53d384cacb78d8d9e80827269', '216B', '副局长', 28, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('232975e9a3094b3aad069fdf275143f2', '73', '社会团体分支、代表机构', 17, 'organizationType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('23e87f2eaf254ea193fc7011db2bb96a', '252B', '副市长', 40, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('2ab71abda71748a5a203730feaf4e08e', '17', '个人独资企业、合伙企业', 3, 'organizationType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('2b817661d02047b3bd3a9c1c0ad6d91a', '96', '城市居民委员会', 23, 'organizationType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('2fa22fbfeaae4dc88de57604330e6102', NULL, '其他', 4, 'officialType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('37601f7a1c2b461380aafde5b6939b53', '004B', '副主任', 5, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('3781e539c5d04b839ac9ec5c32a85249', '215A', '关长', 25, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('37feb0725e204df09c4e640896ec4d3e', '51', '事业单位法人', 13, 'organizationType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('387710124df443f79555c8361cd60637', '259B', '副旗长', 53, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('3d51a080d38b449ba1608b2ce505624d', '102A', '秘书长', 12, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('431208558b284cfbb925dce9ebfe3f91', '211A', '部长', 18, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('43aa4e722f1d409faddbc403163acb93', '5', '司、局、地厅级', 4, 'dutyLevel');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('447315c05a3e48faa99edf3f671e529b', '220B', '副科长', 32, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('460420ad395c4aaa80d561a1efde3f69', '219B', '副处长', 30, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('48d0420e2c47468d90774c8ce948a0a7', '59', '其它事业单位', 15, 'organizationType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('4c9cb1c30ea14e7487ab4e749870612d', '841B', '副队长', 78, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('4cf3b39c9335438f9cae2e352ce735f2', '35', '政协组织', 9, 'organizationType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('4eee0d920ec248e08f54c13852bfd56a', '261B', '副乡长', 57, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('50f3d567544e4d3d8ae86c4681fac871', '221T', '副主任科员', 34, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('5109118f1dc94d0884b8ce1cae45d33e', '95', '农村居民委员会', 22, 'organizationType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('5132875dae594e52b6261d755da3827d', '12', '办事员级', 11, 'dutyLevel');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('519b5357c4a54e618f7ffa0f249f22bf', '252J', '市长助理', 41, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('51f355002c914825a471fdaeb668950f', '002A', '委员', 2, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('5746c4c9a7b345a6bb664c3d1a5d92b3', '219A', '处长', 29, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('57c3276f42c540ef97e5976179e88c10', '261A', '乡长', 56, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('593cef9d823f468b9b55b45c57cb5cd3', '840B', '副大队长', 76, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('5c9fccc8f4894385a71f267464df10fb', '254B', '副州长', 43, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('5f54c3d0b30f4d9b87e0322cfa52aee4', '99', '其它', 25, 'organizationType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('61321472a8c34719a3f2eaf032b9cb3e', '71', '社会团体法人', 16, 'organizationType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('63e55a9d802348c1b06dc1d6c7aeec25', '13', '非公司制企业法人', 1, 'organizationType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('64b9c473bd434caa875a998a90b707bd', '2', '副总理、国务委员级', 1, 'dutyLevel');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('67ce6cc7c5b84b3386a3dc9b1e24a391', '201A', '主席', 16, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('69997e58aa7e4cbab27ff85ec4c29d31', '405A', '理事长', 62, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('6a85ed9e5041431189b51d30d855e181', '212B', '副审计长', 22, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('6ac870b95abd4902b1320d97b227e4db', '004A', '主任', 4, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('6ae7653523a64d4986845d0e4f1dfbe4', '32', '国家权力机关法人', 6, 'organizationType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('6c3f4f25f9244f05b46b95f4338e4676', '438B', '副站长', 71, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('6d2fdda03f8045258cc921dae7e07b3f', '220A', '科长', 31, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('6d41bfd7ff5d4e05a6fd7b78d7e4a9c9', '15', '企业分支机构', 2, 'organizationType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('6dc3c7de31824107966c00cb31a67595', '083Q', '总工程师', 10, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('73aab6034e3542518b3f61c87c97ad69', '255B', '副区长', 45, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('7488ad7d2da54998bb79ec35d2cc3c4b', '256B', '副盟长', 47, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('7691aa11a6a24e46a9676a46399f190a', '251B', '副省长', 38, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('76e1bc0e313d43aca18c43dd7411aebb', '258A', '县长', 50, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('774c9cb92903438bad6ea98652d3907c', '262B', '副村长', 59, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('77a91680512d42c49372ed875f88f70f', '93', '基金会', 20, 'organizationType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('78fcf40f4b78442ca04017f12a77b63a', '33', '国家行政机关法人', 7, 'organizationType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('7ae37684b80d4a07b343b84d5e652eae', '801A', '参谋长', 72, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('7c4373bb478d41e1b796abbea77c136a', '8', '副县、副处级', 7, 'dutyLevel');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('7d2f8f3d8cd841f288153adcc34e6a0e', '12', '户口簿', 2, 'principalIDType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('7e9366bed97a4799a8f6257b554b01b7', '36', '民主党派', 10, 'organizationType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('7ff2b306a3bb4388b2e3f12da9ac9b83', '254A', '州长', 42, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('7ffb6ca4a86b4dd8a8543361b1679fe4', '102B', '副秘书长', 13, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('804ea6d9d7b34aff9d2672321f963c83', '201B', '副主席', 17, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('81c58467fe2e404ebeffaaf9b66f803d', '105A', '检察长', 14, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('826faef74f1044279c098a4602fd29e0', '11', '科员级', 10, 'dutyLevel');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('83d553ec53fe4c79a16ffeb70b11b653', '011B', '副调研员', 8, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('83e8093a47a548f395ab4f849f153967', '14', '县长', 14, 'dutyLevel');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('88c9e1e45b4b41419a9eaa2f5f8379d6', '015A', '纪检员', 9, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('8bf9770e967743df933aa2333f7921ba', '257A', '专员', 48, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('8c6d60f1c44e4335bbffb4394980161c', '10', '身份证', 0, 'principalIDType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('8f1ec868988845aaa1a4dc3d40736957', '410B', '副会长', 61, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('8f2fbc0b66214234b1580c622daa7bd2', '221S', '主任科员', 33, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('900b00946dd1428eb234075adef18429', '214B', '副署长', 24, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('921731b8b61d44848f6ca0626183df8d', '211J', '部长助理', 20, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('929e1206260241f698e400fec19da823', '418B', '副校长', 69, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('94686a44bee94a999f2afeeaac8d8657', '34', '国家司法机关法人', 8, 'organizationType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('95fd5f5748314496ac6dbcebc0266760', '258B', '副县长', 51, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('963979258b264809b07ff4d1f955da83', '105B', '副检察长', 15, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('97246f4d1968413d9a6219fa0ef0ed23', '255A', '区长', 44, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('99a5b4acac3e474a821c60c0b125d32d', '251A', '省长', 37, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('a3f947e984f94e0e93bfa1a7c97ccaff', '416B', '副所长', 65, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('a8981513953048d689f2f3edf64df7c6', '19', '其它企业', 4, 'organizationType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('a9b2faa2bd3f445b931e14fc4c64ef76', '221A', '科员', 35, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('aad28d6b6c7742aabfd47bbf73a070dc', NULL, '事业编', 2, 'officialType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('ab373ebfff4d4e038895adb9775df382', '1', '国家主席、副主席、总理级', 0, 'dutyLevel');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('ab55a42263db42d1aefa80ecf3c46a66', NULL, '公务员', 0, 'officialType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('b01f4c1b66e54a93b6b71adf1bef1cfd', '417B', '副院长', 67, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('b45d55b2210b45049dc812f36c853d1e', '211B', '副部长', 19, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('b4e13bf34d1e4fa8b95e8cf0c5cfd92d', '31', '中国共产党', 5, 'organizationType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('b7d8ce3f099a415a8c54ce06cf113fbe', '79', '其它社会团体', 18, 'organizationType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('be5e96a558f04e0b9762b61bfd718d8f', '216A', '局长', 27, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('bf4687b15fb2444cac267d7dab8dedcb', '802A', '参谋', 73, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('bf7584e01b1041da8b3c22728e50e4b1', '3', '部、省级', 2, 'dutyLevel');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('c3ff25d92a534e459a73d6778284e4c5', '405B', '副理事长', 63, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('c4379b948f0f4f2c8e283c16c6393560', '214A', '署长', 23, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('c4c343c1d83744cc8d7792bf2e1d6d1c', '851A', '政委', 74, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('c9319f98f29e4f958f393de203b80df0', '257B', '副专员', 49, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('cbced0ee92c1465aa9dbf8d081ae5ea4', '010A', '巡视员', 6, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('cbd73fad33b44749a89434473f9899ff', NULL, '企业编', 3, 'officialType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('cf88c800bf92475c8f8026fe8473b839', '416A', '所长', 64, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('d49217ffd772487daf0c46152a899a90', '97', '自定义区', 24, 'organizationType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('d62e0187d468440f98d9bbb4a644a1a0', '260A', '镇长', 54, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('d96ca5bcbf9041cb94bae9cd9ed7c885', '13', '军人证', 3, 'principalIDType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('da512a994bf54a69bceab8c78d67de5c', NULL, '领导', 0, 'dutyType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('dcabeaef5cf04bf1ae5562d981dc7917', '7', '县、处级', 6, 'dutyLevel');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('e3ea98c6b3144e3d8a0cc7b1ad74e137', '53', '事业单位分支、派出机构', 14, 'organizationType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('e4606c0d240b421d98974397372ac8f1', '840A', '大队长', 75, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('e4b883c78bed4c4c9fce02a029d676f3', NULL, '公务员', 1, 'dutyType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('e6326e32a76a4429a937e7be05194de1', '215B', '副关长', 26, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('e77678ae92f84b89846f09c10136586e', '410A', '会长', 60, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('e9d79a30734c479dad90c72c66a7e984', '438A', '站长', 70, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('e9e2b1101832485fad684d94ab3b75d6', '11', '护照', 1, 'principalIDType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('eb4c21f3e0494ddb8a9de6f0629826c7', '083R', '副总工程师', 11, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('ed04f10eef74410b9b7b4fefb994b481', '252A', '市长', 39, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('ed89ebc8e2514e4cb65969850adb90e2', '842A', '负责人', 79, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('f200b10cfe3a4ad19d6c8c5eff0b4faf', '6', '副司、副局、副地、副厅级', 5, 'dutyLevel');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('f64242ec8fd644038f6a4af42abc775c', '001A', '书记', 0, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('f982a863b76f45238f547eff8a3e390e', '94', '宗教活动场所', 21, 'organizationType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('fc14643c487545a19c5ca04311b67bb2', '37', '人民解放军、武警部队', 11, 'organizationType');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('fde719d2d51143968952d9c2fea26e56', '001B', '副书记', 1, 'duty');
INSERT INTO RC8_ORG_OPTIONVALUE (ID, CODE, NAME, TABINDEX, TYPE) VALUES ('fdf580762ffe45368d406f824a50d303', '212A', '审计长', 21, 'duty');