package io.erfan.llogger.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Arrays;
import java.util.List;

import io.erfan.llogger.PrefMan;
import io.erfan.llogger.R;
import io.erfan.llogger.activity.welcome.WelcomeCarFragment;
import io.erfan.llogger.activity.welcome.WelcomeDriverFragment;
import io.erfan.llogger.activity.welcome.WelcomeFragment;
import io.erfan.llogger.activity.welcome.WelcomeSupervisorFragment;

public class WelcomeActivity extends AppCompatActivity {
    private FragmentManager mFragmentManager;
    private int mPosition;

    // create a list of fragments
    private List<Fragment> pages = Arrays.asList(new WelcomeFragment(),
            new WelcomeDriverFragment(), new WelcomeSupervisorFragment(),
            new WelcomeCarFragment());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mFragmentManager = getSupportFragmentManager();

        mPosition = 0;
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.add(R.id.welcome_fragment, pages.get(0));
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if (mPosition == 0) {
            // If the user is currently looking at the first page, allow the system to handle the Back button.
            super.onBackPressed();
        } else {
            changePage(mPosition - 1);
        }
    }

    public void nextPage() {
        if (mPosition + 1 < pages.size()) {
            // Go to next page
            changePage(mPosition + 1);
        }
        // do nothing
    }

    public void done() {
        PrefMan prefMan = new PrefMan(this);
        prefMan.setFirstLaunch(false);

        Intent intent = new Intent(this, RootActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out);
        startActivity(intent, options.toBundle());
    }

    private void changePage(int position) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        if (mPosition > position) {
            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        } else {
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        ft.replace(R.id.welcome_fragment, pages.get(position));
        ft.commit();
        mPosition = position;
    }
}
