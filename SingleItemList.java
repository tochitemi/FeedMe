package com.example.tochi.feedme;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Tochi on 10/26/14.
 */
public class SingleItemList extends Activity {

    String recipe_image;
    String recipe_name;
    String cuisine;
    String source;
    String rating;
    String total_time;
    String Recipe_ID;
    String Ingredients;
    String url;

    JSONObject jsonobject;
    JSONArray jsonarray;
    ProgressDialog progress;

    TextView INGREDIENTS;
    ImageView ImageRecipe;
    ImageLoader imageLoader = new ImageLoader(this);
    ArrayList<String> list_of_ingredients = new ArrayList<String>();
    StringBuilder sb = new StringBuilder("");



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from singleitemview.xml

        Intent i = getIntent();

        ActionBar actionBar = getActionBar();
        actionBar.setTitle(i.getStringExtra("recipeName"));
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.single_item_list);

        //recipe_image = i.getStringExtra("image_food");
        recipe_name = i.getStringExtra("recipeName");
        Recipe_ID = i.getStringExtra("id");
       // Ingredients = i.getStringExtra("ingredients");
        cuisine = i.getStringExtra("cuisine");
        source = i.getStringExtra("sourceDisplayName");
        rating = i.getStringExtra("rating");
        total_time = i.getStringExtra("totalTimeInSeconds");


        url= "http://api.yummly.com/v1/api/recipe/"+Recipe_ID +
                "?_app_id=14e90418&_app_key=b001fe16678db03e1d894390015f2351&requirePictures=true";

         ImageRecipe = (ImageView) findViewById(R.id.recipe_image);
        TextView  RecipeName = (TextView) findViewById(R.id.recipe_name);
        //TextView CUISINE = (TextView) findViewById(R.id.cuisine);
        INGREDIENTS = (TextView) findViewById(R.id.ingredients);
        TextView SOURCE = (TextView) findViewById(R.id.source);
        TextView RATING= (TextView) findViewById(R.id.rating);
        TextView TOTALTIME= (TextView) findViewById(R.id.totaltime);



        RecipeName.setText(recipe_name);
       // CUISINE.setText(cuisine);
        SOURCE.setText(source);
        RATING.setText(rating);
        TOTALTIME.setText(total_time + " min");

        new GetRecipeInfo().execute();

    }

    private class GetRecipeInfo extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progress = new ProgressDialog(SingleItemList.this);
            progress.setTitle("FeedMe");
            progress.setMessage("Loading...");
            progress.setIndeterminate(false);
            progress.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            jsonobject = JSONfunctions
                    .getJSONfromURL(url);

            try {

                jsonarray = jsonobject.getJSONArray("images");
               // recipe_image  = jsonarray.getJSONObject(0).getJSONObject("imageUrlsBySize").getString("360");
                recipe_image  = jsonarray.getJSONObject(0).getString("hostedLargeUrl");

                JSONArray jsonStrings = jsonobject.getJSONArray("ingredientLines");

                for(int i=0;i<jsonStrings.length();i++) {
                    list_of_ingredients.add(jsonStrings.getString(i));
                }



            } catch (JSONException e) {
                //Log.e("Couldn't get any data from the url", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void args) {
            imageLoader.DisplayImage(recipe_image, ImageRecipe);

            INGREDIENTS.setLines(list_of_ingredients.size());


            for (int i=0; i<list_of_ingredients.size(); i++) {
                sb.append(list_of_ingredients.get(i));
                sb.append("\n");

            }
            INGREDIENTS.setText(sb.toString());
           // "Ingredients: \n"+
                    //INGREDIENTS.setText(list_of_ingredients.get(0)+ "\n" +list_of_ingredients.get(1));
            progress.dismiss();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.single, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.action_share:
                share_Action();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    private void share_Action() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, recipe_name + "\n" + "\n"+ sb.toString());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
        //startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
       // + "Ingredients: \n"
    }
}

