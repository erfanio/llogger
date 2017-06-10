package io.erfan.llogger.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import io.erfan.llogger.PreferenceManager;
import io.erfan.llogger.R;

public class RootActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager prefMan = new PreferenceManager(this);
        if (prefMan.isFirstLaunch()) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(this, 0, 0);
            startActivity(intent, options.toBundle());
            return;
        }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_root, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // switch based on item ID
        switch (item.getItemId()) {
            case R.id.action_settings:

                return true;

            case R.id.action_edit_assets:
                Intent intent = new Intent(this, AddAssetsActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
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
