package net.androidbootcamp.campmoab.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import net.androidbootcamp.campmoab.MainActivity;
import net.androidbootcamp.campmoab.R;
import net.androidbootcamp.campmoab.UserAccountAttributes.LogoutPage;

public class AdminMainActivity extends AppCompatActivity {
    CardView confirmRes, logout, viewUsers, mainMenu;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_main);
        confirmRes = (CardView) findViewById(R.id.btnConfirmRes);
        viewUsers = (CardView) findViewById(R.id.btnUserList);
        logout = (CardView) findViewById(R.id.btnLogout);
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

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminMainActivity.this, LogoutPage.class));
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
