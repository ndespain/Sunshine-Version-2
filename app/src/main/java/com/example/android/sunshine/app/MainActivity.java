package com.example.android.sunshine.app;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity implements ForecastFragment.Callback {
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    public static final String LOCATION_KEY = "location";
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private String mLocation;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "ON CREATE");
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.weather_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

        // If I do this then calling onLocationChanged in onResume below won't work because onCreate is called when coming back from the SettingActivity,
        // so mLocation gets updated here before onResume gets called.
//        mLocation = Utility.getPreferredLocation(this);
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        outState.putString(LOCATION_KEY, mLocation);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mLocation = savedInstanceState.getString(LOCATION_KEY);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings :
                Intent showSettings = new Intent(this, SettingsActivity.class);
                startActivity(showSettings);
                return true;
            case R.id.action_show_location:
                showLocationOnMap();
                return true;
            default:
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void showLocationOnMap() {
//        String location = Utility.getPreferredLocation(this);

        Uri uri = Uri.parse("geo:0,0?q=" + mLocation);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);

        } else {
            Utility.showToast(this, "No app to show location.");
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "ON DESTROY");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "ON PAUSE");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "ON RESTART");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "ON RESUME");

        String location = Utility.getPreferredLocation(this);
        if (location != null && !location.equals(mLocation)) {
            mLocation = location;
            final ForecastFragment forecastFragment = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            if (forecastFragment != null) {
                forecastFragment.onLocationChanged(mLocation);
            }

            final DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if (detailFragment != null) {
                detailFragment.onLocationChanged(mLocation);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "ON START");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "ON STOP");
    }

    @Override
    public void onItemSelected(final Uri forecastUri) {

        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.FORECASTURI, forecastUri);

            DetailFragment df = new DetailFragment();
            df.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, df, DETAILFRAGMENT_TAG)
                    .commit();


        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.setData(forecastUri);
            startActivity(intent);
        }
    }
}
