package com.example.paraghedawoo.frags_task;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    String provider;
    String result;
    List<LocationData> locationDataList;
    ImageButton myLocation;
    Marker marker;
    ImageButton sendLocation;
    LocationData sendLocationData;

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
        myLocation = (ImageButton) findViewById(R.id.myLocation);
        sendLocation = (ImageButton) findViewById(R.id.send);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //get location stored in device
        Location location = locationManager.getLastKnownLocation(provider);
        final double lat = location.getLatitude();
        final double lng = location.getLongitude();

        sendLocationData = new LocationData(); // this object created to temporarily save location data

        sendLocationData.setName("My Location");
        sendLocationData.setLat(lat);
        sendLocationData.setLng(lng);

        ListView listView = (ListView)findViewById(R.id.nearbyList);
        locationDataList = new ArrayList<>();
        ArrayList<String> places = new ArrayList<>();
        DownloadPlacesJSON task = new DownloadPlacesJSON();
        //execute class that will download list of nearby places using google API
        try {
            result = task.execute("https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                    "location=" + String.valueOf(lat) + "," + String.valueOf(lng) + "&radius=1000&type=nearby%20places" +
                    "&sensor=true&key=AIzaSyB_F-oerBg1ldX3d1uw-btRhd2XKzI4pwg").get();

            String crappyPrefix = "null";

            if (result.startsWith(crappyPrefix)) {
                result = result.substring(crappyPrefix.length(), result.length());
            }
            JSONObject jo = new JSONObject(result);
            String res = jo.getString("results");
            JSONArray ja = new JSONArray(res);
            // sorting put names and location from JSON data string
            for (int i = 0; i < 15; i++) {
                LocationData locationData = new LocationData();
                String loc = ja.getString(i);
                JSONObject obj = new JSONObject(loc);
                String geo = obj.getString("geometry");
                String name = obj.getString("name");
                places.add(name);
                locationData.setName(name);
                JSONObject locatn = new JSONObject(geo);
                String locate = locatn.getString("location");
                JSONObject latlng = new JSONObject(locate);
                locationData.setLat(latlng.getDouble("lat"));
                locationData.setLng(latlng.getDouble("lng"));
                locationDataList.add(locationData);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, places);
            listView.setAdapter(adapter);

        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }

        // on clicking an item on the nearby list, one can get to its location
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LatLng latLng = new LatLng(locationDataList.get(position).getLat(), locationDataList.get(position).getLng());
                mMap.clear();
                marker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                sendLocationData.setName(locationDataList.get(position).getName());
                sendLocationData.setLat(latLng.latitude);
                sendLocationData.setLng(latLng.longitude);
            }
        });

        // the button to redirect you to your own location
        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                marker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title("Your Location")); // add a marker over location
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng),18)); // move camera to the specified lat-lng
                sendLocationData.setName("My Location");
                sendLocationData.setLat(lat);
                sendLocationData.setLng(lng);
            }
        });

        //the button to send your location to the main activity
        sendLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                bundle.putString("Name", sendLocationData.getName());
                bundle.putString("Lat", String.valueOf(sendLocationData.getLat()));
                bundle.putString("Lng", String.valueOf(sendLocationData.getLng()));
                i.putExtras(bundle);
                startActivity(i);
            }
        });
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
        marker=mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title("Your Location")); // add a marker over location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng),18)); // move camera to the specified lat-lng
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
