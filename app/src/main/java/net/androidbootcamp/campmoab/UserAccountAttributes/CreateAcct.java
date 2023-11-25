package net.androidbootcamp.campmoab.UserAccountAttributes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;

import net.androidbootcamp.campmoab.MainActivity;
import net.androidbootcamp.campmoab.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class CreateAcct extends AppCompatActivity {
    //Variables called
    EditText firstName, lastName, phoneNum, email, password;
    Button signUp;
    TextView login;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_acct);
        firstName = findViewById(R.id.txtFirstName);
        lastName = findViewById(R.id.txtLastName);
        phoneNum = findViewById(R.id.txtNumber);
        email = findViewById(R.id.txtEmail);
        password = findViewById(R.id.txtPass);
        signUp = (Button) findViewById(R.id.btnSignUp);
        login = (TextView) findViewById(R.id.loginAcc);
        //Method closes keyboard after use
        //closeKeyboard();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //creates an account in firebase database
        signUp.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {

                  String mFirstName = firstName.getText().toString();
                  String mLastName = lastName.getText().toString();
                  String mPhoneNum = phoneNum.getText().toString();
                  String mEmail = email.getText().toString();
                  String mPassword = password.getText().toString();

                  //if statements checks that all fields are properly filled in
                  if (TextUtils.isEmpty(mFirstName)) {
                      firstName.setError("Name Is Required");
                      firstName.requestFocus();
                      return;
                  }
                  if (TextUtils.isEmpty(mLastName)) {
                      lastName.setError("Name Is Required");
                      lastName.requestFocus();
                      return;
                  }
                  if (TextUtils.isEmpty(mEmail)) {
                      email.setError("Email Is Required");
                      email.requestFocus();
                      return;
                  } else if (!Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
                      email.setError("Valid Email Required");
                      email.requestFocus();
                  }
                  if (TextUtils.isEmpty(mPhoneNum)) {
                      phoneNum.setError("Phone Number Is Required");
                      phoneNum.requestFocus();
                      return;
                  } else if (mPhoneNum.length() != 10) {
                      phoneNum.setError("Phone Number Is Not Long Enough");
                      phoneNum.requestFocus();
                      return;
                  }
                  if (TextUtils.isEmpty(mPassword)) {
                      password.setError("Password Is Required");
                      password.requestFocus();
                      return;
                  } else if (mPassword.length() < 6) {
                      password.setError("Password Must Be Longer Than 6 Characters");
                      password.requestFocus();
                      return;
                  }

                  //creates account in firebase
                  mAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                      @Override
                      public void onComplete(@NonNull Task<AuthResult> task) {
                          if (task.isSuccessful()) {
                              //send verification email
                                user = mAuth.getCurrentUser();
                                Log.i("CreateAcct", "User: " + user);

                                if (user != null) {
                                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //Toast.makeText(CreateAcct.this, "Check Your Email To Verify Your Account", Toast.LENGTH_SHORT).show();
                                                updateUI(mFirstName, mLastName, mPhoneNum, mEmail);
                                                Log.v("CreateAcct", "Email Sent");
                                            } else {
                                                Toast.makeText(CreateAcct.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                                                Log.v("CreateAcct", "Error Occurred");
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(CreateAcct.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                                    Log.v("CreateAcct", "Error Occurred");
                                }
                          } else {
                              Toast.makeText(CreateAcct.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                          }
                      }
                  });

                  /*user = mAuth.getInstance().getCurrentUser();
                  Log.i("CreateAcct", "User: " + user);

                  //Add email verification before creating account

                  user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {
                          if (task.isSuccessful()) {
                              Toast.makeText(CreateAcct.this, "Verification Email Sent", Toast.LENGTH_SHORT).show();
                              Log.v("CreateAcct", "Email Sent");

                              //creates account in firebase
                              mAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                  @Override
                                  public void onComplete(@NonNull Task<AuthResult> task) {
                                      if (task.isSuccessful()) {
                                          //if (user.isEmailVerified()) {
                                              updateUI(mFirstName, mLastName, mPhoneNum, mEmail);
                                              startActivity(new Intent(CreateAcct.this, MainActivity.class));
                                              //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                          //} else {
                                              //user.sendEmailVerification();
                                              //Toast.makeText(CreateAcct.this, "Check Your Email To Verify Your Account", Toast.LENGTH_SHORT).show();
                                         // }
                                      } else {
                                          Toast.makeText(CreateAcct.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                      }
                                  }
                              });
                          } else {
                              Toast.makeText(CreateAcct.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                              Log.v("error Occurred", "Error Occurred");
                          }
                      }
                  });*/
              }
          });

        //moves to login page
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateAcct.this, LoginActivity.class));
            }
        });
    }

    // adds user to fire database and after registration moves to main page
    private void updateUI(String mFirstName, String mLastName, String mPhoneNum, String email) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();
        String userId = user.getUid();
        //Instantiate from UserClass
        UserClass userClass = new UserClass(mFirstName, mLastName, mPhoneNum, email);
        userClass.toMap();

        //ref.child("Users").push().setValue(map);
        ref.child("Users").child(userId).setValue(userClass);

        Intent intent = new Intent(CreateAcct.this, LoginActivity.class);
        intent.putExtra("message", "Check Your Email To Verify Your Account");
        intent.putExtra("firstName", mFirstName);
        intent.putExtra("lastName", mLastName);
        startActivity(intent);

    }
}
/*
    public void computeMD5Hash(String password) {
        try {
            //create MD5 hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            //password.update(password.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer MD5Hash = new StringBuffer();
            for(int i = 0; i < messageDigest.length; i++){
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while(h.length() < 2) {
                    h = "0" + h;
                    MD5Hash.append(h);
                }
                hashPassword.setText(MD5Hash);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}*/

/**
 *
 *
 * closes keyboard
 *

 private void closeKeyboard() {
 View view = this.getCurrentFocus();
 if (view != null) {
 InputMethodManager inMesMan = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
 inMesMan.hideSoftInputFromWindow(view.getWindowToken(), 0);
 }
 }     */