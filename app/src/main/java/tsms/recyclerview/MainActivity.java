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

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    Socket mSocket;
    final String MESSAGES_ENDPOINT = "http://52.27.201.136:4001";

    Context cn = this;

    {
        try {
            mSocket = IO.socket(MESSAGES_ENDPOINT);
        } catch (URISyntaxException e) {
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        toolbar = (Toolbar) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);

        retrieveEvents();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_assigned_projects:
                        Toast.makeText(MainActivity.this, "You're in Assigned Teams", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.menu_all_projects:
                        Toast.makeText(MainActivity.this, "All Teams", Toast.LENGTH_SHORT).show();
                        Intent first = new Intent(MainActivity.this, AllProjectsActivity.class);
                       startActivity(first);
                        break;
                    case R.id.menu_timeline:
                        Toast.makeText(MainActivity.this, "Timeline", Toast.LENGTH_SHORT).show();
                        Intent second = new Intent(MainActivity.this, Timeline.class);
                        startActivity(second);
                        break;

                    case R.id.menu_chat:
                        Toast.makeText(MainActivity.this, "Chat", Toast.LENGTH_SHORT).show();
                        Intent third = new Intent(MainActivity.this, ChatActivity.class);
                        startActivity(third);
                        break;

                    case R.id.menu_parking:
                        Toast.makeText(MainActivity.this, "Parking", Toast.LENGTH_SHORT).show();
                        Intent fourth = new Intent(MainActivity.this, Parking.class);
                        startActivity(fourth);
                        break;
                }
                return true;
            }
        });



        mSocket.connect();
        JSONObject obj1 = new JSONObject();
        try {
            obj1.put("username", SharedPref.getInstance(this).getUser().getLoginCode());
            obj1.put("room", "1judge");

            mSocket.emit("subscribe", obj1);

            Log.d("SUBS", "sent");

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("SUBS", "error subscribing");
        }
        mSocket.on("roundchange", onEvent);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        viewPager.setOffscreenPageLimit(4);

        setTitle("Assigned Teams");
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
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                //dialog.setCancelable(true);
                dialog.setTitle("Logging Out?");
                dialog.setMessage("Are you sure you want to log out from TSMSJUDGE?" );
                dialog.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(MainActivity.this, "Logging Out", Toast.LENGTH_SHORT).show();
                        //remove ctivities stCK,
                        //try this first
                        //then after here
                        SharedPref.getInstance(cn).logout();
                        Intent logout = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(logout);
                    }
                }).setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "Logout Cancelled", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        });

                final AlertDialog alert = dialog.create();
                alert.show();
                break;
            // action with ID action_settings was selected
            case R.id.info_icon:
                Toast.makeText(MainActivity.this, "Assigned Teams Info. Page", Toast.LENGTH_SHORT).show();
                Intent mainActivityInfoPage = new Intent(MainActivity.this, MainActivityInfoPage.class);
                startActivity(mainActivityInfoPage);
                break;

            default:
                break;
        }

        return true;
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FirstTab(), "All");
        adapter.addFragment(new SecondTab(), "Comp. Sci.");
        adapter.addFragment(new ThirdTab(), "Eng.");
        adapter.addFragment(new PrevRoundFragment(), "Marked Teams");
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

    private void retrieveEvents() {
        User user = SharedPref.getInstance(this).getUser();
        final String username = user.getId();
        class RegisterUser extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("judgeid", username);


                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_GetCurrentRound, params);
            }

            @Override
            protected void onPreExecute() {
//               pd.show();
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //hiding the progressbar after completion
                //              pd.hide();
                try {
                    JSONObject obj = new JSONObject(s);

                    if (!obj.getBoolean("error")) {

                        //getting the user from the response
                        JSONObject eventJson = obj.getJSONObject("ev");

                        //creating a new user object
                        for(int i = 0; i < eventJson.length();i++) {
                            String index = "event" + Integer.toString(i);
                            ((MyApplication) getApplication()).currentround =
                            eventJson.getJSONObject(index).getInt("round");

                        }

                        Log.d("LC", username);
                        Log.d("CR", Integer.toString(((MyApplication) getApplication()).currentround));
                        Log.d("ERR", s);




                    } else {
//                        Toast.makeText(r, "Some error occurred", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //executing the async task
        RegisterUser ru = new RegisterUser();
        ru.execute();
    }
    private Emitter.Listener onEvent = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String event;
                    int round;
                    try {
                        event = data.getString("event");
                        round = data.getInt("round");
                    } catch (JSONException e) {
                        return;
                    }

                    Toast.makeText(getBaseContext(),"Round Change", Toast.LENGTH_SHORT).show();

                    new FirstTab().renew();
                }
            });
        }

    };
}//end of class
