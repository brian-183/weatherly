package com.penrosewharf.brian.weatherly;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.renderscript.Allocation;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_CODE_LOCATION = 2;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLocation;
    protected Location mLastLocation;
    private double mLatitude;// = 37.8267;
    private double mLongitude;// = -122.4233;


    private CurrentWeather mCurrentWeather;

    @BindView(R.id.timeLabel) TextView mTimeLabel;
    @BindView(R.id.temperatureLabel) TextView mTemperatureLabel;
    @BindView(R.id.humidityValue) TextView mHumidityValue;
    @BindView(R.id.precipValue) TextView mPrecipValue;
    @BindView(R.id.summaryLabel) TextView mSummaryLabel;
    @BindView(R.id.iconImageView) ImageView mIconImageView;
    @BindView(R.id.locationLabel) TextView mLocationLabel;
    @BindView(R.id.refreshImageView) ImageView mRefreshImageView;
    @BindView(R.id.progressBar)  ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        buildGoogleApiClient();

        mProgressBar.setVisibility(View.INVISIBLE);


        getForecast(mLatitude, mLongitude);



        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            getForecast(mLatitude, mLongitude);
            }
        });

        //Check logcat to see that main is running whilst asynchronous connection is ongoing
        //Log.d(TAG, "Main UI code is running");


    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                //Want connection callbacks & listeners to come to this class
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

    }



    //Check if API is connected before disconnecting
    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "onConnectionFailed: ");
    }


    //Runs when a GoogleApiClient object successfully connects
    @Override
    public void onConnected(Bundle connectionHint) {

        //Check for user permission - required on all android devices over 6.0
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION);
        }else{
            //Provides a simple way of getting a devices location and is well suited for
            //applciations that do not require a fine grained location and that do not need
            //updates. Gets the best and most recent location currently available, which in
            //rare cases a location is unavailable.
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(mLastLocation != null){
                mLatitude = mLastLocation.getLatitude();
                mLongitude = mLastLocation.getLongitude();
            }

        }
        Log.i(TAG, "onConnected: ");
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "onConnectionSuspended: ");
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged: ");
        mLongitude = mLocation.getLongitude();
        mLatitude = mLocation.getLatitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void getForecast(double mLatitude, double mLongitude) {
        String apiKey = "89c024f80ca6a616d5af69a314a85629";
        String forecastUrl = "https://api.darksky.net/forecast/" + apiKey + "/" + mLatitude + "," + mLongitude;

        if (isNetworkAvailable()) {
            //Build client to send request
            toggleRefresh();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(forecastUrl)
                    .build();

            Call call = client.newCall(request);
            //Executes the call in the background so we can continue to execute code on the main thread
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    alertUserAboutError("There is something wrong - try again");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });

                    //store response and print to log
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mCurrentWeather = getCurrrentDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });

                        }
                        //Handle the case where response is not successful
                        else {
                            //Create method to alert user using dialog - create in main
                            alertUserAboutError("There is something wrong, try again!");
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception Caught" + e);
                    }
                    catch(JSONException e){
                        Log.e(TAG, "Exception Caught" + e);
                    }

                }
            });
        }
        //If network is unavailable a message to prompt the user.
        else{
            alertUserAboutError("The network is unavailable!");
        }
    }

    private void toggleRefresh() {
        if(mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);
        }
        else{
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);
        }
    }

    private void updateDisplay() {

        //--Some code not working!!!! -- FIX ---//

        mTimeLabel.setText("At " + mCurrentWeather.getFormattedTime() + " it will be");
        mHumidityValue.setText(mCurrentWeather.getHumidity() + "");
        mPrecipValue.setText(mCurrentWeather.getPrecipChance() + "");
        mSummaryLabel.setText(mCurrentWeather.getSummary());
        mTemperatureLabel.setText(mCurrentWeather.getTemp() + "");
        mLocationLabel.setText(mCurrentWeather.getLocation() + "");



        Drawable drawable = ContextCompat.getDrawable(this, mCurrentWeather.getIconId());
        mIconImageView.setImageDrawable(drawable);
    }

    //Throws the responsibilty for the error to the methos call
    private CurrentWeather getCurrrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");


        JSONObject currently = forecast.getJSONObject("currently");
        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setHumidity(currently.getDouble("humidity"));
        currentWeather.setTemp(currently.getDouble("temperature"));
        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setPrecipChance(currently.getDouble("precipProbability"));
        currentWeather.setTemp(currently.getDouble("temperature"));
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setTimezone("timezone");
        currentWeather.setLocation(timezone);
        currentWeather.setSummary(currently.getString("summary"));

        Log.d(TAG, currentWeather.getFormattedTime());

        //--------Test for outputs----------//
        //int time = forecast.getInt("time");
        //Log.i(TAG, "From JSON: " + timezone + currently.getString("icon"));

        return currentWeather;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvaiable = false;
        if(networkInfo != null && networkInfo.isConnected()){
            isAvaiable = true;
        }
        return isAvaiable;
    }

    private void alertUserAboutError(String errorMessage) {
        //Toast only stays on screen a short while so use a dialog
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.setErrorText(errorMessage);
        dialog.show(getFragmentManager(), "error_dialog");

    }
}
