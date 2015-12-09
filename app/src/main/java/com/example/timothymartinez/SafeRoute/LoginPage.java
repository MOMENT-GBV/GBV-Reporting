package com.example.timothymartinez.SafeRoute;

import android.app.Activity;
import android.content.Intent;
import android.os.StrictMode;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.UCSB.SafeRoute.R;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.DefaultSyncCallback;
import com.amazonaws.regions.Regions;

import java.util.List;

public class LoginPage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        StrictMode.VmPolicy policy1 = StrictMode.VmPolicy.LAX;
        StrictMode.setVmPolicy(policy1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void anonymousLogin(View v){
        Intent i = new Intent(this, LaunchPage.class);
        startActivity(i);
    }
    public void userLogin(View v){
            boolean loginAllowed = authenticateUser();
        if (loginAllowed){
            goToMainPage();
            //also set userID locally, and save access token.
            //something about caching credentials?
        }
    }
    public void createNewUser(View v){
            //launch create new user activity
        Intent i = new Intent(this, CreateNewUserAccount.class);
        startActivity(i);
    }
    public boolean authenticateUser(){
        ApiFunctions d = new ApiFunctions();
        Log.d("Starting","Authing user");
        String email = ((TextView)findViewById(R.id.editText9)).getText().toString();
        String password = d.generatePasswordHash(((TextView) findViewById(R.id.editText10)).getText().toString());

        authenticationProvider provider = new authenticationProvider(null, getString(R.string.aws_api_key),
                        Regions.US_EAST_1);
        try{
        provider.user_email = email;
        provider.user_password = password;
        provider.refresh();

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(getApplicationContext(),
                provider, Regions.US_EAST_1);
        CognitoSyncManager syncClient = new CognitoSyncManager(getApplicationContext(), Regions.US_EAST_1, credentialsProvider);

        Dataset dataset = syncClient.openOrCreateDataset("myDataset");
        dataset.put("email", email);

        dataset.synchronize(new DefaultSyncCallback() {
            @Override
            public void onSuccess(Dataset dataset, List newRecords) {
                //Your handler code here
                Log.d("Complete", "Authenticatoin successful, procceed to map");
            }
        });
        dataset.put("IdentityID", credentialsProvider.getIdentityId());
        dataset.put("Token", credentialsProvider.getToken());

        //user authenticate
        return true;
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(LoginPage.this, "Error logging in, try anonymous login instead", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    public void goToMainPage(){
        Intent i = new Intent(this, LaunchPage.class);
        startActivity(i);
    }
}
