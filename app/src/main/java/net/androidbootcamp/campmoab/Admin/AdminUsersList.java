package net.androidbootcamp.campmoab.Admin;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.androidbootcamp.campmoab.Admin.Adapters.AdminUserAdapter;
import net.androidbootcamp.campmoab.BaseActivities.BaseActivity;
import net.androidbootcamp.campmoab.R;
import net.androidbootcamp.campmoab.Classes.UserClass;

import java.util.ArrayList;

public class AdminUsersList extends BaseActivity {
    private String firstName, lastName, phoneNum, email;
    private RecyclerView mainRecycler;
    private AdminUserAdapter adapter;
    private ArrayList<UserClass> users = new ArrayList<>();
    private ArrayList<String> UIDs = new ArrayList<>();
    private FirebaseAuth mAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_admin_user_list, findViewById(R.id.content_frame));
        toolbar.setTitle("Users");

        mainRecycler = (RecyclerView) findViewById(R.id.recyclerView);
        mainRecycler.setLayoutManager(new LinearLayoutManager(this));

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
                        UIDs.add(dataSnapshot.getKey());
                        UserClass user = dataSnapshot.getValue(UserClass.class);
                        firstName = user.getFirstName();
                        lastName = user.getLastName();
                        phoneNum = user.getPhoneNum();
                        email = user.getEmail();
                        users.add(new UserClass(firstName, lastName, phoneNum, email));
                        Log.d("AdminUserList", "Users: " + firstName + " " + lastName + " " + phoneNum + " " + email);
                    }
                    Log.d("AdminUserList","UIDs: " + UIDs);

                    // Connecting object of required Adapter class to
                    // the Adapter class itself
                    adapter = new AdminUserAdapter(users, UIDs, AdminUsersList.this);
                    // Connecting Adapter class with the Recycler view
                    mainRecycler.setAdapter(adapter);
                } else {
                    Log.d("AdminUserList","No Data: No data found in query");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
