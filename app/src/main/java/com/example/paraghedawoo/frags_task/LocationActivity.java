package com.example.paraghedawoo.frags_task;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    String provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); //access service to know device location
        provider = locationManager.getBestProvider(new Criteria(), false);  // string that stores data of best provider of location data

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //get location stored in device
        Location location = locationManager.getLastKnownLocation(provider);
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        String result;
        ListView listView = (ListView)findViewById(R.id.nearbyList);
        DownloadPlacesJSON task = new DownloadPlacesJSON();
        //execute class that will download list of nearby places using google API
        try {
            result = task.execute("https://maps.googleapis.com/maps/api/place/nearbysearch/xml?" +
                "location="+String.valueOf(lat)+","+String.valueOf(lng)+"&radius=1000&type=nearby%20places" +
                "&sensor=true&key=AIzaSyB_F-oerBg1ldX3d1uw-btRhd2XKzI4pwg").get();

            Pattern p1 = Pattern.compile("<name>(.*?)</name>");       //Getting names of nearby places places
            Matcher m1 = p1.matcher(result);                        //and then arrange them in the Arraylist locationDatas
            ArrayList<String> places = new ArrayList<>();
            while (m1.find()) {
                places.add(m1.group(1));     //adding places to arraylist as it is found in the string
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, places);
            listView.setAdapter(adapter);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //auto-generated check
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // a final check to see if location manager has a stored location
        if (locationManager.getLastKnownLocation(provider) != null) {
            locationManager.requestLocationUpdates(provider, 400, 1, this); // keep updating location data in every 400 milliseconds and 1 meter difference
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {

        double lat = location.getLatitude(); // get latitude
        double lng = location.getLongitude(); // get longitude
        mMap.clear(); //clear position of previous marker
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng),18)); // move camera to the specified lat-lng
        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title("Your Location")); // add a marker over location

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
}

// class created to download nearby places data

class DownloadPlacesJSON extends AsyncTask<String, Void, String>{

    @Override
    protected String doInBackground(String... params) {

        String result = null;

        try{
            URL url = new URL(params[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();      //receives the input dtream of data from httpconnection
            InputStreamReader reader = new InputStreamReader(inputStream); // a reader to read the imported data
            int data = reader.read();           //command to read
            while (data != -1){
                char current = (char) data;
                result += current;              // each character is added to the result, as it is been read
                data = reader.read();           // reader reassigned to read next character
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
