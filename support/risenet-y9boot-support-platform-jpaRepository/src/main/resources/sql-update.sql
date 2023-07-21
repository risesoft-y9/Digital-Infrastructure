ALTER TABLE `y9_public`.`y9_common_tenant`
  ADD FOREIGN KEY (`PARENT_ID`) REFERENCES `y9_public`.`y9_common_tenant` (`ID`);
  
ALTER TABLE `y9_public`.`y9_common_system`
  ADD FOREIGN KEY (`ISV_GUID`) REFERENCES `y9_public`.`y9_common_tenant` (`ID`);  
  
ALTER TABLE `y9_public`.`y9_common_app_store`
  ADD FOREIGN KEY (`SYSTEM_ID`) REFERENCES `y9_public`.`y9_common_system` (`ID`);
  
ALTER TABLE `y9_public`.`y9_common_menu`
  ADD FOREIGN KEY (`APP_ID`) REFERENCES `y9_public`.`y9_common_app_store` (`ID`);
  
ALTER TABLE `y9_public`.`y9_common_operation`
  ADD FOREIGN KEY (`APP_ID`) REFERENCES `y9_public`.`y9_common_app_store` (`ID`);
  
ALTER TABLE `y9_public`.`y9_org_permission_datarow`
  ADD FOREIGN KEY (`APP_ID`) REFERENCES `y9_public`.`y9_common_app_store` (`ID`);
  
ALTER TABLE `y9_public`.`y9_org_permission_datafield`
  ADD FOREIGN KEY (`APP_ID`) REFERENCES `y9_public`.`y9_common_app_store` (`ID`);
  
ALTER TABLE `y9_public`.`y9_common_tenant_system`
  ADD FOREIGN KEY (`SYSTEM_ID`) REFERENCES `y9_public`.`y9_common_system` (`ID`),
  ADD FOREIGN KEY (`TENANT_DATA_SOURCE`) REFERENCES `y9_public`.`y9_common_datasource` (`ID`),
  ADD FOREIGN KEY (`TENANT_ID`) REFERENCES `y9_public`.`y9_common_tenant` (`ID`);
  
ALTER TABLE `y9_public`.`y9_org_authorization`
  ADD FOREIGN KEY (`ROLE_ID`) REFERENCES `y9_public`.`y9_org_role` (`ID`),
  ADD FOREIGN KEY (`TENANT_ID`) REFERENCES `y9_public`.`y9_common_tenant` (`ID`);
  
ALTER TABLE `y9_public`.`y9_org_department`
  ADD FOREIGN KEY (`TENANT_ID`) REFERENCES `y9_public`.`y9_common_tenant` (`ID`);
  
ALTER TABLE `y9_public`.`y9_org_department_admin`
  ADD FOREIGN KEY (`TENANT_ID`) REFERENCES `y9_public`.`y9_common_tenant` (`ID`),
  ADD FOREIGN KEY (`PERSON_ID`) REFERENCES `y9_public`.`y9_org_person` (`ID`);
  
ALTER TABLE `y9_public`.`y9_org_group`
  ADD FOREIGN KEY (`TENANT_ID`) REFERENCES `y9_public`.`y9_common_tenant` (`ID`),
  ADD FOREIGN KEY (`PARENT_ID`) REFERENCES `y9_public`.`y9_org_department` (`ID`);
  
ALTER TABLE `y9_public`.`y9_org_optionvalue`
  ADD FOREIGN KEY (`TYPE`) REFERENCES `y9_public`.`y9_org_optionclass` (`TYPE`);
  
ALTER TABLE `y9_public`.`y9_org_organization`
  ADD FOREIGN KEY (`TENANT_ID`) REFERENCES `y9_public`.`y9_common_tenant` (`ID`);
  
ALTER TABLE `y9_public`.`y9_org_orgbases_roles`
  ADD FOREIGN KEY (`ROLE_ID`) REFERENCES `y9_public`.`y9_org_role` (`ID`);
  
ALTER TABLE `y9_public`.`y9_org_person`
  ADD FOREIGN KEY (`TENANT_ID`) REFERENCES `y9_public`.`y9_common_tenant` (`ID`),
  ADD FOREIGN KEY (`PARENT_ID`) REFERENCES `y9_public`.`y9_org_department` (`ID`),
  ADD FOREIGN KEY (`PARENT_ID`) REFERENCES `y9_public`.`y9_org_organization` (`ID`);
  
ALTER TABLE `y9_public`.`y9_org_orgbases_roles`
  ADD FOREIGN KEY (`ORG_ID`) REFERENCES `y9_public`.`y9_org_department` (`ID`),
  ADD FOREIGN KEY (`ORG_ID`) REFERENCES `y9_public`.`y9_org_organization` (`ID`),
  ADD FOREIGN KEY (`ORG_ID`) REFERENCES `y9_public`.`y9_org_position` (`ID`),
  ADD FOREIGN KEY (`ORG_ID`) REFERENCES `y9_public`.`y9_org_group` (`ID`);
  
ALTER TABLE `y9_public`.`y9_org_authorization`
  ADD FOREIGN KEY (`RESOURCE_ID`) REFERENCES `y9_public`.`y9_common_app_store` (`ID`),
  ADD FOREIGN KEY (`RESOURCE_ID`) REFERENCES `y9_public`.`y9_common_menu` (`ID`),
  ADD FOREIGN KEY (`RESOURCE_ID`) REFERENCES `y9_public`.`y9_common_operation` (`ID`);
  
