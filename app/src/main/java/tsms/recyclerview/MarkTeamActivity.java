package tsms.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;

public class MarkTeamActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private LinearLayout categoryLayout;

    private Context cn = this;
    private List<MarkingCriteria> markingCriteriaList = new ArrayList<>();

    final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
    final LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
    final LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
    private Button updateMark;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_team);
        ButterKnife.bind(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        categoryLayout = findViewById(R.id.categoryLayout);
        retrieveCriteria();


        updateMark = (Button) findViewById(R.id.mark_team_button);

        updateMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(MarkTeamActivity.this, "Innovation: " + innovationMark + "\nApplicability: " + applicabilityMark + "\nIntegration: " + integrationMark + "\nEffectiveness: " + effectivnessMark + "\nDemo: " + demoMark, Toast.LENGTH_LONG).show();
                //  marks(innovationMark,applicabilityMark,integrationMark,effectivnessMark,demoMark);
                final int childCount = categoryLayout.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    CardView c = (CardView) categoryLayout.getChildAt(i);
                    LinearLayout horizontalLayout = (LinearLayout) c.getChildAt(0);
                    TextView et = (TextView) horizontalLayout.getChildAt(0);
                    TextView et2 = (TextView) horizontalLayout.getChildAt(2);
                    if (!et.getText().toString().trim().isEmpty() && !et2.getText().toString().trim().isEmpty()) {
                        Log.d("ET", et2.getText().toString().trim());
                        String l = et2.getText().toString().trim();
                        l = l.replaceAll("\\s+", "");
                        Log.d("L", l);

                        mark(et.getText().toString().trim(), Integer.parseInt(l),markingCriteriaList.get(i).getTotal());
                    }
                }

                finish();
            }
        });
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_assigned_projects);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_assigned_projects:
                        Toast.makeText(MarkTeamActivity.this, "Assigned Teams", Toast.LENGTH_SHORT).show();
                        Intent first = new Intent(MarkTeamActivity.this, MainActivity.class);
                        startActivity(first);
                        break;
                    case R.id.menu_all_projects:
                        Toast.makeText(MarkTeamActivity.this, "All Teams", Toast.LENGTH_SHORT).show();
                        Intent second = new Intent(MarkTeamActivity.this, AllProjectsActivity.class);
                        startActivity(second);
                        break;
                    case R.id.menu_timeline:
                        Toast.makeText(MarkTeamActivity.this, "Timeline", Toast.LENGTH_SHORT).show();
                        Intent third = new Intent(MarkTeamActivity.this, Timeline.class);
                        startActivity(third);
                        break;

                    case R.id.menu_chat:
                        Toast.makeText(MarkTeamActivity.this, "Chat", Toast.LENGTH_SHORT).show();
                        Intent fourth = new Intent(MarkTeamActivity.this, ChatActivity.class);
                        startActivity(fourth);
                        break;

                    case R.id.menu_parking:
                        Toast.makeText(MarkTeamActivity.this, "Parking", Toast.LENGTH_SHORT).show();
                        Intent fifth = new Intent(MarkTeamActivity.this, Parking.class);
                        startActivity(fifth);
                        break;
                }
                return true;
            }
        });

        String teamNAME = "TEAMNAME";

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            teamNAME = bundle.getString("teamName");
        }
        setTitle("Marking: " + teamNAME);
    }






    private void retrieveCriteria() {


        final String event = ((MyApplication) getApplication()).getEvent();
        class RetrieveTeams extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;
            @Override
            protected void onPreExecute(){
            }
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("Event", event);


                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_rMarkingCriteria, params);
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //hiding the progressbar after completion

                try {
                    JSONObject obj = new JSONObject(s);

                    if (!obj.getBoolean("error")) {
                        JSONObject teamJson = obj.getJSONObject("marks");

                        for(int i = 0; i < teamJson.length(); i++) {               //getting the user from the response
                            String index = "mark" + i;
                            MarkingCriteria markingCriteria = new MarkingCriteria(
                                    teamJson.getJSONObject(index).getInt("id"),
                                    teamJson.getJSONObject(index).getInt("total"),
                                    teamJson.getJSONObject(index).getString("name")

                            );
                            markingCriteriaList.add(markingCriteria);
                            Log.d("RM", markingCriteriaList.get(i).getName());
                        }

                        categoryLayout.removeAllViews();

                        for(final MarkingCriteria m : markingCriteriaList ){
                            final CardView newCV = new CardView(cn);
                            LinearLayout linearLayout = new LinearLayout(cn);
                            linearLayout.setLayoutParams(params);

                            newCV.setLayoutParams(params);
                            newCV.setPadding(5,10,5,10);
                            newCV.setElevation(8f);

                            linearLayout.setLayoutParams(params);
                            linearLayout.setOrientation(LinearLayout.VERTICAL);
                            linearLayout.setPadding(10,20,10,20);
                            linearLayout.setWeightSum(1);

                            TextView newTV = new TextView(cn);
                            newTV.setLayoutParams(param);
                            newTV.setText(m.getName());
                            DiscreteSeekBar  innET = new DiscreteSeekBar(cn);
                            innET.setMin(0);
                            final TextView tv = new TextView(cn);
                            innET.setMax(m.getTotal());
                            innET.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
                                @Override
                                public int transform(int value)
                                {
                                    return value;
                                }
                            });

                            innET.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
                                                                  @SuppressLint("SetTextI18n")
                                                                  @Override
                                                                  public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                                                                      if (fromUser) {

                                                                          tv.setText(Integer.toString(value));
                                                                      }
                                                                  }

                                                                  @Override
                                                                  public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

                                                                  }

                                                                  @Override
                                                                  public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

                                                                  }

                                                              });



                            linearLayout.addView(newTV);
                            linearLayout.addView(innET);
                            linearLayout.addView(tv);
                            newCV.addView(linearLayout);

                            categoryLayout.addView(newCV);
                        }
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

    private void mark(final String name,  final int score, final int total) {
        final User user = SharedPref.getInstance(this).getUser();

        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        final Team team =  ((MyApplication) getApplication()).team;


        final String tname =  team.gettname();
        final int round = 1;



        class RegisterJudge extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //TODO: fix location code
                //creating request parameters
                HashMap<String, String> params = new HashMap<>();

                params.put("team",team.gettname());
                params.put("event", team.getEvent());
                params.put("judge", user.getLoginCode() );
                params.put("name", name);
                params.put("round", Integer.toString(round));
                params.put("score", Integer.toString(score));
                params.put("total", Integer.toString(score));

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_iMark, params);
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


}//end of class
