package io.erfan.llogger.activity;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import java.util.Arrays;
import java.util.List;

import io.erfan.llogger.R;
import io.erfan.llogger.activity.asset.NewCarFragment;
import io.erfan.llogger.activity.asset.NewDriverFragment;
import io.erfan.llogger.activity.asset.NewSupervisorFragment;

public class AddAssetsActivity extends AppCompatActivity {
    private static List<Fragment> PAGES = Arrays.asList(new NewDriverFragment(),
            new NewSupervisorFragment(), new NewCarFragment());
    private static List<Integer> PAGES_TITLE = Arrays.asList(R.string.welcome_add_driver,
            R.string.welcome_add_supervisor, R.string.welcome_add_car);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assets);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PAGES.get(position);
        }

        @Override
        public int getCount() {
            return PAGES.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(PAGES_TITLE.get(position));
        }
    }
}
