package io.erfan.llogger.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import io.erfan.llogger.R;

public class RootActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);

        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.add(R.id.root_fragment, new HomeFragment());
        ft.commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // switch to fragment based on the ID
        switch (item.getItemId()) {
            case R.id.nav_home:
                switch_fragment(Fragments.HOME);
                break;
            case R.id.nav_stats:
                switch_fragment(Fragments.STATS);
                break;
            case R.id.nav_history:
                switch_fragment(Fragments.HISTORY);
        }
        return true;
    }

    public enum Fragments {HOME, STATS, HISTORY}

    // allows children fragment to change the current fragment
    public void switch_fragment(Fragments fragment) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        switch(fragment) {
            case HOME:
                ft.replace(R.id.root_fragment, new HomeFragment());
                break;
            case STATS:
                ft.replace(R.id.root_fragment, new StatsFragment());
                break;
            case HISTORY:
                ft.replace(R.id.root_fragment, new HistoryFragment());
                break;
        }
        ft.commit();
    }
}
