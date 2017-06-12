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
import android.view.SubMenu;
import android.widget.Toast;

import java.util.List;

import io.erfan.llogger.App;
import io.erfan.llogger.PreferenceManager;
import io.erfan.llogger.R;
import io.erfan.llogger.model.DaoSession;
import io.erfan.llogger.model.Driver;
import io.erfan.llogger.model.DriverDao;

public class RootActivity extends AppCompatActivity {
    private BottomNavigationView mNavigation;
    private FragmentManager mFragmentManager;
    private List<Driver> mDrivers;

    public enum Pages {HOME, STATS, HISTORY}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check if this is the first launching the app
        PreferenceManager prefMan = new PreferenceManager(this);
        if (prefMan.isFirstLaunch()) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(this, 0, 0);
            startActivity(intent, options.toBundle());
            return;
        }

        setContentView(R.layout.activity_root);

        // Home fragment will be added by default
        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.replace(R.id.root_fragment, new HomeFragment());
        ft.commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNavigation = (BottomNavigationView) findViewById(R.id.navigation);
        mNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                // switch to fragment based on the ID
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        switchFragment(Pages.HOME);
                        break;
                    case R.id.nav_stats:
                        switchFragment(Pages.STATS);
                        break;
                    case R.id.nav_history:
                        switchFragment(Pages.HISTORY);
                }
                return true;
            }
        });

        // get a list of mDrivers
        DaoSession daoSession = ((App) getApplication()).getDaoSession();
        DriverDao driverDao = daoSession.getDriverDao();
        mDrivers = driverDao.loadAll();

        if (prefMan.getUser() == null) {
            // realistically this should never happen (since they are required to create a
            // driver in the welcome screen) but just in case
            if (mDrivers.isEmpty()) {
                // create a driver so we have a driver to work with
                Driver driver = new Driver();
                driver.setName("Default");
                driverDao.insert(driver);

                mDrivers = driverDao.loadAll();

                Toast.makeText(this,
                        "A default driver has been created",
                        Toast.LENGTH_LONG).show();
            }

            // default the current user to the first user in the database
            Long firstID = driverDao.getKey(mDrivers.get(0));
            prefMan.setUser(firstID);
        }

        // test only
        Driver driver = driverDao.load(prefMan.getUser());
        Toast.makeText(this, String.format("Logged in as %s", driver.getName()),
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_root, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // add driver options
        if (!mDrivers.isEmpty()) {
            SubMenu subMenu = menu.findItem(R.id.action_change_driver).getSubMenu();
            subMenu.clear();

            // id will the index in the array
            int id = 0;
            for (Driver driver : mDrivers) {
                // add a menu item and advance id by one
                subMenu.add(Menu.NONE, id++, Menu.NONE, driver.getName());
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // switch based on item ID
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:

                return true;

            case R.id.action_edit_assets:
                Intent intent = new Intent(this, AddAssetsActivity.class);
                startActivity(intent);
                return true;
        }

        if (id < mDrivers.size()) {
            // setup context
            PreferenceManager prefMan = new PreferenceManager(this);
            DaoSession daoSession = ((App) getApplication()).getDaoSession();
            DriverDao driverDao = daoSession.getDriverDao();

            // change the driver
            Long driverID = driverDao.getKey(mDrivers.get(id));
            prefMan.setUser(driverID);
            // restart the activity to take effect
            recreate();
        }

        return super.onOptionsItemSelected(item);
    }


    // allows children fragment to change the current fragment
    public void switchFragment(Pages fragment, boolean updateNav) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);

        Integer destRes = null;
        switch(fragment) {
            case HOME:
                ft.replace(R.id.root_fragment, new HomeFragment());
                destRes = R.id.nav_home;
                break;
            case STATS:
                ft.replace(R.id.root_fragment, new StatsFragment());
                destRes = R.id.nav_stats;
                break;
            case HISTORY:
                ft.replace(R.id.root_fragment, new HistoryFragment());
                destRes = R.id.nav_history;
                break;
        }
        ft.commit();

        if (updateNav && destRes != null) {
            mNavigation.setSelectedItemId(destRes);
        }
    }
    private void switchFragment(Pages fragment) {
        switchFragment(fragment, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // update the list of drivers
        mDrivers = ((App) getApplication()).getDaoSession().getDriverDao().loadAll();
    }
}
