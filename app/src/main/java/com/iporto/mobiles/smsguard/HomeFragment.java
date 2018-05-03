package com.iporto.mobiles.smsguard;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

public class HomeFragment extends Fragment{

    SwitchCompat autoreply_switch;
    MyDatabase mdb;
    SQLiteDatabase db;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_home, container, false);
        autoreply_switch=(SwitchCompat)v.findViewById(R.id.autoreply_switch);

        //Database Object
        mdb=new MyDatabase(getActivity());

        //Switch enable / disable
        autoreply_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    ViewDialog alert = new ViewDialog();
                    alert.setIarMessage(new ViewDialog.IARMessage() {
                        @Override
                        public void isAR(boolean x) {
                            if (x == true) {
                                enbaleAutoReply("1");
                                autoreply_switch.setText("On");
                                Toast.makeText(getActivity(), "The auto reply service is on", Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                autoreply_switch.setChecked(false);
                                enbaleAutoReply("0");
                                autoreply_switch.setText("Off");
                                Toast.makeText(getActivity(), "The auto reply service is off", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    alert.showDialog(getActivity());

                } else {
                    enbaleAutoReply("0");
                    autoreply_switch.setText("Off");
                    Toast.makeText(getActivity(), "The auto reply service is off", Toast.LENGTH_LONG).show();
                }
            }
        });
        return v;
    }


    //Enable Auto Reply
    void enbaleAutoReply(String ar)
    {
        db=mdb.getWritableDatabase();
        db.execSQL("update user_password set auto_reply=? where id=1", new String[]{ar});
    }



}
