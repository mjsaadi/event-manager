package tsms.recyclerview;

/**
 * Created by shams on 10/23/2017.
 */

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class SocketIOService extends Service {
    private  SocketListener socketListener;
    private Boolean appConnectedToService;
    private  Socket mSocket;
    private boolean serviceBinded = false;
    private final LocalBinder mBinder = new LocalBinder();
    SharedPreferences prefs;
    public SocketIOService that = this;
    Context ctx;
    public void setAppConnectedToService(Boolean appConnectedToService) {
        this.appConnectedToService = appConnectedToService;
    }

    public void setSocketListener(SocketListener socketListener) {
        this.socketListener = socketListener;
    }

    public class LocalBinder extends Binder{
        public SocketIOService getService(){
            return SocketIOService.this;
        }
    }

    public void setServiceBinded(boolean serviceBinded) {
        this.serviceBinded = serviceBinded;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("START SERVICE", "iN SERVICE");
        initializeSocket();
        addSocketHandlers();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeSocketSession();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return serviceBinded;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void initializeSocket() {

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
        try{
            IO.Options options = new IO.Options();
            options.forceNew = true;
            mSocket = IO.socket(constants.CHAT_SERVER_URL,options);


        }
        catch (Exception e){
            Log.e("Error", "Exception in socket creation");
            throw new RuntimeException(e);
        }
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
            SocketService.configEditor = prefs.edit();
            SocketService.configEditor.putStringSet("occupied", occupied);
            SocketService.configEditor.apply();

            Log.d("username", username);
            Log.d("message", message);
            that.createNotification("New message from " + username, message, 0);

        }

    };

    private void closeSocketSession(){
//        mSocket.disconnect();
//        mSocket.off();
    }
    private void addSocketHandlers(){

        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Intent intent = new Intent(constants.socketConnection);
                intent.putExtra("connectionStatus", true);
                broadcastEvent(intent);
            }
        });

        mSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Intent intent = new Intent(constants.socketConnection);
                intent.putExtra("connectionStatus", false);
                broadcastEvent(intent);
            }
        });


        mSocket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Intent intent = new Intent(constants.connectionFailure);
                broadcastEvent(intent);
            }
        });

        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Intent intent = new Intent(constants.connectionFailure);
                broadcastEvent(intent);
            }
        });
        if (SharedPref.getInstance(getApplicationContext()).isLoggedIn()) {
            addNewMessageHandler();
        }
        mSocket.connect();



    }

    public void addNewMessageHandler(){
        mSocket.off(constants.newMessage);
        mSocket.on(constants.newMessage, new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                JSONObject data = (JSONObject) args[0];
                String username;
                String message, room;
                try {
                    username = data.getString("user");
                    message = data.getString("message");
                    room = data.getString("room");
                } catch (JSONException e) {
                    return;
                }

                if (isForeground("tsms.tsmscleaned.ChatActivity")) {
                    Intent intent = new Intent(constants.newMessage);
                    intent.putExtra("username", username);
                    intent.putExtra("message + from", message);
                    broadcastEvent(intent);
                } else {

                    showNotifications(username, message, room);
                }
            }
        });

        mSocket.on("notify", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                JSONObject data = (JSONObject) args[0];
                String username;
                String message;
                try {
                    username = data.getString("user");
                    message = data.getString("notification");
                } catch (JSONException e) {
                    return;
                }

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Calendar cal = Calendar.getInstance();
                String [] date = dateFormat.format(cal).split(" ");

                               showNotifications(username, message);

            }
        });
    }
    public void createNotification(String title,String text, int type){

        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new android.support.v4.app.NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.chat)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setAutoCancel(true);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        Intent resultIntent = null;
// Creates an explicit intent for an Activity in your app
        if(type == 0) {
           resultIntent = new Intent(this, ChatActivity.class);
        }

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

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

        mNotificationManager.notify(1, mBuilder.build());

    }
    public void removeMessageHandler() {
        mSocket.off(constants.newMessage);
    }

    public void emit(String event,Object[] args,Ack ack){
        mSocket.emit(event, args, ack);
    }
    public void emit (String event,Object... args) {
        try {
            mSocket.emit(event, args,null);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addOnHandler(String event,Emitter.Listener listener){
        mSocket.on(event, listener);

        mSocket.on("updatechat",onNewMessage);

    }



    public void connect(){
        mSocket.connect();
    }

    public void disconnect(){
        mSocket.disconnect();
    }

    public void restartSocket(){
        mSocket.off();
        mSocket.disconnect();
        addSocketHandlers();
    }

    public void off(String event){
        mSocket.off(event);
    }

    private void broadcastEvent(Intent intent){
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public boolean isSocketConnected(){
        if (mSocket == null){
            return false;
        }
        return mSocket.connected();
    }

    public void showNotifications(String username, String message, String room){
        PendingIntent pIntent = null;
                Intent toLaunch;

            toLaunch = new Intent(getApplicationContext(), ChatActivity.class);
            toLaunch.putExtra("UniqueId", "socket");
            toLaunch.putExtra("room", room);

            toLaunch.setAction("android.intent.action.MAIN");
            pIntent = PendingIntent.getActivity(getApplicationContext(), 0, toLaunch,
                    PendingIntent.FLAG_UPDATE_CURRENT);

        Notification n  = new NotificationCompat.Builder(this)
                .setContentTitle("You have pending new messages")
                .setContentText("New Message")
                .setContentIntent(pIntent)
                .setAutoCancel(true).setDefaults(Notification.DEFAULT_SOUND)
                .setSmallIcon(R.drawable.chat)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        notificationManager.notify(0, n);
    }



    public void showNotifications(String username, String message){
        PendingIntent pIntent;
        Intent toLaunch;

            toLaunch = new Intent(getApplicationContext(), MainActivity.class);
            toLaunch.putExtra("username", message);
            toLaunch.putExtra("message", message);

            toLaunch.setAction("android.intent.action.MAIN");
            pIntent = PendingIntent.getActivity(getApplicationContext(), 0, toLaunch,
                    PendingIntent.FLAG_UPDATE_CURRENT);

        Notification n  = new NotificationCompat.Builder(this)
                .setContentTitle("You have pending new messages")
                .setContentText("New Message")
                .setContentIntent(pIntent)
                .setAutoCancel(true).setDefaults(Notification.DEFAULT_SOUND)
                .setSmallIcon(R.drawable.chat)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        notificationManager.notify(0, n);
    }
    public boolean isForeground(String myPackage) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        return componentInfo.getPackageName().equals(myPackage);
    }



}
