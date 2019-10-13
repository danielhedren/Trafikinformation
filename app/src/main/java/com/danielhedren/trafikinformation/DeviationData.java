package com.danielhedren.trafikinformation;

import java.io.Serializable;

public class DeviationData implements Serializable {
    public double latitude, longitude;
    public String messageType, roadNumber, message, locationDescriptor, severityText;
    public int severity;

    public DeviationData(Deviation d) {
        latitude = d.getLocation().getLatitude();
        longitude = d.getLocation().getLongitude();
        messageType = d.getMessageType();
        roadNumber = d.getRoadNumber();
        message = d.getMessage();
        locationDescriptor = d.getTag("LocationDescriptor");
        severityText = d.getTag("SeverityText");
        severity = Integer.parseInt(d.getTag("SeverityCode"));
    }
}
