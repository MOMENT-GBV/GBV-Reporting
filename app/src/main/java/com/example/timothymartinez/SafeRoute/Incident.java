package com.example.timothymartinez.SafeRoute;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by timothymartinez on 10/13/15.
 */
public class Incident {
    public String user_id;
    public String incident_description;
    public String incident_latitude;
    public String incident_longitude;
    public String incident_datetime;
    public String incident_type;
    public String incident_id;
    public String incident_location_type_ids;
    public String incident_harassment_type_ids;

    Incident(String userid, String incidentdescription, String latitude, String Longitude, String Datetime, String type, String incident_location_type){
        this.user_id = userid;
        this.incident_description = incidentdescription;
        this.incident_latitude = latitude;
        this.incident_longitude = Longitude;
        this.incident_datetime = Datetime;
        this.incident_type = type;
        this.incident_location_type_ids = incident_location_type;
    }
    Incident(String incidentdescription, String latitude, String Longitude, String Datetime, String type){
        this.incident_description = incidentdescription;
        this.incident_latitude = latitude;
        this.incident_longitude = Longitude;
        this.incident_datetime = Datetime;
        this.incident_type = type;
    }
    public JSONObject getJSON(){
        JSONObject incident = new JSONObject();
        try {
            incident.put("incident_latitude", this.incident_latitude);
            incident.put("incident_longitude", this.incident_longitude);
            incident.put("incident_description", this.incident_description);
            incident.put("incident_type", this.incident_type);
            incident.put("incident_datetime", this.incident_datetime);
            incident.put("incident_user_identity_id", this.user_id);
            incident.put("incident_harassment_type_ids", getIncidentType(this.incident_type));
            incident.put("incident_location_type_ids", getIncidentLocationID(this.incident_location_type_ids));
        }
        catch (Exception e){
            Log.d("Error", e.getStackTrace().toString());
        }
        return incident;
    }
    public void submitIncident(){
        ApiFunctions functions = new ApiFunctions();
        functions.makeAPICall(getJSON().toString(), APIEnum.ADDINCIDENTREPORT);
    }
    private String getIncidentType(String type){;
        if(type.equals("Verbal Harrassment")){
            return "0";
        }
        if(type.equals("Physial Confrontation")){
            return  "1";
        }
        if(type.equals("Other")){
            return "2";
        }
        return  null;
    }
    private String getIncidentLocationID(String locationType){
        if(locationType.equals("Walking"))
            return "1";
        if(locationType.equals("Bus"))
            return "0";
        if(locationType.equals("Train"))
            return "2";
        if(locationType.equals("Taxi"))
            return "3";
        return null;
    }
}
