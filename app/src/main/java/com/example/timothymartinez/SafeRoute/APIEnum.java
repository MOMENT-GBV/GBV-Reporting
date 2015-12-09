package com.example.timothymartinez.SafeRoute;

import java.net.URL;

/**
 * Created by timothymartinez on 10/5/15.
 */
public enum APIEnum {
    ADDLOCATION("",""),
    ADDUSER("https://n4vkb2men9.execute-api.us-west-2.amazonaws.com/Live/user", "POST"),
    ADDINCIDENTREPORT("https://n4vkb2men9.execute-api.us-west-2.amazonaws.com/Live/incident", "POST"),
    //UPDATEINCIDENTREPORT("https://etesq0k4sa.execute-api.us-west-2.amazonaws.com/live/incident","PUT"),
    DELETEINCIDENTREPORT("https://n4vkb2men9.execute-api.us-west-2.amazonaws.com/Live/incident","DELETE"),
    //GETINCIDENTREPORT("https://etesq0k4sa.execute-api.us-west-2.amazonaws.com/live/incident","GET"),
    GETUSER("https://n4vkb2men9.execute-api.us-west-2.amazonaws.com/Live/user", "GET"),
    UPDATEUSER("https://n4vkb2men9.execute-api.us-west-2.amazonaws.com/Live/user", "PUT"),
    GETINCIDENTSBYLATLNG("https://n4vkb2men9.execute-api.us-west-2.amazonaws.com/Live/latlng", "POST"),
    AUTHENTICATEUSER("https://n4vkb2men9.execute-api.us-west-2.amazonaws.com/Live/authenticate", "POST");
    ;
    public URL url;
    public String methodType;
    APIEnum(String apiUrl, String method){
        try {
            url = new URL(apiUrl);
        }
        catch(Exception e){
            url = null;
        }
        methodType = method;
    }

}
