package com.example.timothymartinez.SafeRoute;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.UCSB.SafeRoute.R;

public class CreateNewUserAccount extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_user_account);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_new_user_account, menu);
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
    public void CreateNewUser(View v){
        try {
            Log.d("Loading", "New user creation");
            Integer checkedId = ((RadioGroup) findViewById(R.id.radioGroup2)).getCheckedRadioButtonId();
            ApiFunctions d = new ApiFunctions();
            String user_name = ((EditText) findViewById(R.id.editText3)).getText().toString();
            String user_password = ((EditText) findViewById(R.id.editText5)).getText().toString();
            String user_email = ((EditText) findViewById(R.id.editText3)).getText().toString();
            String user_age = ((EditText) findViewById(R.id.editText6)).getText().toString();
            String user_city = ((EditText) findViewById(R.id.editText7)).getText().toString();
            String user_country = ((EditText) findViewById(R.id.editText8)).getText().toString();
            String user_gender = ((RadioButton) findViewById(checkedId)).getText().toString();

            d.addNewUser(user_name, user_age, user_city, user_country, user_email, user_gender, user_password);
            finish();
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(CreateNewUserAccount.this, "Error creating user!", Toast.LENGTH_SHORT).show();
        }

    }
}
