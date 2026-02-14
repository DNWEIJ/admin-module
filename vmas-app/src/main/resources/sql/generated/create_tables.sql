
    create table ANALYSE (
        ANALYSE_ID bigint not null auto_increment,
        VERSION bigint not null,
        MID bigint not null,
        AnalyseDescription_ID bigint not null,
        COSTING_ID bigint,
        LOOKUPCOSTINGCATEGORY_ID bigint,
        QUANTITY double precision not null,
        ADDEDBY varchar(25),
        ADDEDON datetime,
        LASTEDITEDBY varchar(25),
        LASTEDITEDON datetime,
        primary key (ANALYSE_ID)
    ) ENGINE=InnoDB;

    create table ANALYSEDESCRIPTION (
        ANALYSEDESCRIPTION_ID bigint not null auto_increment,
        VERSION bigint not null,
        MID bigint not null,
        DESCRIPTION varchar(512) not null,
        ADDEDBY varchar(25),
        ADDEDON datetime,
        LASTEDITEDBY varchar(25),
        LASTEDITEDON datetime,
        primary key (ANALYSEDESCRIPTION_ID)
    ) ENGINE=InnoDB;

    create table ANALYSELEMENT (
        ANALYSELEMENT_ID bigint not null auto_increment,
        VERSION bigint not null,
        MID bigint not null,
        APPOINTMENT_ID bigint not null,
        PATIENT_ID bigint not null,
        COSTING_ID bigint,
        ANALYSE_ID bigint,
        VETINDICATOR varchar(1) not null,
        OWNERINDICATOR varchar(1) not null,
        COMMENT varchar(255),
        NOMENCLATURE varchar(255),
        QUANTITY double precision,
        primary key (ANALYSELEMENT_ID)
    ) ENGINE=InnoDB;

    create table APPOINTMENT (
        APPOINTMENT_ID bigint not null auto_increment,
        VERSION bigint not null,
        MID bigint not null,
        MLID bigint not null,
        VISITDATE datetime not null,
        CANCELLED varchar(1) not null,
        COMPLETED varchar(1) not null,
        PICKEDUP varchar(1) not null,
        OTC varchar(1) not null,
        primary key (APPOINTMENT_ID)
    ) ENGINE=InnoDB;

    create table COSTING (
        COSTING_ID bigint not null auto_increment,
        VERSION bigint not null,
        LOOKUPCOSTINGCATEGORY_ID bigint not null,
        MID bigint not null,
        NOMENCLATURE varchar(40) not null,
        COST double precision not null,
        PRICE double precision not null,
        DISTRIBUTOR varchar(100),
        DISTDESCRIPTION varchar(100),
        ITEMNUMBER varchar(25),
        QUANTITYPERPACKAGE double precision not null,
        UPLIFT double precision not null,
        BATCH_NR varchar(1) not null,
        SPILLAGE varchar(1) not null,
        PROCESSINGFEE double precision not null,
        TAXED varchar(255) not null,
        AUTOREMINDER smallint,
        RNOMENCLATURE varchar(50),
        `INTERVAL` smallint,
        RREMOVEPENDINGREMINDERSCONTAINING varchar(40),
        CODE varchar(10),
        DECEASEDPETPROMPT smallint not null,
        CERTIFICATEMANUFACTURER varchar(50),
        CERTIFICATETYPE varchar(20),
        CERTIFICATESERIALNUMBER varchar(20),
        CERTIFICATEVACCINEEXPIRES varchar(20),
        INSTRUCTIONS varchar(512),
        PRESCRIPTIONLABEL varchar(512),
        SUPPLIES2_ID bigint,
        SUPPLIES2_IDINDYQTYDEDUCTION double precision not null,
        BARCODE bigint,
        DELETED varchar(1) not null,
        ADDEDBY varchar(25),
        ADDEDON datetime,
        LASTEDITEDBY varchar(25),
        LASTEDITEDON datetime,
        primary key (COSTING_ID)
    ) ENGINE=InnoDB;

    create table COSTINGBATCHNUMBER (
        COSTINGBATCHNR_ID bigint not null auto_increment,
        VERSION bigint not null,
        Costing_ID bigint,
        BATCH_NUMBER varchar(20),
        MID bigint not null,
        MLID bigint not null,
        STARTDATE datetime,
        ENDDATE datetime,
        ADDEDBY varchar(25),
        ADDEDON datetime,
        LASTEDITEDBY varchar(25),
        LASTEDITEDON datetime,
        primary key (COSTINGBATCHNR_ID)
    ) ENGINE=InnoDB;

    create table COSTINGBATCHNUMBERLINEITEM (
        COSTINGBATCHNRUSAGE_ID bigint not null auto_increment,
        VERSION bigint not null,
        COSTINGBATCHNR_ID bigint,
        LINEITEM_ID bigint,
        ADDEDBY varchar(25),
        ADDEDON datetime,
        LASTEDITEDBY varchar(25),
        LASTEDITEDON datetime,
        primary key (COSTINGBATCHNRUSAGE_ID)
    ) ENGINE=InnoDB;

    create table COSTINGGROUP (
        COSTINGGROUP_ID bigint not null auto_increment,
        VERSION bigint not null,
        MID bigint,
        PARENTCOSTING_ID bigint not null,
        CHILDCOSTING_ID bigint not null,
        QUANTITY double precision default 1.0 not null,
        primary key (COSTINGGROUP_ID)
    ) ENGINE=InnoDB;

    create table COSTINGSPECIALPRICE (
        COSTINGSPECIALPRICE_ID bigint not null auto_increment,
        VERSION bigint not null,
        Costing_ID bigint not null,
        STARTDATE datetime not null,
        ENDDATE datetime not null,
        COST double precision not null,
        PROCESSINGFEE double precision not null,
        AMOUNT double precision not null,
        REDUCTION double precision not null,
        MID bigint not null,
        ADDEDBY varchar(25),
        ADDEDON datetime,
        LASTEDITEDBY varchar(25),
        LASTEDITEDON datetime,
        primary key (COSTINGSPECIALPRICE_ID)
    ) ENGINE=InnoDB;

    create table COSTINGSPILLAGE (
        COSTINGSPILLAGE_ID bigint not null auto_increment,
        VERSION bigint not null,
        COSTING_ID bigint not null,
        PACKAGEAMOUNT double precision not null,
        MID bigint not null,
        MLID bigint not null,
        STARTDATE datetime not null,
        ENDDATE datetime,
        primary key (COSTINGSPILLAGE_ID)
    ) ENGINE=InnoDB;

    create table COSTINGSPILLAGEUSAGE (
        COSTINGSPILLAGEUSAGE_ID bigint not null auto_increment,
        VERSION bigint not null,
        COSTINGSPILLAGE_ID bigint not null,
        LINITEM_ID bigint not null,
        primary key (COSTINGSPILLAGEUSAGE_ID)
    ) ENGINE=InnoDB;

    create table CUSTOMER (
        CUSTOMER_ID bigint not null auto_increment,
        VERSION bigint not null,
        MID bigint not null,
        LASTNAME varchar(20) not null,
        FirstName varchar(20) not null,
        SURNAME varchar(9),
        MIDDLEINITIAL varchar(1),
        TITLE varchar(20),
        EMAIL varchar(70),
        ADDRESS1 varchar(100),
        ADDRESS2 varchar(100),
        ADDRESS3 varchar(100),
        CITY varchar(50),
        STATE varchar(25),
        ZIPCODE varchar(10),
        HOMEPHONE varchar(25),
        WORKPHONE varchar(25),
        MOBILEPHONE varchar(25),
        EMERGENCYCONTACT varchar(50),
        EMERGENCYCONTACTPHONE varchar(25),
        VETERINARIAN varchar(50),
        VETERINARIANPHONE varchar(25),
        COMMENTS longtext,
        STATUS varchar(1) not null,
        NEWSLETTER varchar(1) not null,
        ADDEDBY varchar(25),
        ADDEDON datetime,
        LASTEDITEDBY varchar(25),
        LASTEDITEDON datetime,
        primary key (CUSTOMER_ID)
    ) ENGINE=InnoDB;

    create table DAILYTASK (
        DAILYTASK_ID bigint not null auto_increment,
        VERSION bigint not null,
        DAILYTASKGROUP_ID bigint not null,
        MID bigint not null,
        TASKDATETIME datetime not null,
        COMPLETEDUSERNAME varchar(25),
        COMPLETED datetime,
        primary key (DAILYTASK_ID)
    ) ENGINE=InnoDB;

    create table DAILYTASKSGROUP (
        DAILYTASKGROUP_ID bigint not null auto_increment,
        VERSION bigint not null,
        MID bigint not null,
        MLID bigint not null,
        CUSTOMER_ID bigint not null,
        PATIENT_ID bigint not null,
        TASK varchar(50) not null,
        TASKNOTES varchar(500),
        STAFFMEMBER varchar(70),
        primary key (DAILYTASKGROUP_ID)
    ) ENGINE=InnoDB;

    create table DIAGNOSE (
        DIAGNOSE_ID bigint not null auto_increment,
        VERSION bigint not null,
        MID bigint,
        APPOINTMENT_ID bigint not null,
        PATIENT_ID bigint not null,
        LOOKUPDIAGNOSIS_ID bigint not null,
        ADDEDBY varchar(25),
        ADDEDON datetime,
        primary key (DIAGNOSE_ID)
    ) ENGINE=InnoDB;

    create table DISTRIBUTOR (
        DISTRIBUTOR_ID bigint not null auto_increment,
        VERSION bigint not null,
        MID bigint not null,
        DISTRIBUTOR varchar(100),
        ADDRESS1 varchar(100),
        ADDRESS2 varchar(100),
        ADDRESS3 varchar(100),
        CITY varchar(50),
        STATE varchar(25),
        ZIPCODE varchar(10),
        PHONE1 varchar(25),
        PHONE2 varchar(25),
        FAX varchar(25),
        EMAIL varchar(70),
        URL varchar(200),
        COMMENTS longtext,
        primary key (DISTRIBUTOR_ID)
    ) ENGINE=InnoDB;

    create table EMAILTEMPLATE (
        TEMPLATE_ID bigint not null auto_increment,
        VERSION bigint not null,
        MID bigint not null,
        TEMPLATE longtext not null,
        TITLE varchar(100) not null,
        DESCRIPTION varchar(100),
        ADDEDBY varchar(25),
        ADDEDON datetime,
        LASTEDITEDBY varchar(25),
        LASTEDITEDON datetime,
        primary key (TEMPLATE_ID)
    ) ENGINE=InnoDB;

    create table ESTIMATE (
        ESTIMATE_ID bigint not null auto_increment,
        VERSION bigint not null,
        MLID bigint,
        MID bigint not null,
        ESTIMATEDATE datetime not null,
        TRANSTOVISIT datetime,
        primary key (ESTIMATE_ID)
    ) ENGINE=InnoDB;

    create table ESTIMATELINEITEM (
        ESTIMATELINEITEM_ID bigint not null auto_increment,
        VERSION bigint not null,
        MID bigint not null,
        ESTIMATE_ID bigint,
        PATIENT_ID bigint not null,
        LOOKUPCOSTINGCATEGORY_ID bigint not null,
        NOMENCLATURE varchar(40) not null,
        QUANTITY double precision not null,
        TAX varchar(255) not null,
        TAXGOODPERCENTAGE double precision,
        TAXSERVICEPERCENTAGE double precision,
        COST double precision,
        PROCESSINGFEE double precision not null,
        TOTAL double precision,
        COSTTAXPORTION double precision,
        PROCESSINGFEESERVICETAXPORTION double precision not null,
        ADDEDBY varchar(25),
        ADDEDON datetime,
        LASTEDITEDBY varchar(25),
        LASTEDITEDON datetime,
        primary key (ESTIMATELINEITEM_ID)
    ) ENGINE=InnoDB;

    create table ESTIMATESPECIFIC (
        ESTIMATESPECIFIC_ID bigint not null auto_increment,
        VERSION bigint not null,
        MID bigint not null,
        PATIENT_ID bigint,
        ESTIMATE_ID bigint,
        VETERINARIAN varchar(70),
        PURPOSE varchar(100),
        COMMENTS longtext,
        ADDEDBY varchar(25),
        ADDEDON datetime,
        LASTEDITEDBY varchar(25),
        LASTEDITEDON datetime,
        primary key (ESTIMATESPECIFIC_ID)
    ) ENGINE=InnoDB;

    create table EXPENSE (
        EXPENSE_ID bigint not null auto_increment,
        VERSION bigint not null,
        MID bigint not null,
        DESCRIPTION varchar(100) not null,
        MLID bigint not null,
        PURCHASEDATE datetime not null,
        PURCHASEPRICE double precision,
        PURCHASEDFROM varchar(50),
        PURCHASEDWITH varchar(50),
        STAFFMEMBER varchar(70),
        COMMENTS varchar(255),
        primary key (EXPENSE_ID)
    ) ENGINE=InnoDB;

    create table LINEITEM (
        LINEITEM_ID bigint not null auto_increment,
        VERSION bigint not null,
        MID bigint not null,
        CATEGORY_ID bigint,
        APPOINTMENT_ID bigint not null,
        PATIENT_ID bigint not null,
        NOMENCLATURE varchar(40) not null,
        QUANTITY double precision not null,
        TAX varchar(255) not null,
        TAXGOODPERCENTAGE double precision,
        TAXSERVICEPERCENTAGE double precision,
        COST double precision,
        PROCESSINGFEE double precision,
        TOTAL double precision,
        COSTTAXPORTION double precision,
        PROCESSINGFEESERVICETAXPORTION double precision,
        ADDEDBY varchar(25),
        ADDEDON datetime,
        LASTEDITEDBY varchar(25),
        LASTEDITEDON datetime,
        primary key (LINEITEM_ID)
    ) ENGINE=InnoDB;

    create table LINEITEMSMISC (
        LINEITEMSMISC_ID bigint not null auto_increment,
        VERSION bigint not null,
        MID bigint not null,
        APPOINTMENT_ID bigint not null,
        LOOKUPCOSTINGCATEGORY_ID bigint not null,
        NOMENCLATURE varchar(40) not null,
        QUANTITY double precision not null,
        TAX varchar(255) not null,
        TAXGOODPERCENTAGE double precision,
        TAXSERVICEPERCENTAGE double precision,
        COST double precision,
        PROCESSINGFEE double precision,
        TOTAL double precision,
        COSTTAXPORTION double precision,
        PROCESSINGFEESERVICETAXPORTION double precision,
        ADDEDBY varchar(25),
        ADDEDON datetime,
        LASTEDITEDBY varchar(25),
        LASTEDITEDON datetime,
        primary key (LINEITEMSMISC_ID)
    ) ENGINE=InnoDB;

    create table LOCATION (
        LOCATION_ID bigint not null auto_increment,
        VERSION bigint not null,
        MID bigint not null,
        DIAGNOSE_ID bigint not null,
        LOOKUPLOCATION_ID bigint not null,
        AddedBy varchar(25),
        AddedOn datetime,
        primary key (LOCATION_ID)
    ) ENGINE=InnoDB;

    create table LOOKUPBREED (
        LOOKUPBREED_ID bigint not null auto_increment,
        VERSION bigint not null,
        MID bigint,
        LOOKUPSPECIES_ID bigint not null,
        SPECIES varchar(25),
        BREED varchar(50),
        primary key (LOOKUPBREED_ID)
    ) ENGINE=InnoDB;

    create table LOOKUPCOSTINGCATEGORY (
        LOOKUPCOSTINGCATEGORY_ID bigint not null auto_increment,
        VERSION bigint not null,
        MID bigint not null,
        CATEGORY varchar(25) not null,
        primary key (LOOKUPCOSTINGCATEGORY_ID)
    ) ENGINE=InnoDB;

    create table LOOKUPDIAGNOSE (
        LOOKUPDIAGNOSIS_ID bigint not null auto_increment,
        VERSION bigint not null,
        MID bigint,
        NOMENCLATURE varchar(100),
        VENOM_ID bigint,
        primary key (LOOKUPDIAGNOSIS_ID)
    ) ENGINE=InnoDB;

    create table LOOKUPLOCATION (
        LOOKUPLOCATION_ID bigint not null auto_increment,
        VERSION bigint not null,
        NOMENCLATURE varchar(100) not null,
        primary key (LOOKUPLOCATION_ID)
    ) ENGINE=InnoDB;

    create table LOOKUPPREDEFINEDNOTEPADPURPOSE (
        LOOKUPPREDEFINEDNOTEPADPURPOSE_ID bigint not null auto_increment,
        VERSION bigint not null,
        MID bigint,
        PREDEFINEDPURPOSE varchar(100),
        primary key (LOOKUPPREDEFINEDNOTEPADPURPOSE_ID)
    ) ENGINE=InnoDB;

    create table LOOKUPPREDEFINEDPURPOSE (
        LOOKUPPREDEFINEDPURPOSE_ID bigint not null auto_increment,
        VERSION bigint not null,
        MID bigint,
        LOOKUPPREDEFINEDPURPOSE varchar(100),
        TYPICALTIME bigint,
        primary key (LOOKUPPREDEFINEDPURPOSE_ID)
    ) ENGINE=InnoDB;

    create table LOOKUPPREDEFINEDROOM (
        LOOKUPPREDEFINEDROOM_ID bigint not null auto_increment,
        VERSION bigint not null,
        MID bigint,
        MLID bigint,
        ROOM varchar(15),
        primary key (LOOKUPPREDEFINEDROOM_ID)
    ) ENGINE=InnoDB;

    create table LOOKUPSPECIES (
        LOOKUPSPECIES_ID bigint not null auto_increment,
        VERSION bigint not null,
        MID bigint,
        SPECIES varchar(25),
        primary key (LOOKUPSPECIES_ID)
    ) ENGINE=InnoDB;

    create table NOTEPAD (
        NOTEPAD_ID bigint not null auto_increment,
        VERSION bigint not null,
        PATIENT_ID bigint not null,
        MID bigint not null,
        NOTEDATE datetime not null,
        PURPOSE varchar(100) not null,
        STAFFMEMBER varchar(70) not null,
        NOTES varchar(7000),
        ADDEDBY varchar(25),
        ADDEDON datetime,
        LASTEDITEDBY varchar(25),
        LASTEDITEDON datetime,
        primary key (NOTEPAD_ID)
    ) ENGINE=InnoDB;

    create table PATIENT (
        PATIENT_ID bigint not null auto_increment,
        VERSION bigint not null,
        MID bigint not null,
        CUSTOMER_ID bigint,
        PETNAME varchar(40) not null,
        SPECIES varchar(25) not null,
        BREED varchar(50),
        BREEDOTHER varchar(50),
        SEX varchar(25) not null,
        BIRTHDAY datetime,
        DECEASED varchar(1) not null,
        ALLERGIES varchar(1) not null,
        GPWARNING varchar(1) not null,
        INSURED varchar(1) not null,
        DECEASEDDATE datetime,
        ALLERGIESDESCRIPTION varchar(200),
        DANGEROUSDESCRIPTION varchar(200),
        CHIPDATE datetime,
        INSUREDBY varchar(50),
        RABIESID varchar(20),
        CHIPTATTOOID varchar(20),
        BRIEFDESCRIPTION varchar(50),
        COMMENTS longtext,
        ADDEDBY varchar(25),
        ADDEDON datetime,
        LASTEDITEDBY varchar(25),
        LASTEDITEDON datetime,
        IDEALWEIGHT varchar(10),
        primary key (PATIENT_ID)
    ) ENGINE=InnoDB;

    create table PATIENTPICTURE (
        PATIENTPICTURE_ID bigint not null auto_increment,
        VERSION bigint not null,
        MID bigint not null,
        PATIENT_ID bigint,
        OWNER_ID integer not null,
        PICTURE longblob not null,
        primary key (PATIENTPICTURE_ID)
    ) ENGINE=InnoDB;

    create table PAYMENT (
        PAYMENT_ID bigint not null auto_increment,
        VERSION bigint not null,
        CUSTOMER_ID bigint not null,
        MID bigint not null,
        MLID bigint not null,
        PAYMENTDATE datetime not null,
        AMOUNT double precision not null,
        METHOD varchar(1) not null,
        REFERENCENUMBER varchar(50),
        COMMENTS longtext,
        ADDEDBY varchar(25),
        ADDEDON datetime,
        LASTEDITEDBY varchar(25),
        LASTEDITEDON datetime,
        primary key (PAYMENT_ID)
    ) ENGINE=InnoDB;

    create table PAYMENTMISC (
        PAYMENTMISC_ID bigint not null auto_increment,
        VERSION bigint not null,
        MID bigint not null,
        APPOINTMENT_ID bigint not null,
        METHOD varchar(1) not null,
        REFERENCENUMBER varchar(50),
        COMMENTS longtext,
        ADDEDBY varchar(25),
        ADDEDON datetime,
        LASTEDITEDBY varchar(25),
        LASTEDITEDON datetime,
        primary key (PAYMENTMISC_ID)
    ) ENGINE=InnoDB;

    create table PAYMENTVISIT (
        PAYMENTVISIT_ID bigint not null auto_increment,
        VERSION bigint not null,
        MID bigint not null,
        PAYMENT_ID bigint not null,
        VISIT_ID bigint not null,
        ADDEDBY varchar(25) not null,
        ADDEDON datetime not null,
        LASTEDITEDBY varchar(25) not null,
        LASTEDITEDON datetime not null,
        primary key (PAYMENTVISIT_ID)
    ) ENGINE=InnoDB;

    create table PHARMACEUTICALLABEL (
        PHARMACEUTICALLABEL_ID bigint not null auto_increment,
        VERSION bigint not null,
        CREATEDATE datetime not null,
        MID bigint not null,
        MLID bigint not null,
        APPOINTMENT_ID bigint not null,
        OWNER varchar(55) not null,
        PET varchar(40) not null,
        DRUGDOSAGE varchar(50) not null,
        STAFFMEMBER varchar(70),
        EXPIRATIONDATE varchar(25),
        DIRECTIONS varchar(512),
        LABELSIZE integer not null,
        primary key (PHARMACEUTICALLABEL_ID)
    ) ENGINE=InnoDB;

    create table PHARMACEUTICALLABELDEFAULT (
        PHARMACEUTICALLABELDEFAULT_ID bigint not null auto_increment,
        VERSION bigint not null,
        MID bigint not null,
        DRUGDOSAGE varchar(50) not null,
        DIRECTIONS varchar(512) not null,
        ADDEDBY varchar(25),
        ADDEDON datetime,
        LASTEDITEDBY varchar(25),
        LASTEDITEDON datetime,
        primary key (PHARMACEUTICALLABELDEFAULT_ID)
    ) ENGINE=InnoDB;

    create table REFUND (
        REFUND_ID bigint not null auto_increment,
        VERSION bigint not null,
        MID bigint not null,
        MLID bigint not null,
        OWNER_ID bigint not null,
        REFUNDDATE datetime not null,
        AMOUNT double precision not null,
        COMMENTS varchar(500),
        primary key (REFUND_ID)
    ) ENGINE=InnoDB;

    create table REMINDER (
        REMINDER_ID bigint not null auto_increment,
        VERSION bigint not null,
        PATIENT_ID bigint,
        MID bigint not null,
        DUEDATE datetime not null,
        REMINDER varchar(100) not null,
        ORIGINATIONAPPOINTMENT_ID bigint not null,
        ADDEDBY varchar(25),
        ADDEDON datetime,
        LASTEDITEDBY varchar(25),
        LASTEDITEDON datetime,
        primary key (REMINDER_ID)
    ) ENGINE=InnoDB;

    create table SUPPLIERPRODUCTLIST (
        SUPPLIERPRODUCTLIST_ID bigint not null auto_increment,
        VERSION bigint not null,
        YEARMONTH varchar(6),
        SUPPLIER_NAME varchar(20),
        PRODUCTION_DESCRIPTION varchar(40),
        PRODUCT_PRODUCER varchar(40),
        QUANTITY_PACKAGE integer,
        PACKET varchar(10),
        COST double precision,
        TAX varchar(1),
        ARTICLE_CODE varchar(15),
        REGISTRATION_NUMBER1 varchar(18),
        REGISTRATION_NUMBER2 varchar(18),
        BRANCHE_CODE varchar(15),
        WEIGHT integer,
        SELL_DATE varchar(4),
        EXPIRATION_DATE datetime,
        primary key (SUPPLIERPRODUCTLIST_ID)
    ) ENGINE=InnoDB;

    create table SUPPLIERPRODUCTLISTDIFF (
        SUPPLIERPRODUCTLISTDIFF_ID bigint not null auto_increment,
        VERSION bigint not null,
        SUPPLIER_NAME varchar(20),
        ARTICLE_CODE varchar(20),
        SUPPLIERPRODUCTLIST_ID bigint not null,
        CHANGE_TYPE varchar(1) not null,
        primary key (SUPPLIERPRODUCTLISTDIFF_ID)
    ) ENGINE=InnoDB;

    create table SUPPLIES2 (
        SUPPLIES2_ID bigint not null auto_increment,
        VERSION bigint not null,
        MID bigint not null,
        NOMENCLATURE varchar(40) not null,
        QUANTITYPERPACKAGE double precision not null,
        MINQUANTITY double precision not null,
        BUYQUANTITY double precision not null,
        PRICE double precision not null,
        DISTRIBUTOR varchar(100),
        ITEMNUMBER varchar(25),
        DISTDESCRIPTION varchar(100),
        USERNAME varchar(25),
        LASTALTERED datetime not null,
        BARCODE bigint,
        primary key (SUPPLIES2_ID)
    ) ENGINE=InnoDB;

    create table SUPPLIES2LOCAL (
        SUPPLIES2LOCAL_ID bigint not null auto_increment,
        VERSION bigint not null,
        SUPPLIES2_ID bigint not null,
        MID bigint not null,
        MLID bigint not null,
        QUANTITY double precision not null,
        INDIVIDUALQUANTITY double precision not null,
        MINQUANTITY double precision,
        BUYQUANTITY double precision,
        USERNAME varchar(25),
        LASTALTERED datetime,
        primary key (SUPPLIES2LOCAL_ID),
        unique (SUPPLIES2_ID, MLID)
    ) ENGINE=InnoDB;

    create table SUPPLIES2LOCALSUPDATED (
        SUPPLIES2LOCALSUPDATED_ID bigint not null auto_increment,
        VERSION bigint not null,
        SUPPLIES2_ID bigint not null,
        MID bigint not null,
        MLID bigint not null,
        SIUDate datetime not null,
        Quantity double precision not null,
        Acquisition smallint not null,
        primary key (SUPPLIES2LOCALSUPDATED_ID)
    ) ENGINE=InnoDB;

    create table THAU_DOMAIN_PROPERTY (
        DOMAIN_ID bigint not null auto_increment,
        VERSION bigint not null,
        MEMBER_ID bigint not null,
        DOMAIN varchar(40) not null,
        CODE varchar(3) not null,
        SHORT_DESCRIPTION varchar(250) not null,
        LONG_DESCRIPTION varchar(1024),
        SEQUENCE_NUMBER bigint,
        primary key (DOMAIN_ID)
    ) ENGINE=InnoDB;

    create table THAU_FUNCTION (
        FUNCTION_ID bigint not null auto_increment,
        VERSION bigint not null,
        NAME varchar(60) not null unique,
        START_DATE datetime not null,
        MODIFICATION_DATE datetime,
        SEQUENCE_NUMBER bigint not null,
        TECHNICAL_NAME varchar(60) not null,
        primary key (FUNCTION_ID),
        unique (NAME)
    ) ENGINE=InnoDB;

    create table THAU_FUNCTION_ROLE (
        FUNCTIONROLE_ID bigint not null auto_increment,
        VERSION bigint not null,
        FUNCTION_ID bigint not null,
        ROLE_ID bigint not null,
        primary key (FUNCTIONROLE_ID),
        unique (FUNCTION_ID, ROLE_ID)
    ) ENGINE=InnoDB;

    create table THAU_IPSECURITY (
        IPSECURITY_ID bigint not null auto_increment,
        VERSION bigint not null,
        MEMBER_ID bigint not null,
        USER_ID bigint not null,
        IP_NUMBER varchar(15) not null,
        primary key (IPSECURITY_ID)
    ) ENGINE=InnoDB;

    create table THAU_MEMBER (
        MEMBER_ID bigint not null auto_increment,
        VERSION bigint not null,
        NAME varchar(25) not null,
        PASSWORD varchar(25) not null,
        SIMULTANEOUSUSERS tinyint not null,
        TRANSLATOR varchar(10) not null,
        SHORT_CODE varchar(15) not null,
        ACTIVE varchar(1) not null,
        START datetime,
        STOP datetime,
        COMMENTS varchar(255),
        INVOICE_COUNTER bigint,
        PAYMENT_PERIOD smallint,
        REMINDER_PERIOD1 smallint,
        REMINDER_PERIOD2 smallint,
        USE_INVOICE_REMINDER varchar(1) not null,
        VAT_VIA_PAYMENT varchar(1) not null,
        PROCESSED_DIFF varchar(1) not null,
        USE_VENOM varchar(1) default N not null,
        primary key (MEMBER_ID)
    ) ENGINE=InnoDB;

    create table THAU_MEMBERLOCAL (
        MEMBERLOCAL_ID bigint not null auto_increment,
        VERSION bigint not null,
        MEMBER_ID bigint not null,
        CLINIC varchar(100) not null,
        FULLNAMEPOC varchar(70),
        EMAILPOC varchar(70),
        ADDRESS1 varchar(100),
        ADDRESS2 varchar(100),
        ADDRESS3 varchar(100),
        CITY varchar(50),
        STATE varchar(25),
        ZIPCODE varchar(10),
        PHONE1 varchar(25),
        PHONE2 varchar(25),
        FAX varchar(25),
        TAXBOARDERS bit,
        FIRST_PAGE_MESSAGE varchar(2000) not null,
        CONSULT_TEXT_TEMPLATE varchar(2000) not null,
        PREF_ESTIMATED_TIME integer not null,
        PREF_INSURANCE_COMPANY varchar(50),
        PREF_PAYMENT_METHOD varchar(1) not null,
        PREF_ROOM1 varchar(15) not null,
        PREF_ROOM2 varchar(15) not null,
        PREF_ROOM3 varchar(15) not null,
        PREF_ROOM4 varchar(15),
        PREF_ROOM_AGENDA varchar(1) not null,
        PREF_RXLABEL varchar(1) not null,
        MANDATORY_REASON varchar(1) default Y not null,
        ADDED_BY varchar(25),
        ADDED_ON datetime,
        LAST_EDITED_BY varchar(25),
        LAST_EDITED_ON datetime,
        primary key (MEMBERLOCAL_ID)
    ) ENGINE=InnoDB;

    create table THAU_MEMBERLOCAL_TAX (
        MEMBERLOCAL_TAX_ID bigint not null auto_increment,
        VERSION bigint not null,
        MEMBERLOCAL_ID bigint not null,
        START_DATE datetime,
        END_DATE datetime,
        TAX_LOW double precision,
        TAX_HIGH double precision,
        primary key (MEMBERLOCAL_TAX_ID)
    ) ENGINE=InnoDB;

    create table THAU_MENU_COMPONENTS (
        MENUCOMPONENT_ID bigint not null auto_increment,
        VERSION bigint not null,
        FUNTION_ID bigint,
        PARENT_ID bigint,
        IMAGE varchar(256),
        ALT_IMAGE varchar(256),
        URL varchar(256),
        LABEL varchar(100) unique,
        LABEL_NL varchar(100) unique,
        TYPES varchar(40),
        SEQ bigint,
        primary key (MENUCOMPONENT_ID)
    ) ENGINE=InnoDB;

    create table THAU_REPORT (
        REPORT_ID bigint not null auto_increment,
        VERSION bigint not null,
        MEMBER_ID bigint,
        PARENT_ID bigint,
        NAME varchar(50) not null,
        REPORT_XML longtext not null,
        REPORT_JASPER MEDIUMBLOB not null,
        ADDED_BY varchar(25),
        ADDED_ON datetime,
        LAST_EDITED_BY varchar(25),
        LAST_EDITED_ON datetime,
        primary key (REPORT_ID)
    ) ENGINE=InnoDB;

    create table THAU_ROLE (
        ROLE_ID bigint not null auto_increment,
        VERSION bigint not null,
        NAME varchar(32) not null unique,
        ADDED_BY varchar(25),
        ADDED_ON datetime,
        LAST_EDITED_BY varchar(25),
        LAST_EDITED_ON datetime,
        primary key (ROLE_ID),
        unique (NAME)
    ) ENGINE=InnoDB;

    create table THAU_SYSTEM_PROPERTY (
        SYSTEMPROPERTY_ID bigint not null auto_increment,
        VERSION bigint not null,
        NAME varchar(80),
        VALUE varchar(200),
        IND_ENCRYPT varchar(1) not null,
        COMMENT varchar(1000),
        primary key (SYSTEMPROPERTY_ID)
    ) ENGINE=InnoDB;

    create table THAU_USER (
        USER_ID bigint not null auto_increment,
        VERSION bigint not null,
        MEMBER_ID bigint,
        ACCOUNT varchar(50) not null,
        PASSWORD varchar(50) not null,
        PERSONNEL_TYPE varchar(1) not null,
        LANGUAGE varchar(2) not null,
        LOGIN_ENABLED varchar(1) not null,
        NAME varchar(100) not null,
        EMAIL varchar(100),
        LAST_VISIT_DATE datetime,
        NUMBER_VISIT_DAYS bigint,
        PREF_AGENDA_VET1 varchar(100),
        PREF_AGENDA_VET2 varchar(100),
        PREF_AGENDA_VET3 varchar(100),
        PREF_SEARCH_CUST_START varchar(1) default Y not null,
        PREF_SEARCH_CUST_STREET varchar(1) default Y not null,
        ADDED_BY varchar(25),
        ADDED_ON datetime,
        LAST_EDITED_BY varchar(25),
        LAST_EDITED_ON datetime,
        PREF_VISIT_APPOINTMENT_LIST varchar(1) default Y not null,
        PREF_VISIT_TOTAL_VISIT varchar(1) default Y not null,
        PREF_VISIT_APPOINTMENT_INFO varchar(1) default Y not null,
        PREF_VISIT_VISIT_INFO varchar(1) default Y not null,
        PREF_VISIT_ANALYSE_INFO varchar(1) default Y not null,
        PREF_VISIT_COMMENTS varchar(1) default Y not null,
        PREF_VISIT_PRODUCTS varchar(1) default Y not null,
        PREF_VISIT_DIAGNOSES varchar(1) default Y not null,
        PREF_VISIT_IMAGES varchar(1) default Y not null,
        primary key (USER_ID),
        unique (ACCOUNT, PASSWORD)
    ) ENGINE=InnoDB;

    create table THAU_USER_ROLE (
        USERROLE_ID bigint not null auto_increment,
        VERSION bigint not null,
        USER_ID bigint not null,
        ROLE_ID bigint not null,
        primary key (USERROLE_ID),
        unique (USER_ID, ROLE_ID)
    ) ENGINE=InnoDB;

    create table VISIT (
        VISIT_ID bigint not null auto_increment,
        VERSION bigint not null,
        MID bigint not null,
        PATIENT_ID bigint,
        APPOINTMENT_ID bigint,
        WEIGHT double precision,
        GLUCOSE double precision,
        TEMPERATURE double precision,
        VETERINARIAN varchar(70),
        PURPOSE varchar(100),
        ROOM varchar(15),
        STATUS varchar(1) not null,
        COMMENTS longtext,
        ESTIMATEDTIME integer not null,
        INVOICE_NR bigint,
        INVOICE_STATUS varchar(1),
        INVOICE_DATE datetime,
        REMINDER_SEND_DATE datetime,
        REMINDER2_SEND_DATE datetime,
        REMINDER3_SEND_DATE datetime,
        SENT_TO_INSURANCE varchar(1) not null,
        ADDEDBY varchar(25),
        ADDEDON datetime,
        LASTEDITEDBY varchar(25),
        LASTEDITEDON datetime,
        primary key (VISIT_ID)
    ) ENGINE=InnoDB;

    create table VISITIMAGE (
        VISITIMAGE_ID bigint not null auto_increment,
        VERSION bigint not null,
        MID bigint not null,
        APPOINTMENT_ID bigint not null,
        PATIENT_ID bigint not null,
        TYPE varchar(25) not null,
        IMAGE longblob,
        COMMENTS varchar(255),
        ADDEDBY varchar(25),
        ADDEDON datetime,
        LASTEDITEDBY varchar(25),
        LASTEDITEDON datetime,
        primary key (VISITIMAGE_ID)
    ) ENGINE=InnoDB;

    create table lineitemrefund (
        LINEITEM_ID bigint not null auto_increment,
        VERSION bigint not null,
        MID bigint not null,
        REFUND_ID bigint not null,
        NOMENCLATURE varchar(40) not null,
        QUANTITY double precision not null,
        TAX varchar(255) not null,
        TAXGOODPERCENTAGE double precision,
        TAXSERVICEPERCENTAGE double precision,
        COST double precision,
        PROCESSINGFEE double precision,
        TOTAL double precision,
        COSTTAXPORTION double precision,
        PROCESSINGFEESERVICETAXPORTION double precision,
        ADDEDBY varchar(25),
        ADDEDON datetime,
        LASTEDITEDBY varchar(25),
        LASTEDITEDON datetime,
        primary key (LINEITEM_ID)
    ) ENGINE=InnoDB;

    alter table ANALYSE 
        add index FKF746E853DBA6315B (AnalyseDescription_ID), 
        add constraint FKF746E853DBA6315B 
        foreign key (AnalyseDescription_ID) 
        references ANALYSEDESCRIPTION (ANALYSEDESCRIPTION_ID);

    alter table ANALYSE 
        add index FKF746E853858DFD9 (COSTING_ID), 
        add constraint FKF746E853858DFD9 
        foreign key (COSTING_ID) 
        references COSTING (COSTING_ID);

    alter table ANALYSELEMENT 
        add index FK3CBED06A458B4719 (ANALYSE_ID), 
        add constraint FK3CBED06A458B4719 
        foreign key (ANALYSE_ID) 
        references ANALYSE (ANALYSE_ID);

    alter table ANALYSELEMENT 
        add index FK3CBED06A858DFD9 (COSTING_ID), 
        add constraint FK3CBED06A858DFD9 
        foreign key (COSTING_ID) 
        references COSTING (COSTING_ID);

    create index APPNTMNT_VSTDT on APPOINTMENT (VISITDATE);

    create index COS_BARCODE_IDX on COSTING (BARCODE);

    alter table COSTING 
        add index FK63C944D51EA7AB19 (LOOKUPCOSTINGCATEGORY_ID), 
        add constraint FK63C944D51EA7AB19 
        foreign key (LOOKUPCOSTINGCATEGORY_ID) 
        references LOOKUPCOSTINGCATEGORY (LOOKUPCOSTINGCATEGORY_ID);

    alter table DAILYTASK 
        add index FK5EBE6EFEE7AA8CCC (DAILYTASKGROUP_ID), 
        add constraint FK5EBE6EFEE7AA8CCC 
        foreign key (DAILYTASKGROUP_ID) 
        references DAILYTASKSGROUP (DAILYTASKGROUP_ID);

    alter table DIAGNOSE 
        add index FK206EFDBEFC6C7935 (LOOKUPDIAGNOSIS_ID), 
        add constraint FK206EFDBEFC6C7935 
        foreign key (LOOKUPDIAGNOSIS_ID) 
        references LOOKUPDIAGNOSE (LOOKUPDIAGNOSIS_ID);

    alter table DIAGNOSE 
        add index FK206EFDBEEFD02F06 (PATIENT_ID), 
        add constraint FK206EFDBEEFD02F06 
        foreign key (PATIENT_ID) 
        references PATIENT (PATIENT_ID);

    alter table DIAGNOSE 
        add index FK206EFDBE734E9AC6 (APPOINTMENT_ID), 
        add constraint FK206EFDBE734E9AC6 
        foreign key (APPOINTMENT_ID) 
        references APPOINTMENT (APPOINTMENT_ID);

    create index DISTRIBUTOR_IDX on DISTRIBUTOR (DISTRIBUTOR_ID);

    alter table ESTIMATE 
        add index FKB9D615282D39199D (MLID), 
        add constraint FKB9D615282D39199D 
        foreign key (MLID) 
        references THAU_MEMBERLOCAL (MEMBERLOCAL_ID);

    alter table ESTIMATELINEITEM 
        add index FKC12B6E2FA41816CE (ESTIMATE_ID), 
        add constraint FKC12B6E2FA41816CE 
        foreign key (ESTIMATE_ID) 
        references ESTIMATE (ESTIMATE_ID);

    alter table ESTIMATESPECIFIC 
        add index FKFB27465AEFD02F06 (PATIENT_ID), 
        add constraint FKFB27465AEFD02F06 
        foreign key (PATIENT_ID) 
        references PATIENT (PATIENT_ID);

    alter table ESTIMATESPECIFIC 
        add index FKFB27465AA41816CE (ESTIMATE_ID), 
        add constraint FKFB27465AA41816CE 
        foreign key (ESTIMATE_ID) 
        references ESTIMATE (ESTIMATE_ID);

    create index LNTM_PTNT on LINEITEM (PATIENT_ID);

    alter table LINEITEM 
        add index FK75890107F04F2034 (CATEGORY_ID), 
        add constraint FK75890107F04F2034 
        foreign key (CATEGORY_ID) 
        references LOOKUPCOSTINGCATEGORY (LOOKUPCOSTINGCATEGORY_ID);

    alter table LINEITEM 
        add index FK75890107734E9AC6 (APPOINTMENT_ID), 
        add constraint FK75890107734E9AC6 
        foreign key (APPOINTMENT_ID) 
        references APPOINTMENT (APPOINTMENT_ID);

    alter table LOCATION 
        add index DIA_LOC_FK1 (DIAGNOSE_ID), 
        add constraint DIA_LOC_FK1 
        foreign key (DIAGNOSE_ID) 
        references DIAGNOSE (DIAGNOSE_ID);

    alter table LOCATION 
        add index FK9FF58FB5AD288F94 (LOOKUPLOCATION_ID), 
        add constraint FK9FF58FB5AD288F94 
        foreign key (LOOKUPLOCATION_ID) 
        references LOOKUPLOCATION (LOOKUPLOCATION_ID);

    alter table LOOKUPBREED 
        add index FKE363201AB1003B00 (LOOKUPSPECIES_ID), 
        add constraint FKE363201AB1003B00 
        foreign key (LOOKUPSPECIES_ID) 
        references LOOKUPSPECIES (LOOKUPSPECIES_ID);

    alter table NOTEPAD 
        add index FKA9B52F41EFD02F06 (PATIENT_ID), 
        add constraint FKA9B52F41EFD02F06 
        foreign key (PATIENT_ID) 
        references PATIENT (PATIENT_ID);

    alter table PATIENT 
        add index FKFB9F76E55BB4930E (CUSTOMER_ID), 
        add constraint FKFB9F76E55BB4930E 
        foreign key (CUSTOMER_ID) 
        references CUSTOMER (CUSTOMER_ID);

    alter table PAYMENT 
        add index FKFBE7BDE65BB4930E (CUSTOMER_ID), 
        add constraint FKFBE7BDE65BB4930E 
        foreign key (CUSTOMER_ID) 
        references CUSTOMER (CUSTOMER_ID);

    alter table PAYMENTVISIT 
        add index FK6DBF5705CB174366 (PAYMENT_ID), 
        add constraint FK6DBF5705CB174366 
        foreign key (PAYMENT_ID) 
        references PAYMENT (PAYMENT_ID);

    alter table PAYMENTVISIT 
        add index FK6DBF5705A9C3AC46 (VISIT_ID), 
        add constraint FK6DBF5705A9C3AC46 
        foreign key (VISIT_ID) 
        references VISIT (VISIT_ID);

    alter table REMINDER 
        add index FKFBCB072EFD02F06 (PATIENT_ID), 
        add constraint FKFBCB072EFD02F06 
        foreign key (PATIENT_ID) 
        references PATIENT (PATIENT_ID);

    create index LIST_SUPPLIERNAME_IDX on SUPPLIERPRODUCTLIST (SUPPLIER_NAME);

    create index LIST_SUPPLIERNAME_IDX on SUPPLIERPRODUCTLISTDIFF (SUPPLIER_NAME);

    create index LIST_ARTICLECODE_IDX on SUPPLIERPRODUCTLISTDIFF (ARTICLE_CODE);

    alter table SUPPLIERPRODUCTLISTDIFF 
        add index FKBD450C266D228C80 (SUPPLIERPRODUCTLIST_ID), 
        add constraint FKBD450C266D228C80 
        foreign key (SUPPLIERPRODUCTLIST_ID) 
        references SUPPLIERPRODUCTLIST (SUPPLIERPRODUCTLIST_ID);

    create index SUP_BARCODE_IDX on SUPPLIES2 (BARCODE);

    alter table SUPPLIES2LOCAL 
        add index FKA8B2D2665B143219 (SUPPLIES2_ID), 
        add constraint FKA8B2D2665B143219 
        foreign key (SUPPLIES2_ID) 
        references SUPPLIES2 (SUPPLIES2_ID);

    alter table SUPPLIES2LOCALSUPDATED 
        add index FKD7D9054E5B143219 (SUPPLIES2_ID), 
        add constraint FKD7D9054E5B143219 
        foreign key (SUPPLIES2_ID) 
        references SUPPLIES2 (SUPPLIES2_ID);

    create index DOMAIN_IDX on THAU_DOMAIN_PROPERTY (DOMAIN_ID);

    create index FUNCTION_IDX on THAU_FUNCTION (FUNCTION_ID);

    create index FUNCTIONROLE_IDX on THAU_FUNCTION_ROLE (FUNCTIONROLE_ID);

    alter table THAU_FUNCTION_ROLE 
        add index RL_FNCTRL_FK1 (ROLE_ID), 
        add constraint RL_FNCTRL_FK1 
        foreign key (ROLE_ID) 
        references THAU_ROLE (ROLE_ID);

    alter table THAU_FUNCTION_ROLE 
        add index FNCT_FNCTRL_FK1 (FUNCTION_ID), 
        add constraint FNCT_FNCTRL_FK1 
        foreign key (FUNCTION_ID) 
        references THAU_FUNCTION (FUNCTION_ID);

    create index IPSECURITY_IDX on THAU_IPSECURITY (IPSECURITY_ID);

    alter table THAU_IPSECURITY 
        add index USR_IPS_FK1 (USER_ID), 
        add constraint USR_IPS_FK1 
        foreign key (USER_ID) 
        references THAU_USER (USER_ID);

    create index MEMBER_IDX on THAU_MEMBER (MEMBER_ID);

    alter table THAU_MEMBERLOCAL 
        add index MMB_MMBL_FK1 (MEMBER_ID), 
        add constraint MMB_MMBL_FK1 
        foreign key (MEMBER_ID) 
        references THAU_MEMBER (MEMBER_ID);

    alter table THAU_MEMBERLOCAL_TAX 
        add index MMBL_MMBLT_FK1 (MEMBERLOCAL_ID), 
        add constraint MMBL_MMBLT_FK1 
        foreign key (MEMBERLOCAL_ID) 
        references THAU_MEMBERLOCAL (MEMBERLOCAL_ID);

    create index CFG_PARENT_ID_IDX on THAU_MENU_COMPONENTS (PARENT_ID);

    create index MENUCOMPONENT_IDX on THAU_MENU_COMPONENTS (MENUCOMPONENT_ID);

    alter table THAU_MENU_COMPONENTS 
        add index FK_CFG_PARENT_ID (PARENT_ID), 
        add constraint FK_CFG_PARENT_ID 
        foreign key (PARENT_ID) 
        references THAU_MENU_COMPONENTS (MENUCOMPONENT_ID);

    alter table THAU_MENU_COMPONENTS 
        add index FKA463415F3D96172D (FUNTION_ID), 
        add constraint FKA463415F3D96172D 
        foreign key (FUNTION_ID) 
        references THAU_FUNCTION (FUNCTION_ID);

    alter table THAU_REPORT 
        add index FKE425D3AB1F13F688 (MEMBER_ID), 
        add constraint FKE425D3AB1F13F688 
        foreign key (MEMBER_ID) 
        references THAU_MEMBER (MEMBER_ID);

    alter table THAU_REPORT 
        add index FK_RPRT_PARENT_ID (PARENT_ID), 
        add constraint FK_RPRT_PARENT_ID 
        foreign key (PARENT_ID) 
        references THAU_REPORT (REPORT_ID);

    create index ROLE_IDX on THAU_ROLE (ROLE_ID);

    create index SYSTEMPROPERTY_IDX on THAU_SYSTEM_PROPERTY (SYSTEMPROPERTY_ID);

    alter table THAU_USER 
        add index FKE2ACA0021F13F688 (MEMBER_ID), 
        add constraint FKE2ACA0021F13F688 
        foreign key (MEMBER_ID) 
        references THAU_MEMBER (MEMBER_ID);

    create index USERROLE_IDX on THAU_USER_ROLE (USERROLE_ID);

    create index USRROL_USR_ID_IDX on THAU_USER_ROLE (USER_ID);

    alter table THAU_USER_ROLE 
        add index USR_ROL_FK1 (USER_ID), 
        add constraint USR_ROL_FK1 
        foreign key (USER_ID) 
        references THAU_USER (USER_ID);

    alter table THAU_USER_ROLE 
        add index RL_USRCNTL_FK1 (ROLE_ID), 
        add constraint RL_USRCNTL_FK1 
        foreign key (ROLE_ID) 
        references THAU_ROLE (ROLE_ID);

    create index INVOICE_STATUS_IDX on VISIT (MID, INVOICE_STATUS);

    create index INVOICE_NR_IDX on VISIT (MID, INVOICE_NR);

    create index STATUS_IDX on VISIT (MID, STATUS);

    alter table VISIT 
        add index FK4DE552BEFD02F06 (PATIENT_ID), 
        add constraint FK4DE552BEFD02F06 
        foreign key (PATIENT_ID) 
        references PATIENT (PATIENT_ID);

    alter table VISIT 
        add index FK4DE552B734E9AC6 (APPOINTMENT_ID), 
        add constraint FK4DE552B734E9AC6 
        foreign key (APPOINTMENT_ID) 
        references APPOINTMENT (APPOINTMENT_ID);

    alter table lineitemrefund 
        add index FKD525CF3F7D2A7029 (REFUND_ID), 
        add constraint FKD525CF3F7D2A7029 
        foreign key (REFUND_ID) 
        references REFUND (REFUND_ID);
