package com.danielhedren.trafikinformation;

import android.location.Location;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Deviation {
    private JSONObject data;
    private Location location;

    public Deviation (JSONObject data) {
        this.data = data;
        this.location = new Location("");

        try {
            String[] WGS84 = data.getJSONObject("Geometry").getString("WGS84").split(" ");
            this.location.setLongitude(Double.valueOf(WGS84[1].substring(1)));
            this.location.setLatitude(Double.valueOf(WGS84[2].substring(0, WGS84[2].length() - 1)));
        } catch (JSONException e) {
            Log.d("LOCATION", e.getMessage());
        }
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }

    public String getMessage() {
        try {
            return data.getString("Message");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }

    public String getSeverityText() {
        try {
            return data.getString("SeverityText");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }

    public String getRoadNumber() {
        try {
            return data.getString("RoadNumber");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }

    public Location getLocation() {
        return location;
    }
}
