-- Oracle script file
-- set to your schema and tablespace names

--------------------------------------------------------
--  DDL for Sequence JPA_SEQ
--------------------------------------------------------
BEGIN
   EXECUTE IMMEDIATE 'DROP SEQUENCE "SYSTEM"."JPA_SEQ"';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/
CREATE SEQUENCE  "SYSTEM"."JPA_SEQ"  MINVALUE 1 MAXVALUE 9223372036854775807 INCREMENT BY 10 START WITH 1 CACHE 20 NOORDER  NOCYCLE ;
/
--------------------------------------------------------
--  DDL for Table PLANT_ENTITY
--------------------------------------------------------
BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE "SYSTEM"."PLANT_ENTITY"';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/
CREATE TABLE "SYSTEM"."PLANT_ENTITY" 
(	
    "ENT_KEY" NUMBER(19,0), 
	"VERSION" NUMBER(10,0), 
	"NAME" NVARCHAR2(64), 
	"DESCRIPTION" NVARCHAR2(256), 
	"PARENT_KEY" NUMBER(19,0), 
	"HIER_LEVEL" NVARCHAR2(16), 
	"WS_KEY" NUMBER(19,0),
	"RETENTION" NUMBER(19,0)
) TABLESPACE "SYSTEM" ;
/
CREATE UNIQUE INDEX "SYSTEM"."ENTITY_PK" ON "SYSTEM"."PLANT_ENTITY" ("ENT_KEY") TABLESPACE "SYSTEM" ; 
/
CREATE UNIQUE INDEX "SYSTEM"."IDX_ENT_NAME" ON "SYSTEM"."PLANT_ENTITY" ("NAME") TABLESPACE "SYSTEM" ;
/
--------------------------------------------------------
--  DDL for Table REASON
--------------------------------------------------------
/
BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE "SYSTEM"."REASON"';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/
CREATE TABLE "SYSTEM"."REASON" 
   (	
    "REASON_KEY" NUMBER(19,0), 
	"VERSION" NUMBER(10,0), 
	"NAME" NVARCHAR2(64), 
	"DESCRIPTION" NVARCHAR2(256), 
	"PARENT_KEY" NUMBER(19,0), 
	"LOSS" NVARCHAR2(32)
   ) TABLESPACE "SYSTEM" ;
/   
CREATE UNIQUE INDEX "SYSTEM"."REASON_PK" ON "SYSTEM"."REASON" ("REASON_KEY") TABLESPACE "SYSTEM" ;   
/
CREATE UNIQUE INDEX "SYSTEM"."IDX_REASON_NAME" ON "SYSTEM"."REASON" ("NAME") TABLESPACE "SYSTEM" ;
/   
--------------------------------------------------------
--  DDL for Table MATERIAL
--------------------------------------------------------
BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE "SYSTEM"."MATERIAL"';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/
CREATE TABLE "SYSTEM"."MATERIAL" 
   (	
    "MAT_KEY" NUMBER(19,0), 
	"VERSION" NUMBER(10,0), 
	"NAME" NVARCHAR2(64), 
	"DESCRIPTION" NVARCHAR2(256), 
	"CATEGORY" NVARCHAR2(32)
   ) TABLESPACE "SYSTEM" ;
/
CREATE UNIQUE INDEX "SYSTEM"."MATERIAL_PK" ON "SYSTEM"."MATERIAL" ("MAT_KEY") TABLESPACE "SYSTEM" ;
/
CREATE UNIQUE INDEX "SYSTEM"."IDX_MATERIAL_NAME" ON "SYSTEM"."MATERIAL" ("NAME") TABLESPACE "SYSTEM" ;
/
--------------------------------------------------------
--  DDL for Table EQUIPMENT_MATERIAL
--------------------------------------------------------
BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE "SYSTEM"."EQUIPMENT_MATERIAL"';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/
CREATE TABLE "SYSTEM"."EQUIPMENT_MATERIAL" 
   (	"EM_KEY" NUMBER(19,0), 
	"EQ_KEY" NUMBER(19,0), 
	"MAT_KEY" NUMBER(19,0), 
	"OEE_TARGET" BINARY_DOUBLE, 
	"RUN_AMOUNT" BINARY_DOUBLE, 
	"RUN_UOM_KEY" NUMBER(19,0), 
	"REJECT_UOM_KEY" NUMBER(19,0), 
	"IS_DEFAULT" NUMBER(5,0)
   ) TABLESPACE "SYSTEM" ;
