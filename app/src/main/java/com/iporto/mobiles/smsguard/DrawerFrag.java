package com.iporto.mobiles.smsguard;


import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class DrawerFrag extends Fragment {

    ActionBarDrawerToggle mDrawerToggle;
    NavigationView nv;
    OnDrawerItem di;
    DrawerLayout drawerLayout;

    public DrawerFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_drawer, container, false);
        nv=(NavigationView)v.findViewById(R.id.navigation_view);

        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                item.setChecked(true);

                if(item.getItemId()==R.id.item1)
                {
                    di.selectItem(1);

                }
                if(item.getItemId()==R.id.item2)
                {
                    di.selectItem(2);

                }
                if(item.getItemId()==R.id.item3)
                {
                    di.selectItem(3);

                }

                drawerLayout.closeDrawers();
                return false;
            }
        });

        return v;

    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        di=(OnDrawerItem)activity;
    }

    public interface OnDrawerItem
    {
        public void selectItem(int x);
    }

    public void setUp(DrawerLayout drawerLayout, final Toolbar toolbar) {

        this.drawerLayout=drawerLayout;

        mDrawerToggle=new ActionBarDrawerToggle(getActivity(),drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close)
        {

        };
        drawerLayout.setDrawerListener(mDrawerToggle);

        //mDrawerLayout.post(new Runnable() {
        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

}