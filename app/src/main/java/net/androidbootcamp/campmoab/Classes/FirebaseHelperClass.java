package net.androidbootcamp.campmoab.Classes;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FirebaseHelperClass {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    // Constructor
    public FirebaseHelperClass() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
    }

    // Method to get the authentication instance
    public FirebaseAuth getAuth() {
        return mAuth;
    }

    // Method to get the current user
    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    // Method to get the database reference
    public DatabaseReference getRef() {
        return ref;
    }

    // Example method to add data to the database
    public void addData(String path, Object data) {
        ref.child(path).setValue(data)
                .addOnSuccessListener(aVoid -> {
                    // Data was added successfully
                    Log.d("Firebase", "Data added successfully at path: " + path);
                })
                .addOnFailureListener(e -> {
                    // Failed to add data
                    Log.e("Firebase", "Failed to add data: " + e.getMessage());
                });
    }

    // Example method to read data from the database
    public void readData(String path, ValueEventListener listener) {
        ref.child(path).addListenerForSingleValueEvent(listener);
    }
    
    // Method to check if the user is an admin based on Firebase structure
    public void checkIfAdmin(String uid, AdminCheckCallback callback) {
        DatabaseReference ref = this.ref;

        // Query to check if the user is an admin
        Query query = ref.child("Admins").child(uid).child("isAdmin");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isAdmin = snapshot.exists() && snapshot.getValue(Boolean.class) != null && snapshot.getValue(Boolean.class);
                Log.d("Firebase", "User is an admin: " + isAdmin);
                callback.onAdminCheck(isAdmin);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error occurred: " + error.getMessage());
                callback.onAdminCheck(false); // Assume not admin if an error occurs
            }
        });
    }

    public interface AdminCheckCallback {
        void onAdminCheck(boolean isAdmin);
    }
}
