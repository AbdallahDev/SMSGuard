package com.iporto.mobiles.smsguard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;

public class CallReceiver extends BroadcastReceiver {

    MyDatabase mdb;
    SQLiteDatabase db;
    public static String TAG="PhoneStateReceiver";
    boolean flag=false;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        mdb = new MyDatabase(context);
        if(isAREnabled().equals("1")) {
            flag=true;
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            PhoneStateListener listener = new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    if(flag==true) {
                        super.onCallStateChanged(state, incomingNumber);
                        rejectCall(context, intent);
                        Toast.makeText(context, isAREnabled() + "\n" + getMessage() + "\n" + incomingNumber, Toast.LENGTH_SHORT).show();
                        try {
                            SmsManager sms_manager = SmsManager.getDefault();
                            sms_manager.sendTextMessage(incomingNumber, null, getMessage(), null, null);
                        }
                        catch (Exception ex)
                        {
                            Log.d("SMS Gurad",ex.getMessage());
                        }
                    }
                    flag=false;
                }
            };
            manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        }

    }

    String getMessage()
    {
        db=mdb.getReadableDatabase();
        Cursor cur=db.rawQuery("select message from auto_reply_msg where msg_id=(select auto_reply_msg_id from user_password where id=1)",null);
        cur.moveToFirst();
        return cur.getString(0);
    }

    String isAREnabled()
    {
        db=mdb.getReadableDatabase();
        Cursor cur=db.rawQuery("select auto_reply from user_password where id=1",null);
        cur.moveToFirst();
        return cur.getString(0);
    }

    void rejectCall(Context context,Intent intent)
    {
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            Log.d(TAG, "PhoneStateReceiver**Call State=" + state);

            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                Log.d(TAG,"PhoneStateReceiver**Idle");
            } else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                // Incoming call
                String incomingNumber =
                        intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Log.d(TAG,"PhoneStateReceiver**Incoming call " + incomingNumber);

                if (!killCall(context)) { // Using the method defined earlier
                    Log.d(TAG,"PhoneStateReceiver **Unable to kill incoming call");
                }

            } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                Log.d(TAG,"PhoneStateReceiver **Offhook");
            }
        } else if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            // Outgoing call
            String outgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.d(TAG,"PhoneStateReceiver **Outgoing call " + outgoingNumber);

            setResultData(null); // Kills the outgoing call

        } else {
            Log.d(TAG,"PhoneStateReceiver **unexpected intent.action=" + intent.getAction());
        }
    }

    boolean killCall(Context context) {
        try {
            // Get the boring old TelephonyManager
            TelephonyManager telephonyManager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            // Get the getITelephony() method
            Class classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");

            // Ignore that the method is supposed to be private
            methodGetITelephony.setAccessible(true);

            // Invoke getITelephony() to get the ITelephony interface
            Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);

            // Get the endCall method from ITelephony
            Class telephonyInterfaceClass =
                    Class.forName(telephonyInterface.getClass().getName());
            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");

            // Invoke endCall()
            methodEndCall.invoke(telephonyInterface);

        } catch (Exception ex) { // Many things can go wrong with reflection calls
            Log.d(TAG,"PhoneStateReceiver **" + ex.toString());
            return false;
        }
        return true;
    }
}
