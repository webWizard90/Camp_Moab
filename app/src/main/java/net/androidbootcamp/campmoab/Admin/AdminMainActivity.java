package net.androidbootcamp.campmoab.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.cardview.widget.CardView;

import net.androidbootcamp.campmoab.BaseMainActivity;
import net.androidbootcamp.campmoab.MainActivity;
import net.androidbootcamp.campmoab.R;

public class AdminMainActivity extends BaseMainActivity {
    CardView confirmRes, viewUsers, mainMenu;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.admin_activity_main, findViewById(R.id.content_frame));
        toolbar.setTitle("Admin Home");

        confirmRes = (CardView) findViewById(R.id.btnConfirmRes);
        viewUsers = (CardView) findViewById(R.id.btnUserList);
        mainMenu = (CardView) findViewById(R.id.btnMainMenu);

        confirmRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminMainActivity.this, AdminReservations.class));
            }
        });

        viewUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminMainActivity.this, AdminUsersList.class));
            }
        });

        mainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminMainActivity.this, MainActivity.class));
            }
        });

    }
}
