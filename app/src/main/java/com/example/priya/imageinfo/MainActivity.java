package com.example.priya.imageinfo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Template> arrayList;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE;

    static {
        PERMISSIONS_STORAGE = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(isOnline()) {
            arrayList = new ArrayList<>();
            TemplateAdapter modelAdapter = new TemplateAdapter(this,arrayList);
            ListView listView = (ListView) findViewById(R.id.clarifai_collection);
            listView.setAdapter(modelAdapter);

            arrayList.add(new Template("General", R.drawable.general_001, "The ‘General’ model recognizes over 11,000 different concepts including objects, themes, moods, and more. This model is a great all-purpose solution for most visual recognition needs."));
            arrayList.add(new Template("Travel", R.drawable.travel_001, "The ‘Travel’ model recognizes specific features of residential, hotel, and travel-related properties. This model is great for anyone building travel and hospitality-related apps."));
            arrayList.add(new Template("Colors",R.drawable.color_006,"The ‘Color’ model returns density values for dominant colors present in images. Color predictions are returned in hex format and also mapped to their closest W3C counterparts. This model is great for anyone building an app where color is an important distinguisher."));


        }
        else{
            Toast.makeText(this,"NO Internet ACCESS",Toast.LENGTH_LONG).show();
        }


        verifyStoragePermissions(MainActivity.this);
    }

    protected boolean isOnline() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                if (activeNetwork.isConnected())
                    haveConnectedWifi = true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                if (activeNetwork.isConnected())
                    haveConnectedMobile = true;
            }
        }

        return haveConnectedWifi || haveConnectedMobile;
    }
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
