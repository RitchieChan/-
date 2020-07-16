package com.shashank.platform.loginui;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME="Housekeeper.db";
    private static final int DB_VERSION=1;
    public DBHelper(@Nullable Context context) {
        super(context,DB_NAME,null,DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE ARTICLE(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                +"IMAGEPATH TEXT,"
                +"TIME TEXT,"
                +"NAME TEXT,"
                +"SORT TEXT,"
                +"PRICE TEXT,"
                +"POSITION TEXT,"
                +"TABLE1 TEXT,"
                +"CODEVALUE TEXT );"
        );
        db.execSQL("CREATE TABLE SIGN_UP(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ACCOUNT TEXT," +
                "PASSWORD TEXT," +
                "USERNAME TEXT," +
                "MOTTO TEXT," +
                "HEADIMAGE TEXT);"
        );
//需要存入数据库的值有：codevalue、time、name、category、price、position、table、imagePath
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }


}
