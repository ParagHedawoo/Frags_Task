package com.example.paraghedawoo.frags_task;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public TextView nameText;
    public TextView latlngText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameText = (TextView) findViewById(R.id.locationText);
        latlngText = (TextView) findViewById(R.id.latlngText);

        Bundle bundle = getIntent().getExtras(); // getting the pushed parameters from the mapsActivity
        if (bundle != null) {
            nameText.setText(bundle.getString("Name")); // setting name to location selected
            latlngText.setText(bundle.getString("Lat") + ", " + bundle.getString("Lng")); // setting the latitude and longitude
        }
    }


    // class to determine if internet is working
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    //onClick parameter set in xml
    public void onClick(View v){
        //redirects to the LocationActivity
        if (isNetworkAvailable()) {
            startActivity(new Intent(v.getContext(), LocationActivity.class));
        }
        else{
            Toast.makeText(v.getContext(), "Check your Internet Connection again!", Toast.LENGTH_LONG).show();
        }
    }
}
