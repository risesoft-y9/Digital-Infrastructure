/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : y9_public_tjjw

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2018-09-06 11:03:47
*/

SET
FOREIGN_KEY_CHECKS=0;

CREATE TABLE `rc8_ac_iconitem`
(
    `id`         varchar(38) NOT NULL,
    `personId`   varchar(38)  DEFAULT NULL,
    `iconType`   varchar(50)  DEFAULT NULL,
    `appId`      varchar(38)  DEFAULT NULL,
    `systemId`   varchar(38)  DEFAULT NULL,
    `appName`    varchar(255) DEFAULT NULL,
    `systemName` varchar(255) DEFAULT NULL,
    `url`        varchar(255) DEFAULT NULL,
    `icon`       varchar(255) DEFAULT NULL,
    `deskIndex`  int(11) DEFAULT NULL,
    `tabIndex`   int(11) DEFAULT NULL,
    `status`     int(11) DEFAULT NULL,
    `showHome`   int(11) DEFAULT NULL,
    `folderId`   varchar(38)  DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_i9mas9molqcmh5ybacc97sie1` (`appId`,`personId`),
    KEY          `PK_Icon_personID` (`personId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rc8_ac_iconitem
-- ----------------------------

-- ----------------------------
-- Table structure for rc8_ac_operation
-- ----------------------------
CREATE TABLE `rc8_ac_operation`
(
    `ID`             varchar(255) COLLATE utf8_bin NOT NULL,
    `CODE`           varchar(255) COLLATE utf8_bin NOT NULL,
    `CREATEDATETIME` datetime(0) NOT NULL,
    `DESCRIPTION`    varchar(1000) COLLATE utf8_bin DEFAULT NULL,
    `NAME`           varchar(255) COLLATE utf8_bin NOT NULL,
    `PROPERTIES`     varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `resourceID`     varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `TABINDEX`       int(11) NOT NULL,
    `TYPE`           varchar(255) COLLATE utf8_bin NOT NULL,
    PRIMARY KEY (`ID`),
    KEY              `index_resourceID_operation` (`resourceID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of rc8_ac_operation
-- ----------------------------

-- ----------------------------
-- Table structure for rc8_ac_personrole_mapping
-- ----------------------------
CREATE TABLE `rc8_ac_personrole_mapping`
(
    `ID`           varchar(38) COLLATE utf8_bin  NOT NULL,
    `appID`        varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `appName`      varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `departmentID` varchar(255) COLLATE utf8_bin NOT NULL,
    `description`  varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `personID`     varchar(255) COLLATE utf8_bin NOT NULL,
    `roleID`       varchar(255) COLLATE utf8_bin NOT NULL,
    `roleName`     varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `systemCnName` varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `systemID`     varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `systemName`   varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `tenantID`     varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `updateTime`   datetime(0) DEFAULT NULL,
    PRIMARY KEY (`ID`),
    UNIQUE KEY `UK5ja5kuivh4c866pagjn4tg1dn` (`personID`,`roleID`),
    KEY            `index_personID_roleMapping` (`personID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of rc8_ac_personrole_mapping
-- ----------------------------

-- ----------------------------
-- Table structure for rc8_ac_person_menu
-- ----------------------------
CREATE TABLE `rc8_ac_person_menu`
(
    `ID`       varchar(255) COLLATE utf8_bin NOT NULL,
    `json`     longtext COLLATE utf8_bin,
    `personID` varchar(50) COLLATE utf8_bin  DEFAULT NULL,
    `url`      varchar(200) COLLATE utf8_bin DEFAULT NULL,
    PRIMARY KEY (`ID`),
    KEY        `index_personID_Menu` (`personID`),
    KEY        `index_url` (`url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of rc8_ac_person_menu
-- ----------------------------

-- ----------------------------
-- Table structure for rc8_ac_resource
-- ----------------------------
CREATE TABLE `rc8_ac_resource`
(
    `ID`             varchar(255) COLLATE utf8_bin NOT NULL,
    `ALIASNAME`      varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `CREATEDATETIME` datetime(0) DEFAULT NULL,
    `CUSTOMID`       varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `DESCRIPTION`    varchar(500) COLLATE utf8_bin  DEFAULT NULL,
    `dn`             varchar(1000) COLLATE utf8_bin DEFAULT NULL,
    `guidPath`       varchar(1200) COLLATE utf8_bin DEFAULT NULL,
    `ICON`           varchar(400) COLLATE utf8_bin  DEFAULT NULL,
    `inherent`       int(11) NOT NULL,
    `isMenu`         int(11) NOT NULL,
    `isSpecial`      int(11) NOT NULL,
    `NAME`           varchar(255) COLLATE utf8_bin NOT NULL,
    `OPENTYPE`       varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `PARENTID`       varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `PROPERTIES`     varchar(500) COLLATE utf8_bin  DEFAULT NULL,
    `SITE`           varchar(400) COLLATE utf8_bin  DEFAULT NULL,
    `systemName`     varchar(255) COLLATE utf8_bin NOT NULL,
    `TABINDEX`       int(11) NOT NULL,
    `TARGET`         varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `TYPE`           varchar(60) COLLATE utf8_bin   DEFAULT NULL,
    `URL`            varchar(400) COLLATE utf8_bin  DEFAULT NULL,
    PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of rc8_ac_resource
-- ----------------------------

-- ----------------------------
-- Table structure for rc8_ac_rolenode
-- ----------------------------
CREATE TABLE `rc8_ac_rolenode`
(
    `ID`           varchar(255) COLLATE utf8_bin NOT NULL,
    `CREATETIME`   datetime(0) DEFAULT NULL,
    `CUSTOMID`     varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `DESCRIPTION`  varchar(1000) COLLATE utf8_bin DEFAULT NULL,
    `DN`           varchar(2000) COLLATE utf8_bin DEFAULT NULL,
    `guidPath`     varchar(1200) COLLATE utf8_bin DEFAULT NULL,
    `NAME`         varchar(255) COLLATE utf8_bin NOT NULL,
    `PARENT_ID`    varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `PROPERTIES`   varchar(500) COLLATE utf8_bin  DEFAULT NULL,
    `SHORTDN`      varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `systemCnName` varchar(255) COLLATE utf8_bin NOT NULL,
    `systemName`   varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `TABINDEX`     int(11) NOT NULL,
    `TYPE`         varchar(255) COLLATE utf8_bin NOT NULL,
    PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of rc8_ac_rolenode
-- ----------------------------

-- ----------------------------
-- Table structure for rc8_ac_rolenode_mapping
-- ----------------------------
CREATE TABLE `rc8_ac_rolenode_mapping`
(
    `id`                int(11) NOT NULL AUTO_INCREMENT,
    `ORG_UNIT_ID`       varchar(255) COLLATE utf8_bin NOT NULL,
    `org_unit_parentId` varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `ORG_UNITS_ORDER`   int(11) NOT NULL,
    `ROLE_NODE_ID`      varchar(255) COLLATE utf8_bin NOT NULL,
    PRIMARY KEY (`id`),
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of rc8_ac_rolenode_mapping
-- ----------------------------

-- ----------------------------
-- Table structure for rc8_ac_role_permission
-- ----------------------------
CREATE TABLE `rc8_ac_role_permission`
(
    `id`             varchar(255) COLLATE utf8_bin NOT NULL,
    `authorizer`     varchar(30) COLLATE utf8_bin  DEFAULT NULL,
    `code`           varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `CREATEDATETIME` datetime(0) NOT NULL,
    `inheritID`      varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `negative`       int(11) DEFAULT NULL,
    `orgUnitID`      varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `RESOURCEID`     varchar(255) COLLATE utf8_bin NOT NULL,
    `ROLENODEID`     varchar(255) COLLATE utf8_bin NOT NULL,
    `tenantID`       varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `type`           int(11) DEFAULT '0',
    PRIMARY KEY (`id`),
    KEY              `index_roleNodeID` (`ROLENODEID`),
    KEY              `index_resourceID_permission` (`RESOURCEID`),
    KEY              `index_orgUnitID` (`orgUnitID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of rc8_ac_role_permission
-- ----------------------------

-- ----------------------------
-- Table structure for rc8_common_ipaddresswhitelist
-- ----------------------------
CREATE TABLE `rc8_common_ipaddresswhitelist`
(
    `id`        varchar(38) COLLATE utf8_bin NOT NULL,
    `ipAddress` varchar(255) COLLATE utf8_bin DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of rc8_common_ipaddresswhitelist
-- ----------------------------

-- ----------------------------
-- Table structure for rc8_importxml_log
-- ----------------------------
CREATE TABLE `rc8_importxml_log`
(
    `ID`           varchar(50) COLLATE utf8_bin NOT NULL,
    `errorContent` longtext COLLATE utf8_bin,
    PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of rc8_importxml_log
-- ----------------------------

-- ----------------------------
-- Table structure for rc8_importxml_org
-- ----------------------------
CREATE TABLE `rc8_importxml_org`
(
    `ID`         varchar(50) COLLATE utf8_bin  NOT NULL,
    `error`      bit(1)                        NOT NULL,
    `filePath`   varchar(255) COLLATE utf8_bin NOT NULL,
    `tenantId`   varchar(50) COLLATE utf8_bin  DEFAULT NULL,
    `uploadDate` varchar(50) COLLATE utf8_bin  DEFAULT NULL,
    `userId`     varchar(50) COLLATE utf8_bin  DEFAULT NULL,
    `userName`   varchar(50) COLLATE utf8_bin  DEFAULT NULL,
    `xmlName`    varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `xmlSize`    varchar(10) COLLATE utf8_bin  DEFAULT NULL,
    PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of rc8_importxml_org
-- ----------------------------

-- ----------------------------
-- Table structure for rc8_org_department
-- ----------------------------
CREATE TABLE `rc8_org_department`
(
    `ID`            varchar(255) COLLATE utf8_bin NOT NULL,
    `createTime`    datetime(0) DEFAULT NULL,
    `CUSTOMID`      varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `deleted`       bit(1)                        NOT NULL,
    `description`   varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `disabled`      bit(1)                        NOT NULL,
    `DN`            varchar(2000) COLLATE utf8_bin DEFAULT NULL,
    `guidPath`      varchar(800) COLLATE utf8_bin  DEFAULT NULL,
    `name`          varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `orgType`       varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `properties`    varchar(500) COLLATE utf8_bin  DEFAULT NULL,
    `shortDN`       varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `systemName`    varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `tabIndex`      int(11) NOT NULL,
    `aliasName`     varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `BUREAU`        bit(1)                        NOT NULL,
    `DEPTADDRESS`   varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `DEPTFAX`       varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `DEPTGIVENNAME` varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `DEPTOFFICE`    varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `DEPTPHONE`     varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `DEPTTYPE`      varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `DEPTTYPENAME`  varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `DIVISIONSCODE` varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `ENNAME`        varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `ESTABLISHDATE` date                           DEFAULT NULL,
    `GRADECODE`     varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `GRADECODENAME` varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `LEADER`        varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `MANAGER`       varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `PARENT_ID`     varchar(255) COLLATE utf8_bin NOT NULL,
    `TENANTID`      varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `VERSION`       int(11) NOT NULL,
    `ZIPCODE`       varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of rc8_org_department
-- ----------------------------

-- ----------------------------
-- Table structure for rc8_org_group
-- ----------------------------
CREATE TABLE `rc8_org_group`
(
    `ID`          varchar(255) COLLATE utf8_bin NOT NULL,
    `createTime`  datetime(0) DEFAULT NULL,
    `CUSTOMID`    varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `deleted`     bit(1)                        NOT NULL,
    `description` varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `disabled`    bit(1)                        NOT NULL,
    `DN`          varchar(2000) COLLATE utf8_bin DEFAULT NULL,
    `guidPath`    varchar(800) COLLATE utf8_bin  DEFAULT NULL,
    `name`        varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `orgType`     varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `properties`  varchar(500) COLLATE utf8_bin  DEFAULT NULL,
    `shortDN`     varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `systemName`  varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `tabIndex`    int(11) NOT NULL,
    `PARENT_ID`   varchar(255) COLLATE utf8_bin NOT NULL,
    PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of rc8_org_group
-- ----------------------------

-- ----------------------------
-- Table structure for rc8_org_optionclass
-- ----------------------------
CREATE TABLE `rc8_org_optionclass`
(
    `TYPE` varchar(255) NOT NULL,
    `NAME` varchar(255) NOT NULL,
    PRIMARY KEY (`TYPE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rc8_org_optionclass
-- ----------------------------
INSERT INTO `rc8_org_optionclass`
VALUES ('1', '测试');
INSERT INTO `rc8_org_optionclass`
VALUES ('duty', '职务');
INSERT INTO `rc8_org_optionclass`
VALUES ('dutyLevel', '职级');
INSERT INTO `rc8_org_optionclass`
VALUES ('dutyType', '职务类型');
INSERT INTO `rc8_org_optionclass`
VALUES ('officialType', '编制类型');
INSERT INTO `rc8_org_optionclass`
VALUES ('organizationType', '机构类型');
INSERT INTO `rc8_org_optionclass`
VALUES ('principalIDType', '证件类型');

-- ----------------------------
-- Table structure for rc8_org_optionvalue
-- ----------------------------
CREATE TABLE `rc8_org_optionvalue`
(
    `ID`       varchar(255) NOT NULL,
    `CODE`     varchar(255) DEFAULT NULL,
    `NAME`     varchar(255) NOT NULL,
    `TABINDEX` int(11) NOT NULL,
    `TYPE`     varchar(255) NOT NULL,
    PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rc8_org_optionvalue
-- ----------------------------
INSERT INTO `rc8_org_optionvalue`
VALUES ('003c4418166d4a6d8c2b88fdbbbcb8a3', '10', '副科级', '9', 'dutyLevel');
INSERT INTO `rc8_org_optionvalue`
VALUES ('0123acb9531649f093880944644cf55e', '259A', '旗长', '52', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('045573f8162f47259b51440c5e7f260a', '841A', '队长', '77', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('0740d0537d9f448cba1bdad4d00105ab', '418A', '校长', '68', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('0a480f603f6e4412b6355e4a743af2de', '91', '民办非企业单位', '19', 'organizationType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('0e4ddb08818140b6a1482801add08ea2', '39', '其他机关', '12', 'organizationType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('0f874d9b1c694f07bae64887091c9e21', '9', '正科级', '8', 'dutyLevel');
INSERT INTO `rc8_org_optionvalue`
VALUES ('0fafd3db43c941dcbe793b2f5df455cb', '417A', '院长', '66', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('0ffbb8247eb6409c86ab14814313e9a1', '256A', '盟长', '46', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('130143a76e234602b451c1f9396cf9c0', '262A', '村长', '58', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('1413f597b898490792c45e0c9c3b83de', '11', '公司', '0', 'organizationType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('180e212bb4444532beb3e599a0dbc764', '002K', '常务委员', '3', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('18677b1b91984f35abd23d2ec3486957', '260B', '副镇长', '55', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('1c3425cc2d4a4c56aecdee665783cd9d', '', '行政编', '1', 'officialType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('1c710ac2730345a5a5c26c8bc873891e', '011A', '调研员', '7', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('1c8c7ea4a8824d65b223561967cc3bf1', '224A', '办事员', '36', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('1e0510a53d384cacb78d8d9e80827269', '216B', '副局长', '28', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('232975e9a3094b3aad069fdf275143f2', '73', '社会团体分支、代表机构', '17', 'organizationType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('23e87f2eaf254ea193fc7011db2bb96a', '252B', '副市长', '40', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('2ab71abda71748a5a203730feaf4e08e', '17', '个人独资企业、合伙企业', '3', 'organizationType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('2b817661d02047b3bd3a9c1c0ad6d91a', '96', '城市居民委员会', '23', 'organizationType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('2fa22fbfeaae4dc88de57604330e6102', '', '其他', '4', 'officialType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('37601f7a1c2b461380aafde5b6939b53', '004B', '副主任', '5', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('3781e539c5d04b839ac9ec5c32a85249', '215A', '关长', '25', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('37feb0725e204df09c4e640896ec4d3e', '51', '事业单位法人', '13', 'organizationType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('387710124df443f79555c8361cd60637', '259B', '副旗长', '53', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('3d51a080d38b449ba1608b2ce505624d', '102A', '秘书长', '12', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('431208558b284cfbb925dce9ebfe3f91', '211A', '部长', '18', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('43aa4e722f1d409faddbc403163acb93', '5', '司、局、地厅级', '4', 'dutyLevel');
INSERT INTO `rc8_org_optionvalue`
VALUES ('447315c05a3e48faa99edf3f671e529b', '220B', '副科长', '32', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('460420ad395c4aaa80d561a1efde3f69', '219B', '副处长', '30', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('48d0420e2c47468d90774c8ce948a0a7', '59', '其它事业单位', '15', 'organizationType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('4c9cb1c30ea14e7487ab4e749870612d', '841B', '副队长', '78', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('4cf3b39c9335438f9cae2e352ce735f2', '35', '政协组织', '9', 'organizationType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('4eee0d920ec248e08f54c13852bfd56a', '261B', '副乡长', '57', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('5027a00b-c7ce-471b-a77a-129fae7c3a36', '2', '测试2', '0', '测试');
INSERT INTO `rc8_org_optionvalue`
VALUES ('50f3d567544e4d3d8ae86c4681fac871', '221T', '副主任科员', '34', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('5109118f1dc94d0884b8ce1cae45d33e', '95', '农村居民委员会', '22', 'organizationType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('5132875dae594e52b6261d755da3827d', '12', '办事员级', '11', 'dutyLevel');
INSERT INTO `rc8_org_optionvalue`
VALUES ('519b5357c4a54e618f7ffa0f249f22bf', '252J', '市长助理', '41', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('51f355002c914825a471fdaeb668950f', '002A', '委员', '2', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('5746c4c9a7b345a6bb664c3d1a5d92b3', '219A', '处长', '29', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('57c3276f42c540ef97e5976179e88c10', '261A', '乡长', '56', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('593cef9d823f468b9b55b45c57cb5cd3', '840B', '副大队长', '76', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('5c9fccc8f4894385a71f267464df10fb', '254B', '副州长', '43', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('5f54c3d0b30f4d9b87e0322cfa52aee4', '99', '其它', '25', 'organizationType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('61321472a8c34719a3f2eaf032b9cb3e', '71', '社会团体法人', '16', 'organizationType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('63e55a9d802348c1b06dc1d6c7aeec25', '13', '非公司制企业法人', '1', 'organizationType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('64b9c473bd434caa875a998a90b707bd', '2', '副总理、国务委员级', '1', 'dutyLevel');
INSERT INTO `rc8_org_optionvalue`
VALUES ('67ce6cc7c5b84b3386a3dc9b1e24a391', '201A', '主席', '16', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('69997e58aa7e4cbab27ff85ec4c29d31', '405A', '理事长', '62', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('6a85ed9e5041431189b51d30d855e181', '212B', '副审计长', '22', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('6ac870b95abd4902b1320d97b227e4db', '004A', '主任', '4', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('6ae7653523a64d4986845d0e4f1dfbe4', '32', '国家权力机关法人', '6', 'organizationType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('6b4fca23-7070-4f75-a6c8-3eecd5a3c9f5', '3', '测试3', '1', '测试');
INSERT INTO `rc8_org_optionvalue`
VALUES ('6c3f4f25f9244f05b46b95f4338e4676', '438B', '副站长', '71', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('6d2fdda03f8045258cc921dae7e07b3f', '220A', '科长', '31', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('6d41bfd7ff5d4e05a6fd7b78d7e4a9c9', '15', '企业分支机构', '2', 'organizationType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('6dc3c7de31824107966c00cb31a67595', '083Q', '总工程师', '10', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('73aab6034e3542518b3f61c87c97ad69', '255B', '副区长', '45', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('7488ad7d2da54998bb79ec35d2cc3c4b', '256B', '副盟长', '47', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('7691aa11a6a24e46a9676a46399f190a', '251B', '副省长', '38', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('76e1bc0e313d43aca18c43dd7411aebb', '258A', '县长', '50', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('774c9cb92903438bad6ea98652d3907c', '262B', '副村长', '59', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('77a91680512d42c49372ed875f88f70f', '93', '基金会', '20', 'organizationType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('78fcf40f4b78442ca04017f12a77b63a', '33', '国家行政机关法人', '7', 'organizationType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('7ae37684b80d4a07b343b84d5e652eae', '801A', '参谋长', '72', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('7c4373bb478d41e1b796abbea77c136a', '8', '副县、副处级', '7', 'dutyLevel');
INSERT INTO `rc8_org_optionvalue`
VALUES ('7d2f8f3d8cd841f288153adcc34e6a0e', '12', '户口簿', '2', 'principalIDType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('7e9366bed97a4799a8f6257b554b01b7', '36', '民主党派', '10', 'organizationType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('7ff2b306a3bb4388b2e3f12da9ac9b83', '254A', '州长', '42', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('7ffb6ca4a86b4dd8a8543361b1679fe4', '102B', '副秘书长', '13', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('804ea6d9d7b34aff9d2672321f963c83', '201B', '副主席', '17', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('81c58467fe2e404ebeffaaf9b66f803d', '105A', '检察长', '14', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('826faef74f1044279c098a4602fd29e0', '11', '科员级', '10', 'dutyLevel');
INSERT INTO `rc8_org_optionvalue`
VALUES ('83d553ec53fe4c79a16ffeb70b11b653', '011B', '副调研员', '8', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('83e8093a47a548f395ab4f849f153967', '14', '县长', '14', 'dutyLevel');
INSERT INTO `rc8_org_optionvalue`
VALUES ('88c9e1e45b4b41419a9eaa2f5f8379d6', '015A', '纪检员', '9', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('8bf9770e967743df933aa2333f7921ba', '257A', '专员', '48', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('8c6d60f1c44e4335bbffb4394980161c', '10', '身份证', '0', 'principalIDType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('8f1ec868988845aaa1a4dc3d40736957', '410B', '副会长', '61', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('8f2fbc0b66214234b1580c622daa7bd2', '221S', '主任科员', '33', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('900b00946dd1428eb234075adef18429', '214B', '副署长', '24', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('921731b8b61d44848f6ca0626183df8d', '211J', '部长助理', '20', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('929e1206260241f698e400fec19da823', '418B', '副校长', '69', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('94686a44bee94a999f2afeeaac8d8657', '34', '国家司法机关法人', '8', 'organizationType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('95fd5f5748314496ac6dbcebc0266760', '258B', '副县长', '51', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('963979258b264809b07ff4d1f955da83', '105B', '副检察长', '15', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('97246f4d1968413d9a6219fa0ef0ed23', '255A', '区长', '44', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('99a5b4acac3e474a821c60c0b125d32d', '251A', '省长', '37', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('9f861b21-424f-4b86-80d2-43040275500d', '4', '测试4', '2', '测试');
INSERT INTO `rc8_org_optionvalue`
VALUES ('a3f947e984f94e0e93bfa1a7c97ccaff', '416B', '副所长', '65', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('a8981513953048d689f2f3edf64df7c6', '19', '其它企业', '4', 'organizationType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('a9b2faa2bd3f445b931e14fc4c64ef76', '221A', '科员', '35', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('aad28d6b6c7742aabfd47bbf73a070dc', '', '事业编', '2', 'officialType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('ab373ebfff4d4e038895adb9775df382', '1', '国家主席、副主席、总理级', '0', 'dutyLevel');
INSERT INTO `rc8_org_optionvalue`
VALUES ('ab55a42263db42d1aefa80ecf3c46a66', '', '公务员', '0', 'officialType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('b01f4c1b66e54a93b6b71adf1bef1cfd', '417B', '副院长', '67', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('b45d55b2210b45049dc812f36c853d1e', '211B', '副部长', '19', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('b4e13bf34d1e4fa8b95e8cf0c5cfd92d', '31', '中国共产党', '5', 'organizationType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('b7d8ce3f099a415a8c54ce06cf113fbe', '79', '其它社会团体', '18', 'organizationType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('be5e96a558f04e0b9762b61bfd718d8f', '216A', '局长', '27', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('bf4687b15fb2444cac267d7dab8dedcb', '802A', '参谋', '73', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('bf7584e01b1041da8b3c22728e50e4b1', '3', '部、省级', '2', 'dutyLevel');
INSERT INTO `rc8_org_optionvalue`
VALUES ('c3ff25d92a534e459a73d6778284e4c5', '405B', '副理事长', '63', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('c4379b948f0f4f2c8e283c16c6393560', '214A', '署长', '23', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('c4c343c1d83744cc8d7792bf2e1d6d1c', '851A', '政委', '74', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('c9319f98f29e4f958f393de203b80df0', '257B', '副专员', '49', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('cbced0ee92c1465aa9dbf8d081ae5ea4', '010A', '巡视员', '6', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('cbd73fad33b44749a89434473f9899ff', '', '企业编', '3', 'officialType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('cf88c800bf92475c8f8026fe8473b839', '416A', '所长', '64', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('d49217ffd772487daf0c46152a899a90', '97', '自定义区', '24', 'organizationType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('d62e0187d468440f98d9bbb4a644a1a0', '260A', '镇长', '54', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('d96ca5bcbf9041cb94bae9cd9ed7c885', '13', '军人证', '3', 'principalIDType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('da512a994bf54a69bceab8c78d67de5c', '', '领导', '0', 'dutyType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('dcabeaef5cf04bf1ae5562d981dc7917', '7', '县、处级', '6', 'dutyLevel');
INSERT INTO `rc8_org_optionvalue`
VALUES ('e3ea98c6b3144e3d8a0cc7b1ad74e137', '53', '事业单位分支、派出机构', '14', 'organizationType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('e4606c0d240b421d98974397372ac8f1', '840A', '大队长', '75', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('e4b883c78bed4c4c9fce02a029d676f3', '', '公务员', '1', 'dutyType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('e6326e32a76a4429a937e7be05194de1', '215B', '副关长', '26', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('e77678ae92f84b89846f09c10136586e', '410A', '会长', '60', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('e9d79a30734c479dad90c72c66a7e984', '438A', '站长', '70', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('e9e2b1101832485fad684d94ab3b75d6', '11', '护照', '1', 'principalIDType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('eb4c21f3e0494ddb8a9de6f0629826c7', '083R', '副总工程师', '11', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('ed04f10eef74410b9b7b4fefb994b481', '252A', '市长', '39', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('ed89ebc8e2514e4cb65969850adb90e2', '842A', '负责人', '79', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('f200b10cfe3a4ad19d6c8c5eff0b4faf', '6', '副司、副局、副地、副厅级', '5', 'dutyLevel');
INSERT INTO `rc8_org_optionvalue`
VALUES ('f64242ec8fd644038f6a4af42abc775c', '001A', '书记', '0', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('f982a863b76f45238f547eff8a3e390e', '94', '宗教活动场所', '21', 'organizationType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('fc14643c487545a19c5ca04311b67bb2', '37', '人民解放军、武警部队', '11', 'organizationType');
INSERT INTO `rc8_org_optionvalue`
VALUES ('fde719d2d51143968952d9c2fea26e56', '001B', '副书记', '1', 'duty');
INSERT INTO `rc8_org_optionvalue`
VALUES ('fdf580762ffe45368d406f824a50d303', '212A', '审计长', '21', 'duty');

-- ----------------------------
-- Table structure for rc8_org_organization
-- ----------------------------
CREATE TABLE `rc8_org_organization`
(
    `ID`               varchar(255) COLLATE utf8_bin NOT NULL,
    `createTime`       datetime(0) DEFAULT NULL,
    `CUSTOMID`         varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `deleted`          bit(1)                        NOT NULL,
    `description`      varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `disabled`         bit(1)                        NOT NULL,
    `DN`               varchar(2000) COLLATE utf8_bin DEFAULT NULL,
    `guidPath`         varchar(800) COLLATE utf8_bin  DEFAULT NULL,
    `name`             varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `orgType`          varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `properties`       varchar(500) COLLATE utf8_bin  DEFAULT NULL,
    `shortDN`          varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `systemName`       varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `tabIndex`         int(11) NOT NULL,
    `ENNAME`           varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `ORGANIZATIONCODE` varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `ORGANIZATIONTYPE` varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `PRINCIPALIDNUM`   varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `PRINCIPALIDTYPE`  varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `PRINCIPALNAME`    varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `TENANTID`         varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `VERSION`          int(11) NOT NULL,
    PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of rc8_org_organization
-- ----------------------------

-- ----------------------------
-- Table structure for rc8_org_person
-- ----------------------------
CREATE TABLE `rc8_org_person`
(
    `ID`              varchar(255) COLLATE utf8_bin NOT NULL,
    `createTime`      datetime(0) DEFAULT NULL,
    `CUSTOMID`        varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `deleted`         bit(1)                        NOT NULL,
    `description`     varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `disabled`        bit(1)                        NOT NULL,
    `DN`              varchar(2000) COLLATE utf8_bin DEFAULT NULL,
    `guidPath`        varchar(800) COLLATE utf8_bin  DEFAULT NULL,
    `name`            varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `orgType`         varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `properties`      varchar(500) COLLATE utf8_bin  DEFAULT NULL,
    `shortDN`         varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `systemName`      varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `tabIndex`        int(11) NOT NULL,
    `CAID`            varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `IDNUM`           varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `IDTYPE`          varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `avator`          varchar(100) COLLATE utf8_bin  DEFAULT NULL,
    `BIRTHDAY`        date                           DEFAULT NULL,
    `CITY`            varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `COUNTRY`         varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `DUTY`            varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `dutyLevelName`   varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `dutylevel`       int(11) NOT NULL,
    `education`       varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `email`           varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `homeAddress`     varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `homePhone`       varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `innerRole`       varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `loginName`       varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `maritalStatus`   int(11) NOT NULL,
    `MOBILE`          varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `officeAddress`   varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `officeFax`       varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `officePhone`     varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `official`        int(11) NOT NULL,
    `officialType`    varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `orderedPath`     varchar(500) COLLATE utf8_bin  DEFAULT NULL,
    `PARENT_ID`       varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `PASSWORD`        varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `personType`      varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `PHOTO`           longblob,
    `PLAINTEXT`       varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `policitalStatus` varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `professional`    varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `province`        varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `roles`           longtext COLLATE utf8_bin,
    `sex`             int(11) NOT NULL,
    `SIGN`            longblob,
    `tenantID`        varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `updateTime`      datetime(0) DEFAULT NULL,
    `VERSION`         int(11) NOT NULL,
    `weixinId`        varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `WORKTIME`        date                           DEFAULT NULL,
    PRIMARY KEY (`ID`),
    UNIQUE KEY `UK_e3jxuwl9bi0xmtwi2miq82qrp` (`email`),
    UNIQUE KEY `UK_rbygy60rlroqri29xy5ftwo4i` (`loginName`),
    UNIQUE KEY `UK_3flcr97cgiacatla5sthp8f8y` (`MOBILE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of rc8_org_person
-- ----------------------------

-- ----------------------------
-- Table structure for rc8_org_persons_groups
-- ----------------------------
CREATE TABLE `rc8_org_persons_groups`
(
    `id`            int(11) NOT NULL AUTO_INCREMENT,
    `GROUPS_ORDER`  int(11) NOT NULL,
    `ORG_GROUP_ID`  varchar(255) COLLATE utf8_bin NOT NULL,
    `ORG_PERSON_ID` varchar(255) COLLATE utf8_bin NOT NULL,
    `PERSONS_ORDER` int(11) NOT NULL,
    PRIMARY KEY (`id`),
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of rc8_org_persons_groups
-- ----------------------------

-- ----------------------------
-- Table structure for rc8_org_position
-- ----------------------------
CREATE TABLE `rc8_org_position`
(
    `ID`            varchar(255) COLLATE utf8_bin NOT NULL,
    `createTime`    datetime(0) DEFAULT NULL,
    `CUSTOMID`      varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `deleted`       bit(1)                        NOT NULL,
    `description`   varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `disabled`      bit(1)                        NOT NULL,
    `DN`            varchar(2000) COLLATE utf8_bin DEFAULT NULL,
    `guidPath`      varchar(800) COLLATE utf8_bin  DEFAULT NULL,
    `name`          varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `orgType`       varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `properties`    varchar(500) COLLATE utf8_bin  DEFAULT NULL,
    `shortDN`       varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `systemName`    varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `tabIndex`      int(11) NOT NULL,
    `DUTY`          varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `DUTYLEVEL`     int(11) NOT NULL,
    `DUTYLEVELNAME` varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `DUTYTYPE`      varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `PARENT_ID`     varchar(255) COLLATE utf8_bin NOT NULL,
    PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of rc8_org_position
-- ----------------------------

-- ----------------------------
-- Table structure for rc8_org_positions_persons
-- ----------------------------
CREATE TABLE `rc8_org_positions_persons`
(
    `id`              int(11) NOT NULL AUTO_INCREMENT,
    `ORG_PERSON_ID`   varchar(255) COLLATE utf8_bin NOT NULL,
    `ORG_POSITION_ID` varchar(255) COLLATE utf8_bin NOT NULL,
    `PERSONS_ORDER`   int(11) NOT NULL,
    `POSITIONS_ORDER` int(11) NOT NULL,
    PRIMARY KEY (`id`),
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of rc8_org_positions_persons
-- ----------------------------

-- ----------------------------
-- Table structure for rc8_person_workcalendar
-- ----------------------------
CREATE TABLE `rc8_person_workcalendar`
(
    `id`            varchar(38) NOT NULL,
    `title`         varchar(500) DEFAULT NULL,
    `content`       longtext,
    `startTime`     varchar(255) DEFAULT NULL,
    `endTime`       varchar(255) DEFAULT NULL,
    `isremind`      int(11) DEFAULT NULL,
    `remindtime`    varchar(255) DEFAULT NULL,
    `otheruserguid` varchar(50)  DEFAULT NULL,
    `otherusername` varchar(50)  DEFAULT NULL,
    `creatorguid`   varchar(50)  DEFAULT NULL,
    `creator`       varchar(50)  DEFAULT NULL,
    `activityguid`  varchar(50)  DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='rc8_person_workcalendar';

-- ----------------------------
-- Records of rc8_person_workcalendar
-- ----------------------------

-- ----------------------------
-- Table structure for rc8_publishedevent
-- ----------------------------
CREATE TABLE `rc8_publishedevent`
(
    `ID`               varchar(255) COLLATE utf8_bin NOT NULL,
    `clientIP`         varchar(255) COLLATE utf8_bin NOT NULL,
    `eventDescription` varchar(255) COLLATE utf8_bin NOT NULL,
    `eventName`        varchar(255) COLLATE utf8_bin NOT NULL,
    `eventTarget`      varchar(255) COLLATE utf8_bin NOT NULL,
    `eventType`        varchar(255) COLLATE utf8_bin NOT NULL,
    `objId`            varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `operator`         varchar(255) COLLATE utf8_bin NOT NULL,
    `publishTime`      datetime(0) NOT NULL,
    `tenantID`         varchar(255) COLLATE utf8_bin DEFAULT NULL,
    PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of rc8_publishedevent
-- ----------------------------

-- ----------------------------
-- Table structure for rc8_publishedeventlistener
-- ----------------------------
CREATE TABLE `rc8_publishedeventlistener`
(
    `ID`           varchar(255) COLLATE utf8_bin NOT NULL,
    `clientIP`     varchar(255) COLLATE utf8_bin NOT NULL,
    `eventTarget`  varchar(255) COLLATE utf8_bin NOT NULL,
    `eventType`    varchar(255) COLLATE utf8_bin NOT NULL,
    `listenerTime` datetime(0) NOT NULL,
    `objId`        varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `tenantID`     varchar(255) COLLATE utf8_bin DEFAULT NULL,
    PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of rc8_publishedeventlistener
-- ----------------------------