ALTER TABLE `y9_public`.`y9_org_person_ext`
  ADD FOREIGN KEY (`PERSON_ID`) REFERENCES `y9_public`.`y9_org_person` (`ID`);
  
ALTER TABLE `y9_public`.`y9_org_persons_groups`
  ADD FOREIGN KEY (`GROUP_ID`) REFERENCES `y9_public`.`y9_org_group` (`ID`),
  ADD FOREIGN KEY (`PERSON_ID`) REFERENCES `y9_public`.`y9_org_person` (`ID`);
  
ALTER TABLE `y9_public`.`y9_org_persons_positions`
  ADD FOREIGN KEY (`PERSON_ID`) REFERENCES `y9_public`.`y9_org_person` (`ID`),
  ADD FOREIGN KEY (`POSITION_ID`) REFERENCES `y9_public`.`y9_org_position` (`ID`);
  
ALTER TABLE `y9_public`.`y9_org_persons_resources`
  ADD FOREIGN KEY (`APP_ID`) REFERENCES `y9_public`.`y9_common_app_store` (`ID`),
  ADD FOREIGN KEY (`DEPARTMENT_ID`) REFERENCES `y9_public`.`y9_org_department` (`ID`),
  ADD FOREIGN KEY (`PERSON_ID`) REFERENCES `y9_public`.`y9_org_person` (`ID`),
  ADD FOREIGN KEY (`RESOURCE_ID`) REFERENCES `y9_public`.`y9_common_app_store` (`ID`),
  ADD FOREIGN KEY (`RESOURCE_ID`) REFERENCES `y9_public`.`y9_common_menu` (`ID`),
  ADD FOREIGN KEY (`RESOURCE_ID`) REFERENCES `y9_public`.`y9_common_operation` (`ID`),
  ADD FOREIGN KEY (`SYSTEM_ID`) REFERENCES `y9_public`.`y9_common_system` (`ID`),
  ADD FOREIGN KEY (`TENANT_ID`) REFERENCES `y9_public`.`y9_common_system` (`ID`);
  
ALTER TABLE `y9_public`.`y9_org_persons_roles`
  ADD FOREIGN KEY (`APP_ID`) REFERENCES `y9_public`.`y9_common_app_store` (`ID`),
  ADD FOREIGN KEY (`DEPARTMENT_ID`) REFERENCES `y9_public`.`y9_org_department` (`ID`),
  ADD FOREIGN KEY (`PERSON_ID`) REFERENCES `y9_public`.`y9_org_person` (`ID`),
  ADD FOREIGN KEY (`ROLE_ID`) REFERENCES `y9_public`.`y9_org_role` (`ID`),
  ADD FOREIGN KEY (`SYSTEM_ID`) REFERENCES `y9_public`.`y9_common_system` (`ID`),
  ADD FOREIGN KEY (`TENANT_ID`) REFERENCES `y9_public`.`y9_common_tenant` (`ID`);

ALTER TABLE `y9_public`.`y9_org_position`
  ADD FOREIGN KEY (`PARENT_ID`) REFERENCES `y9_public`.`y9_org_department` (`ID`),
  ADD FOREIGN KEY (`TENANT_ID`) REFERENCES `y9_public`.`y9_common_tenant` (`ID`),
  ADD FOREIGN KEY (`DEPARTMENT_ID`) REFERENCES `y9_public`.`y9_org_department` (`ID`);

ALTER TABLE `y9_public`.`y9_org_role`
  ADD FOREIGN KEY (`APP_ID`) REFERENCES `y9_public`.`y9_common_app_store` (`ID`),
  ADD FOREIGN KEY (`PARENT_ID`) REFERENCES `y9_public`.`y9_org_role` (`ID`),
  ADD FOREIGN KEY (`TENANT_ID`) REFERENCES `y9_public`.`y9_common_tenant` (`ID`);
  
ALTER TABLE `y9_public`.`y9_orgbase_deleted`
  ADD FOREIGN KEY (`ORG_ID`) REFERENCES `y9_public`.`y9_org_organization` (`ID`),
  ADD FOREIGN KEY (`ORG_ID`) REFERENCES `y9_public`.`y9_org_department` (`ID`),
  ADD FOREIGN KEY (`ORG_ID`) REFERENCES `y9_public`.`y9_org_group` (`ID`),
  ADD FOREIGN KEY (`ORG_ID`) REFERENCES `y9_public`.`y9_org_person` (`ID`);
  
ALTER TABLE `y9_public`.`y9_orgbase_moved`
  ADD FOREIGN KEY (`ORG_ID`) REFERENCES `y9_public`.`y9_org_department` (`ID`),
  ADD FOREIGN KEY (`ORG_ID`) REFERENCES `y9_public`.`y9_org_organization` (`ID`),
  ADD FOREIGN KEY (`ORG_ID`) REFERENCES `y9_public`.`y9_org_person` (`ID`),
  ADD FOREIGN KEY (`ORG_ID`) REFERENCES `y9_public`.`y9_org_group` (`ID`),
  ADD FOREIGN KEY (`ORG_ID`) REFERENCES `y9_public`.`y9_org_position` (`ID`);
