package com.example.android.sunshine.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.logging.Logger;


public class MainActivity extends ActionBarActivity {
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "ON CREATE");
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
        }
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
        }

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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String location = preferences.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));

//        Uri uri = Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q",location).build();  //this is removing the 0,0 for some reason. Which messed up the intent
        Uri uri = Uri.parse("geo:0,0?q=" + location);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

//        intent.setData(uri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);

        } else {
            showToast(this, "No app to show location.");
        }
    }

    private void showToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
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
}