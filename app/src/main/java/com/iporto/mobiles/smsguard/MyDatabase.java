package com.iporto.mobiles.smsguard;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 11/09/2016.
 */
public class MyDatabase extends SQLiteOpenHelper {

    public MyDatabase(Context context) {
        super(context, "MCAW.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table user_password(id number(2),password varchar(4),auto_reply number(1),auto_reply_msg_id number(1));");
        db.execSQL("Create table auto_reply_msg(msg_id number(2),message varchar(255));");
        //db.execSQL("insert into user_password values(1,'0000',0,1);");
        db.execSQL("insert into auto_reply_msg values(1,'I am busy now I will call you later');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
