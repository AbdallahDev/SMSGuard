package com.iporto.mobiles.smsguard;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;


public class SMSReceiver extends BroadcastReceiver {
    MyDatabase mdb;
    SQLiteDatabase db;
    String strResult="";
    boolean blnReacher=false;
    LocationManager manager;
    LocationListener listener;

    @Override
    public void onReceive(final Context context, Intent intent) {
        manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String messageReceived = "";
        if (bundle != null) {
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                messageReceived += msgs[i].getMessageBody().toString();
                messageReceived += "\n";
            }

            mdb = new MyDatabase(context);
            String strPassword = messageReceived.substring(1, 5);
            if (isOldPassword(strPassword) == true) {
                String strContact = messageReceived.substring(messageReceived.lastIndexOf("#") + 1);
                strContact = strContact.substring(0, strContact.length() - 1);
                //GPS
                if (strContact.equals("gps")) {
                    blnReacher = true;
                    final SmsMessage[] finalMsgs = msgs;
                    listener = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {

                            if (blnReacher == true) {
                                strResult = "https://www.google.com/maps/@" + location.getLatitude() + "," + location.getLongitude() + ",20z";
                                SmsManager manager = SmsManager.getDefault();
                                manager.sendTextMessage(finalMsgs[0].getOriginatingAddress(), null, strResult, null, null);
                            }
                            blnReacher = false;
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }

                    };

                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
                }
                else {
                    //Contact
                    String search = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like '" + strContact + "%'";
                    Cursor cur = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, search, null, null);
                    if(cur.getCount()==0)
                        strResult="Not Found";
                    else {
                        while (cur.moveToNext()) {
                            String n = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            String m = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            strResult += n + "-" + m + "\n";
                        }
                    }
                        Toast.makeText(context, strResult, Toast.LENGTH_LONG).show();
                        SmsManager manager = SmsManager.getDefault();
                        manager.sendTextMessage(msgs[0].getOriginatingAddress(), null, strResult, null, null);

                }

            }
        }
    }

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
}
