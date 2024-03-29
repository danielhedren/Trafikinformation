package com.danielhedren.trafikinformation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private SwipeRefreshLayout refreshView;
    private RecyclerView recyclerView;
    private ArrayList<Deviation> dataset = new ArrayList<>();

    private String response;
    private Date responseTime;

    public Location getLocation() {
        return location;
    }

    private Location location = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, this);
            if (location == null) {
                location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if (location == null) {
                location = new Location("");
                location.setLatitude(55.559);
                location.setLongitude(13.005);
            }
            if (location != null) Log.d("LOCATION", location.toString());
        }

        // Attach refresh listener
        refreshView = findViewById(R.id.refreshView);
        refreshView.setOnRefreshListener(() -> {
            responseTime = null;
            new FetchDataTask().execute();
        });

        new FetchDataTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ArrayList<DeviationData> deviationData = new ArrayList<>();
        for (Deviation d : dataset) {
            deviationData.add(new DeviationData(d));
        }

        if (item.getItemId() == R.id.action_map) {
            Intent intent = new Intent(this, DeviationMap.class);
            intent.putExtra("deviations", deviationData);
            intent.putExtra("location", location);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onLocationChanged(Location newLocation) {
        location = newLocation;

        if (Build.VERSION.SDK_INT >= 24 && location != null) {
            dataset.sort((o1, o2) -> (int) (location.distanceTo(o1.getLocation()) - location.distanceTo(o2.getLocation())));
        }

        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private class FetchDataTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPostExecute(String s) {
            if (s == null) {
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

                if (Build.VERSION.SDK_INT >= 24 && location != null) {
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
            runOnUiThread(() -> refreshView.setRefreshing(true));

            if (responseTime != null && ((new Date()).getTime() - responseTime.getTime()) < 30 * 60 * 1000) {
                return response;
            }

            if (location != null) {
                TrafikverketRequest request = new TrafikverketRequest(location.getLatitude(), location.getLongitude(), getString(R.string.trafikverket_key));
                Log.d("request", request.fetchResponse());
                response = request.fetchResponse();
                responseTime = new Date();
                return response;
            }

            return null;
        }
    }
}
