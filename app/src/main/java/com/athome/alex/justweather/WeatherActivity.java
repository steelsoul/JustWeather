package com.athome.alex.justweather;

import android.content.res.AssetManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WeatherActivity extends FragmentActivity {
    // Pages number
    private static final int NUM_PAGES = 2;
    private static final String API_KEY_NAME = "api_key.txt";
    private static final String DEBUG_TAG = "WeatherActivity";
    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_weather);
        RemoteFetch.SetAPIKey(TryReadAPIKeyFromAsset());
        SetupUI();
    }

    private void SetupUI() {
        mPager = findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }

    private String TryReadAPIKeyFromAsset() {
        AssetManager assetManager = getApplicationContext().getAssets();
        return ReadApiKeyFromAsset(assetManager);
     }

    private String ReadApiKeyFromAsset(AssetManager assetManager) {
        try {
            final InputStream inputStream = assetManager.open(API_KEY_NAME);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            return ReadFirstLine(reader);
        } catch (IOException ex) {
            Log.e(DEBUG_TAG, "No api key found in asset");
        }
        return null;
    }

    private String ReadFirstLine(BufferedReader reader) throws IOException {
        return reader.readLine();
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }


    /**
     * A simple pager adapter that represents 2 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new WeatherFragment();
                case 1:
                    return new FragmentForecastGraph();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

}