/   
--------------------------------------------------------
--  DDL for Table DATA_SOURCE
--------------------------------------------------------
BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE "SYSTEM"."DATA_SOURCE"';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/
CREATE TABLE "SYSTEM"."DATA_SOURCE" 
   (	
    "SOURCE_KEY" NUMBER(19,0), 
	"VERSION" NUMBER(10,0), 
	"NAME" NVARCHAR2(64), 
	"DESCRIPTION" NVARCHAR2(256), 
	"TYPE" NVARCHAR2(16), 
	"HOST" NVARCHAR2(128), 
	"USER_NAME" NVARCHAR2(64), 
	"PASSWORD" NVARCHAR2(64), 
	"PORT" NUMBER(10,0), 
	"END_PATH" NVARCHAR2(128),
	"SEC_POLICY" NVARCHAR2(128),
	"MSG_MODE" NVARCHAR2(128),
	"KEYSTORE" NVARCHAR2(128),
	"KEYSTORE_PWD" NVARCHAR2(64)
   ) TABLESPACE "SYSTEM" ;
/
--------------------------------------------------------
--  DDL for Table EVENT_RESOLVER
--------------------------------------------------------
BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE "SYSTEM"."EVENT_RESOLVER"';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/
CREATE TABLE "SYSTEM"."EVENT_RESOLVER" 
   (	
    "ER_KEY" NUMBER(19,0), 
	"ENT_KEY" NUMBER(19,0), 
	"SOURCE_ID" NVARCHAR2(256), 
	"SOURCE_KEY" NUMBER(19,0), 
	"SCRIPT" NVARCHAR2(1024), 
	"PERIOD" NUMBER(10,0), 
	"ER_TYPE" NVARCHAR2(16), 
	"DATA_TYPE" NVARCHAR2(32), 
	"COLLECT_KEY" NUMBER(19,0)
   ) TABLESPACE "SYSTEM" ;
/
--------------------------------------------------------
--  DDL for Table UOM
--------------------------------------------------------
BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE "SYSTEM"."UOM"';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/
CREATE TABLE "SYSTEM"."UOM" 
   (	
    "UOM_KEY" NUMBER(19,0), 
	"VERSION" NUMBER(10,0), 
	"NAME" NVARCHAR2(64), 
	"DESCRIPTION" NVARCHAR2(256), 
	"SYMBOL" NVARCHAR2(16), 
	"CATEGORY" NVARCHAR2(64), 
	"UNIT_TYPE" NVARCHAR2(32), 
	"UNIT" NVARCHAR2(32), 
	"CONV_FACTOR" BINARY_DOUBLE, 
	"CONV_UOM_KEY" NUMBER(19,0), 
	"CONV_OFFSET" BINARY_DOUBLE, 
	"BRIDGE_FACTOR" BINARY_DOUBLE, 
	"BRIDGE_UOM_KEY" NUMBER(19,0), 
	"BRIDGE_OFFSET" BINARY_DOUBLE, 
	"UOM1_KEY" NUMBER, 
	"EXP1" NUMBER(10,0), 
	"UOM2_KEY" NUMBER(19,0), 
	"EXP2" NUMBER(10,0)
   ) TABLESPACE "SYSTEM" ;
/   
--------------------------------------------------------
--  DDL for Table WORK_SCHEDULE
--------------------------------------------------------
BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE "SYSTEM"."WORK_SCHEDULE"';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/
CREATE TABLE "SYSTEM"."WORK_SCHEDULE" 
   (	
    "WS_KEY" NUMBER(19,0), 
	"VERSION" NUMBER(10,0), 
	"NAME" NVARCHAR2(64), 
	"DESCRIPTION" NVARCHAR2(512)
   ) TABLESPACE "SYSTEM" ;
/   
--------------------------------------------------------
--  DDL for Table SHIFT
--------------------------------------------------------
BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE "SYSTEM"."SHIFT"';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/
CREATE TABLE "SYSTEM"."SHIFT" 
   (	
    "SHIFT_KEY" NUMBER(19,0), 
	"NAME" NVARCHAR2(64), 
	"DESCRIPTION" NVARCHAR2(256), 
	"START_TIME" TIMESTAMP(3), 
	"DURATION" NUMBER(19,0), 
	"WS_KEY" NUMBER(19,0)
   ) TABLESPACE "SYSTEM" ;
