package com.danielhedren.trafikinformation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLayout refreshView;
    private RecyclerView recyclerView;
    private ArrayList<Deviation> dataset = new ArrayList<>();

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
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
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
            try {
                JSONObject json = new JSONObject(s);
                JSONArray result = json.getJSONObject("RESPONSE").getJSONArray("RESULT").getJSONObject(0).getJSONArray("Situation");
                for (int i = 0; i < result.length(); i++) {
                    JSONObject deviation = result.getJSONObject(i).getJSONArray("Deviation").getJSONObject(0);

                    if (!dataset.contains(deviation)) {
                        dataset.add(new Deviation(deviation));
                    }
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

            String result;

            try {
                URL url = new URL("https://api.trafikinfo.trafikverket.se/v1.3/data.json");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                // TODO: Break request builder out into its own class
                String request =
                        "<REQUEST>" +
                            "<LOGIN authenticationkey=\"10e4048173064997a88edcd9defb0a5a\" />" +
                            "<QUERY objecttype=\"Situation\">" +
                                "<FILTER>" +
                                    "<WITHIN name=\"Deviation.Geometry.WGS84\" shape=\"center\" value=\"13.005 55.559\" radius=\"100000m\" />" +
                                "</FILTER>" +
                            "</QUERY>" +
                        "</REQUEST>";
                connection.getOutputStream().write(request.getBytes("UTF-8"));
                connection.getOutputStream().flush();
                connection.getOutputStream().close();

                Log.d("REQUEST", request);

                Log.d("RESPONSE", String.valueOf(connection.getResponseCode()));

                InputStream response = null;
                if (connection.getResponseCode() == 200) response = connection.getInputStream();
                else response = connection.getErrorStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(response));
                StringBuilder sb = new StringBuilder();

                try {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

                result = sb.toString();
                reader.close();
                Log.d("RESPONSE", result);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            return result;
        }
    }
}
