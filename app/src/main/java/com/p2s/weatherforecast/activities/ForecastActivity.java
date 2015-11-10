package com.p2s.weatherforecast.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.p2s.weatherforecast.R;
import com.p2s.weatherforecast.WeatherApp;
import com.p2s.weatherforecast.classes.ForecastResult;
import com.p2s.weatherforecast.fragments.CurrentWeatherFragment;

import java.text.SimpleDateFormat;

import io.fabric.sdk.android.Fabric;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class ForecastActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ViewPager.OnPageChangeListener {
    private final static String TAG = "ForecastActivity";
    private String mSubtitle;

    private boolean mInited = false;

    private ForecastResult mForecastResult;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private CurrentWeatherFragment[] mCurrentWeatherFragments = new CurrentWeatherFragment[6];

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_forecast);

        buildGoogleApiClient();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        for (int i = 0; i < 6; i++) {
            mCurrentWeatherFragments[i] = CurrentWeatherFragment.newInstance();
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        mSubtitle = mSectionsPagerAdapter.getPageTitle(0).toString();
        setSubTitle(mSubtitle);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(onRefreshClickListener);
    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildGoogleApiClient();
            mGoogleApiClient.reconnect();
        } else {
            showEnableLocationDialog();

        }
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

    private void showEnableLocationDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                ForecastActivity.this);
        alertDialogBuilder
                .setMessage("GPS is disabled in your device. Enable it?")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(myIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void getWeatherFromDarkSkyByLatLng(double lat, double lng) {
        Call<ForecastResult> call = WeatherApp.getWeatherService().forecast(lat, lng);
        call.enqueue(new Callback<ForecastResult>() {
            @Override
            public void onResponse(Response<ForecastResult> response, Retrofit retrofit) {
                int statusCode = response.code();
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

        public SectionsPagerAdapter(FragmentManager fm) {
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
            switch (position) {
                case 0:
                    //getActionBar().setSubtitle("Today's Weather");
                    return "Today's Weather";

                case 1:
                    //getActionBar().setSubtitle("Tomorrow's Weather");

                    return "Tomorrow's Weather";
                default:

                    if (mForecastResult != null) {
                        SimpleDateFormat format = new SimpleDateFormat("EEEE");
                        String date = format.format(mForecastResult.getDaily().getData()[position].getTime());

                        return date + "'s Weather";
                    } else return "Future Weather";
            }
        }
    }

    private View.OnClickListener onRefreshClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mGoogleApiClient.reconnect();


            Snackbar.make(view, "Synchronizing Weather", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            if (mLastLocation != null) {
                getWeatherFromDarkSkyByLatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            } else {

                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    mInited = false;
                    mGoogleApiClient.reconnect();

                    //    getWeatherFromDarkSkyByLatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                } else {
                    showEnableLocationDialog();

                }

                Toast.makeText(getApplicationContext(), "Location Not Found", Toast.LENGTH_LONG).show();
            }
        }
    };


    private void setSubTitle(String subtitle) {
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        if (ab != null)
            ab.setSubtitle(subtitle);
    }

    //==============Location==================================


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null && !mInited) {
            mInited = true;
            getWeatherFromDarkSkyByLatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "Failed To Get Location", Toast.LENGTH_LONG).show();

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
}