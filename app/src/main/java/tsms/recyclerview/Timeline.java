package tsms.recyclerview;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class Timeline extends AppCompatActivity {

    List<DataModal> dataModals = new ArrayList<>();
    private Toolbar toolbar;
    private ViewPager viewPager;
    Context cn = this;

    //private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);



        toolbar = (Toolbar) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
rTimeline();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

       // swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        //swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
        //    @Override
        //    public void onRefresh() {
        //        //new GetLinks().execute();
        //    }
      //  });

        viewPager = (ViewPager) findViewById(R.id.viewpager_timeline);
        setupViewPager(viewPager);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_timeline);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_assigned_projects:
                        Toast.makeText(Timeline.this, "Assigned Teams", Toast.LENGTH_SHORT).show();
                        Intent first = new Intent(Timeline.this, MainActivity.class);
                        startActivity(first);
                        break;
                    case R.id.menu_all_projects:
                        Toast.makeText(Timeline.this, "All Teams", Toast.LENGTH_SHORT).show();
                        Intent second = new Intent(Timeline.this, AllProjectsActivity.class);
                        startActivity(second);
                        break;
                    case R.id.menu_timeline:
                        Toast.makeText(Timeline.this, "You're in Timeline!", Toast.LENGTH_SHORT).show();
                        // Intent second = new Intent(MainActivity.this, Playboard.class);
                        // startActivity(second);
                        break;

                    case R.id.menu_chat:
                        Toast.makeText(Timeline.this, "Chat", Toast.LENGTH_SHORT).show();
                        Intent third = new Intent(Timeline.this, ChatActivity.class);
                        startActivity(third);
                        break;

                    case R.id.menu_parking:
                        Toast.makeText(Timeline.this, "Parking", Toast.LENGTH_SHORT).show();
                        Intent fourth = new Intent(Timeline.this, Parking.class);
                        startActivity(fourth);
                        break;
                }
                return true;
            }
        });

        setTitle("Timeline");
    }

    private void setupViewPager(ViewPager viewPager) {
        Timeline.ViewPagerAdapter adapter = new Timeline.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TimelineFrag(), "TimelineFrag");
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
                AlertDialog.Builder dialog = new AlertDialog.Builder(Timeline.this);
                //dialog.setCancelable(true);
                dialog.setTitle("Logging Out?");
                dialog.setMessage("Are you sure you want to log out from TSMSJUDGE?" );
                dialog.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(Timeline.this, "Logging Out", Toast.LENGTH_SHORT).show();
                        SharedPref.getInstance(cn).logout();
                        Intent logout = new Intent(Timeline.this, LoginActivity.class);
                        startActivity(logout);
                    }
                }).setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(Timeline.this, "Logout Cancelled", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });

                final AlertDialog alert = dialog.create();
                alert.show();
                break;
            // action with ID action_settings was selected
            case R.id.info_icon:
                Toast.makeText(Timeline.this, "Timeline Info. Page", Toast.LENGTH_SHORT).show();
                Intent timelineActivityInfoPage = new Intent(Timeline.this, TimelineInfoPage.class);
                startActivity(timelineActivityInfoPage);
                //Intent logout = new Intent(MainActivity.this, LoginActivity.class);
                //startActivity(logout);
                break;
            default:
                break;
        }

        return true;
    }

    private void rTimeline() {


        final String eventid = ((MyApplication)  getApplication()).getEvent();

        class RetrieveStudents extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();

                params.put("eventid", eventid);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_rTimeline, params);
            }



            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //hiding the progressbar after completion
                try {
                    JSONObject obj = new JSONObject(s);

                    if (!obj.getBoolean("error")) {

                        //getting the user from the response
                        JSONObject studentJson = obj.getJSONObject("timeline");

                        //creating a new user object
                        for(int i = 0; i < studentJson.length();i++) {
                            String index = "timeline" + Integer.toString(i);
                            DataModal data = new DataModal(
                                    studentJson.getJSONObject(index).getInt("eventid"),
                                    studentJson.getJSONObject(index).getString("time"),
                                    studentJson.getJSONObject(index).getString("def")
                            );
                            dataModals.add(data);

                        }


                        Collections.sort(dataModals, new Comparator<DataModal>() {
                            @Override
                            public int compare(DataModal lhs,DataModal rhs) {
                                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                                String []s = lhs.getTime().split(":");
                                String s1= s[0] + "." + s[1];
                                float l =  Float.parseFloat(s1);

                                String []t = rhs.getTime().split(":");
                                String s2= t[0] + "." + t[1];
                                float r =  Float.parseFloat(s2);
                                Log.d("FLOATS"," a " + Float.toString(l) + " b " + Float.toString(r));
                                return l < r ? -1 : (l > r) ? 1 : 0;
                            }
                        });

                       // rvAdapter.notifyDataSetChanged();


                    } else {
                    //    Toast.makeText(getContext(), "Empty", Toast.LENGTH_SHORT).show();
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

}//end of class
