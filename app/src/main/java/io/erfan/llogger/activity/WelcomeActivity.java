package io.erfan.llogger.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.erfan.llogger.R;
import io.erfan.llogger.activity.welcome.WelcomeDriverFragment;
import io.erfan.llogger.activity.welcome.WelcomeFragment;

public class WelcomeActivity extends AppCompatActivity {
    FragmentManager mFragmentManager;
    int mPosition;

    // create a list of fragments
    List<Fragment> pages = Arrays.asList(new WelcomeFragment(), new WelcomeDriverFragment());

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
