package tsms.recyclerview;


import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class PrevRoundFragment extends Fragment {
    private RecyclerView recyclerView;
    List<Grade> marksList;
    private TeamAdapter adapter;
    private List<Team> teamList;
    private static final String TAG = "All";
    RvJoiner rvJoiner;
    final LinearLayout.LayoutParams paramss = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
    final LinearLayout.LayoutParams paramHorizontal = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
    final LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
    final LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
    private SwipeRefreshLayout swipeRefreshLayout;

    public PrevRoundFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        teamList = new ArrayList<>();
        marksList = new ArrayList<>();
        rvJoiner = new RvJoiner();
        adapter = new TeamAdapter(getActivity(), teamList, getActivity().getApplication());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_prev_round, container, false);
        rootView.setTag(TAG);
        retriveTeams(((MyApplication) getActivity().getApplication()).currentround);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        retriveTeams();
        recyclerView.setAdapter(rvJoiner.getAdapter());
        recyclerView.setLayoutManager(layoutManager);


        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        return rootView;

    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                retriveTeams(((MyApplication) getActivity().getApplication()).currentround);
            }
        });
    }

    private void retriveTeams(final int round) {
        final String event = ((MyApplication) getActivity().getApplication()).getEvent();
        Log.d("event", ((MyApplication) getActivity().getApplication()).getEvent());
        teamList.clear();


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

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("Event", event);
                params.put("judge", SharedPref.getInstance(getContext()).getUser().getLoginCode());
                params.put("round", Integer.toString(round));


                Log.d("Event", event);
                Log.d("judge", SharedPref.getInstance(getContext()).getUser().getLoginCode());
                params.put("round", Integer.toString(1));
                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_rATeams, params);
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //hiding the progressbar after completion
                try {
                    JSONObject obj = new JSONObject(s);
                    int covers =
                            R.drawable.background;

                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        //getting the user from the response
                        JSONObject teamJson = obj.getJSONObject("teams");

                        //creating a new user object
                        for (int i = 0; i < teamJson.length(); i++) {
                            String index = "team" + Integer.toString(i);
                            Team team = new Team(
                                    teamJson.getJSONObject(index).getString("tname"),
                                    teamJson.getJSONObject(index).getString("mentor"),
                                    teamJson.getJSONObject(index).getString("institution"),
                                    teamJson.getJSONObject(index).getString("event"),
                                    teamJson.getJSONObject(index).getInt("votes"),
                                    teamJson.getJSONObject(index).getString("type"),
                                    covers);

                            teamList.add(team);
                        }
                        adapter.notifyDataSetChanged();
                        ((MyApplication) getActivity().getApplication()).teamList = teamList;

                    } else {

                        Toast.makeText(getContext(), "Some error occurred", Toast.LENGTH_SHORT).show();
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

    public void renew() {
        retriveTeams(((MyApplication) getActivity().getApplication()).currentround);
    }

    private void retriveTeams() {

        new Thread(new Runnable() {
            @Override
            public void run() {


                final String event = ((MyApplication) getActivity().getApplication()).getEvent();


                //creating request handler teamObject
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("Event", event);
                HashMap<String, String> p = new HashMap<>();


                //returing the response
                String f = requestHandler.sendPostRequest(URLs.URL_rJMarks, params);
                String s = requestHandler.sendPostRequest(URLs.URL_rTeam, params);


                //hiding the progressbar after completion
                try {
                    JSONObject teamObj = null;

                    JSONObject marksObj = null;
                    JSONObject judgeObj = null;
                    if (f != "" && f != null && !f.isEmpty()) {
                        Log.d("Json", s);
                        Log.d("Json", f);
                        if (!Objects.equals(f, "[]")) {
                            marksObj = new JSONObject(f);
                            teamObj = new JSONObject(s);
                        }
                    }

                    int covers = R.drawable.background;


                    if (teamObj != null && marksObj != null ) {
                        if (!teamObj.getBoolean("error") && !marksObj.getBoolean("error")) {
                            //  Toast.makeText(getContext(), teamObj.getString("message"), Toast.LENGTH_SHORT).show();

                            //getting the user from the response
                            JSONObject teamJson = teamObj.getJSONObject("teams");

                            //creating a new user teamObject
                            for (int i = 0; i < teamJson.length(); i++) {
                                String index = "team" + Integer.toString(i);
                                Team team = new Team(
                                        teamJson.getJSONObject(index).getString("tname"),
                                        teamJson.getJSONObject(index).getString("mentor"),
                                        teamJson.getJSONObject(index).getString("institution"),
                                        teamJson.getJSONObject(index).getString("event"),
                                        teamJson.getJSONObject(index).getInt("votes"),
                                        covers);
                                teamList.add(team);
                            }


                            JSONObject markJson = marksObj.getJSONObject("marks");

                            //creating a new user teamObject
                            for (int i = 0; i < markJson.length(); i++) {
                                String index = "mark" + Integer.toString(i);
                                Grade marks = new Grade(
                                        markJson.getJSONObject(index).getInt("event"),
                                        markJson.getJSONObject(index).getInt("score"),
                                        markJson.getJSONObject(index).getInt("round"),
                                        markJson.getJSONObject(index).getInt("total"),
                                        markJson.getJSONObject(index).getString("judge"),
                                        markJson.getJSONObject(index).getString("name"),
                                        markJson.getJSONObject(index).getString("team")

                                );
                                if (markJson.getJSONObject(index).getInt("round") == 1) {
                                    marksList.add(marks);
                                }
                                Log.d("marks", marks.getName());


                            }

                            //creating a new user object


                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {



                                        List<Grade> grades = new ArrayList<>();
                                        for (Grade g : marksList) {
                                            Log.d("MARKS", g.getName());
                                            if (Objects.equals(g.getJudge(), SharedPref.getInstance(getContext()).getUser().getLoginCode())) {
                                                grades.add(g);
                                                Log.d("GRADE", g.getName());
                                            }
                                        }


                                        List<List> teams = new ArrayList<>();


                                        param2.setMargins(200, 5, 200, 5);
                                        param2.gravity = Gravity.CENTER;


                                        for (int k = 0; k < teamList.size(); k++) {
                                            List<Grade> teamgrade = new ArrayList<>();
                                            for (Grade g : grades) {

                                                if (Objects.equals(g.getTeam(), teamList.get(k).gettname())) {
                                                    teamgrade.add(g);

                                                }

                                            }
                                            teams.add(teamgrade);
                                        }

                                        for (int k = 0; k < teams.size(); k++) {


                                            CardView cardView = new CardView(getContext());
                                            cardView.setLayoutParams(param2);

                                            cardView.removeAllViews();

                                            TextView tv = new TextView(getContext());
                                            if (teams.get(k).size() > 0) {
                                                Grade grade = (Grade) teams.get(k).get(0);
                                                tv.setText(grade.getTeam().toUpperCase());
                                            }
                                            tv.setTextColor(Color.BLACK);
                                            tv.setLayoutParams(param);
                                            tv.setGravity(View.TEXT_ALIGNMENT_CENTER);


                                            cardView.addView(tv);
                                            param2.setMargins(200, 5, 200, 5);
                                            param2.gravity = Gravity.CENTER;
                                            cardView.setPadding(25, 25, 25, 25);

                                            tv.setTextSize(21);
                                            rvJoiner.add(new JoinableLayout(cardView));

                                            rvJoiner.add(new JoinableAdapter(new WinnerActivityMarksAdapter(teams.get(k), getActivity(), getContext())));
                                            // rvJoiner.add(new JoinableAdapter(new TeamAdapter(getActivity(),teamList,getActivity().getApplication())));

                                        }


                                    recyclerView.setAdapter(rvJoiner.getAdapter());
                                }
                            });


                        } else {

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });

                        }
                    }
                    // Toast.makeText(getContext(), marksObj.getString("message"), Toast.LENGTH_SHORT).show();

                    //getting the user from the response


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
