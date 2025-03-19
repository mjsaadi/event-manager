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


public class TimelineFrag extends Fragment {
    private RecyclerView recyclerView;
    private TimelineAdapter adapter;
    private List<TimelineData> timelineDataList;
    private static final String TAG = "TIMELINE";

    private SwipeRefreshLayout swipeRefreshLayout;

    public TimelineFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        timelineDataList = new ArrayList<>();

        //for(int i = 0; i < 50; i++){
         //   TimelineData t = new TimelineData("Agenda Text" + Integer.toString(i+1),"AM", "12");
        //    timelineDataList.add(t);
       // }
        adapter = new TimelineAdapter(getActivity(), timelineDataList, getActivity().getApplication());
        retreiveTimeline();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timeline, container, false);
        //rootView.setTag(TAG);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_firsttab_timeline);
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
                retreiveTimeline();
            }
        });
    }

    private void retreiveTimeline() {
        timelineDataList.clear();


        class RetreiveTimeline extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;
            final String event = ((MyApplication) getActivity().getApplication()).getEvent();


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
                params.put("eventid", event);

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
                        Toast.makeText(getContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        //getting the user from the response
                        JSONObject timelineJson = obj.getJSONObject("timeline");

                        //creating a new user object
                        for(int i = 0; i < timelineJson.length(); i++) {
                            String index = "timeline" + Integer.toString(i);
                            TimelineData timelineData = new TimelineData(
                                    timelineJson.getJSONObject(index).getString("name"),
                                    timelineJson.getJSONObject(index).getString("time"));

                            timelineDataList.add(timelineData);

                        }
                        adapter.notifyDataSetChanged();
                        ((MyApplication) getActivity().getApplication()).timelineDataList = timelineDataList;

                    } else {

                        Toast.makeText(getContext(), "Some error occurred", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //executing the async task
        RetreiveTimeline ru = new RetreiveTimeline();
        ru.execute();
    }

}
