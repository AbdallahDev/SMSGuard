package com.iporto.mobiles.smsguard;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import customfonts.MyEditText;
import customfonts.MyTextView;

public class SettingFragment extends Fragment {

    MyDatabase mdb;
    SQLiteDatabase db;
    MyEditText old_password,new_password,confirm_password;
    MyTextView btn_save;

    public SettingFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v= inflater.inflate(R.layout.fragment_setting, container, false);
        //Declaration
        old_password=(MyEditText)v.findViewById(R.id.old_password);
        new_password=(MyEditText)v.findViewById(R.id.new_password);
        confirm_password=(MyEditText)v.findViewById(R.id.confirm_new_password);
        btn_save=(MyTextView)v.findViewById(R.id.btn_change_password);

        //Database Object
        mdb=new MyDatabase(getActivity());


        //Change Password Click
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOldPassword(old_password.getText().toString()) == false)
                    Toast.makeText(getActivity(), "Please check your old password", Toast.LENGTH_LONG).show();
                else {
                    if (new_password.getText().toString().equals(confirm_password.getText().toString())) {
                        if (new_password.getText().toString().length() == 4) {
                            saveNewPassword(new_password.getText().toString());
                            Toast.makeText(getActivity(), "Password Changed", Toast.LENGTH_LONG).show();
                            old_password.setText("");
                            new_password.setText("");
                            confirm_password.setText("");
                        } else
                            Toast.makeText(getActivity(), "The password should be 4 digits", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });



        return v;
    }

    //Check the old password
    boolean isOldPassword(String strOldPassword)
    {
        db=mdb.getReadableDatabase();
        Cursor cur=db.rawQuery("select password from user_password where id=1",null);
        cur.moveToFirst();
        if(strOldPassword.equals(cur.getString(0)))
            return true;
        else
            return false;
    }

    //Save the new password
    void saveNewPassword(String strNewPassword)
    {
        db=mdb.getWritableDatabase();
        db.execSQL("update user_password set password=? where id=1", new String[]{strNewPassword});
    }
}
