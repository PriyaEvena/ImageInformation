package com.example.priya.imageinfo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.dto.input.ClarifaiInput;

public class PredictionActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private String imgurl;
    private String title;
    private String desc;
    private byte[] barray;
    private ArrayList<HashMap<String,String>> arrayList;
    private GenAdapter genAdapter;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);

        Intent intent = getIntent();
        imgurl = intent.getStringExtra("imgurl");
        title = intent.getStringExtra("model");
        desc = intent.getStringExtra("desc");
        String reqimg = intent.getStringExtra("req");

        try {

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(reqimg));
            // Log.d(TAG, String.valueOf(bitmap));
            //img1.setImageBitmap(bitmap);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            barray = stream.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }
        progressDialog=new ProgressDialog(PredictionActivity.this);
        progressDialog.setMessage("Please wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        arrayList = new ArrayList<>();
        genAdapter = new GenAdapter(this,arrayList);
        listView = (ListView) findViewById(R.id.general_lvew);
        listView.setAdapter(genAdapter);

       // TextView titlegen = (TextView)  findViewById(R.id.titleGen);
      //  TextView descgen = (TextView) findViewById(R.id.descGen);
        ImageView genImg = (ImageView) findViewById(R.id.general_img);

      //  titlegen.setText(title);
      //  descgen.setText(desc);
        Log.e("check",""+reqimg);
        genImg.setImageURI(Uri.parse(reqimg));

        if(title.equalsIgnoreCase("General")){
            new PredictGeneral().execute();
            TextView pr = (TextView) findViewById(R.id.prob);
            pr.setText("Probability");
        }
        if(title.equalsIgnoreCase("Colors")){
            new PredictColors().execute();
            TextView pr = (TextView) findViewById(R.id.prob);
            pr.setText("Density");
        }
        if(title.equalsIgnoreCase("Travel")){
            new PredictTravel().execute();
            TextView pr = (TextView) findViewById(R.id.prob);
            pr.setText("Probability");
        }
    }
    private void updateUi(){
        genAdapter.notifyDataSetChanged();
    }
    private class PredictGeneral extends AsyncTask<URL,Void,String> {
        @Override
        protected String doInBackground(URL... urls) {
            ClarifaiClient client = new ClarifaiBuilder("b2b51aa449a44cb3a27a5dea6e9d8907")
                    .buildSync();
            Log.e("imgurl",imgurl);
            String jsonString = client.getDefaultModels().generalModel().predict().withInputs(ClarifaiInput.forImage(barray))
                    .executeSync().rawBody();
            Log.e("JSON",jsonString);
            extractFeatureFromJson(jsonString);
            return "";
        }
        @Override
        protected void onPostExecute(String s) {
            updateUi();
            progressDialog.dismiss();
        }
        private void extractFeatureFromJson(String jsonstr){
            try{
                JSONObject baseJsonResponse = new JSONObject(jsonstr);
                JSONArray featureArray = baseJsonResponse.getJSONArray("outputs");
                if (featureArray.length() > 0) {
                    JSONObject firstFeature = featureArray.getJSONObject(0);
                    JSONObject data1 = firstFeature.getJSONObject("data");
                    JSONArray concepts = data1.getJSONArray("concepts");
                    for(int i=0;i<concepts.length();i++){
                        JSONObject temp = concepts.getJSONObject(i);
                        String name = temp.getString("name");
                        double prob = temp.getDouble("value");
                        HashMap<String,String> h = new HashMap();

                        h.put("name",name);
                        h.put("prob",""+RoundTo2Decimals(prob));
                        if(prob>0.25){
                            arrayList.add(h);
                        }
                    }
                }
            }
            catch(Exception e){
                Log.e("JSON","Exception");
            }
        }
    }
    private class PredictColors extends AsyncTask<URL,Void,String> {
        @Override
        protected String doInBackground(URL... urls) {
            ClarifaiClient client = new ClarifaiBuilder("b2b51aa449a44cb3a27a5dea6e9d8907")
                    .buildSync();

            String jsonString = client.getDefaultModels().colorModel().predict().withInputs(ClarifaiInput.forImage(barray))
                    .executeSync().rawBody();
            Log.e("JSON",jsonString);
            extractFeatureFromJson(jsonString);
            return "";

        }
        @Override
        protected void onPostExecute(String s) {
            updateUi();
            progressDialog.dismiss();
        }
        private void extractFeatureFromJson(String jsonstr){
            try{
                JSONObject baseJsonResponse = new JSONObject(jsonstr);
                JSONArray featureArray = baseJsonResponse.getJSONArray("outputs");
                if (featureArray.length() > 0) {
                    JSONObject firstFeature = featureArray.getJSONObject(0);
                    JSONObject data1 = firstFeature.getJSONObject("data");
                    JSONArray colors = data1.getJSONArray("colors");
                    for(int i=0;i<colors.length();i++){
                        JSONObject temp = colors.getJSONObject(i);
                        JSONObject data2 = temp.getJSONObject("w3c");
                        String name = data2.getString("name");
                        String hex = data2.getString("hex");
                        double prob = temp.getDouble("value");
                        HashMap<String,String> h = new HashMap();

                        h.put("name",name+"   "+hex);
                        h.put("prob",""+RoundTo2Decimals(prob));
                        Log.e(name+"   "+hex,(RoundTo2Decimals(prob)*100)+"");

                        arrayList.add(h);

                    }
                }
            }
            catch(Exception e){
                Log.e("JSON","Exception");
            }
        }
    }
    private class PredictTravel extends AsyncTask<URL,Void,String> {
        @Override
        protected String doInBackground(URL... urls) {
            ClarifaiClient client = new ClarifaiBuilder("b2b51aa449a44cb3a27a5dea6e9d8907")
                    .buildSync();
            String jsonString = client.getDefaultModels().travelModel().predict()
                    .withInputs(ClarifaiInput.forImage(barray))
                    .executeSync().rawBody();
            Log.e("JSON",jsonString);
            extractFeatureFromJson(jsonString);
            return "";
        }
        @Override
        protected void onPostExecute(String s) {
            updateUi();
            progressDialog.dismiss();
        }
        private void extractFeatureFromJson(String jsonstr){
            try{
                JSONObject baseJsonResponse = new JSONObject(jsonstr);
                JSONArray featureArray = baseJsonResponse.getJSONArray("outputs");
                if (featureArray.length() > 0) {
                    JSONObject firstFeature = featureArray.getJSONObject(0);
                    JSONObject data1 = firstFeature.getJSONObject("data");
                    JSONArray colors = data1.getJSONArray("colors");
                    for(int i=0;i<colors.length();i++){
                        JSONObject temp = colors.getJSONObject(i);
                        JSONObject data2 = temp.getJSONObject("w3c");
                        String name = data2.getString("name");
                        String hex = data2.getString("hex");
                        double prob = temp.getDouble("value");
                        HashMap<String,String> h = new HashMap();

                        h.put("name",name+"   "+hex);
                        h.put("prob",""+RoundTo2Decimals(prob));
                        Log.e(name+"   "+hex,(RoundTo2Decimals(prob)*100)+"");
                        if(prob>0.25){
                        arrayList.add(h);
                        }
                    }
                }
            }
            catch(Exception e){
                Log.e("JSON","Exception");
            }
        }
    }
    double RoundTo2Decimals(double val) {
        DecimalFormat df2 = new DecimalFormat("###.###");
        return Double.valueOf(df2.format(val));
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

