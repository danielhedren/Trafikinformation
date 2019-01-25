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
        if (data.has("Message")) {
            try {
                return data.getString("Message");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return "";
    }

    public String getSeverityText() {
        if (data.has("SeverityText")) {
            try {
                return data.getString("SeverityText");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return "";
    }

    public String getRoadNumber() {
        if (data.has("RoadNumber")) {
            try {
                return data.getString("RoadNumber");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return "";
    }

    public String getMessageType() {
        if (data.has("MessageType")) {
            try {
                return data.getString("MessageType");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return "";
    }

    public String getTag(String tag) {
        if (data.has(tag)) {
            try {
                return data.getString(tag);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public Location getLocation() {
        return location;
    }
}
