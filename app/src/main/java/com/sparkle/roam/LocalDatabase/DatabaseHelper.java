package com.sparkle.roam.LocalDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "b2b_dev.db";
    private static final String PAYACCOUNT_TABLE_NAME = "payAccount";
    private static final String USER_TABLE_NAME = "user";
    private static final String PAYEVENT_TABLE_NAME = "payEvent";
    private static final String PRODUCTITEM_TABLE_NAME = "productItem";
    private static final String LAST_SYNC_TABLE_NAME = "lastSyncDate";
    private static final String BATCH_CODE_TABLE_NAME = "batchCodeGenerate";
    private static final String DISRTIBUTOR_TABLE_NAME = "distributorTable";

    private static final String BATCH_CODE_COL_1 = "ID";
    private static final String BATCH_CODE_COL_2 = "userID";
    private static final String BATCH_CODE_COL_3 = "days";

    private static final String LAST_SYNC_COL_1 = "sync_ID";
    private static final String LAST_SYNC_COL_2 = "payAccount";
    private static final String LAST_SYNC_COL_3 = "payEvent";
    private static final String LAST_SYNC_COL_4 = "user";
    private static final String LAST_SYNC_COL_5 = "productItem";

    private static final String COL_1 = "payAccountID";
    private static final String COL_2 = "startDate";
    private static final String COL_3 = "depositDays";
    private static final String COL_4 = "payoffAmt";
    private static final String COL_5 = "minPayDays";
    private static final String COL_6 = "maxPayDays";
    private static final String COL_7 = "schPayDays";
    private static final String COL_8 = "userID";
    private static final String COL_9 = "distributorID";
    private static final String COL_10 ="assignedItemsID";
    private static final String COL_11 ="agentID";
    private static final String COL_12 ="agentAssignmentStatus";
    private static final String COL_13 ="agentAssignment";
    private static final String COL_14 ="initialCreditDays";
    private static final String COL_15 ="receivedPayAmt";
    private static final String COL_16 ="durationDays";
    private static final String COL_17 ="creditDaysIssued";
    private static final String COL_18 ="payDaysReceived";

    private static final String PAYEVENT_COL_1 = "payEventID";
    private static final String PAYEVENT_COL_2 = "payEventDate";
    private static final String PAYEVENT_COL_3 = "payDays";
    private static final String PAYEVENT_COL_4 = "payRecordAmt";
    private static final String PAYEVENT_COL_5 = "payRecordNotes";
    private static final String PAYEVENT_COL_6 = "payAccountID";
    private static final String PAYEVENT_COL_7 = "eventType";
    private static final String PAYEVENT_COL_8 = "codeDays";
    private static final String PAYEVENT_COL_9 = "codeIssued";
    private static final String PAYEVENT_COL_10 ="codehashTop";
    private static final String PAYEVENT_COL_11 ="sync";

    private static final String USER_COL_1 = "userID";
    private static final String USER_COL_2 = "distributorID";
    private static final String USER_COL_3 = "userCode";
    private static final String USER_COL_4 = "agentID";
    private static final String USER_COL_5 = "lastName";
    private static final String USER_COL_6 = "firstName";
    private static final String USER_COL_7 = "phoneNumber";
    private static final String USER_COL_8 = "email";
    private static final String USER_COL_9 = "locationGPS";
    private static final String USER_COL_10 = "address1";
    private static final String USER_COL_11 = "address2";
    private static final String USER_COL_12 = "city";
    private static final String USER_COL_13 = "state";
    private static final String USER_COL_14 = "country";
    private static final String USER_COL_15 = "postCode";

    private static final String PRODUCT_COL_1 = "productItemID";
    private static final String PRODUCT_COL_2 = "productModelID";
    private static final String PRODUCT_COL_3 = "productBatchID";
    private static final String PRODUCT_COL_4 = "productItemOEM_SN";
    private static final String PRODUCT_COL_5 = "productItemPAYG_SN";
    private static final String PRODUCT_COL_6 = "lifeCycleStatus";
    private static final String PRODUCT_COL_7 = "firmwareVersion";
    private static final String PRODUCT_COL_8 = "assignedItemsID";

    private static final String DISRTIBUTOR_COL_1 = "distributorID";
    private static final String DISRTIBUTOR_COL_2 = "customerName";
    private static final String DISRTIBUTOR_COL_3 = "distributorProfileURL";
    private static final String DISRTIBUTOR_COL_4 = "distributorAccountNo";
    private static final String DISRTIBUTOR_COL_5 = "contactLastName";
    private static final String DISRTIBUTOR_COL_6 = "contactFirstName";
    private static final String DISRTIBUTOR_COL_7 = "contactEmail";
    private static final String DISRTIBUTOR_COL_8 = "phone";
    private static final String DISRTIBUTOR_COL_9 = "addressLine1";
    private static final String DISRTIBUTOR_COL_10 ="addressLine2";
    private static final String DISRTIBUTOR_COL_11 ="city";
    private static final String DISRTIBUTOR_COL_12 ="state";
    private static final String DISRTIBUTOR_COL_13 ="postalCode";
    private static final String DISRTIBUTOR_COL_14 ="country_countryID";
    private static final String DISRTIBUTOR_COL_15 ="countryID";
    private static final String DISRTIBUTOR_COL_16 ="nameEnglish";
    private static final String DISRTIBUTOR_COL_17 ="nameNative";
    private static final String DISRTIBUTOR_COL_18 ="countrySymbol";
    private static final String DISRTIBUTOR_COL_19 ="flagImage";
    private static final String DISRTIBUTOR_COL_20 ="currencyNameEnglish";
    private static final String DISRTIBUTOR_COL_21 ="currencySymbol";
    private static final String DISRTIBUTOR_COL_22 ="currencyFXCode";
    private static final String DISRTIBUTOR_COL_23 ="phoneAreaCode";





    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(" CREATE TABLE IF NOT EXISTS " + PAYACCOUNT_TABLE_NAME + "( payAccountID  INTEGER, startDate DATETIME, depositDays INTEGER, payoffAmt DECIMAL," +
                " minPayDays INTEGER, maxPayDays INTEGER, schPayDays INTEGER, userID INTEGER, distributorID INTEGER, assignedItemsID INTEGER, agentID INTEGER, " +
                "agentAssignmentStatus VARCHAR, agentAssignment TEXT, initialCreditDays INTEGER, receivedPayAmt DECIMAL, durationDays INTEGER, creditDaysIssued INTEGER, payDaysReceived INTEGER)");

        db.execSQL(" CREATE TABLE IF NOT EXISTS " + USER_TABLE_NAME + "( userID  INTEGER, distributorID INTEGER, userCode INTEGER, agentID INTEGER," +
                " lastName VARCHAR, firstName VARCHAR, phoneNumber VARCHAR, email VARCHAR, locationGPS VARCHAR, address1 VARCHAR, " +
                "address2 VARCHAR, city VARCHAR, state VARCHAR, country VARCHAR, postCode VARCHAR)");

        db.execSQL(" CREATE TABLE IF NOT EXISTS " + PAYEVENT_TABLE_NAME + "( payEventID  INTEGER , payEventDate DATETIME, payDays INTEGER, payRecordAmt DECIMAL," +
                " payRecordNotes VARCHAR, payAccountID INTEGER, eventType ENUM, codeDays INTEGER, codeIssued MEDIUMTEXT, codehashTop MEDIUMTEXT, sync INTEGER)");

        db.execSQL(" CREATE TABLE IF NOT EXISTS " + PRODUCTITEM_TABLE_NAME + "( productItemID  INTEGER, productModelID INTEGER, productBatchID INTEGER, productItemOEM_SN VARCHAR," +
                " productItemPAYG_SN VARCHAR, lifeCycleStatus VARCHAR, firmwareVersion VARCHAR, assignedItemsID VARCHAR)");

        db.execSQL(" CREATE TABLE IF NOT EXISTS " + LAST_SYNC_TABLE_NAME + "( sync_ID INTEGER, payAccount DATETIME, payEvent DATETIME, user DATETIME, productItem DATETIME)");

        db.execSQL(" CREATE TABLE IF NOT EXISTS " + BATCH_CODE_TABLE_NAME + "( ID  INTEGER PRIMARY KEY AUTOINCREMENT, userID TEXT, days TEXT)");

        db.execSQL(" CREATE TABLE IF NOT EXISTS " + DISRTIBUTOR_TABLE_NAME + "( distributorID  VARCHAR, customerName VARCHAR, distributorProfileURL VARCHAR, distributorAccountNo VARCHAR," +
                " contactLastName VARCHAR, contactFirstName VARCHAR, contactEmail VARCHAR, phone VARCHAR, addressLine1 VARCHAR, addressLine2 VARCHAR, city VARCHAR, " +
                "state VARCHAR, postalCode VARCHAR, country_countryID VARCHAR, countryID VARCHAR, nameEnglish VARCHAR, nameNative VARCHAR, countrySymbol VARCHAR, flagImage VARCHAR, " +
                "currencyNameEnglish VARCHAR, currencySymbol VARCHAR, currencyFXCode VARCHAR, phoneAreaCode VARCHAR)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " +PAYACCOUNT_TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " +USER_TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " +PAYEVENT_TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " +PRODUCTITEM_TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " +LAST_SYNC_TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " +BATCH_CODE_TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " +DISRTIBUTOR_TABLE_NAME);
        onCreate(db);
    }
    public boolean insertPayAccountData(String payAccountID, String startDate,String depositDays, String payoffAmt, String minPayDays, String maxPayDays, String schPayDays, String userID, String distributorID,
                              String assignedItemsID, String agentID, String agentAssignmentStatus, String agentAssignment, String initialCreditDays, String receivedPayAmt,
                              String durationDays, String creditDaysIssued, String payDaysReceived){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1 , payAccountID);
        contentValues.put(COL_2 , startDate);
        contentValues.put(COL_3 , depositDays);
        contentValues.put(COL_4 , payoffAmt);
        contentValues.put(COL_5 , minPayDays);
        contentValues.put(COL_6 , maxPayDays);
        contentValues.put(COL_7 , schPayDays);
        contentValues.put(COL_8 , userID);
        contentValues.put(COL_9 , distributorID);
        contentValues.put(COL_10 , assignedItemsID);
        contentValues.put(COL_11 , agentID);
        contentValues.put(COL_12 , agentAssignmentStatus);
        contentValues.put(COL_13 , agentAssignment);
        contentValues.put(COL_14 , initialCreditDays);
        contentValues.put(COL_15 , receivedPayAmt);
        contentValues.put(COL_16 , durationDays);
        contentValues.put(COL_17 , creditDaysIssued);
        contentValues.put(COL_18 , payDaysReceived);
        long result = db.insert(PAYACCOUNT_TABLE_NAME,null,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean insertDistributorData(String distributorID, String customerName, String distributorProfileURL , String distributorAccountNo , String
            contactLastName , String contactFirstName , String contactEmail , String phone , String addressLine1 , String addressLine2 , String city , String
            state , String postalCode , String country_countryID , String countryID , String nameEnglish , String nameNative , String countrySymbol , String flagImage , String
            currencyNameEnglish , String currencySymbol , String currencyFXCode , String phoneAreaCode ){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DISRTIBUTOR_COL_1 ,distributorID);
        contentValues.put(DISRTIBUTOR_COL_2 ,customerName);
        contentValues.put(DISRTIBUTOR_COL_3 ,distributorProfileURL);
        contentValues.put(DISRTIBUTOR_COL_4 ,distributorAccountNo);
        contentValues.put(DISRTIBUTOR_COL_5 ,contactLastName);
        contentValues.put(DISRTIBUTOR_COL_6 ,contactFirstName);
        contentValues.put(DISRTIBUTOR_COL_7 ,contactEmail);
        contentValues.put(DISRTIBUTOR_COL_8 ,phone);
        contentValues.put(DISRTIBUTOR_COL_9 ,addressLine1);
        contentValues.put(DISRTIBUTOR_COL_10,addressLine2);
        contentValues.put(DISRTIBUTOR_COL_11,city);
        contentValues.put(DISRTIBUTOR_COL_12,state);
        contentValues.put(DISRTIBUTOR_COL_13,postalCode);
        contentValues.put(DISRTIBUTOR_COL_14,country_countryID);
        contentValues.put(DISRTIBUTOR_COL_15,countryID);
        contentValues.put(DISRTIBUTOR_COL_16,nameEnglish);
        contentValues.put(DISRTIBUTOR_COL_17,nameNative);
        contentValues.put(DISRTIBUTOR_COL_18,countrySymbol);
        contentValues.put(DISRTIBUTOR_COL_19,flagImage);
        contentValues.put(DISRTIBUTOR_COL_20,currencyNameEnglish);
        contentValues.put(DISRTIBUTOR_COL_21,currencySymbol);
        contentValues.put(DISRTIBUTOR_COL_22,currencyFXCode);
        contentValues.put(DISRTIBUTOR_COL_23,phoneAreaCode);
        long result = db.insert(DISRTIBUTOR_TABLE_NAME,null,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean insertUserData(String userID, String distributorID,String userCode, String agentID, String lastName, String firstName, String phoneNumber, String email, String locationGPS,
                                        String address1, String address2, String city, String state, String country, String postCode){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COL_1 , userID);
        contentValues.put(USER_COL_2 , distributorID);
        contentValues.put(USER_COL_3 , userCode);
        contentValues.put(USER_COL_4 , agentID);
        contentValues.put(USER_COL_5 , lastName);
        contentValues.put(USER_COL_6 , firstName);
        contentValues.put(USER_COL_7 , phoneNumber);
        contentValues.put(USER_COL_8 , email);
        contentValues.put(USER_COL_9 , locationGPS);
        contentValues.put(USER_COL_10 ,address1);
        contentValues.put(USER_COL_11 ,address2);
        contentValues.put(USER_COL_12 ,city);
        contentValues.put(USER_COL_13 ,state);
        contentValues.put(USER_COL_14 ,country);
        contentValues.put(USER_COL_15 ,postCode);

        long result = db.insert(USER_TABLE_NAME,null,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean insertPayEventData(String payEventID, String payEventDate,String payDays, String payRecordAmt, String payRecordNotes, String payAccountID,
                                      String eventType, String codeDays, String codeIssued, String codehashTop,String sync){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PAYEVENT_COL_1 , payEventID);
        contentValues.put(PAYEVENT_COL_2 , payEventDate);
        contentValues.put(PAYEVENT_COL_3 , payDays);
        contentValues.put(PAYEVENT_COL_4 , payRecordAmt);
        contentValues.put(PAYEVENT_COL_5 , payRecordNotes);
        contentValues.put(PAYEVENT_COL_6 , payAccountID);
        contentValues.put(PAYEVENT_COL_7 , eventType);
        contentValues.put(PAYEVENT_COL_8 , codeDays);
        contentValues.put(PAYEVENT_COL_9 , codeIssued);
        contentValues.put(PAYEVENT_COL_10 , codehashTop);
        contentValues.put(PAYEVENT_COL_11 , sync);
        long result = db.insert(PAYEVENT_TABLE_NAME,null,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean insertProductItemData(String productItemID, String productModelID,String productBatchID, String productItemOEM_SN, String productItemPAYG_SN, String lifeCycleStatus,
                                      String firmwareVersion, String assignedItemsID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PRODUCT_COL_1 , productItemID);
        contentValues.put(PRODUCT_COL_2 , productModelID);
        contentValues.put(PRODUCT_COL_3 , productBatchID);
        contentValues.put(PRODUCT_COL_4 , productItemOEM_SN);
        contentValues.put(PRODUCT_COL_5 , productItemPAYG_SN);
        contentValues.put(PRODUCT_COL_6 , lifeCycleStatus);
        contentValues.put(PRODUCT_COL_7 , firmwareVersion);
        contentValues.put(PRODUCT_COL_8 , assignedItemsID);

        long result = db.insert(PRODUCTITEM_TABLE_NAME,null,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean insertLastSyncData(String id,String payAccount,String payEvent,String user, String productItem){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LAST_SYNC_COL_1 , id);
        contentValues.put(LAST_SYNC_COL_2 , payAccount);
        contentValues.put(LAST_SYNC_COL_3 , payEvent);
        contentValues.put(LAST_SYNC_COL_4 , user);
        contentValues.put(LAST_SYNC_COL_5 , productItem);

        long result = db.insert(LAST_SYNC_TABLE_NAME,null,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }


    public boolean insertBatchCodeData(String userID,String days){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(BATCH_CODE_COL_2 , userID);
        contentValues.put(BATCH_CODE_COL_3 , days);

        long result = db.insert(BATCH_CODE_TABLE_NAME,null,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Integer deleteBatchCodeData (String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(BATCH_CODE_TABLE_NAME, " ID = ? ", new String[] {id});
    }
    public void deleteSyncPayEvent (String sync){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PAYEVENT_TABLE_NAME, " sync = ? ", new String[]{sync});
    }

    public Cursor getBatchCodeData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery(" select * from " + BATCH_CODE_TABLE_NAME ,  null);
        return res;
    }

    public boolean updateLastSyncData(String id,String payAccount,String payEvent,String user, String productItem){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LAST_SYNC_COL_2 , payAccount);
        contentValues.put(LAST_SYNC_COL_3 , payEvent);
        contentValues.put(LAST_SYNC_COL_4 , user);
        contentValues.put(LAST_SYNC_COL_5 , productItem);
        db.update(LAST_SYNC_TABLE_NAME,contentValues, " sync_ID = ? ", new String[] {id});
        return true;
    }

    public void updatePayEventData(String codeIssued,String payEventDate){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PAYEVENT_COL_2 , payEventDate);
        db.update(PAYEVENT_TABLE_NAME,contentValues, " codeIssued = ? ", new String[] {codeIssued});
    }

    public Cursor getLastSyncData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery(" select * from " + LAST_SYNC_TABLE_NAME ,  null);
        return res;
    }

    public Cursor getDistributorData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery(" select * from " + DISRTIBUTOR_TABLE_NAME ,  null);
        return res;
    }

    public Cursor getPayAccountData(String s){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery(" select * from " + PAYACCOUNT_TABLE_NAME + " LEFT JOIN " + USER_TABLE_NAME + " ON "+PAYACCOUNT_TABLE_NAME+".userID = "+USER_TABLE_NAME+".userID " +
                "LEFT JOIN "+PRODUCTITEM_TABLE_NAME+" ON "+PAYACCOUNT_TABLE_NAME+".assignedItemsID = "+PRODUCTITEM_TABLE_NAME+".assignedItemsID" +
                " WHERE "+USER_TABLE_NAME+".firstName LIKE '%"+s+"%' OR "+USER_TABLE_NAME+".lastName LIKE '%"+s+"%'",  null);
        return res;
    }

    public Cursor getPayEventData(String s){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = " select * from " + PAYEVENT_TABLE_NAME + " WHERE payAccountID = ? ";
        Cursor cursor = db.rawQuery(query, new String[] {s});
        return cursor;
    }

    public Cursor orderbyPayAccId(String s){
        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.query(PAYACCOUNT_TABLE_NAME, new String[]{COL_1,COL_2,COL_3,COL_4,COL_5,COL_6,COL_7,COL_8,COL_9,COL_10,COL_11,COL_12,COL_13,COL_14,COL_15,COL_16,COL_17,COL_18}, null, null, null, null, COL_1+" ASC");
        Cursor cursor1 = db.rawQuery(" select * from " + PAYACCOUNT_TABLE_NAME + " LEFT JOIN " + USER_TABLE_NAME + " ON "+PAYACCOUNT_TABLE_NAME+".userID = "+USER_TABLE_NAME+".userID " +
                        "LEFT JOIN "+PRODUCTITEM_TABLE_NAME+" ON "+PAYACCOUNT_TABLE_NAME+".assignedItemsID = "+PRODUCTITEM_TABLE_NAME+".assignedItemsID" +
                        " WHERE "+USER_TABLE_NAME+".firstName LIKE '%"+s+"%' OR "+USER_TABLE_NAME+".lastName LIKE '%"+s+"%'"+
                        " ORDER BY "+COL_1 + " ASC"
                , new String[] {});
        return cursor1;
    }

    public Cursor orderbyDueDate(String s){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor1 = db.rawQuery(" select * from " + PAYACCOUNT_TABLE_NAME + " LEFT JOIN " + USER_TABLE_NAME + " ON "+PAYACCOUNT_TABLE_NAME+".userID = "+USER_TABLE_NAME+".userID " +
                        "LEFT JOIN "+PRODUCTITEM_TABLE_NAME+" ON "+PAYACCOUNT_TABLE_NAME+".assignedItemsID = "+PRODUCTITEM_TABLE_NAME+".assignedItemsID" +
                        " WHERE "+USER_TABLE_NAME+".firstName LIKE '%"+s+"%' OR "+USER_TABLE_NAME+".lastName LIKE '%"+s+"%'"+
                        " ORDER BY "+COL_2 + " ASC"//DESC
                , new String[] {});
        return cursor1;
    }

    public Cursor orderbyname(String s){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor1 = db.rawQuery(" select * from " + PAYACCOUNT_TABLE_NAME + " LEFT JOIN " + USER_TABLE_NAME + " ON "+PAYACCOUNT_TABLE_NAME+".userID = "+USER_TABLE_NAME+".userID " + "LEFT JOIN "+PRODUCTITEM_TABLE_NAME+" ON "+PAYACCOUNT_TABLE_NAME+".assignedItemsID = "+PRODUCTITEM_TABLE_NAME+".assignedItemsID" + " WHERE "+USER_TABLE_NAME+".firstName LIKE '%"+s+"%' OR "+USER_TABLE_NAME+".lastName LIKE '%"+s+"%'"+ " ORDER BY "+USER_COL_6 + " ASC"//DESC
                , new String[] {});
        return cursor1;
    }


    public Cursor checkAssignmentType(String s){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select count(*) as total from "+PAYACCOUNT_TABLE_NAME+" WHERE userID = ? AND agentAssignmentStatus = 'limited by days'";
        System.out.println("Count query 1: "+query);
        Cursor cursor = db.rawQuery(query, new String[] {s});
        return cursor;
    }

    public Cursor checkAssignmentTypeCode(String s){
        SQLiteDatabase db = this.getWritableDatabase();
        // Cursor res = db.rawQuery(" SELECT count(*) as total FROM (SELECT payAccountID,agentAssignmentStatus,JSON_LENGTH(agentAssignment->'$.assignedCodes') t FROM b2b_dev.payAccount WHERE userID = 100  ORDER BY payAccountID DESC) as r WHERE r.t <= 0; "  ,  null);
        String query = "select agentAssignment from "+PAYACCOUNT_TABLE_NAME+" WHERE userID = ? AND agentAssignmentStatus = 'limited by codes'";
        System.out.println("Count query 1: "+query);
        Cursor cursor = db.rawQuery(query, new String[] {s});
        return cursor;
    }

//    public Cursor checkAssignmentType(String s){
//SELECT count(*) as total FROM (SELECT payAccountID,agentAssignmentStatus,json_array_length(json_extract(payAccount.agentAssignment, '$.assignedCodes')) t FROM payAccount WHERE userID = 107  ORDER BY payAccountID DESC) as r WHERE r.t <= 0;
//        SQLiteDatabase db = this.getWritableDatabase();
//        String query = " select count(*) as total from (select payAccountID,agentAssignmentStatus,json_array_length(json_extract(payAccount.agentAssignment, '$.assignedCodes')) t from payAccount WHERE userID = 99  ORDER BY payAccountID DESC) as r WHERE r.t <= 0;";
//        Cursor cursor = db.rawQuery(query,null);
//        return cursor;
//    }
    public Cursor getUserData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery(" select * from " + USER_TABLE_NAME ,  null);
        return res;
    }

    public Cursor getUserPayAccountData(String s){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = " select * from " + PAYACCOUNT_TABLE_NAME + " LEFT JOIN " + PRODUCTITEM_TABLE_NAME + " ON "+PAYACCOUNT_TABLE_NAME+".assignedItemsID = "+PRODUCTITEM_TABLE_NAME+".assignedItemsID WHERE userID = ? ";
        Cursor cursor = db.rawQuery(query, new String[] {s});
        return cursor;
    }
    public Cursor getLastEventData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery(" select * from " + PAYEVENT_TABLE_NAME ,  null);
        return res;
    }

    public Cursor getOnePayAccountData(String s){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = " select * from " + PAYACCOUNT_TABLE_NAME + " WHERE payAccountID = ? ";
        Cursor cursor = db.rawQuery(query, new String[] {s});
        return cursor;
    }

    public Cursor getAllPayAccountData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery(" select * from " + PAYACCOUNT_TABLE_NAME, null);
        return res;
    }

    public boolean updateAssignCodeArrayData(String id,String data){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_13,data);
        db.update(PAYACCOUNT_TABLE_NAME,contentValues, " payAccountID = ? ", new String[] {id});
        return true;
    }

    public void deleteAlltable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(" DROP TABLE IF EXISTS " +PAYACCOUNT_TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " +USER_TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " +PAYEVENT_TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " +PRODUCTITEM_TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " +LAST_SYNC_TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " +BATCH_CODE_TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " +DISRTIBUTOR_TABLE_NAME);
    }
//    public Integer deleteData (String id){
//        SQLiteDatabase db = this.getWritableDatabase();
//        return db.delete(TABLE_NAME, " ID = ? ", new String[] {id});
//    }
//
//    public Cursor viewPData(String s){
//        SQLiteDatabase db = this.getWritableDatabase();
//        String query = " SELECT * FROM " + TABLE_NAME +  " WHERE ID = ? ";
//        Cursor cursor = db.rawQuery(query, new String[] {s});
//        return cursor;
//    }
//    public Cursor viewNameData(String s){
//        SQLiteDatabase db = this.getWritableDatabase();
//        String query = " SELECT * FROM " + TABLE_NAME +  " WHERE NAME = ? ";
//        Cursor cursor = db.rawQuery(query,new String[] {s});
//        return cursor;
//    }

 }
