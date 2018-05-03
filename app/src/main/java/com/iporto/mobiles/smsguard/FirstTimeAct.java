package com.iporto.mobiles.smsguard;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import customfonts.MyEditText;
import customfonts.MyTextView;

public class FirstTimeAct extends AppCompatActivity {

    MyEditText password,confirm_password;
    MyTextView btn_save_password;
    MyDatabase mdb;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time);

        password=(MyEditText)findViewById(R.id.password);
        confirm_password=(MyEditText)findViewById(R.id.confirm_password);
        btn_save_password=(MyTextView)findViewById(R.id.btn_save_password);
        mdb=new MyDatabase(this);

        //check first time
        if(checkFirstTime()==false) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        //Click Event
        btn_save_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkEmpty())
                {
                    if(checkPasswordMatch())
                    {
                        if (password.getText().toString().length() != 4)
                            Toast.makeText(FirstTimeAct.this,"The password should be 4 digits",Toast.LENGTH_LONG).show();
                        else{
                        savePassword();
                        startActivity(new Intent(FirstTimeAct.this, MainActivity.class));
                        finish();}
                    }
                    else
                    {
                        Toast.makeText(FirstTimeAct.this,"Password and confirm password not match !!",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(FirstTimeAct.this,"Please enter password and confirm !!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //check empty
    boolean checkEmpty()
    {
        if(password.getText().toString().equals("") || confirm_password.getText().toString().equals(""))
            return false;
        else
            return true;
    }

    //check password match
    boolean checkPasswordMatch()
    {
        if(password.getText().toString().equals(confirm_password.getText().toString()))
            return true;
        else
            return false;
    }

    //save password
    void savePassword()
    {
        db=mdb.getWritableDatabase();
        db.execSQL("insert into user_password values(1,?,0,1);", new String[]{password.getText().toString()});
    }

    //check the first time
    boolean checkFirstTime()
    {
        db=mdb.getReadableDatabase();
        Cursor cur=db.rawQuery("select password from user_password where id=1",null);
        if(cur.getCount()==0)
            return true;
        else
            return false;
    }
}
