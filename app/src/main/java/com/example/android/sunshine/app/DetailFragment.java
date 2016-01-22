package com.example.android.sunshine.app;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final int DETAIL_LOADER_ID = 2;

    private static final String FORECAST_SHARE_HASHTAG = " Ta-da";
    public static String FORECASTURI = "forecastUri";
    @InjectView(R.id.detail_day)
    TextView mDetailDay;
    @InjectView(R.id.detail_date)
    TextView mDetailDate;
    @InjectView(R.id.detail_high_temp)
    TextView mDetailHighTemp;
    @InjectView(R.id.detail_low_temp)
    TextView mDetailLowTemp;
    @InjectView(R.id.detail_icon)
    ImageView mDetailIcon;
    @InjectView(R.id.detail_description)
    TextView mDetailDescription;
    private String mForecastStr;
    private Uri mForecastUri;
    private ShareActionProvider mShareActionProvider;

    private String[] DETAIL_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE
    };

    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;
    static final int COL_HUMIDITY = 9;
    static final int COL_WIND_SPEED = 10;
    static final int COL_DEGREES = 11;
    static final int COL_PRESSURE = 12;


    private TextView mWeatherSummaryTV;
    private TextView mWindSpeed;
    private TextView mAirPressure;
    private TextView mHumidity;


    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.inject(this, rootView);

//        Intent intent = getActivity().getIntent();

//        mWeatherSummaryTV = (TextView) rootView.findViewById(R.id.weatherSummary);
        mWindSpeed = (TextView) rootView.findViewById(R.id.detail_wind);
        mAirPressure = (TextView) rootView.findViewById(R.id.detail_pressure);
        mHumidity = (TextView) rootView.findViewById(R.id.detail_humidity);
        final Bundle args = getArguments();
        if (args != null) {
            mForecastUri = args.getParcelable(FORECASTURI);
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);

        MenuItem item = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        if (mForecastStr != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);
    }

    private Intent createShareForecastIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, mForecastStr + FORECAST_SHARE_HASHTAG);
        return intent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
//        Intent intent = getActivity().getIntent();
        if (mForecastUri == null) {
            return null;
        }

        return new CursorLoader(
                getActivity(),
                mForecastUri,
                DETAIL_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor) {
        if (cursor.moveToFirst()) {
            Context context = getActivity();
            boolean isMetric = Utility.isMetric(context);

            // Change this later when I have other icons.
            int weatherId = cursor.getInt(COL_WEATHER_CONDITION_ID);
            mDetailIcon.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

            long date = cursor.getLong(COL_WEATHER_DATE);
            mDetailDay.setText(Utility.getFriendlyDayString(context, date));
            mDetailDate.setText(Utility.getFormattedMonthDay(context, date));
            mDetailHighTemp.setText(Utility.formatTemperature(context, cursor.getDouble(COL_WEATHER_MAX_TEMP), isMetric));
            mDetailLowTemp.setText(Utility.formatTemperature(context, cursor.getDouble(COL_WEATHER_MIN_TEMP), isMetric));
            mDetailDescription.setText(cursor.getString(COL_WEATHER_DESC));
            mHumidity.setText(context.getString(R.string.format_humidity, cursor.getFloat(COL_HUMIDITY)));

            mWindSpeed.setText(Utility.getFormattedWind(context, cursor.getFloat(COL_WIND_SPEED), cursor.getFloat(COL_DEGREES)));
            mAirPressure.setText(context.getString(R.string.format_pressure, cursor.getFloat(COL_PRESSURE)));
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            }
        }

    }

    @Override
    public void onLoaderReset(final Loader<Cursor> loader) {

    }

    /*
        This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
        string.
     */
    private String convertCursorRowToUXFormat(Cursor cursor) {

        String highAndLow = Utility.formatHighLows(getActivity(),
                cursor.getDouble(COL_WEATHER_MAX_TEMP),
                cursor.getDouble(COL_WEATHER_MIN_TEMP));

        return Utility.formatDate(cursor.getLong(COL_WEATHER_DATE)) +
                " - " + cursor.getString(COL_WEATHER_DESC) +
                " - " + highAndLow;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    void onLocationChanged( String newLocation ) {
        // replace the uri, since the location has changed
        Uri uri = mForecastUri;
        if (null != uri) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            mForecastUri = updatedUri;
            getLoaderManager().restartLoader(DETAIL_LOADER_ID, null, this);
        }
    }

}
