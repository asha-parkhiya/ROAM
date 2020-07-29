package com.sparkle.roam.ContentProvider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anildeshpande on 3/23/17.
 */

public class ToDoListDBAdapter {

    private static final String TAG=ToDoListDBAdapter.class.getSimpleName();

    public static final String DB_NAME = "ppid_code.db";
    public static final int DB_VERSION = 2;

    public static final String TABLE_TODO = "table_ppid_code";
//    public static final String COLUMN_ID="id";
    public static final String COLUMN_PPID = "ppid";
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_TYPE = "device_type";

    public static String CREATE_TABLE_TODO="CREATE TABLE "+TABLE_TODO+"("+COLUMN_PPID+" TEXT, "+COLUMN_CODE+" TEXT NOT NULL, "+ COLUMN_TYPE+ " TEXT )";

    private Context context;
    private SQLiteDatabase  sqLliteDatabase;
    private static ToDoListDBAdapter toDoListDBAdapterInstance;




    private ToDoListDBAdapter(Context context){
        this.context=context;
        sqLliteDatabase=new ToDoListDBHelper(this.context,DB_NAME,null,DB_VERSION).getWritableDatabase();
    }

    public  static ToDoListDBAdapter getToDoListDBAdapterInstance(Context context){
        if(toDoListDBAdapterInstance==null){
            toDoListDBAdapterInstance=new ToDoListDBAdapter(context);
        }
        return toDoListDBAdapterInstance;
    }

    //Will be used in the content provider
    public Cursor getCursorsForAllToDos(){
        Cursor cursor=sqLliteDatabase.query(TABLE_TODO,new String[]{COLUMN_PPID,COLUMN_CODE, COLUMN_TYPE},null,null,null,null,null,null);
        return cursor;
    }

    public Cursor getCursorForSpecificPlace(String place){
        //SELECT task_id,todo FROM table_todo WHERE place LIKE '%desk%'
        Cursor cursor=sqLliteDatabase.query(TABLE_TODO,new String[]{COLUMN_CODE},COLUMN_TYPE +" LIKE '%"+place+"%'",null,null,null,null,null);
        return cursor;
    }

    public Cursor getCount(){
        Cursor cursor=sqLliteDatabase.rawQuery("SELECT COUNT(*) FROM "+TABLE_TODO,null);
        return cursor;
    }

    //insert,delete,modify,query methods

//    public boolean insert(String toDoItem){
//        ContentValues contentValues=new ContentValues();
//        contentValues.put(COLUMN_CODE,toDoItem);
//
//        return sqLliteDatabase.insert(TABLE_TODO,null,contentValues)>0;
//    }

    public boolean insert(String ppid,String toDoItem, String place){
        ContentValues contentValues=new ContentValues();
        contentValues.put(COLUMN_PPID,ppid);
        contentValues.put(COLUMN_CODE,toDoItem);
        contentValues.put(COLUMN_TYPE,place);

        return sqLliteDatabase.insert(TABLE_TODO,null,contentValues)>0;
    }

    //Will be used in the content provider
    public long insert(ContentValues contentValues){
        return sqLliteDatabase.insert(TABLE_TODO,null,contentValues);
    }

//    public boolean delete(int taskId){
//        return sqLliteDatabase.delete(TABLE_TODO, COLUMN_ID+" = "+taskId,null)>0;
//    }
    public int delete(){
//        db.execSQL(" DROP TABLE IF EXISTS " +TABLE_CODE);
        return sqLliteDatabase.delete(TABLE_TODO,null,null);
    }

    //Will be used by the provider
    public int delete(String whereClause, String [] whereValues){
        return sqLliteDatabase.delete(TABLE_TODO,whereClause,whereValues);
    }

//    public boolean modify(int taskId, String newToDoItem){
//        ContentValues contentValues=new ContentValues();
//        contentValues.put(COLUMN_TODO,newToDoItem);
//
//        return sqLliteDatabase.update(TABLE_TODO,contentValues, COLUMN_TODO_ID+" = "+taskId,null)>0;
//    }

