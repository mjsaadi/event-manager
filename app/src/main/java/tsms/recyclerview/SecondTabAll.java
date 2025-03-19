package tsms.recyclerview;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class SecondTabAll extends Fragment {
    private RecyclerView recyclerView;
    private AllTeamAdapter adapter;
    private List<Team> teamList;
    private static final String TAG = "Comp. Sci.";

    private SwipeRefreshLayout swipeRefreshLayout;

    public SecondTabAll() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


        teamList = new ArrayList<>();
        adapter = new AllTeamAdapter(getActivity(), teamList, getActivity().getApplication());
        retriveTeams();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_second_tab, container, false);
        rootView.setTag(TAG);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_secondtab_all);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        return rootView;

    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState){
        super.onViewCreated(rootView, savedInstanceState);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                retriveTeams();
            }
        });
    }

    private void retriveTeams() {
        teamList.clear();



        class RetrieveTeams extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;
            final String event = constants.event;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                Log.d("event",((MyApplication) getActivity().getApplication()).getEvent());
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
                        Toast.makeText(getContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

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

                            if(Objects.equals(team.getCat(), "Computer Science")) {
                                teamList.add(team);
                            }
                        }

                        for(Team t : teamList){


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

}

