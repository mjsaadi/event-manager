package tsms.recyclerview;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AllProjectsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    Context cn = this;

    List<Team> teamList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_projects);

        //retriveTeams();

        toolbar = (Toolbar) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_all_projects);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_assigned_projects:
                        Toast.makeText(AllProjectsActivity.this, "Assigned Teams", Toast.LENGTH_SHORT).show();
                        Intent first = new Intent(AllProjectsActivity.this, MainActivity.class);
                        startActivity(first);
                        item.setChecked(false);
                        break;
                    case R.id.menu_all_projects:
                        Toast.makeText(AllProjectsActivity.this, "You're in All Teams", Toast.LENGTH_SHORT).show();
                        item.setChecked(true);
                        break;
                    case R.id.menu_timeline:
                        Toast.makeText(AllProjectsActivity.this, "Timeline", Toast.LENGTH_SHORT).show();
                        Intent second = new Intent(AllProjectsActivity.this, Timeline.class);
                        startActivity(second);
                        break;

                    case R.id.menu_chat:
                        Toast.makeText(AllProjectsActivity.this, "Chat", Toast.LENGTH_SHORT).show();
                        Intent third = new Intent(AllProjectsActivity.this, ChatActivity.class);
                        startActivity(third);
                        break;

                    case R.id.menu_parking:
                        Toast.makeText(AllProjectsActivity.this, "Parking", Toast.LENGTH_SHORT).show();
                        Intent fourth = new Intent(AllProjectsActivity.this, Parking.class);
                        startActivity(fourth);
                        break;
                }
                return true;
            }
        });


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        setTitle("All Teams");
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


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
                AlertDialog.Builder dialog = new AlertDialog.Builder(AllProjectsActivity.this);
                //dialog.setCancelable(true);
                dialog.setTitle("Logging Out?");
                dialog.setMessage("Are you sure you want to log out from TSMSJUDGE?");
                dialog.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(AllProjectsActivity.this, "Logging Out", Toast.LENGTH_SHORT).show();
                        SharedPref.getInstance(cn).logout();
                        Intent logout = new Intent(AllProjectsActivity.this, LoginActivity.class);
                        startActivity(logout);
                    }
                }).setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(AllProjectsActivity.this, "Logout Cancelled", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });

                final AlertDialog alert = dialog.create();
                alert.show();
                break;
            // action with ID action_settings was selected
            case R.id.info_icon:
                Toast.makeText(AllProjectsActivity.this, "All Projects Activity Info. Page", Toast.LENGTH_SHORT).show();
                Intent allProjectsActivityInfoPage = new Intent(AllProjectsActivity.this, AllProjectsActivityInfoPage.class);
                startActivity(allProjectsActivityInfoPage);
                //Intent logout = new Intent(MainActivity.this, LoginActivity.class);
                //startActivity(logout);
                break;
            default:
                break;
        }

        return true;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FirstTabAll(), "All");
        adapter.addFragment(new SecondTabAll(), "Comp. Sci.");
        adapter.addFragment(new ThirdTabAll(), "Eng.");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    /*
    private void retriveTeams() {


        final String event = ((MyApplication) getApplication()).getEvent();
        class RetrieveTeams extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
               
            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                Log.d("event",event);
                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("Event", event);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_rTeam, params);
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //hiding the progressbar after completion
                try {
                    JSONObject obj = new JSONObject(s);
                    int[] covers = new int[]{
                            R.drawable.background,
                            R.drawable.background,
                            R.drawable.background,
                            R.drawable.background,
                            R.drawable.background,
                            R.drawable.background,
                            R.drawable.background,
                            R.drawable.background,
                            R.drawable.background,
                            R.drawable.background,
                            R.drawable.background
                    };
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        //getting the user from the response
                        JSONObject teamJson = obj.getJSONObject("teams");

                        //creating a new user object
                        for(int i = 0; i < teamJson.length();i++) {
                            String index = "team" + Integer.toString(i);
                            Team team = new Team(
                                    teamJson.getJSONObject(index).getString("tname"),
                                    teamJson.getJSONObject(index).getString("mentor"),
                                    teamJson.getJSONObject(index).getString("institution"),
                                    teamJson.getJSONObject(index).getString("event"),
                                    teamJson.getJSONObject(index).getInt("votes"),
                                    teamJson.getJSONObject(index).getString("type"),
                                     covers[i]);

                            teamList.add(team);
                        }

                        ((MyApplication) getApplication()).teamList = teamList;

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
    }*/


}//end of class
