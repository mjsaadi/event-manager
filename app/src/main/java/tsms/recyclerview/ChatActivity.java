package tsms.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatActivity extends AppCompatActivity {

    Context cn = this;
    final String MESSAGES_ENDPOINT = "http://52.27.201.136:4001";



    EditText messageInput;
    Button sendButton;
    String username;
    private Toolbar toolbar;
    static SharedPreferences.Editor configEditor;
    private Random random;
    private Context ctx = this;
    public ArrayList<ChatMessage> chatlist;
    public ChatAdapter chatAdapter;
    SharedPreferences prefs;
    ListView lv;
    String room = "";
    Socket mSocket;
    Activity act = this;
    Context context = this;
    private View parent_view;


    private String user1 = "khushi", user2 = "khushi1", mUsername, friend;
    private EditText msg_edittext;

    {
        try {
            mSocket = IO.socket(MESSAGES_ENDPOINT);
        } catch (URISyntaxException e) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        toolbar = (Toolbar) findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
int id = 1;
        room = id + "judge";

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_chat);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_assigned_projects:
                        Toast.makeText(ChatActivity.this, "Assigned Teams", Toast.LENGTH_SHORT).show();
                        Intent first = new Intent(ChatActivity.this, MainActivity.class);
                        startActivity(first);
                        break;
                    case R.id.menu_all_projects:
                        Toast.makeText(ChatActivity.this, "All Teams", Toast.LENGTH_SHORT).show();
                        Intent second = new Intent(ChatActivity.this, AllProjectsActivity.class);
                        startActivity(second);
                        break;
                    case R.id.menu_timeline:
                        Toast.makeText(ChatActivity.this, "Timeline", Toast.LENGTH_SHORT).show();
                        Intent third = new Intent(ChatActivity.this, Timeline.class);
                        startActivity(third);
                        break;

                    case R.id.menu_chat:
                        Toast.makeText(ChatActivity.this, "You're in Chat", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.menu_parking:
                        Toast.makeText(ChatActivity.this, "Parking", Toast.LENGTH_SHORT).show();
                        Intent fourth = new Intent(ChatActivity.this, Parking.class);
                        startActivity(fourth);
                        break;
                }
                return true;
            }
        });

        setTitle("Chat");
        retriveChat();

        random = new Random();
        lv = (ListView) findViewById(R.id.msgListView);

        msg_edittext = (EditText) findViewById(R.id.messageEdit);
        sendButton = findViewById(R.id.MessageButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("bot", "called");

                try {
                    sendTextMessage(view);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Context ctx = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        chatlist = new ArrayList<>();
        configEditor = prefs.edit();
        configEditor.putBoolean("activityStarted",true);
        configEditor.commit();
        Boolean status = prefs.getBoolean("serviceStopped",false);

        refresh();
        Log.d("service exists ?", status + "");

        if(status){

            Log.d("service","not running");
            Log.d("activity running",prefs.getBoolean("activityStarted",false)+"");

        }
        else{
            Log.d("service running","true");
            Log.d("activity running", prefs.getBoolean("activityStarted", false) + "");
            stopService(new Intent(getBaseContext(), SocketIOService.class));
            mSocket.disconnect();
        }
        JSONObject obj1 = new JSONObject();


        User user = SharedPref.getInstance(this).getUser();
        username = user.getId();


        try {
            obj1.put("username", username);
            obj1.put("room", room);

            mSocket.emit("subscribe", obj1);

            Log.d("SUBS", "sent");

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("SUBS", "error subscribing");
        }

        mSocket.connect();
        mSocket.on("updatechat", onNewMessage);
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
                AlertDialog.Builder dialog = new AlertDialog.Builder(ChatActivity.this);
                //dialog.setCancelable(true);
                dialog.setTitle("Logging Out?");
                dialog.setMessage("Are you sure you want to log out from TSMSJUDGE?" );
                dialog.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(ChatActivity.this, "Logging Out", Toast.LENGTH_SHORT).show();
                        SharedPref.getInstance(cn).logout();
                        Intent logout = new Intent(ChatActivity.this, LoginActivity.class);
                        startActivity(logout);
                    }
                }).setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ChatActivity.this, "Logout Cancelled", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });

                final AlertDialog alert = dialog.create();
                alert.show();
                break;
            // action with ID action_settings was selected
            case R.id.info_icon:
                Toast.makeText(ChatActivity.this, "Chat Info. Page", Toast.LENGTH_SHORT).show();
                Intent chatActivityInfoPage = new Intent(ChatActivity.this, ChatActivityInfoPage.class);
                startActivity(chatActivityInfoPage);
                break;
            default:
                break;
        }

        return true;
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
            try {
                sendTextMessage(v);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
    public void switchRoom() throws JSONException {
        String event = ((MyApplication) getApplication()).getEvent() + "All";
        JSONObject obj = new JSONObject();
        obj.put("newroom", event);
        obj.put("username", username);
        mSocket.emit("switchRoom", obj);

    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.d("activity","inside onstart");
        Log.d("onStart","getOccupied");

    }


    private void postMessage(String message) throws JSONException {


        if (message.equals("")) {
            return;
        }
        User user = SharedPref.getInstance(this).getUser();

        String username = user.getLoginCode();
        Toast.makeText(context, username,Toast.LENGTH_SHORT).show();

        JSONObject obj = new JSONObject();
        obj.put("username", username);
        obj.put("message", message);
        obj.put("room", room);
        mSocket.emit("sendchat", obj);

    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d("activity", "inside onPause");

    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d("activity","inside onstop");

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("activity","inside onDestroy");
        AppSocketListener.getInstance().setAppConnectedToService(false);
        mSocket.off("updatechat", onNewMessage);
        mSocket.disconnect();
        configEditor = prefs.edit();
        configEditor.putBoolean("activityStarted", false);
        configEditor.apply();
        startService(new Intent(getBaseContext(), SocketIOService.class));
    }

    public void getOccupied(){
        Set<String> click = prefs.getStringSet("occupied", new HashSet<String>());
        if(click != null){

            Iterator<String> iterator = click.iterator();

            while(iterator.hasNext()){
                String message = iterator.next();
                Log.d("Message", message);
                final ChatMessage chatMessage = new ChatMessage("shamsu", username,
                        message, "" + random.nextInt(1000), false);
                chatMessage.setMsgID();
                chatMessage.body = message;
                chatMessage.Date = CommonMethods.getCurrentDate(new Date());
                chatMessage.Time = CommonMethods.getCurrentTime(new Date());
                chatlist.add(chatMessage);

                chatAdapter.notifyDataSetChanged();


            }

        }
    }

    public void refresh(){
        chatAdapter = new ChatAdapter(act,chatlist);
        lv.setAdapter(chatAdapter);
    }


    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        username = data.getString("user");
                        message = data.getString("message");
                    } catch (JSONException e) {
                        return;
                    }

                    final ChatMessage chatMessage = new ChatMessage(user1, username,
                            message, "" + random.nextInt(1000), false);
                    chatMessage.setMsgID();
                    chatMessage.body = username + "\n" + message;
                    chatMessage.Date = CommonMethods.getCurrentDate(new Date());
                    chatMessage.Time = CommonMethods.getCurrentTime(new Date());
                    chatlist.add(chatMessage);
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    chatAdapter.notifyDataSetChanged();

                }
            });
        }

    };

    public void sendTextMessage(View v) throws JSONException {
        String message = msg_edittext.getEditableText().toString();
        Log.d("M", message);
        try {
            postMessage(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!message.equalsIgnoreCase("")) {
            final ChatMessage chatMessage = new ChatMessage(user1, user2,
                    message, "" + random.nextInt(1000), true);
            chatMessage.setMsgID();
            chatMessage.body = message;
            chatMessage.Date = CommonMethods.getCurrentDate(new Date());
            chatMessage.Time = CommonMethods.getCurrentTime(new Date());

            msg_edittext.setText("");
            chatlist.add(chatMessage);
            chatAdapter.notifyDataSetChanged();

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK != resultCode) {
            finish();
            return;
        }
        mUsername = data.getStringExtra("username");
        int numUsers = data.getIntExtra("numUsers", 1);
        final ChatMessage chatMessage = new ChatMessage(user1, user2,
                "There is " + numUsers +" in chat", "" + random.nextInt(1000), true);
        chatMessage.setMsgID();
        chatMessage.body = "There is " + numUsers +" in chat";
        chatMessage.Date = CommonMethods.getCurrentDate(new Date());
        chatMessage.Time = CommonMethods.getCurrentTime(new Date());

        msg_edittext.setText("");
        chatlist.add(chatMessage);
        chatAdapter.notifyDataSetChanged();

    }
    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i("Failed","Failed to connect");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ctx,
                            "Cant connect", Toast.LENGTH_LONG).show();
                }
            });
        }
    };








    private void retriveChat() {


        User user = SharedPref.getInstance(ctx).getUser();
        final String un = user.getId();

        class RetrieveStudents extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();

                params.put("room", room);                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_rChat, params);
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                //hiding the progressbar after completion
                try {
                    JSONObject obj = new JSONObject(s);

                    if (!obj.getBoolean("error")) {



                        //getting the user from the response
                        JSONObject studentJson = obj.getJSONObject("chat");

                        //creating a new user object
                        for (int i = 0; i < studentJson.length(); i++) {
                            String index = "chat" + Integer.toString(i);
                            Boolean mine = false;
                            if(Objects.equals(studentJson.getJSONObject(index).getString("username"), SharedPref.getInstance(ctx).getUser().getId())){
                                mine = true;
                            }

                            ChatMessage chat = new ChatMessage(
                                    studentJson.getJSONObject(index).getString("username"),
                                    SharedPref.getInstance(ctx).getUser().getId(),
                                    studentJson.getJSONObject(index).getString("msg"),
                                    "" + random.nextInt(1000), mine
                            );
                            chatlist.add(chat);

                        }
                        chatAdapter.notifyDataSetChanged();


                    } else {
                        if(obj.getBoolean("empty")) {

                        }

                        Toast.makeText(ctx, "Some error occurred", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        RetrieveStudents ru = new RetrieveStudents();
        ru.execute();
    }
}//end of class
