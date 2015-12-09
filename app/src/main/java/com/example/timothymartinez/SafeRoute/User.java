package com.example.timothymartinez.SafeRoute;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by timothymartinez on 10/13/15.
 */
public class User {
    private String user_name;
    private String user_age;
    private String user_city;
    private String user_country;
    private String user_email;
    private String user_gender;
    private String user_password;

    public User(String name, String age, String email, String city, String country, String gender, String password){
        user_name = name;
        user_email = email;
        user_city = city;
        user_country = country;
        user_gender = gender;
        user_password = password;
        user_age = age;
    }
    public JSONObject getJSON(){
        JSONObject user = new JSONObject();
        try {
            user.put("user_name", user_name);
            user.put("user_age", user_age);
            user.put("user_email", user_email);
            user.put("user_city", user_city);
            user.put("user_country", user_country);
            user.put("user_password", user_password);
            user.put("user_gender", user_gender);
        }
        catch (Exception e){
            Log.d("Error", e.getStackTrace().toString());
        }
        return user;
    }
    public void submitUser(){
        ApiFunctions functions = new ApiFunctions();
        functions.makeAPICall(getJSON().toString(), APIEnum.ADDUSER);
    }
}
