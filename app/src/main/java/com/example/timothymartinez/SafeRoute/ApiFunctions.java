package com.example.timothymartinez.SafeRoute;

import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by timothymartinez on 10/5/15.
 */

public class ApiFunctions {

    public String generatePasswordHash(String password){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] hashed = md.digest();
            return new String(Base64.encode(hashed, Base64.DEFAULT));

        }
        catch (Exception e){

        }
        return null;
    }
    public String testUserAuth(){

        return  "";
    }

    public void addNewIncident(Incident event){
        makeAPICall(event.getJSON().toString(), APIEnum.ADDINCIDENTREPORT);

    }
    public void getUserById(Integer id){
        JSONObject user = new JSONObject();
        try{
            user.put("user_id", id.toString());
        }
        catch(Exception e){

        }
        makeAPICall(user.toString(), APIEnum.GETUSER);
    }
    public void getIncidentById(Integer id){
        JSONObject incident = new JSONObject();
        try{
            incident.put("incident_id", id.toString());
        }
        catch(Exception e){

        }
        //makeAPICall(incident.toString(), APIEnum.GETINCIDENTREPORT);
    }
    public ArrayList<Incident> getIncidentsByLatLng(com.google.maps.model.LatLng start, com.google.maps.model.LatLng end, int daysAgo){
        JSONObject j = new JSONObject();
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DAY_OF_YEAR, -1*daysAgo);
        try{
            j.put("incident_startLatitude", ((Double)start.lat).toString());
            j.put("incident_startLongitude", ((Double)start.lng).toString());
            j.put("incident_endLatitude", ((Double)end.lat).toString());
            j.put("incident_endLongitude", ((Double)end.lng).toString());
            j.put("incident_date_time", instance.getTime().toString());
        }
        catch (Exception e){

        }
        JSONObject result = makeAPICall(j.toString(), APIEnum.GETINCIDENTSBYLATLNG);
        try {
            //Log.d("Result", result.getString("Count").toString());
            JSONArray array = result.getJSONArray("Items");
            Log.d("desc",array.getJSONObject(0).getJSONObject("incident_description").getString("S").toString());
            ArrayList<Incident> incidentds = new ArrayList<Incident>();
            for (int i = 0; i < array.length(); i++){
                String description = array.getJSONObject(i).getJSONObject("incident_description").getString("S");
                String lat = array.getJSONObject(i).getJSONObject("incident_latitude").getString("N");
                String longi = array.getJSONObject(i).getJSONObject("incident_longitude").getString("N");
                String time = array.getJSONObject(i).getJSONObject("incident_datetime").getString("S");
                String type = array.getJSONObject(i).getJSONObject("incident_type").getString("S");
                Incident inc = new Incident(description, lat, longi, time,type );
                incidentds.add(inc);
                return incidentds;



            }
        }
        catch (Exception e){
                e.printStackTrace();
        }
        return null;

    }
    public void deleteIncident(Integer id){
        JSONObject incident = new JSONObject();
        try {
            incident.put("incident_id", id.toString());
        }
        catch(Exception e ){

        }
        makeAPICall(incident.toString(), APIEnum.DELETEINCIDENTREPORT);
    }
    public void updateIncident(Integer ID, Incident newIncident){
        JSONObject incident = newIncident.getJSON();
        try{
            incident.put("incident_id", ID.toString());
            incident.put("incident_locationid", "2322");
        }
        catch (Exception e){

        }
        //makeAPICall(incident.toString(), APIEnum.UPDATEINCIDENTREPORT);
    }
    public void updateUser(Integer id, User newUser){
        JSONObject user = newUser.getJSON();
        try{
            user.put("user_id", id.toString());
        }
        catch (Exception e){

        }
        makeAPICall(user.toString(), APIEnum.UPDATEUSER);
    }
    public void addNewUser(String UserName, String UserAge, String UserCity, String UserCountry, String UserEmail, String UserGender, String UserPassword){
        JSONObject user = new JSONObject();
        try{
            user.put("user_name", UserName);
            user.put("user_age", UserAge);
            user.put("user_city", UserCity);
            user.put("user_country", UserCountry);
            user.put("user_email", UserEmail);
            user.put("user_gender", UserGender);
            user.put("user_password", generatePasswordHash(UserPassword));//generatePasswordHash(UserPassword));
        }
        catch (Exception e){
            e.printStackTrace();

        }
        makeAPICall(user.toString(), APIEnum.ADDUSER);
    }
    public JSONObject makeAPICall(String JSONBody, APIEnum Call){
        try {
            //TEMPORARY
            //TODO:REMOVE!!!! UNSAFE!
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);


            URL url = Call.url;
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            //connection.setDoInput(true);
            connection.setRequestMethod(Call.methodType);
            connection.setRequestProperty("x-api-key","gJdVo3OtzQvKuup7EsT14sc4TvNyphU8Q4JRhJ51");

            OutputStream requestBody = connection.getOutputStream();
            Log.d("Request body", JSONBody);


            OutputStream d = connection.getOutputStream();
            OutputStream contentToPost = new BufferedOutputStream(d);

            contentToPost.write(JSONBody.getBytes());
            contentToPost.flush();

            InputStream response = connection.getInputStream();
            InputStream responseToRead = new BufferedInputStream(response);

            InputStreamReader reader = new InputStreamReader(response);
            BufferedReader buffreader = new BufferedReader(reader);
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = buffreader.readLine()) != null){
                sb.append(line);
            }
            String s= sb.toString();
            Log.d("Response body:", s);
            JSONObject j = new JSONObject(s);

            connection.disconnect();
            Log.d("Response code", connection.getResponseCode() + "");
            Log.d("Successs!", "It worked :)");
            return j;



        }
        catch(Exception e){
            Log.d("Error", "Failure");
            Log.d("Error", e.getMessage() + "");
            e.printStackTrace();
        }
        return null;
    }
}
