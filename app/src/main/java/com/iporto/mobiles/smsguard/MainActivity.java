package com.iporto.mobiles.smsguard;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements DrawerFrag.OnDrawerItem{

    Toolbar toolbar;
    int perm_flag=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        DrawerFrag drawerFrag=(DrawerFrag)getSupportFragmentManager().findFragmentById(R.id.fragment_dr_nv);;
        drawerFrag.setUp((DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        //Android 6
        requestPerm();

        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        HomeFragment obj=new HomeFragment();
        ft.replace(R.id.container, obj);
        ft.commit();
    }

    @Override
    public void selectItem(int x) {

        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();

        if(x==1)
        {
            HomeFragment obj=new HomeFragment();
            ft.replace(R.id.container, obj);
            ft.commit();
        }
        else if(x==2)
        {
            SettingFragment obj=new SettingFragment();
            ft.replace(R.id.container, obj);
            ft.commit();
        }

    }

    //request persmissions
    public void requestPerm()
    {
        String[] perms={Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CALL_PHONE
                };

        ActivityCompat.requestPermissions(this,perms,perm_flag);
    }

    //Permissions Result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if(requestCode==perm_flag)
        {
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED
                    && grantResults[1]== PackageManager.PERMISSION_GRANTED)
                enableGPS();
        }
    }

    //enable gps services
    void enableGPS()
    {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {

            if(Build.VERSION.SDK_INT<19) {
                Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
                intent.putExtra("enabled", true);
                sendBroadcast(intent);
            }
            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(i);
        }
        else
        {
            Toast.makeText(this,"Done",Toast.LENGTH_LONG).show();
        }

    }

}
