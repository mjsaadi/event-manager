package tsms.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

//Mind the commented code
public class LoginActivity extends AppCompatActivity {
    EditText loginCodeObj;
    Button loginButtonObj;
    int counter = 5;

    //this function does not connect to the database, just verified login if it is 0000


    //this is the login functions which connect to the database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (SharedPref.getInstance(this).isLoggedIn()) {
            constants.event = SharedPref.getInstance(this).getUser().getEvent();
            ((MyApplication) getApplication()).setEvent(SharedPref.getInstance(this).getUser().getEvent());
            finish();
            startActivity(new Intent(this, MainActivity.class));
            return;
        }

        loginButtonObj = (Button) findViewById(R.id.login_button);
        loginCodeObj = (EditText) findViewById(R.id.login_code_editText);

        loginButtonObj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    userLogin();
                } else {
                    Toast.makeText(LoginActivity.this, "Internet Not Connected", Toast.LENGTH_SHORT).show();
                    counter--;
                    if (counter == 0) {
                        loginButtonObj.setEnabled(false);
                        Toast.makeText(LoginActivity.this, "Wrong Login Code Entered Too Many Times!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void userLogin()
    {
        //first getting the values
        final String loginCode = loginCodeObj.getText().toString();

        //validating inputs
        if (TextUtils.isEmpty(loginCode)) {
            loginCodeObj.setError("Please enter your login code");
            loginCodeObj.requestFocus();
            return;
        }


        class UserLogin extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);


                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        //getting the user from the response
                        JSONObject userJson = obj.getJSONObject("user");

                        //creating a new user object
                        User user = new User(
                                userJson.getJSONObject("log").getString("id"),
                                userJson.getJSONObject("log").getString("username"),
                                userJson.getJSONObject("log").getString("event"));


                        String l = userJson.getJSONObject("log").getString("event");
                        constants.event = l;
                        Log.d("log",l);
                        ((MyApplication) getApplication()).setEvent(l);
                        //storing the user in shared preferences
                        SharedPref.getInstance(getApplicationContext()).userLogin(user);

                        //starting the profile activity
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("reg", loginCode);


                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_LOGIN, params);
            }
        }

        UserLogin ul = new UserLogin();
        ul.execute();
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }




}