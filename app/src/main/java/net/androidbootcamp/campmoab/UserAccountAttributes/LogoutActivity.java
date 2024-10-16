package net.androidbootcamp.campmoab.UserAccountAttributes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import net.androidbootcamp.campmoab.R;

public class LogoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout_page);

        // Logout of account
        FirebaseAuth.getInstance().signOut();

        // Takes user to login screen
        startActivity(new Intent(LogoutActivity.this, LoginActivity.class));


        //Splash Screen set briefly after logout
        /*
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                //'finish' closes the activity for splash activity
                finish();
            }
        };
        //Timer named opening created and set for 5 seconds
        Timer opening = new Timer();
        opening.schedule(task, 5000);
        startActivity(new Intent(LogoutActivity.this, LoginActivity.class));
        */
    }
}