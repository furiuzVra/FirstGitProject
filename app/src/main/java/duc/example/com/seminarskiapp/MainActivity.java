package duc.example.com.seminarskiapp;


import android.content.Context;

import android.content.Intent;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;

import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.text.format.DateFormat;
import android.widget.Toast;


import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;




public class MainActivity extends AppCompatActivity {

    private static int SIGN_IN_REQ_CODE = 1;
    private FirebaseListAdapter<ChatMessage> adapter;
    RelativeLayout activity_main;
    FloatingActionButton fab_send;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.back_blue:
                activity_main.setBackgroundResource(R.drawable.images);
                break;
            case R.id.back_black:
                activity_main.setBackgroundResource(R.drawable.black);
                break;
            case R.id.back_kids:
                activity_main.setBackgroundResource(R.drawable.kids);
                break;
            case R.id.back_space:
                activity_main.setBackgroundResource(R.drawable.space);
                break;
            case R.id.back_love:
                activity_main.setBackgroundResource(R.drawable.love);
                break;
            case R.id.menu_sign_out:
                exitApp();
                break;

        }

        return true;

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                Snackbar.make(activity_main, "Successfully signed in.Welcome!", Snackbar.LENGTH_SHORT).show();
                displayChatMessage();
            } else {
                Snackbar.make(activity_main, "We could not sign you in.Please try again later!", Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkInternetConnections();
        activity_main = (RelativeLayout) findViewById(R.id.activity_main);
        fab_send = (FloatingActionButton) findViewById(R.id.fab_send);
        fab_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = (EditText) findViewById(R.id.input);
                FirebaseDatabase.getInstance().getReference().push().setValue(new ChatMessage(input.getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                input.setText("");
            }
        });

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {

            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQ_CODE);
        } else {
            Snackbar.make(activity_main, "Welcome " + FirebaseAuth.getInstance().getCurrentUser().getEmail(), Snackbar.LENGTH_SHORT).show();
            displayChatMessage();
        }





    }


    private void displayChatMessage() {
        ListView listOfMessage = (ListView) findViewById(R.id.list);
        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class, R.layout.list_item, FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                TextView messageText, messageUser, messageTime;
                messageText = (TextView) v.findViewById(R.id.message_text);
                messageUser = (TextView) v.findViewById(R.id.message_user);
                messageTime = (TextView) v.findViewById(R.id.message_time);

                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));
            }

        };
        listOfMessage.setAdapter(adapter);
    }

    public void checkInternetConnections() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
            Toast.makeText(this, "Connection Available", Toast.LENGTH_SHORT).show();
        } else {
            connected = false;
            Toast.makeText(this, "Please Turn on your Internet Connection", Toast.LENGTH_SHORT).show();


        }


    }
    public void exitApp(){
        ExitDialog dialog = new ExitDialog();
        dialog.show(getFragmentManager(), "ExitDialog");


    }


}