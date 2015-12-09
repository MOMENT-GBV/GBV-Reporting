package com.example.timothymartinez.SafeRoute;

import android.util.Log;

import com.amazonaws.auth.AWSAbstractCognitoDeveloperIdentityProvider;
import com.amazonaws.regions.Regions;

import org.json.JSONObject;

/**
 * Created by timothymartinez on 11/9/15.
 */
public class authenticationProvider extends AWSAbstractCognitoDeveloperIdentityProvider {
    public String user_id;
    public String user_email;
    public String user_password;

    private static final String developerProvider = "login.UCSB.GBVAndroidApplication";

    public authenticationProvider (String accountId, String identityPoolId, Regions region) {
        super(accountId, identityPoolId, region);
        // Initialize any other objects needed here.
    }

    // Return the developer provider name which you choose while setting up the
    // identity pool in the Amazon Cognito Console

    @Override
    public String getProviderName() {
        return developerProvider;
    }

    // Use the refresh method to communicate with your backend to get an
    // identityId and token.

    @Override
    public String refresh() {

        // Override the existing token
        setToken(null);

        // Get the identityId and token by making a call to your backend
        // (Call to your backend)
        ApiFunctions f = new ApiFunctions();
        JSONObject userInfo = new JSONObject();
        try {
            userInfo.put("user_email", user_email);
            userInfo.put("user_password", user_password);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        JSONObject response = f.makeAPICall(userInfo.toString(), APIEnum.AUTHENTICATEUSER);
        try {
            Log.d("Response", response.toString());
            identityId = response.getString("IdentityId");
            token = response.getString("Token");
            Log.d("Token", token);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        // Call the update method with updated identityId and token to make sure
        // these are ready to be used from Credentials Provider.

        update(identityId, token);
        return token;

    }

    // If the app has a valid identityId return it, otherwise get a valid
    // identityId from your backend.

    @Override
    public String getIdentityId() {

        // Load the identityId from the cache
        //identityId = cachedIdentityId;
        if (identityId == null) {
            // Call to your backend
            ApiFunctions f = new ApiFunctions();
            JSONObject userInfo = new JSONObject();
            try {
                userInfo.put("user_email", user_email);
                userInfo.put("user_password", user_password);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            JSONObject response = f.makeAPICall(userInfo.toString(), APIEnum.AUTHENTICATEUSER);
            try {
                identityId = response.getString("IdentityId");
                token = response.getString("Token");
                Log.d("Token", token);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return identityId;
        } else {
            return identityId;
        }

    }
}