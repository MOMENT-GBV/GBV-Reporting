package com.example.timothymartinez.SafeRoute;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.UCSB.SafeRoute.R;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.regions.Regions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;

import java.util.Calendar;

public class MakeIncidentReport extends Activity {

    Location currentLocation;
    LocationManager mLocationManager;
    GoogleApiClient mGoogleApiClient;
    GeoApiContext context;

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
                currentLocation = location;
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
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_incident_report);
        context = new GeoApiContext().setApiKey(getString(R.string.google_maps_geocode_key));
        setupSpinners();
        //setupLocationManger();
        createLocationObj();
        addRadioGroupListener();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_make_incident_report, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void makeIncidentReport(View v){

        String user_identity_id  = getUserID();
        String incident_description = ((EditText)findViewById(R.id.editText)).getText().toString();
        String incident_latitude = String.valueOf(getCurrentLocation().latitude);
        String incident_longitude = String.valueOf(getCurrentLocation().longitude);
        String incident_datetime = Calendar.getInstance().getTime().toString();
        String incident_type = ((Spinner)findViewById(R.id.spinner)).getSelectedItem().toString();
        String incident_location_type = ((Spinner)findViewById(R.id.spinner2)).getSelectedItem().toString();

        Incident i = new Incident(user_identity_id, incident_description, incident_latitude,
                incident_longitude,incident_datetime,incident_type, incident_location_type);
        i.submitIncident();
        Toast toast = Toast.makeText(getApplicationContext(), "Incident submitted", Toast.LENGTH_LONG);
        finish();
    }
    public LatLng getCurrentLocation(){
        RadioGroup group = (RadioGroup)findViewById(R.id.radioGroup4);
        RadioButton button  = (RadioButton)findViewById(group.getCheckedRadioButtonId());
        if(group.getCheckedRadioButtonId() == R.id.radioButton){
            String address = ((EditText)findViewById(R.id.editText11)).getText().toString();
            try {
                GeocodingResult[] results = GeocodingApi.geocode(context, address).await();
                return new LatLng(results[0].geometry.location.lat, results[0].geometry.location.lng);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
        /*try {
            Location l = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (l == null){
                Log.d("Why>>", "Location is null");
            }
            else {
                return new LatLng(l.getLatitude(), l.getLongitude());
            }
        }
        catch (SecurityException e){
            Log.d("Permissions Exception", "Need location priveleges");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;*/
    }
    public String getUserID(){


        authenticationProvider provider = new authenticationProvider(null, getString(R.string.aws_api_key),
                Regions.US_EAST_1);
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(getApplicationContext(),
                provider, Regions.US_EAST_1);
        CognitoSyncManager syncClient = new CognitoSyncManager(getApplicationContext(), Regions.US_EAST_1, credentialsProvider);

        Dataset dataset = syncClient.openOrCreateDataset("myDataset");

        String user_identity_id =  dataset.get("IdentityID");

        if(user_identity_id == null){
            user_identity_id = "";
        }
        return user_identity_id;
    }
    public void setupSpinners(){
        Spinner dropdown = (Spinner)findViewById(R.id.spinner);
        Spinner dropdown2 = (Spinner)findViewById(R.id.spinner2);
        String[] items = new String[]{"Verbal Harrassment", "Physical Confrontation", "Other"};
        String[] transportationMethods = new String[]{"Walking", "Bus", "Train", "Taxi"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, transportationMethods);
        dropdown2.setAdapter(adapter1);
    }
    public void setupLocationManger(){
        mLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
        }
        catch (SecurityException e){
            e.printStackTrace();
        }
        try {
            mLocationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, mLocationListener, null);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void createLocationObj(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                    }
                })
                .build();
        mGoogleApiClient.connect();
    }
    public void ShowOrHideText(View v){
        RadioGroup group = (RadioGroup)findViewById(R.id.radioGroup4);
        TextView writtenLocation = (TextView)findViewById(R.id.editText11);
        writtenLocation.setVisibility(View.VISIBLE);
    }
    public void addRadioGroupListener(){
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup4);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            EditText input = (EditText)findViewById(R.id.editText11);

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButton){
                    input.setVisibility(View.VISIBLE);
                }
                else{
                    input.setVisibility(View.GONE);
                }
            }
        });

    }
}
