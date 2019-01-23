package com.danielhedren.trafikinformation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLayout refreshView;
    private RecyclerView recyclerView;
    private ArrayList<Deviation> dataset = new ArrayList<>();

    public Location getLocation() {
        return location;
    }

    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize recyclerview
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DeviationAdapter adapter = new DeviationAdapter(dataset);
        recyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Check for and request permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        } else {
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER); // TODO: Make a location handler
            Log.d("LOCATION", location.toString());
        }

        // Attach refresh listener
        refreshView = findViewById(R.id.refreshView);
        refreshView.setOnRefreshListener(() -> {
            new FetchDataTask().execute();
        });

        new FetchDataTask().execute();
    }

    private class FetchDataTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                // TODO: Notify user
                refreshView.setRefreshing(false);
                return;
            }

            dataset.clear();

            try {
                JSONObject json = new JSONObject(s);
                JSONArray result = json.getJSONObject("RESPONSE").getJSONArray("RESULT").getJSONObject(0).getJSONArray("Situation");
                for (int i = 0; i < result.length(); i++) {
                    JSONObject deviation = result.getJSONObject(i).getJSONArray("Deviation").getJSONObject(0);

                    if (!dataset.contains(deviation)) { // TODO: Make this actually work
                        dataset.add(new Deviation(deviation));
                    }
                }

                if (Build.VERSION.SDK_INT >= 24) {
                    dataset.sort((o1, o2) -> (int) (location.distanceTo(o1.getLocation()) - location.distanceTo(o2.getLocation())));
                }

                recyclerView.getAdapter().notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            refreshView.setRefreshing(false);
        }

        @Override
        protected String doInBackground(Void... voids) {
            refreshView.setRefreshing(true);

            TrafikverketRequest request = new TrafikverketRequest(13.005, 55.559);
            return request.fetchResponse();
        }
    }
}
