package net.androidbootcamp.campmoab.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.androidbootcamp.campmoab.Admin.Adapters.AdminUserAdapter;
import net.androidbootcamp.campmoab.R;
import net.androidbootcamp.campmoab.UserAccountAttributes.UserAccount;
import net.androidbootcamp.campmoab.UserAccountAttributes.UserClass;

import java.util.ArrayList;

public class AdminUsersList extends AppCompatActivity {
    private String firstName, lastName, phoneNum, email;
    private RecyclerView mainRecycler;
    private AdminUserAdapter adapter;
    private ImageView home, account;

    private ArrayList<UserClass> users = new ArrayList<>();
    private ArrayList<String> uids = new ArrayList<>();

    private FirebaseAuth mAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_user_list);
        mainRecycler = (RecyclerView) findViewById(R.id.recyclerView);
        mainRecycler.setLayoutManager(new LinearLayoutManager(this));
        home = (ImageView) findViewById(R.id.home);
        account = (ImageView) findViewById(R.id.acct);

        fetch();

    }
        private void fetch() {
            // Create an instance of the database and get its reference
            Query query = FirebaseDatabase.getInstance().getReference()
                    .child("Users");

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            uids.add(dataSnapshot.getKey());
                            UserClass user = dataSnapshot.getValue(UserClass.class);
                            firstName = user.getFirstName();
                            lastName = user.getLastName();
                            phoneNum = user.getPhoneNum();
                            email = user.getEmail();
                            users.add(new UserClass(firstName, lastName, phoneNum, email));
                            Log.v("users", firstName + " " + lastName + " " + phoneNum + " " + email);
                        }
                        Log.v("uids", String.valueOf(uids));

                        // Connecting object of required Adapter class to
                        // the Adapter class itself
                        adapter = new AdminUserAdapter(users, uids, AdminUsersList.this);
                        // Connecting Adapter class with the Recycler view
                        mainRecycler.setAdapter(adapter);
                    } else {
                        Log.v("No Data", "No data found");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminUsersList.this, AdminMainActivity.class));
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminUsersList.this, UserAccount.class));
            }
        });
    }
}
