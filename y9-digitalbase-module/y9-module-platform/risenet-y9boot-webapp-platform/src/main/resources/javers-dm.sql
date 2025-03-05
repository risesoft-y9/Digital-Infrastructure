CREATE TABLE jv_global_id (
                              global_id_pk NUMBER NOT NULL,
                              local_id VARCHAR2(191),
                              fragment VARCHAR2(200),
                              type_name VARCHAR2(200),
                              owner_id_fk NUMBER,
                              CONSTRAINT jv_global_id_pk PRIMARY KEY(global_id_pk),
                              CONSTRAINT jv_global_id_owner_id_fk FOREIGN KEY(owner_id_fk) REFERENCES jv_global_id(global_id_pk)
);
CREATE INDEX jv_global_id_local_id_idx ON jv_global_id(local_id);
CREATE SEQUENCE jv_global_id_pk_seq;


CREATE TABLE jv_commit (
                           commit_pk NUMBER NOT NULL,
                           author VARCHAR2(200),
                           commit_date TIMESTAMP,
                           commit_date_instant VARCHAR2(30),
                           commit_id NUMBER(22,2),
                           CONSTRAINT jv_commit_pk PRIMARY KEY(commit_pk)
);
CREATE INDEX jv_commit_commit_id_idx ON jv_commit(commit_id);
CREATE SEQUENCE jv_commit_pk_seq;

CREATE TABLE jv_commit_property (
                                    property_name VARCHAR2(191) NOT NULL,
                                    property_value VARCHAR2(600),
                                    commit_fk NUMBER,
                                    CONSTRAINT jv_commit_property_pk PRIMARY KEY(commit_fk, property_name),
                                    CONSTRAINT jv_commit_property_commit_fk FOREIGN KEY(commit_fk) REFERENCES jv_commit(commit_pk)
);
CREATE INDEX jv_commit_property_commit_fk_i ON jv_commit_property(commit_fk);
CREATE INDEX jv_commit_property_property_na ON jv_commit_property(property_name,property_value);

CREATE TABLE jv_snapshot (
                             snapshot_pk NUMBER NOT NULL,
                             type VARCHAR2(200),
                             version NUMBER,
                             state CLOB,
                             changed_properties CLOB,
                             managed_type VARCHAR2(200),
                             global_id_fk NUMBER,
                             commit_fk NUMBER,
                             CONSTRAINT jv_snapshot_pk PRIMARY KEY(snapshot_pk),
                             CONSTRAINT jv_snapshot_global_id_fk FOREIGN KEY(global_id_fk) REFERENCES jv_global_id(global_id_pk),
                             CONSTRAINT jv_snapshot_commit_fk FOREIGN KEY(commit_fk) REFERENCES jv_commit(commit_pk)
);
CREATE INDEX jv_snapshot_global_id_fk_idx ON jv_snapshot(global_id_fk);
CREATE INDEX jv_snapshot_commit_fk_idx ON jv_snapshot(commit_fk);
CREATE SEQUENCE jv_snapshot_pk_seq;
