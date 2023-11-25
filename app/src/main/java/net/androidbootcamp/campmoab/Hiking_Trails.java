package net.androidbootcamp.campmoab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.androidbootcamp.campmoab.UserAccountAttributes.UserAccount;

public class Hiking_Trails extends AppCompatActivity {
    private ImageView home, account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hiking_trails);
        String[] hikes = {"Hidden Valley Trail", "Carona Arch Trail", "Negro Bill Canyon", "Morning Glory Bridge"};

        home = (ImageView) findViewById(R.id.homeImage);
        account = (ImageView) findViewById(R.id.acctImage);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Hiking_Trails.this, MainActivity.class));
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Hiking_Trails.this, UserAccount.class));
            }
        });
    }
}