/
--------------------------------------------------------
--  DDL for Table TEAM
--------------------------------------------------------
BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE "SYSTEM"."TEAM"';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/
CREATE TABLE "SYSTEM"."TEAM" 
   (	
    "TEAM_KEY" NUMBER(19,0), 
	"NAME" NVARCHAR2(64), 
	"DESCRIPTION" NVARCHAR2(256), 
	"WS_KEY" NUMBER(19,0), 
	"ROTATION_KEY" NUMBER(19,0), 
	"ROTATION_START" DATE
   ) TABLESPACE "SYSTEM" ;
/
--------------------------------------------------------
--  DDL for Table ROTATION
--------------------------------------------------------
BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE "SYSTEM"."ROTATION"';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/
CREATE TABLE "SYSTEM"."ROTATION" 
   (	
    "ROTATION_KEY" NUMBER(19,0), 
	"NAME" NVARCHAR2(64), 
	"DESCRIPTION" NVARCHAR2(256)
   ) TABLESPACE "SYSTEM" ;
/
--------------------------------------------------------
--  DDL for Table ROTATION_SEGMENT
--------------------------------------------------------
BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE "SYSTEM"."ROTATION_SEGMENT"';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/
CREATE TABLE "SYSTEM"."ROTATION_SEGMENT" 
   (	
    "SEGMENT_KEY" NUMBER(19,0), 
	"ROTATION_KEY" NUMBER(19,0), 
	"SEQUENCE" NUMBER(5,0), 
	"SHIFT_KEY" NUMBER(19,0), 
	"DAYS_ON" NUMBER(5,0), 
	"DAYS_OFF" NUMBER(5,0)
   ) TABLESPACE "SYSTEM" ;
/
--------------------------------------------------------
--  DDL for Table NON_WORKING_PERIOD
--------------------------------------------------------
BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE "SYSTEM"."NON_WORKING_PERIOD"';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/
CREATE TABLE "SYSTEM"."NON_WORKING_PERIOD" 
   (	
    "PERIOD_KEY" NUMBER(19,0), 
	"NAME" NVARCHAR2(64), 
	"DESCRIPTION" NVARCHAR2(256), 
	"START_DATE_TIME" TIMESTAMP(3), 
	"DURATION" NUMBER(19,0), 
	"WS_KEY" NUMBER(19,0), 
	"LOSS" NVARCHAR2(32)
   ) TABLESPACE "SYSTEM" ;
/
--------------------------------------------------------
--  DDL for Table COLLECTOR
--------------------------------------------------------
BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE "SYSTEM"."COLLECTOR"';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/
CREATE TABLE "SYSTEM"."COLLECTOR" 
   (	
    "COLLECT_KEY" NUMBER(19,0), 
	"VERSION" NUMBER(10,0), 
	"NAME" NVARCHAR2(64), 
	"DESCRIPTION" NVARCHAR2(256), 
	"STATE" NVARCHAR2(16), 
	"HOST" NVARCHAR2(64), 
	"BROKER_HOST" NVARCHAR2(64), 
	"BROKER_PORT" NUMBER(10,0),
	"BROKER_USER" NVARCHAR2(64),
	"BROKER_PWD" NVARCHAR2(64)
   ) TABLESPACE "SYSTEM" ;
/   
--------------------------------------------------------
--  DDL for Table OEE_EVENT
--------------------------------------------------------
BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE "SYSTEM"."OEE_EVENT"';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/
CREATE TABLE "SYSTEM"."OEE_EVENT" 
   (	
    "EVENT_KEY" NUMBER(19,0), 
	"ENT_KEY" NUMBER(19,0), 
	"START_TIME" TIMESTAMP(3) WITH TIME ZONE, 
	"END_TIME" TIMESTAMP(3) WITH TIME ZONE, 
	"SHIFT_KEY" NUMBER(19,0), 
	"TEAM_KEY" NUMBER(19,0), 
	"REASON_KEY" NUMBER(19,0), 
	"DURATION" NUMBER(19,0), 
	"EVENT_TYPE" NVARCHAR2(16), 
	"AMOUNT" BINARY_DOUBLE, 
	"UOM_KEY" NUMBER(19,0), 
	"MATL_KEY" NUMBER(19,0), 
	"JOB" NVARCHAR2(64),
	"IN_VALUE" NVARCHAR2(64)
   ) TABLESPACE "SYSTEM" ;
/




   

