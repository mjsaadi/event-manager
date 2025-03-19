package tsms.recyclerview;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class SocketService extends Service {
    final String MESSAGES_ENDPOINT = "http://52.27.201.136:4001";
    public Socket mSocket;


    public SocketService that = this;
    SharedPreferences prefs;
    Context ctx;
    static SharedPreferences.Editor configEditor;

    public SocketService() {
    }

    @Override
    public void onCreate() {


        ctx = getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        Boolean status = prefs.getBoolean("activityStarted",true);
        Log.d("inside onCreate",status+"");

        if(!status){
            {
                try {
                    Log.d("inside if","oncreate");
                    configEditor = prefs.edit();
                    configEditor.putBoolean("serviceStopped", false);
                    configEditor.apply();
                    Log.d("connecting to server","inside oncreate");
                    mSocket = IO.socket(MESSAGES_ENDPOINT);
                    mSocket.connect();
                    JSONObject obj = new JSONObject();
                    JSONObject obj1 = new JSONObject();
                    JSONObject obj2 = new JSONObject();

                    User user = SharedPref.getInstance(this).getUser();
                    String username = user.getId();



                    mSocket.on("updatechat",onNewMessage);


                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        else {
            Log.d("inside else","oncreate");
            this.onDestroy();
        }

    }

    public void createNotification(String title,String text){

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.chat)
                        .setContentTitle(title)
                        .setContentText(text);
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, ChatActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(ChatActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());

    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {


        ctx = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        Boolean status = prefs.getBoolean("activityStarted",true);
        Log.d("inside onStartCommand",status+"");

        if(!status){
            {
                Log.d("inside if","onstartCommand");
                Log.d("service started",status+"");
            }
        } else {
            Log.d("inside else","onstartcommand");
            this.onDestroy();
        }



        return super.onStartCommand(intent,flags,startId);
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {


                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        username = data.getString("user");
                        message = data.getString("message");
                    } catch (JSONException e) {
                        return;
                    }

            Set<String> occupied = prefs.getStringSet("occupied", new HashSet<String>());
            occupied.add(username + ":"+ message);
            configEditor = prefs.edit();
            configEditor.putStringSet("occupied", occupied);
            configEditor.apply();

            Log.d("username", username);
            Log.d("message", message);
            that.createNotification("New message from " + username, message);

        }

    };

    private Emitter.Listener onSubscribe = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {


            JSONObject data = (JSONObject) args[0];
            String username;
            String message;
            try {
                username = data.getString("user");
                message = data.getString("message");
            } catch (JSONException e) {
                return;
            }

            Set<String> occupied = prefs.getStringSet("occupied", new HashSet<String>());
            occupied.add(username + ":"+ message);
            configEditor = prefs.edit();
            configEditor.putStringSet("occupied", occupied);
            configEditor.apply();

            Log.d("username", username);
            Log.d("message", message);
            that.createNotification("New message from " + username, message);

        }

    };



    private Emitter.Listener onAv = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            Log.d("onAv","recieved something");

            int num;
            try {
                num = data.getInt("av");
                Log.d("onAvailable",num+"");
            } catch (JSONException e) {
                return;
            }

            Set<String> occupied = prefs.getStringSet("occupied",new HashSet<String>());
            if(occupied.contains(num+"")){
                Log.d("inside if",num+"");
                occupied.remove(num + "");
            }
            configEditor = prefs.edit();
            configEditor.putStringSet("occupied",occupied);
            configEditor.apply();
            Log.d("inside onAv", prefs.getStringSet("occupied", new HashSet<String>()).toString());

            that.createNotification("Button Available", "" + num + " available");
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy(){
        Log.d("inside service destroy","destroy service");
        Log.d("service","disconnecting");

        Log.d("service","disconnected");
        mSocket.off("updatechat", onNewMessage);
        configEditor = prefs.edit();
        configEditor.putBoolean("serviceStopped",true);
        configEditor.apply();

    }
}
