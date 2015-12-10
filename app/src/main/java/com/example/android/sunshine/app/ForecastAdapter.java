package com.example.android.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    public static final int VIEW_ITEM_TYPE_TODAY = 0;
    public static final int VIEW_ITEM_TYPE_FUTURE = 1;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

//    /**
//     * Prepare the weather high/lows for presentation.
//     */
//    private String formatHighLows(double high, double low) {
//        boolean isMetric = Utility.isMetric(mContext);
//        String highLowStr = Utility.formatTemperature(high, isMetric) + "/" + Utility.formatTemperature(low, isMetric);
//        return highLowStr;
//    }
//
    /*
        This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
        string.
     */
    private String convertCursorRowToUXFormat(Cursor cursor) {

        String highAndLow = Utility.formatHighLows(mContext,
                cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP),
                cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP));

        return Utility.formatDate(cursor.getLong(ForecastFragment.COL_WEATHER_DATE)) +
                " - " + cursor.getString(ForecastFragment.COL_WEATHER_DESC) +
                " - " + highAndLow;
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view;
        int viewType = getItemViewType(cursor.getPosition());
        if (viewType == VIEW_ITEM_TYPE_TODAY) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_forecast_today, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_forecast, parent, false);
        }

        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);

        return view;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(final int position) {
        if (position == 0) {
            return VIEW_ITEM_TYPE_TODAY;
        } else {
            return VIEW_ITEM_TYPE_FUTURE;
        }
    }

    /*
                This is where we fill-in the views with the contents of the cursor.
             */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder holder = (ViewHolder) view.getTag();

        holder.iconView.setImageResource(R.drawable.ic_launcher);

        holder.descriptionView.setText(cursor.getString(ForecastFragment.COL_WEATHER_DESC));
        holder.dateView.setText(Utility.getFriendlyDayString(mContext, cursor.getLong(ForecastFragment.COL_WEATHER_DATE)));

        boolean isMetric = Utility.isMetric(mContext);
        double minTemp = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        holder.minView.setText(Utility.formatTemperature(context, minTemp, isMetric));

        double maxTemp = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        holder.maxView.setText(Utility.formatTemperature(context, maxTemp, isMetric));

    }

    public static class ViewHolder {
        ImageView iconView;
        TextView descriptionView;
        TextView dateView;
        TextView minView;
        TextView maxView;

        public ViewHolder(View view) {
            this.dateView = (TextView)view.findViewById(R.id.list_item_date_textview);
            this.descriptionView = (TextView)view.findViewById(R.id.list_item_forecast_textview);;
            this.iconView = (ImageView) view.findViewById(R.id.list_item_icon);;
            this.maxView = (TextView) view.findViewById(R.id.list_item_high_textview);;
            this.minView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }
}