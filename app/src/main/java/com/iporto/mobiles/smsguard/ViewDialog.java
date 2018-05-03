package com.iporto.mobiles.smsguard;

import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import customfonts.MyEditText;
import customfonts.MyTextView;

public class ViewDialog {

    MyDatabase mdb;
    SQLiteDatabase db;
    ArrayList<String> list_ids=new ArrayList<>();
    ArrayList<String> list_msgs=new ArrayList<>();
    ArrayAdapter<String> adapter;
    ListView lvARMessages;
    IARMessage iarMessage;
    MyEditText ar_message;

    public void showDialog(final Activity activity){

        //Dialog Declaration
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog);
        //Database Object
        mdb=new MyDatabase(activity);
        //List Declaration
        lvARMessages=(ListView)dialog.findViewById(R.id.listView);
        MyTextView btn_cancel=(MyTextView)dialog.findViewById(R.id.btn_cancel);
        MyTextView btn_add=(MyTextView)dialog.findViewById(R.id.btn_add);
        ar_message=(MyEditText)dialog.findViewById(R.id.ar_message);

        //click cancel
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iarMessage.isAR(false);
                dialog.dismiss();
            }
        });

        //Add Click
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!ar_message.getText().toString().equals("")){
                db=mdb.getWritableDatabase();
                Cursor cursor=db.rawQuery("select max(msg_id) from auto_reply_msg",null);
                cursor.moveToFirst();
                int intMSG_ID=cursor.getInt(0);
                intMSG_ID++;
                db.execSQL("insert into auto_reply_msg values(?,?);", new Object[]{intMSG_ID, ar_message.getText().toString()});
                Toast.makeText(activity,"Saved",Toast.LENGTH_LONG).show();
                ar_message.setText("");
                //Fill List View
                fillARMessages();
                adapter=new ArrayAdapter<String>(activity,R.layout.custom_raw,list_msgs);
                lvARMessages.setAdapter(adapter);}
            }
        });

        //Fill List View
       fillARMessages();
        adapter=new ArrayAdapter<String>(activity,R.layout.custom_raw,list_msgs);
        lvARMessages.setAdapter(adapter);

        //List View Click
        lvARMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                db=mdb.getWritableDatabase();
                db.execSQL("update user_password set auto_reply_msg_id=?",new Object[]{list_ids.get(position)});
                iarMessage.isAR(true);
                dialog.dismiss();
            }
        });

        //List View Remove
        lvARMessages.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                db=mdb.getWritableDatabase();
                db.execSQL("delete from auto_reply_msg where msg_id=?",new String[]{list_ids.get(position)});
                adapter.remove(adapter.getItem(position));

                return true;
            }
        });

        //Show
        dialog.show();
    }

    //fill ar message
    void fillARMessages()
    {
        list_ids.clear();
        list_msgs.clear();
        db=mdb.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from auto_reply_msg",null);
        cursor.moveToFirst();
        while (cursor.isAfterLast()==false) {
            list_ids.add(cursor.getString(0));
            list_msgs.add(cursor.getString(1));
            cursor.moveToNext();
        }

    }

    //Interface
    public static interface IARMessage
    {
        public void isAR(boolean x);
    }

    public void setIarMessage(IARMessage iarMessage) {
        this.iarMessage = iarMessage;
    }

    public IARMessage getIarMessage() {
        return iarMessage;
    }
}
