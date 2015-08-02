package com.example.tochi.feedme;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

    public class ListViewAdapter extends BaseAdapter {

        Context context;
        LayoutInflater inflater;

        ArrayList<HashMap<String, String>> data;

        ImageLoader imageLoader;

        HashMap<String, String> resultp = new HashMap<String, String>();

        public ListViewAdapter(Context context,
                               ArrayList<HashMap<String, String>> List) {
            this.context = context;
            data = List;
            imageLoader = new ImageLoader(context);
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            ImageView recipe_image;
            TextView recipe_name;
            TextView cuisine;
            TextView source;
            TextView rating;
            TextView totaltime;

            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View itemView = inflater.inflate(R.layout.recipe_list, parent, false);
            // Get the position
            resultp = data.get(position);

            recipe_image = (ImageView) itemView.findViewById(R.id.recipe_image);
            recipe_name = (TextView) itemView.findViewById(R.id.recipe_name);
            cuisine = (TextView) itemView.findViewById(R.id.cuisine);
            source = (TextView) itemView.findViewById(R.id.source);
            rating = (TextView) itemView.findViewById(R.id.rating);
            totaltime = (TextView) itemView.findViewById(R.id.totaltime);

            recipe_name.setText(resultp.get(MainActivity.Recipe_Name));
            cuisine.setText(resultp.get(MainActivity.Cuisine));
            source.setText(resultp.get(MainActivity.Recipe_Source));
            rating.setText(resultp.get(MainActivity.Rating) + "/5");

            //String test = resultp.get(MainActivity.TotalTime);
           // int time_taken = NumberFormat.getInstance().parse(test).intValue();


            totaltime.setText(resultp.get(MainActivity.TotalTime) + " minutes");

           imageLoader.DisplayImage(resultp.get(MainActivity.Recipe_Image),recipe_image);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    resultp = data.get(position);

                    Intent intent = new Intent(context, SingleItemList.class);

                    intent.putExtra("image_food", resultp.get(MainActivity.Recipe_Image));
                    intent.putExtra("recipeName", resultp.get(MainActivity.Recipe_Name));
                    intent.putExtra("id", resultp.get(MainActivity.Recipe_ID));
                    intent.putExtra("cuisine", resultp.get(MainActivity.Cuisine));
                    intent.putExtra("sourceDisplayName",resultp.get(MainActivity.Recipe_Source));
                    intent.putExtra("rating", resultp.get(MainActivity.Rating));
                    intent.putExtra("totalTimeInSeconds", resultp.get(MainActivity.TotalTime));

                    context.startActivity(intent);

                }
            });
            return itemView;
        }
    }

