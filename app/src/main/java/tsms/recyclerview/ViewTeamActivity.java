package tsms.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewTeamActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView teamNameFromDb;
    private TextView projectNameFromDb;
    private TextView abstractFromDb;
    private TextView membersFromDb;
    private List<Student> studentList = new ArrayList<>();

    private ImageView teamImageFromDb;

    private Button viewMarkButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_team);


        toolbar = (Toolbar) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //teamNameFromDb = (TextView) findViewById(R.id.text_name_from_DB);
        projectNameFromDb = (TextView) findViewById(R.id.project_name_from_DB);
        abstractFromDb = (TextView) findViewById(R.id.abstract_from_DB);
        membersFromDb = findViewById(R.id.team_members_text);
        retriveStudents();
        retriveProject();


        //this wouldnt be needed bc the activity will retrieve the image from db
        //Glide.with(context).load(url) .placeholder(R.drawable.image)
        //.into(imageView);

        //Picasso.with(context).load(url) .placeholder(R.drawable.image)
         //       .into(imageView);
        teamImageFromDb = (ImageView) findViewById(R.id.team_image);


        LinearLayout layout = (LinearLayout) findViewById(R.id.team_members_linear_layout);

        //i would be less than the number of team members in the database ,, something.length
        //setText would set the dynamicTextView to the team member in the database
        //    textView.setText(textArray[i]);
        viewMarkButton = (Button) findViewById(R.id.view_team_button);

        viewMarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ViewTeamActivity.this, "Going back to All Teams", Toast.LENGTH_LONG).show();
                //submit to database to the same team given by intent
                //Intent toViewTeamActivity = new Intent(ViewTeamActivity.this, AllProjectsActivity.class);
                //startActivity(toViewTeamActivity);
                finish();//could be this.finish()
            }
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_all_projects);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_assigned_projects:
                        Toast.makeText(ViewTeamActivity.this, "Assigned Teams", Toast.LENGTH_SHORT).show();
                        Intent first = new Intent(ViewTeamActivity.this, MainActivity.class);
                        startActivity(first);
                        break;
                    case R.id.menu_all_projects:
                        Toast.makeText(ViewTeamActivity.this, "All Teams", Toast.LENGTH_SHORT).show();
                        Intent second = new Intent(ViewTeamActivity.this, AllProjectsActivity.class);
                        startActivity(second);
                        break;
                    case R.id.menu_timeline:
                        Toast.makeText(ViewTeamActivity.this, "Timeline", Toast.LENGTH_SHORT).show();
                        Intent third = new Intent(ViewTeamActivity.this, Timeline.class);
                        startActivity(third);
                        break;

                    case R.id.menu_chat:
                        Toast.makeText(ViewTeamActivity.this, "Chat", Toast.LENGTH_SHORT).show();
                        Intent fourth = new Intent(ViewTeamActivity.this, ChatActivity.class);
                        startActivity(fourth);
                        break;

                    case R.id.menu_parking:
                        Toast.makeText(ViewTeamActivity.this, "Parking", Toast.LENGTH_SHORT).show();
                        Intent fifth = new Intent(ViewTeamActivity.this, Parking.class);
                        startActivity(fifth);
                        break;
                }
                return true;
            }
        });

        String teamNAME = "TEAMNAME";

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle != null){
            teamNAME = bundle.getString("teamName");
        }
        setTitle("Viewing: " + teamNAME);
    }

    /*
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
                AlertDialog.Builder dialog = new AlertDialog.Builder(ChatActivity.this);
                //dialog.setCancelable(true);
                dialog.setTitle("Logging Out?");
                dialog.setMessage("Are you sure you want to log out from TSMSJUDGE?" );
                dialog.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(ChatActivity.this, "Logging Out", Toast.LENGTH_SHORT).show();
                        Intent logout = new Intent(ChatActivity.this, LoginActivity.class);
                        startActivity(logout);
                    }
                }).setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ChatActivity.this, "Logout Cancelled", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });

                final AlertDialog alert = dialog.create();
                alert.show();
                break;
            // action with ID action_settings was selected
            case R.id.info_icon:
                Toast.makeText(ChatActivity.this, "Info. Page", Toast.LENGTH_SHORT).show();
                //Intent logout = new Intent(MainActivity.this, LoginActivity.class);
                //startActivity(logout);
                break;
            default:
                break;
        }

        return true;
    }*/

    private void retriveStudents() {


        final String team = ((MyApplication) getApplication()).team.gettname();

        class RetrieveStudents extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("Team", team);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_rStudent, params);
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //hiding the progressbar after completion
                try {
                    JSONObject obj = new JSONObject(s);

                    if (!obj.getBoolean("error")) {

                        //getting the user from the response
                        JSONObject studentJson = obj.getJSONObject("students");

                        //creating a new user object
                        for(int i = 0; i < studentJson.length();i++) {
                            String index = "student" + Integer.toString(i);
                            Student student = new Student(
                                    studentJson.getJSONObject(index).getString("name"),
                                    studentJson.getJSONObject(index).getString("team")
                            );
                            studentList.add(student);
                            membersFromDb.setText( membersFromDb.getText().toString().trim() + "\n" + student.getName());
                        }




                    }else if(obj.getBoolean("empty")){
                        Student student = new Student(
                                "Sample Student",
                                ((MyApplication) getApplication()).team.gettname()
                        );
                        studentList.add(student);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Some error occurred", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //executing the async task
        RetrieveStudents ru = new RetrieveStudents();
        ru.execute();
    }
    Context cn = this;
    private void retriveProject() {


        final String event = ((MyApplication) getApplication()).getEvent();
        final String team = ((MyApplication) getApplication()).team.gettname();
        class RetrieveTeams extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("Event", event);
                params.put("Tname", team);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_rProjects, params);
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //hiding the progressbar after completion
                try {
                    JSONObject obj = new JSONObject(s);

                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        //getting the user from the response
                        JSONObject teamJson = obj.getJSONObject("project");


                        Project project = new Project(
                                teamJson.getString("uni"),
                                teamJson.getString("pcat"),
                                teamJson.getString("pname"),
                                teamJson.getString("pdesc")
                        );



                        projectNameFromDb.setText(project.getPname());
                        abstractFromDb.setText(project.getP_abstract());


                    } else {
                        Toast.makeText(getApplicationContext(), "Some error occurred", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //executing the async task
        RetrieveTeams ru = new RetrieveTeams();
        ru.execute();
    }


}//end of class
