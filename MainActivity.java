package com.example.tochi.feedme;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends Activity {

    static String Recipe_Image = "image_food";
    static String Recipe_Name = "recipeName";
    static String Cuisine = "cuisine";
    static String Recipe_Source = "sourceDisplayName";
    static String Rating = "rating";
    static String TotalTime = "totalTimeInSeconds";
    static String Recipe_ID = "id";

    String recipe_image = "";
    String attributes = "";
    String ingredients ="";
    String cuisine ="cuisine";

    JSONObject jsonobject;
    JSONArray jsonarray;
    ListViewAdapter adapter;
    ListView list;

    ProgressDialog mProgressDialog;

    ArrayList<HashMap<String, String>> List = new ArrayList<HashMap<String, String>>();

    private final String TAG = "MainActivity";
    private ShakeEventListener sensorListener;
    private SensorManager sensorManager;

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "on start called");
    }

    @Override
    public void onResume() {

        super.onResume();
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "on Destroy called");
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        sensorManager.unregisterListener(sensorListener);
        super.onPause();
        Log.i(TAG, "on Pause called");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "on create called");
        new GetRecipeInfo().execute();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorListener = new ShakeEventListener();
        sensorListener
                .setOnShakeListener(new ShakeEventListener.OnShakeListener() {

                    public void onShake() {
                        Toast.makeText(MainActivity.this,
                                "Refresh on shake!", Toast.LENGTH_SHORT).show();
                        new GetRecipeInfo().execute();
                    }
                });
    }

    private class GetRecipeInfo extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setTitle("Welcome to FeedMe");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            jsonobject = JSONfunctions
                    .getJSONfromURL("http://api.yummly.com/v1/api/recipes?_app_id=14e90418&_app_key=b001fe16678db03e1d894390015f2351&q=apple+pie&requirePictures=true");

            try {

                jsonarray = jsonobject.getJSONArray("matches");

                for (int i = 0; i < jsonarray.length(); i++) {

                    HashMap<String, String> map = new HashMap<String, String>();

                    jsonobject = jsonarray.getJSONObject(i);

                    recipe_image = jsonobject.getJSONObject("imageUrlsBySize").getString("90");
                    //attributes = jsonobject.getJSONObject("attributes").getJSONArray(cuisine).getString(0);

                    map.put("recipeName", jsonobject.getString("recipeName"));
                    map.put("cuisine", jsonobject.getJSONObject("attributes").getJSONArray("course").getString(0));
                    map.put("sourceDisplayName", jsonobject.getString("sourceDisplayName"));
                    map.put("rating", jsonobject.getString("rating"));
                    map.put("id", jsonobject.getString("id"));
                    map.put("image_food",recipe_image);

                   //To Convert the time to minutes before adding to the Map
                   String time_Str = jsonobject.getString("totalTimeInSeconds");

                   double time_taken =0;
                   try {
                        time_taken = Double.parseDouble(time_Str);
                   }catch (NumberFormatException nfe){
                        nfe.printStackTrace();
                   }
                    time_taken = time_taken / 60;
                    String str_time_taken = String.valueOf(time_taken);

                    map.put("totalTimeInSeconds", str_time_taken);


                    List.add(map);
                }
            } catch (JSONException e) {
                //Log.e("Couldn't get any data from the url", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void args) {

            list = (ListView) findViewById(R.id.list);
            adapter = new ListViewAdapter(MainActivity.this, List);
            list.setAdapter(adapter);
            mProgressDialog.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_refresh:
                refresh_action();
                break;
            case R.id.action_settings:
                settings_Action();
                break;
            case R.id.action_search:
                search_Action();

        }
        return true;
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Noodle Soup, Apple Pie etc.");

        return true;
    }

    private void search_Action() {
    }

    public void refresh_action()
    {
        new GetRecipeInfo().execute();
    }
    public void settings_Action() {

        Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(myIntent);
    }
}
