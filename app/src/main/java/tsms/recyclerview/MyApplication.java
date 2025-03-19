package tsms.recyclerview;

import android.app.Application;
import android.content.Context;

import java.util.List;

/**
 * Created by shams on 11/21/2017.
 */

public class MyApplication extends Application {


    private  String event;
    public int currentround;
    public  List<Team> teamList;
    public Team team;
    public List<TimelineData> timelineDataList;
    public static  Context context;

    public List<TimelineData> getTimelineDataList() { return timelineDataList; }

    public List<Team> getTeamList() {
        return teamList;
    }

    public  void setEvent(String event) {
        this.event = event;
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }

    public void initializeSocket(){
        AppSocketListener.getInstance().initialize();
    }

    public void onCreate(){
        super.onCreate();
        MyApplication.context = getApplicationContext();


        initializeSocket();
    }
    public void destroySocketListener(){
        AppSocketListener.getInstance().destroy();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        destroySocketListener();
    }

    public String getEvent() {
        return event;
    }
}
