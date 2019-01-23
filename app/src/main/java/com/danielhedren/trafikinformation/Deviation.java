package com.danielhedren.trafikinformation;

import org.json.JSONException;
import org.json.JSONObject;

public class Deviation {
    private JSONObject data;

    public Deviation (JSONObject data) {
        this.data = data;
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
}
