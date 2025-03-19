package tsms.recyclerview;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ChatActivityInfoPage extends AppCompatActivity {

    private Toolbar toolbar;

    private Button goBackButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity_info_page);


        toolbar = (Toolbar) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        goBackButton = (Button) findViewById(R.id.chat_info_button);

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatActivityInfoPage.this, "Chat", Toast.LENGTH_LONG).show();
                finish();//could be this.finish()
            }
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_chat);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_assigned_projects:
                        Toast.makeText(ChatActivityInfoPage.this, "Assigned Teams", Toast.LENGTH_SHORT).show();
                        Intent first = new Intent(ChatActivityInfoPage.this, MainActivity.class);
                        startActivity(first);
                        break;
                    case R.id.menu_all_projects:
                        Toast.makeText(ChatActivityInfoPage.this, "All Teams", Toast.LENGTH_SHORT).show();
                        Intent second = new Intent(ChatActivityInfoPage.this, AllProjectsActivity.class);
                        startActivity(second);
                        break;
                    case R.id.menu_timeline:
                        Toast.makeText(ChatActivityInfoPage.this, "Timeline", Toast.LENGTH_SHORT).show();
                        Intent third = new Intent(ChatActivityInfoPage.this, Timeline.class);
                        startActivity(third);
                        break;

                    case R.id.menu_chat:
                        Toast.makeText(ChatActivityInfoPage.this, "Chat", Toast.LENGTH_SHORT).show();
                        Intent fourth = new Intent(ChatActivityInfoPage.this, ChatActivity.class);
                        startActivity(fourth);
                        break;

                    case R.id.menu_parking:
                        Toast.makeText(ChatActivityInfoPage.this, "Parking", Toast.LENGTH_SHORT).show();
                        Intent fifth = new Intent(ChatActivityInfoPage.this, Parking.class);
                        startActivity(fifth);
                        break;
                }
                return true;
            }
        });

        setTitle("Chat Info. Page");
    }

    /*
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
                Toast.makeText(ChatActivity.this, "Info. Page", Toast.LENGTH_SHORT).show();
                //Intent logout = new Intent(MainActivity.this, LoginActivity.class);
                //startActivity(logout);
                break;
            default:
                break;
        }

        return true;
    }*/
}//end of class
