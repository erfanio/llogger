package io.erfan.llogger.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.Arrays;
import java.util.List;

import io.erfan.llogger.R;
import io.erfan.llogger.activity.asset.NewCarFragment;
import io.erfan.llogger.activity.asset.NewDriverFragment;
import io.erfan.llogger.activity.asset.NewSupervisorFragment;

public class AddAssetsActivity extends AppCompatActivity {
    // The list of fragments to use
    private static List<Fragment> PAGES = Arrays.asList(new NewDriverFragment(),
            new NewSupervisorFragment(), new NewCarFragment());
    // list of corresponding titles for each tab
    private static List<Integer> PAGES_TITLE = Arrays.asList(R.string.drivers,
            R.string.supervisors, R.string.cars);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assets);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // setup the pager adapter (will use the static variable on this class to setup fragments)
        final SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // default activity title is for the first tab
        toolbar.setTitle(getString(R.string.title_activity_add_assets_param,
                sectionsPagerAdapter.getPageTitle(0)));

        // Set up the ViewPager with the pager adapter we created.
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // change the title based on the current tab
                toolbar.setTitle(getString(R.string.title_activity_add_assets_param,
                        sectionsPagerAdapter.getPageTitle(position)));
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
