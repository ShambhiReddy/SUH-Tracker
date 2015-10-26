package com.suh.itboy.tracker;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.suh.itboy.tracker.Adapter.ViewPagerAdapter;
import com.suh.itboy.tracker.Fragment.EventListFragment;
import com.suh.itboy.tracker.Fragment.GeofenceListFragment;
import com.suh.itboy.tracker.Provider.Contract.AppContract;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    GeofenceListFragment geofenceListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        geofenceListFragment = new GeofenceListFragment();

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(geofenceListFragment, "Geofence List");
        viewPagerAdapter.addFragment(new EventListFragment(), "Event List");
        viewPager.setAdapter(viewPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void addNewGeofence(View view) {
        geofenceListFragment.addNewGeofence(view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                getContentResolver().delete(AppContract.GeofenceEntry.CONTENT_URI, null, null);
        }
        return super.onOptionsItemSelected(item);
    }
}
