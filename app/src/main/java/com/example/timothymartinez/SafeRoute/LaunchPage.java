package com.example.timothymartinez.SafeRoute;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
//import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SlidingDrawer;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.UCSB.SafeRoute.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.TravelMode;

import java.util.ArrayList;

public class LaunchPage extends FragmentActivity implements OnMapReadyCallback{
    public GoogleMap gMap;
    Location currentLocation;
    LocationManager mLocationManager;
    GoogleApiClient mGoogleApiClient;
    GeoApiContext context;
    ArrayList<PolylineOptions> cachedRoutes = new ArrayList<PolylineOptions>();
    ArrayList<MarkerOptions> cachedIncidentMarkers = new ArrayList<MarkerOptions>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_page);
        context = new GeoApiContext().setApiKey(getString(R.string.google_maps_geocode_key));
        createLocationObj();
        setupSpinner();
        addRadioGroupListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_launch_page, menu);
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

    public void updateLocationDate(Location currentLocation){
        //TextView Location = (TextView)findViewById(R.id.textView4);
        LatLng coords = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
        try {

            double latitude = currentLocation.getLatitude();
            double longitude = currentLocation.getLongitude();
            //Location.setText("Latitude: " + latitude + " Longitude: " + longitude);

        }
        catch (SecurityException e){
            Log.d("String", e.getMessage());
            String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION};
            requestPermissions(permissions, 0);
        }
        gMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coords, 14));
        gMap.setMyLocationEnabled(true);

    }
    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));
    }
    public void makeReport(View view){
        Intent intent = new Intent(this,MakeIncidentReport.class);
        startActivity(intent);
    }
    public void overlayDirections (DirectionsRoute[] routes){
        ArrayList<Integer> colours = new ArrayList<Integer>();
        int highlightedRoute = getRouteToHighlightId();
        colours.add(Color.MAGENTA);
        colours.add(Color.BLUE);
        colours.add(Color.YELLOW);
        int colorIndex = 0;
        try {
            for (DirectionsRoute route:routes) {
                DirectionsStep[] steps = route.legs[0].steps;
                for (DirectionsStep step : steps) {
                    Log.d("Step", step.htmlInstructions.toString());
                    PolylineOptions p = new PolylineOptions();
                    p.width(10);
                    if(colorIndex == getRouteToHighlightId()){
                        p.color(colours.get(colorIndex));
                    }
                    else{
                        p.color(Color.LTGRAY);
                    }
                    for (com.google.maps.model.LatLng g : step.polyline.decodePath()) {
                        p.add(new LatLng(g.lat, g.lng));
                    }
                    gMap.addPolyline(p);
                    //cache the polylines, so that adjustment is easier.
                    //invalidate the cache when new directions received.
                    cachedRoutes.add(p);
                }
                colorIndex++;
            }
        }
        catch (Exception e){

        }
    }
    public void testOutDirections(View v){
        try {
            String fromLocation = getFromLocation();
            String toLocation = getToLocation();
            TravelMode t = getTravelMode();

            DirectionsRoute[] request =  DirectionsApi.getDirections(context, fromLocation, toLocation).mode(t).alternatives(true).await();
            if (request.length > 2){
                showRouteC();
                showRouteB();
            }
            else if (request.length > 2){
                showRouteB();
            }
            overlayDirections(request);
            addIncidentsToMap(request);
            SlidingDrawer drawer = (SlidingDrawer)findViewById(R.id.slidingDrawer);
            drawer.setVisibility(View.VISIBLE);
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(LaunchPage.this, "Sorry, couldn't get directions", Toast.LENGTH_SHORT).show();
        }

    }
    public TravelMode getTravelMode(){
         if ( ((RadioButton)findViewById(R.id.radioButton2)).isChecked()){
             return TravelMode.DRIVING;
         }
         if ( ((RadioButton)findViewById(R.id.radioButton5)).isChecked()){
             return TravelMode.WALKING;
         }
         return TravelMode.TRANSIT;
    }
    public String getFromLocation(){
        try {
            com.google.maps.model.LatLng fromLocationCoords = null;

            if(currentLocation != null)
            {
                fromLocationCoords = new com.google.maps.model.LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            }
            Log.d("From", "" + fromLocationCoords.lat + fromLocationCoords.lng);

            GeocodingResult[] results = GeocodingApi.reverseGeocode(context, fromLocationCoords).await();

            Log.d("Results", results[0].formattedAddress);

            String geocodedAddress = results[0].formattedAddress;
            return geocodedAddress;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
    public String getToLocation(){
        String addressToNavigateTo = ((TextView) findViewById(R.id.editText2)).getText().toString();

        try {
            GeocodingResult[] results = GeocodingApi.geocode(context, addressToNavigateTo).await();
            Log.d("aasdfs", results[0].formattedAddress);
            LatLng coords = new LatLng(results[0].geometry.location.lat,results[0].geometry.location.lng);
            gMap.addMarker(new MarkerOptions().position(coords).title("Destination"));
            cachedIncidentMarkers.add(new MarkerOptions().position(coords).title("Destination"));
            return results[0].formattedAddress;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return "";

    }
    public void addIncidentsToMap(DirectionsRoute[] routes){
        ApiFunctions f = new ApiFunctions();
        int daysAgo = getDaysAgo();
        for(int j = 0; j < routes.length; j++){
            DirectionsRoute route = routes[j];
            for(DirectionsLeg leg: route.legs){
                ArrayList<Incident> incidents = f.getIncidentsByLatLng(leg.startLocation, leg.endLocation, getDaysAgo());
                if(incidents == null){
                    continue;
                }
                updateRouteSelector(j,incidents.size());
                for(Incident i: incidents){
                    if( i != null) {
                        Log.d("Incidents", i.incident_description);
                        Double latitude = Double.parseDouble(i.incident_latitude);
                        Double longitude = Double.parseDouble(i.incident_longitude);
                        LatLng coords = new LatLng(latitude, longitude);
                        gMap.addMarker(new MarkerOptions().position(coords).title(i.incident_description + "\n(" + i.incident_type + ")"));
                        cachedIncidentMarkers.add(new MarkerOptions().position(coords).title(i.incident_description));
                    }
                }
            }
        }
    }
    public void createLocationObj(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        try {
                            currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                            Log.d("Success", "Location Services worked lat = " + currentLocation.getLatitude());
                            updateLocationDate(currentLocation);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(LaunchPage.this, "Error finding location", Toast.LENGTH_SHORT).show();
                        }
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
    public void setupSpinner(){
        Spinner dropdown = (Spinner)findViewById(R.id.spinner3);
        String[] items = new String[]{"Past Month","Past Week", "Past 24 hours"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.drop_list_white, items);
        dropdown.setAdapter(adapter);
    }
    public int getRouteToHighlightId(){
        RadioGroup routeGroup = (RadioGroup)findViewById(R.id.radioGroup3);
        int selected = routeGroup.getCheckedRadioButtonId();
        if (selected == R.id.hostA){
            return 0;
        }
        if (selected == R.id.hostB){
            return 1;
        }
        if (selected == R.id.hostC){
            return 2;
        }
        return 0;

    }
    public void TogglePolyLines(){
        if(gMap == null){
            return; // do something more clever
        }
        gMap.clear();
        for (MarkerOptions o: cachedIncidentMarkers){
            gMap.addMarker(o);
        }
        for(int i = 0; i < cachedRoutes.size(); i++){
            if (i == getRouteToHighlightId()){
                cachedRoutes.get(i).color(Color.MAGENTA);
                cachedRoutes.get(i).zIndex(999);
            }
            else{
                cachedRoutes.get(i).color(Color.LTGRAY);
            }
            gMap.addPolyline(cachedRoutes.get(i));
        }
    }
    public void addRadioGroupListener(){
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup3);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            EditText input = (EditText)findViewById(R.id.editText11);

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                TogglePolyLines();
            }
        });

    }
    public int getDaysAgo(){
        String text = ((Spinner)findViewById(R.id.spinner3)).getSelectedItem().toString();
        if(text.equalsIgnoreCase("Past Month")){
            return 30;
        }
        if(text.equalsIgnoreCase("Past Week")){
            return 7;
        }
        if(text.equalsIgnoreCase("Past 24 hours")){
            return 1;
        }
        return 30;
    }
    public void showRouteC(){
        RadioButton button = (RadioButton)findViewById(R.id.hostC);
        TextView title = (TextView)findViewById(R.id.textView17);
        TextView text = (TextView)findViewById(R.id.textView18);
        button.setVisibility(View.VISIBLE);
        text.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
    }
    public void showRouteB(){
        RadioButton button = (RadioButton)findViewById(R.id.hostB);
        TextView title = (TextView)findViewById(R.id.textView15);
        TextView text = (TextView)findViewById(R.id.textView16);
        button.setVisibility(View.VISIBLE);
        text.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
    }
    public void updateRouteSelector(int route, int numberofincidents){
        TextView text = null;
        Log.d("IncidentCall", (new Integer(numberofincidents).toString()) );
        if (route == 0){
            text = (TextView)findViewById(R.id.textView14);
        }
        if (route == 1){
            text = (TextView)findViewById(R.id.textView16);
        }
        if (route == 2){
            text = (TextView)findViewById(R.id.textView18);
        }
        text.setText("Incidents along Route: "+ numberofincidents);
    }
}
