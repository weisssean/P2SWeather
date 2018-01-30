package com.p2s.weatherforecast.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.p2s.weatherforecast.R;
import com.p2s.weatherforecast.WeatherApp;
import com.p2s.weatherforecast.classes.BoseLocationHelper;
import com.p2s.weatherforecast.classes.ForecastResult;
import com.p2s.weatherforecast.fragments.CurrentWeatherFragment;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class ForecastActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ViewPager.OnPageChangeListener {

    private final static String TAG = "ForecastActivity";

    private CurrentWeatherFragment[] mCurrentWeatherFragments = new CurrentWeatherFragment[6];
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private BoseLocationHelper locationHelper;
    private ForecastResult mForecastResult;
    private Location mLastLocation;

    private String mCity = "";
    private String mSubtitle;

    @BindView(R.id.container)
    View app_container;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_forecast);
        ButterKnife.bind(this);

        locationHelper = new BoseLocationHelper(this);
        locationHelper.checkPermission();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        for (int i = 0; i < 6; i++) {
            mCurrentWeatherFragments[i] = CurrentWeatherFragment.newInstance();
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        mSubtitle = mSectionsPagerAdapter.getPageTitle(0).toString();
        setSubTitle(mSubtitle);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(onRefreshClickListener);

        // check availability of play services
        if (locationHelper.checkPlayServices()) {

            // Building the GoogleApi client
            locationHelper.buildGoogleApiClient();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mSubtitle != null)
            outState.putString("subtitle", mSubtitle);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mSubtitle = savedInstanceState.getString("subtitle");
        }
        if (mSubtitle != null) {
            setSubTitle(mSubtitle);
        }
    }

    private void getWeatherFromDarkSkyByLatLng(double lat, double lng) {
        Call<ForecastResult> call = WeatherApp.getWeatherService().forecast(lat, lng);
        call.enqueue(new Callback<ForecastResult>() {
            @Override
            public void onResponse(Response<ForecastResult> response, Retrofit retrofit) {
                int statusCode = response.code();
                Log.d(TAG, String.format("weather response code: %s", statusCode));
                mForecastResult = response.body();
                mCurrentWeatherFragments[0].setWeather(mForecastResult.getCurrently());
                for (int i = 1; i < mCurrentWeatherFragments.length; i++) {
                    mCurrentWeatherFragments[i].setWeather(mForecastResult.getDaily().getData()[i]);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "failed");
                Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mCurrentWeatherFragments[position];

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 6;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            String retVal;
            switch (position) {
                case 0:
                    //getActionBar().setSubtitle("Today's Weather");
                    retVal = "Today's Weather";
                    break;
                case 1:
                    //getActionBar().setSubtitle("Tomorrow's Weather");

                    retVal = "Tomorrow's Weather";
                    break;
                default:

                    if (mForecastResult != null) {
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("EEEE");
                        String date = format.format(mForecastResult.getDaily().getData()[position].getTime());

                        retVal = String.format("%s's Weather", date);
                    } else retVal = "Future Weather";
                    break;

            }
            retVal += " - " + mCity;
            return retVal;
        }
    }

    private View.OnClickListener onRefreshClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Snackbar.make(view, "Synchronizing Weather", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            // check availability of play services
            if (locationHelper.checkPlayServices()) {

                // Building the GoogleApi client
                locationHelper.buildGoogleApiClient();
            }

            mLastLocation = locationHelper.getLocation();

            if (mLastLocation != null) {

                getCity(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                getWeatherFromDarkSkyByLatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            } else {

                Snackbar.make(view, "Couldn't get the location. Make sure location is enabled on the device", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    };

    private void setSubTitle(String subtitle) {
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        if (ab != null)
            ab.setSubtitle(subtitle);
    }

    public void getCity(double latitude, double longitude) {
        Address locationAddress;

        locationAddress = locationHelper.getAddress(latitude, longitude);

        mCity = "";

        if (locationAddress != null) {

            String city = locationAddress.getLocality();
            String state = locationAddress.getAdminArea();

            if (!TextUtils.isEmpty(city)) {
                mCity += city;

            }
            if (!TextUtils.isEmpty(state)) {
                mCity += ", " + state;

            }
        } else
            mCity = "---";
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "Failed To Get Location", Toast.LENGTH_LONG).show();
        Log.i("Connection failed:", " ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mSubtitle = mSectionsPagerAdapter.getPageTitle(position).toString();
        setSubTitle(mSubtitle);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        locationHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationHelper.checkPlayServices();
    }


    /**
     * Google api callback methods
     */

    @Override
    public void onConnected(Bundle arg0) {
        Snackbar.make(app_container, "Synchronizing Weather", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        mLastLocation = locationHelper.getLocation();
        // Once connected with google api, get the location
        if (mLastLocation != null) {

            getCity(mLastLocation.getLatitude(), mLastLocation.getLongitude());

            getWeatherFromDarkSkyByLatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

            mSubtitle = mSectionsPagerAdapter.getPageTitle(mViewPager.getCurrentItem()).toString();
            setSubTitle(mSubtitle);
        } else {

            Snackbar.make(app_container, "Couldn't get the location. Make sure location is enabled on the device", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        locationHelper.connectApiClient();
    }


    // Permission check functions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // redirects to utils
        locationHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

}