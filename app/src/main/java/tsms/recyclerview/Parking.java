package tsms.recyclerview;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Parking extends AppCompatActivity
{

    private Toolbar toolbar;

    private TextView carLicensePlateText; //remove
    private TextView plateSourceText;

    private EditText carLicensePlateEdit;
    private EditText plateSourceEdit;

    private Button submit;

    private Spinner staticSpinner;
    private Spinner carColorSpinner;

    private String spinnerInput;
    private String carColorSpinnerInput;

    private EditText ownerNameEdit;
    Context cn = this;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);


        toolbar = (Toolbar) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        carLicensePlateText = (TextView) findViewById(R.id.car_license_plate_text);
        plateSourceText = (TextView) findViewById(R.id.car_source_text);

        carLicensePlateEdit = (EditText) findViewById(R.id.car_license_plate_input);

        staticSpinner = (Spinner) findViewById(R.id.static_spinner);

        carColorSpinner = (Spinner) findViewById(R.id.car_color_spinner);

        ownerNameEdit = (EditText) findViewById(R.id.owner_name_input);


        // Create an ArrayAdapter using the string array and a default spinner
        final ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter.createFromResource(this, R.array.city_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        staticSpinner.setAdapter(staticAdapter);

        staticSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                spinnerInput = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                // TODO Auto-generated method stub
            }
        });

        // Create an ArrayAdapter using the string array and a default spinner
        final ArrayAdapter<CharSequence> staticAdapterCarColor = ArrayAdapter.createFromResource(this, R.array.car_color_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapterCarColor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        carColorSpinner.setAdapter(staticAdapterCarColor);

        carColorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                carColorSpinnerInput = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                // TODO Auto-generated method stub
            }
        });

        submit = (Button) findViewById(R.id.parking_button);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (carLicensePlateEdit.getText().toString().equals(""))
                    {
                        Toast.makeText(Parking.this, "Car License Plate Empty!", Toast.LENGTH_SHORT).show();
                        //submit to database
                    }
                    else
                    {
                        String ownerNameInput = ownerNameEdit.getText().toString().trim();
                        String plateInput = carLicensePlateEdit.getText().toString().trim();

                        park(plateInput,spinnerInput,carColorSpinnerInput,ownerNameInput);
                        Toast.makeText(Parking.this, "Plate #: " + plateInput + "\nCity: " + spinnerInput + "\nCar Color: " + carColorSpinnerInput + "\nOwner Name: " + ownerNameInput, Toast.LENGTH_LONG).show();
                        //submit to database
                    }
                }
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_parking);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_assigned_projects:
                        Toast.makeText(Parking.this, "Assigned Teams", Toast.LENGTH_SHORT).show();
                        Intent first = new Intent(Parking.this, MainActivity.class);
                        startActivity(first);
                        break;
                    case R.id.menu_all_projects:
                        Toast.makeText(Parking.this, "All Teams", Toast.LENGTH_SHORT).show();
                        Intent second = new Intent(Parking.this, AllProjectsActivity.class);
                        startActivity(second);
                        break;
                    case R.id.menu_timeline:
                        Toast.makeText(Parking.this, "Timeline", Toast.LENGTH_SHORT).show();
                        Intent third = new Intent(Parking.this, Timeline.class);
                        startActivity(third);
                        break;

                    case R.id.menu_chat:
                        Toast.makeText(Parking.this, "Chat", Toast.LENGTH_SHORT).show();
                        Intent fourth = new Intent(Parking.this, ChatActivity.class);
                        startActivity(fourth);
                        break;

                    case R.id.menu_parking:
                        Toast.makeText(Parking.this, "You're in Parking", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });

        setTitle("Parking");
}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.logout_icon:
                AlertDialog.Builder dialog = new AlertDialog.Builder(Parking.this);
                //dialog.setCancelable(true);
                dialog.setTitle("Logging Out?");
                dialog.setMessage("Are you sure you want to log out from TSMSJUDGE?" );
                dialog.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(Parking.this, "Logging Out", Toast.LENGTH_SHORT).show();
                        SharedPref.getInstance(cn).logout();
                        Intent logout = new Intent(Parking.this, LoginActivity.class);
                        startActivity(logout);
                    }
                }).setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(Parking.this, "Logout Cancelled", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });

                final AlertDialog alert = dialog.create();
                alert.show();
                break;
            // action with ID action_settings was selected
            case R.id.info_icon:
                Toast.makeText(Parking.this, "Parking Info. Page", Toast.LENGTH_SHORT).show();
                Intent parkingInfoPage = new Intent(Parking.this, ParkingInfoPage.class);
                startActivity(parkingInfoPage);
                break;
            default:
                break;
        }

        return true;
    }

    private void park(final String pn, final String ps, final String col, final String on) {
        final User user = SharedPref.getInstance(this).getUser();

        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        final Team team =  ((MyApplication) getApplication()).team;




        class RegisterJudge extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //TODO: fix location code
                //creating request parameters
                HashMap<String, String> params = new HashMap<>();

                params.put("reg",user.getId());
                params.put("owner", on);
                params.put("color", col );
                params.put("plate", pn);
                params.put("state", ps);


                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_iCar, params);
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //hiding the progressbar after completion
                try {
                    JSONObject obj = new JSONObject(s);

                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), "Some error occurred", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        //executing the async task
        RegisterJudge re = new RegisterJudge();
        re.execute();

    }

}
//end of class
