package net.androidbootcamp.campmoab.BaseActivities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseUser;

import net.androidbootcamp.campmoab.Admin.AdminUsersList;
import net.androidbootcamp.campmoab.Classes.FirebaseHelperClass;
import net.androidbootcamp.campmoab.MainActivity;
import net.androidbootcamp.campmoab.R;
import net.androidbootcamp.campmoab.UserAccountAttributes.LogoutActivity;
import net.androidbootcamp.campmoab.UserAccountAttributes.UserAccount;

public class BaseMainActivity extends AppCompatActivity {
        protected MaterialToolbar toolbar;
    private FirebaseHelperClass firebaseHelper;
    private FirebaseUser user;
    private String UID;
    private static final String ADMINS = "Admins";
    private boolean isAdmin = false;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_base_main_layout); // Use a layout that includes the toolbar
            toolbar = findViewById(R.id.topAppBar);
            setSupportActionBar(toolbar);

            // Set up navigation or home button listener
            //toolbar.setNavigationOnClickListener(v -> onBackPressed());

            toolbar = findViewById(R.id.topAppBar);

            firebaseHelper = new FirebaseHelperClass();
            user = firebaseHelper.getCurrentUser();
            UID = user.getUid();

            // Set up navigation or home button listener
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                toolbar.setNavigationOnClickListener(v -> onBackPressed()); // Handle back button
                toolbar.setNavigationOnClickListener(v -> showDropdownMenu(v)); // Handle rounded_dropdown_background menu
            }

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
            }

            // Check user access level
            checkUserAccessLevel();
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);  // Inflate your menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home_button: // ID for the home button
                startActivity(new Intent(this, MainActivity.class)); // Navigate to HomeActivity
                return true;

            case R.id.back_button: // ID for the back button
                onBackPressed(); // Navigate back to the previous activity
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Method to show a rounded_dropdown_background menu when navigation icon is clicked
    private void showDropdownMenu(View anchor) {
        PopupMenu popupMenu = new PopupMenu(this, anchor, R.style.CustomPopupMenu);
        popupMenu.getMenuInflater().inflate(R.menu.menu_popup, popupMenu.getMenu());  // Inflate your popup menu

        if (!isAdmin) {
            MenuItem adminItem = popupMenu.getMenu().findItem(R.id.userAccount);  // Find the admin item
            if (adminItem != null) {
                adminItem.setVisible(false);  // Hide it for regular users
            }
        }

        // Handle item selection from rounded_dropdown_background
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.account:
                    //Toast.makeText(this, "Option One Selected", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, UserAccount.class));
                    return true;
                case R.id.userAccount:
                    //Toast.makeText(this, "Option Two Selected", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, AdminUsersList.class));
                    return true;
                case R.id.logout:
                    startActivity(new Intent(this, LogoutActivity.class));
                default:
                    return false;
            }
        });

        // Show the popup menu
        popupMenu.show();
    }

    private void checkUserAccessLevel() {
        if (UID != null) {
            firebaseHelper.checkIfAdmin(UID, new FirebaseHelperClass.AdminCheckCallback() {
                @Override
                public void onAdminCheck(boolean adminStatus) {
                    if (isAdmin) {
                        isAdmin = adminStatus;
                    } else {
                        isAdmin = adminStatus;
                    }
                    Log.d("BaseMainActivity", "User is an admin: " + isAdmin);
                }
            });
        } else {
            Log.e("BaseMainActivity", "No user is logged in.");
        }
    }
    }
