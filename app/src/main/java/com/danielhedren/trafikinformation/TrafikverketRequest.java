package com.danielhedren.trafikinformation;

import android.telecom.Connection;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class TrafikverketRequest {
    private double latitude, longitude;
    private String objectType, key;

    public TrafikverketRequest(double latitude, double longitude, String key) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.objectType = "Situation";
        this.key = key;
    }

    public HttpURLConnection openConnection() {
        URL url = null;

        try {
            url = new URL("https://api.trafikinfo.trafikverket.se/v1.3/data.json");
        } catch (MalformedURLException e) {
            Log.d("REQUEST", e.getMessage());
            return null;
        }

        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            Log.d("REQUEST", e.getMessage());
            return null;
        }

        try {
            connection.setRequestMethod("POST");
        } catch (ProtocolException e) {
            Log.d("REQUEST", e.getMessage());
            return null;
        }

        connection.setRequestProperty("Content-Type", "");
        connection.setDoOutput(true);
        connection.setDoInput(true);

        return connection;
    }

    public String getRequestString() {
        String request =
            "<REQUEST>" +
                "<LOGIN authenticationkey=\"" + key + "\" />" +
                "<QUERY objecttype=\"Situation\">" +
                    "<FILTER>" +
                        "<WITHIN name=\"Deviation.Geometry.WGS84\" shape=\"center\" value=\"" + longitude + " " + latitude + "\" radius=\"100000m\" />" +
                    "</FILTER>" +
                "</QUERY>" +
            "</REQUEST>";

        return request;
    }

    public byte[] getRequestBytesUTF8() {
        try {
            return getRequestString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String fetchResponse() {
        HttpURLConnection connection = openConnection();
        String result;
        InputStream response;

        try {
            connection.getOutputStream().write(getRequestBytesUTF8());
            connection.getOutputStream().flush();
            connection.getOutputStream().close();

            if (connection.getResponseCode() == 200) response = connection.getInputStream();
            else response = connection.getErrorStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(response));
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            result = sb.toString();

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return result;
    }
}