    public void modify(String ppid,String hascode,String device_type){
        ContentValues contentValues=new ContentValues();
        contentValues.put(COLUMN_CODE, hascode);
        contentValues.put(COLUMN_TYPE , device_type);

//       sqLliteDatabase.update(TABLE_TODO,contentValues, COLUMN_PPID+" = "+ppid, null);
        sqLliteDatabase.update(TABLE_TODO,contentValues, COLUMN_PPID+" = ? ", new String[] {ppid});
    }

    //Will be used in the content provider
    public int update(ContentValues contentValues, String s, String [] strings){
        return sqLliteDatabase.update(TABLE_TODO,contentValues, s,strings);
    }

    public List<ToDo> getAllToDos(){
        List<ToDo> toDoList=new ArrayList<ToDo>();

        Cursor cursor=sqLliteDatabase.query(TABLE_TODO,new String[]{COLUMN_PPID,COLUMN_CODE, COLUMN_TYPE},null,null,null,null,null,null);
        if(cursor!=null &cursor.getCount()>0){
            while(cursor.moveToNext()){
                ToDo toDo=new ToDo(cursor.getString(0),cursor.getString(1), cursor.getString(2));
                toDoList.add(toDo);

            }
        }
        cursor.close();
        return toDoList;
    }

    private static class ToDoListDBHelper extends SQLiteOpenHelper{

        public ToDoListDBHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int dbVersion){
            super(context,databaseName,factory,dbVersion);
        }

