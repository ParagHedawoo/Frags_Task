package com.example.paraghedawoo.frags_task;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
