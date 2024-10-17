package net.androidbootcamp.campmoab.UserAccountAttributes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import net.androidbootcamp.campmoab.BaseActivities.BaseActivity;
import net.androidbootcamp.campmoab.Reservations.CalendarActivity;
import net.androidbootcamp.campmoab.Reservations.ViewReservations;
import net.androidbootcamp.campmoab.Classes.UserClass;
import net.androidbootcamp.campmoab.R;

public class UserAccount extends BaseActivity {
    private TextView fullName, reservations, book, reset, delete;
    private EditText contact, email, firstName, lastName;
    private ImageView acctPhoto;
    private Button updateBtn;
    private String userUri = "";
    private StorageTask uploadStorageTask;
    private FirebaseUser user;
    FirebaseAuth mAuth;
    private DatabaseReference ref;
    private AlertDialog.Builder builder;
    private  static final String USERS = "Users";
    private Uri filePath;
    private int PICK_IMAGE_REQUEST = 1000;
    //creating reference to firebase storage
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReferenceFromUrl("gs://camp-moab.appspot.com");
    private StorageReference imageRef;
    private DatabaseReference dataRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_user_account, findViewById(R.id.content_frame));
        toolbar.setTitle("Account");

        acctPhoto = (ImageView) findViewById(R.id.acctPhoto);
        fullName = (TextView) findViewById(R.id.fullName);
        contact = (EditText) findViewById(R.id.inputNumber);
        email = (EditText) findViewById(R.id.inputEmail);
        firstName = (EditText) findViewById(R.id.inputFirstName);
        lastName = (EditText) findViewById(R.id.inputLastName);
        updateBtn = (Button) findViewById(R.id.updateBtn);
        reset = findViewById(R.id.btnResetPass);
        delete = findViewById(R.id.btnDeleteAcct);
        reservations = (TextView)findViewById(R.id.clickReservation);
        book = (TextView) findViewById(R.id.clickBook);

        builder = new AlertDialog.Builder(this);

        // get the currently logged in user.
        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();

        if(user != null) {
            Log.d("UserAccount", "On Create: user is not null");
            // get the UID of the currently logged in user
            String uid = user.getUid();
            dataRef = ref.child(USERS).child(uid);

            imageRef = storageRef.child("Users/").child(uid).child("/photoURL.jpg");
            //Load Account Photo\\
            if (imageRef != null) {
                Log.d("UserAccount", "On Create: imageRef is not null");
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        userUri = uri.toString();
                        Picasso.get().load(userUri).into(acctPhoto);
                    }
                });
            }
            /*imageRef = storageRef.child("Users/"+mAuth.getCurrentUser().getUid()+"/photoURL.jpg");
            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    Picasso.get().load(uri).into(acctPhoto);
                }
            });*/

            //find current user\\
            Query userQuery = ref.child(USERS).child(uid);
            Log.d("UserAccount", "USERid onCreate: " + uid);
            //On page load, show all user information from snapshot of firebase data\\
            userQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Log.d("UserAccount", "On Data Change: " + snapshot);
                    email.setText(snapshot.child("email").getValue(String.class));
                    contact.setText(snapshot.child("phoneNum").getValue(String.class));
                    firstName.setText(snapshot.child("firstName").getValue(String.class));
                    lastName.setText(snapshot.child("lastName").getValue(String.class));
                    Log.d("UserAccount", "fullName: " + fullName);
                    }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //delete account\\
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.setTitle("Delete Account")
                            .setCancelable(true)
                            .setMessage("Are you sure you want to delete your account?")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                FirebaseAuth.getInstance().signOut(); //logout of account
                                                startActivity(new Intent(UserAccount.this, LoginActivity.class));
                                                Log.d("UserAccount", "User account deleted.");
                                            }
                                        }
                                    });

                                userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                            postSnapshot.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //dialog.cancel();
                                }
                            })
                            .create()
                            .show();
                }
            });

            updateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference ref = db.getReference();

                    String mEmail = email.getText().toString();
                    String mPhoneNum = contact.getText().toString();
                    String mFirstName = firstName.getText().toString();
                    String mLastName = lastName.getText().toString();
                    String isAdmin = "false";

                    //Instantiate from UserClass
                    UserClass userClass = new UserClass(mFirstName, mLastName, mPhoneNum, mEmail, isAdmin);
                    userClass.toMap();
                    ref.child(USERS).child(uid).setValue(userClass);
                }
            });

            acctPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);

                }
            });

            //instructions will be sent to listed email to reset password
            reset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //input from user for email
                    final EditText input = new EditText(UserAccount.this);

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
                                                        Toast.makeText(UserAccount.this, "Successfully Sent", Toast.LENGTH_SHORT).show();
                                                        Log.d("UserAccount", "Email sent.");
                                                    } else {
                                                        Toast.makeText(UserAccount.this, "Failed To Send", Toast.LENGTH_SHORT).show();
                                                        Log.d("UserAccount",  "Email not sent.");
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

            //moves to reservation page
            reservations.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UserAccount.this, ViewReservations.class);
                    startActivity(intent);
                }
            });

            //moves to booking page
            book.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UserAccount.this, CalendarActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK, then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the Uri of data
            filePath = data.getData();
            //acctPhoto.setImageURI(filePath);

            uploadAccountPicture(filePath);

            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting image to ImageView
                acctPhoto.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //upload image to firebase storage
    private void uploadAccountPicture(Uri filePath) {
        if(filePath != null) {
            // Create a reference to "image.jpg" with user UID
            StorageReference imageRef = storageRef.child("Users/").child(user.getUid()).child("/photoURL.jpg");

            //uploading the image
           imageRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //pd.dismiss();
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(acctPhoto);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //pd.dismiss();
                    Toast.makeText(UserAccount.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
