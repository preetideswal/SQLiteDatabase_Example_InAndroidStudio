package com.example.databaseexample;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.*;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteAbortException;
import android.util.Log;
import java.io.File;
import android.widget.*;
public class MainActivity extends AppCompatActivity {
    SQLiteDatabase db;
    TextView textmsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textmsg=(TextView)findViewById(R.id.textsc);
        try{
            openDatabase();
            insertSomeDbData();
            useRawQueryShowAll();
            useRawQuery1();
            showTable("Data1");
            db.close();
            textmsg.append("\nAll Done");
        } catch (Exception e){
            textmsg.append("\nError onCreate:"+e.getMessage());
            finish();
        }
    }
   private void insertSomeDbData(){
        db.beginTransaction();
        try{
            db.execSQL("Create table Data1 ( "
                    + " recId integer PRIMARY KEY autoincrement,"
                    + "Name text ,"+"Phone text);");
            db.setTransactionSuccessful();
            textmsg.append("\n-insertSomeDbdata - Table was created");
        }catch (SQLiteException e1){
            textmsg.append("\n Error insertSomeDbdata: "+ e1.getMessage());
            finish();
        }finally {
            db.endTransaction();
        }
        db.beginTransaction();
        try {
            db.execSQL("insert into Data1(name , phone)"
                    + "values ('AAA', '6578');");
            db.execSQL("insert into Data1(name , phone)"
                    + "values ('BBB', '6719');");
            db.execSQL("insert into Data1(name , phone)"
                    + "values ('CCC', '1234');");
            db.setTransactionSuccessful();
            textmsg.append("\n-insertSomeDbdata - 3 rec. were insert");
        }catch (SQLiteException e2){
            textmsg.append("\n Error insertSomeDbdata: "+ e2.getMessage());
        } finally {
            db.endTransaction();
        }
    }
    private void openDatabase(){
        try{
            File storagePath =getApplication().getFilesDir();
            String myDbPath=storagePath+"/"+"Data1";
            textmsg.setText("DB Path: "+myDbPath);
            db=SQLiteDatabase.openDatabase(myDbPath,null,
                    SQLiteDatabase.CREATE_IF_NECESSARY);
            textmsg.append("\n-openDatabase- DB was opened");
        } catch (SQLiteException e){
            textmsg.append("\n Error openDatabase: "+e.getMessage());
            finish();
        }
    }
   private void useRawQueryShowAll(){
        try {
            String mySQL ="select * from Data1";
            Cursor c1 =db.rawQuery(mySQL, null);
            textmsg.append("\n-useRawQuaryShowAll"+ showCursor(c1));
        }catch (Exception e){
            textmsg.append("\n error"+e.getMessage());
        }
    }
    private String showCursor(Cursor cursor) {
        cursor.moveToPosition(-1);
        String cursorData = "\nCursor:[";
        try {
            String[] colName = cursor.getColumnNames();
            for (int i = 0; i < colName.length; i++) {
                String dataType = getColumnType(cursor, i);
                cursorData += colName[i] + dataType;
                if (i < colName.length - 1) {
                    cursorData += ", ";
                }
            }
        } catch (Exception e) {
            Log.e("<<SCHEMA>>", e.getMessage());
        }
        cursorData += "]";
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            String cursorRow = "\n[";
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                cursorRow += cursor.getString(i);
                if (i < cursor.getColumnCount() - 1)
                    cursorRow += ", ";
            }
            cursorData += cursorRow + "]";
        }
        return cursorData + "\n";
    }
    private String getColumnType( Cursor cursor , int i){
        try {
            cursor.moveToFirst();
            int result =cursor.getType(i);
            String[] types ={":NULL ",":INT",":FLOAT",":STR",":BLOB",":UNK"};
            cursor.moveToPosition(-1);
            return types[result];
        }catch (Exception e){
            return " ";
        }
    }
    private  void useRawQuery1(){
        try {
            String mySQL="select * from Data1";
            Cursor c1=db.rawQuery(mySQL,null);
            c1.moveToFirst();
            int index=c1.getColumnIndex("recId");
            int theRecId=c1.getInt(index);
            textmsg.append("\nrawQuery"+theRecId);
            textmsg.append("\nrawQuery"+showCursor(c1));
        }catch (Exception e){
            textmsg.append("\nError"+e.getMessage());
        }
    }
    private void showTable(String tableName){
        try {
            String sql="select * from"+tableName;
            Cursor c=db.rawQuery(sql,null);
            textmsg.append("\nshowTable: "+tableName +showCursor(c));
        }catch (Exception e){
            textmsg.append(("\n/Error "+e.getMessage()));
        }
    }
}