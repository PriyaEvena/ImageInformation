package com.example.priya.imageinfo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class SelectionActivity extends AppCompatActivity {

    private Uri uri;
    private String p="";
    private String picturePath = "";
    private byte[] byteArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        Intent intent = getIntent();
        final Template modelclass = (Template) intent.getExtras().getSerializable("model");



        TextView title = (TextView) findViewById(R.id.modelT);
        TextView desc = (TextView) findViewById(R.id.modelD);
        ImageView img =  (ImageView) findViewById(R.id.modelI);
        Button predict = (Button) findViewById(R.id.predict);

        title.setText(modelclass.getName());
        desc.setText(modelclass.getDescription());
        img.setImageResource(R.drawable.selectimg);

        Log.e("test",""+modelclass.getLink());

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);
            }
        });

        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i;
                i =  new Intent(SelectionActivity.this,PredictionActivity.class);
                if(!p.equalsIgnoreCase("")){
                    i.putExtra("imgurl",picturePath);
                    i.putExtra("model",modelclass.getName());
                    i.putExtra("desc",modelclass.getDescription());
                    i.putExtra("req",p);
                    if(isOnline()){
                        startActivity(i);
                    }
                    else{
                        Toast.makeText(SelectionActivity.this,"No Internet Access",Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(SelectionActivity.this,"Please Select a Image",Toast.LENGTH_LONG).show();
                }

            }
        });

    }
    public static final int PICK_IMAGE = 1;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView img1 = (ImageView) findViewById(R.id.modelI);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){
            uri = data.getData();
            p = uri+"";
            Log.e("uri",uri+"");
            //String[] filePathColumn = { MediaStore.Images.Media.DATA };

//            Cursor cursor = getContentResolver().query(uri,
//                    filePathColumn, null, null, null);
//            cursor.moveToFirst();
//
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            picturePath = cursor.getString(columnIndex);
//            cursor.close();
            //Log.e("picpath",picturePath);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                img1.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                this.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
