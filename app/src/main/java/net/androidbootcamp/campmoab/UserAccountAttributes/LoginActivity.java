package net.androidbootcamp.campmoab.UserAccountAttributes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import net.androidbootcamp.campmoab.Classes.FirebaseHelperClass;
import net.androidbootcamp.campmoab.MainActivity;
import net.androidbootcamp.campmoab.R;

public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private Button login;
    private TextView createAcct, newPass;
    private FirebaseHelperClass firebaseHelper;
    private FirebaseAuth fAuth;
    private DatabaseReference ref;
    private static final String TAG = "LoginActivity";
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.txtEmail);
        password = (EditText) findViewById(R.id.txtPass);
        login = (Button) findViewById(R.id.btnLogin);
        newPass = (TextView)findViewById(R.id.txtNewPass);
        createAcct = (TextView) findViewById(R.id.txtRegister);

        firebaseHelper = new FirebaseHelperClass();
        fAuth = firebaseHelper.getAuth();
        ref = firebaseHelper.getRef();

        builder = new AlertDialog.Builder(this);

        //get intent from CreateAcct activity and set message
        Intent intent = getIntent();
        String message = intent.getStringExtra("message");
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
        //String firstName = intent.getStringExtra("firstName");
        //String lastName = intent.getStringExtra("lastName");

        //closeKeyboard();

        //searches for user credentials in the database
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail = email.getText().toString().trim();
                String mPassword = password.getText().toString().trim();

                if (TextUtils.isEmpty(mEmail)) {
                    email.setError("Email Required");
                    email.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(mPassword)) {
                    password.setError("Password Required");
                    password.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
                    email.setError("Valid Email Required");
                    email.requestFocus();
                }

                fAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    //if user is found, moves to main page
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (fAuth.getCurrentUser().isEmailVerified()) {
                                //Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                Log.d(TAG, "User Found");
                            } else {
                                Toast.makeText(LoginActivity.this, "Please Verify Email Before Logging In.", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "User email not verified");
                            }
                        } else {
                            Log.e(TAG, "Sign-in Failed: " + task.getException().getMessage());
                            Toast.makeText(LoginActivity.this, "User not found. Check email and password or create an account", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        newPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //input from user for email
                final EditText input = new EditText(LoginActivity.this);

                builder.setTitle("Reset Password")
                        .setCancelable(true)
                        .setMessage("Password Reset Instructions Will Be Sent To The Provided Email Address")
                        .setView(input)
                        .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().sendPasswordResetEmail(input.getText().toString())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(LoginActivity.this, "Successfully Sent", Toast.LENGTH_SHORT).show();
                                                    Log.d(TAG, "Email sent.");
                                                } else {
                                                    Toast.makeText(LoginActivity.this, "Failed To Send", Toast.LENGTH_SHORT).show();
                                                    Log.d(TAG, "Email not sent.");
                                                }
                                            }
                                        });
                            }
                        })

                        .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            }
        });

        //moves to register new user page
        createAcct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, CreateAcct.class);
                startActivity(intent);
            }
        });
    }
    /**
     *
     *
     * CLASS USED TO RETRIEVE USER INFORMATION FROM DATABASE
     *

    //closes keyboard
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inMesMan = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inMesMan.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }     */
}