        @Override
        public void onConfigure(SQLiteDatabase db) {
            super.onConfigure(db);
            db.setForeignKeyConstraintsEnabled(true);
            Log.i(TAG,"Inside onConfigure");
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_TABLE_TODO);
            System.out.println("------------CREATE_TABLE_TODO---------------------"+CREATE_TABLE_TODO);
            Log.i(TAG,"Inside onCreate");

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase,
                              int oldVersion, int newVersion) {
            //Not implemented now

//            switch (oldVersion){
//                case 1: sqLiteDatabase.execSQL("ALTER TABLE "+TABLE_TODO+ " ADD COLUMN "+COLUMN_PLACE+" TEXT");break;
//                default: break;
//            }
//            Log.i(TAG,"Inside onUpgrade");
        }
    }

}
//public class ToDoListDBAdapter {
//
//    private static final String TAG = ToDoListDBAdapter.class.getSimpleName();
//
//    private Context context;
//    private SQLiteDatabase sqLliteDatabase;
//    private static ToDoListDBAdapter toDoListDBAdapterInstance;
//
//    public static final String DB_NAME = "ppid_code.db";
//    public static final int DB_VERSION = 1;
//
//    public static final String TABLE_CODE = "TABLE_CODE";
//
//    public static final String COLUMN_ID = "id";
//    public static final String COLUMN_PPID = "ppid";
//    public static final String COLUMN_CODE = "hascode";
//    public static final String COLUMN_TYPE = "device_type";
//
//    //create table TABLE_CODE(task_id integer primary key, todo text not null);
//
//    private class ToDoListDBHelper extends SQLiteOpenHelper {
//
//        public ToDoListDBHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int dbVersion){
//            super(context,databaseName,factory,dbVersion);
//        }
//        public ToDoListDBHelper(Context context) {
//            super(context, TABLE_CODE, null, 1);
//            SQLiteDatabase db = this.getWritableDatabase();
//
//        }
//
//        @Override
//        public void onConfigure(SQLiteDatabase db) {
//            super.onConfigure(db);
//            db.setForeignKeyConstraintsEnabled(true);
//            Log.i(TAG,"Inside onConfigure");
//        }
//
//        @Override
//        public void onCreate(SQLiteDatabase db) {
//            db.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_CODE + "(id INTEGER PRIMARY KEY ,ppid INTEGER, hascode TEXT, device_type TEXT)");
//        }
//
//        @Override
//        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//            db.execSQL(" DROP TABLE IF EXISTS " +TABLE_CODE);
//            onCreate(db);
//        }
//
//    }
//
//    private ToDoListDBAdapter(Context context){
//        this.context=context;
//        sqLliteDatabase=new ToDoListDBHelper(this.context,DB_NAME,null,DB_VERSION).getWritableDatabase();
//    }
//
//    public  static ToDoListDBAdapter getToDoListDBAdapterInstance(Context context){
//        if(toDoListDBAdapterInstance==null){
//            toDoListDBAdapterInstance=new ToDoListDBAdapter(context);
//        }
//        return toDoListDBAdapterInstance;
//    }
//
//    //Will be used in the content provider
//    public Cursor getCursorsForAllToDos(){
//        Cursor cursor = sqLliteDatabase.query(TABLE_CODE,new String[]{COLUMN_ID,COLUMN_PPID,COLUMN_CODE,COLUMN_TYPE},null,null,null,null,null,null);
//        return cursor;
//    }
//
//    public Cursor getCursorForSpecificPlace(String place){
//        //SELECT task_id,todo FROM TABLE_CODE WHERE place LIKE '%desk%'
//        Cursor cursor=sqLliteDatabase.query(TABLE_CODE,new String[]{COLUMN_PPID,COLUMN_CODE},COLUMN_TYPE +" LIKE '%"+place+"%'",null,null,null,null,null);
//        return cursor;
//    }
//
//    public Cursor getCount(){
//        Cursor cursor=sqLliteDatabase.rawQuery("SELECT COUNT(*) FROM "+TABLE_CODE,null);
//        return cursor;
//    }
//
//
//
//    //insert,delete,modify,query methods
//
////    public boolean insert(String toDoItem){
////        ContentValues contentValues=new ContentValues();
////        contentValues.put(COLUMN_TODO,toDoItem);
////
////        return sqLliteDatabase.insert(TABLE_CODE,null,contentValues)>0;
////    }
//
//    public boolean insert(String ppid,String hascode,String device_type){
//        ContentValues contentValues=new ContentValues();
//        contentValues.put(COLUMN_PPID , ppid);
//        contentValues.put(COLUMN_CODE , hascode);
//        contentValues.put(COLUMN_TYPE , device_type);
//
//        return sqLliteDatabase.insert(TABLE_CODE,null,contentValues)>0;
//    }
//
//    //Will be used in the content provider
//    public long insert(ContentValues contentValues){
//        return sqLliteDatabase.insert(TABLE_CODE,null,contentValues);
//    }
//
//    public boolean delete(int taskId){
//       return sqLliteDatabase.delete(TABLE_CODE, COLUMN_PPID+" = "+taskId,null)>0;
//    }
//
//    //Will be used by the provider
//    public int delete(String whereClause, String[] whereValues){
//        return sqLliteDatabase.delete(TABLE_CODE,whereClause,whereValues);
//    }
//    public int delete(){
////        db.execSQL(" DROP TABLE IF EXISTS " +TABLE_CODE);
//        return sqLliteDatabase.delete(TABLE_CODE,null,null);
//    }
//
//    public boolean modify(int ppid,String hascode,String device_type){
//        ContentValues contentValues=new ContentValues();
//        contentValues.put(COLUMN_CODE , hascode);
//        contentValues.put(COLUMN_TYPE , device_type);
//
//       return sqLliteDatabase.update(TABLE_CODE,contentValues, COLUMN_PPID+" = "+ppid, null)>0;
//    }
//
//    //Will be used in the content provider
//    public int update(ContentValues contentValues, String s, String[] strings){
//        return sqLliteDatabase.update(TABLE_CODE,contentValues, s,strings);
//    }
//
//    public List<ToDo> getAllToDos(){
//        List<ToDo> toDoList=new ArrayList<ToDo>();
//
//        Cursor cursor=sqLliteDatabase.query(TABLE_CODE,new String[]{COLUMN_ID,COLUMN_PPID,COLUMN_CODE,COLUMN_TYPE},null,null,null,null,null,null);
//
//        if(cursor!=null &cursor.getCount()>0){
//            while(cursor.moveToNext()){
//                ToDo toDo=new ToDo(cursor.getString(0),cursor.getString(1), cursor.getString(2));
//                toDoList.add(toDo);
//
//            }
//        }
//        cursor.close();
//        return toDoList;
//    }
//
//}
