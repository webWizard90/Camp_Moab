package net.androidbootcamp.campmoab.Bookings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import net.androidbootcamp.campmoab.MainActivity;
import net.androidbootcamp.campmoab.R;
import net.androidbootcamp.campmoab.UserAccountAttributes.UserAccount;

import java.util.ArrayList;

public class AddGuestsToBooking extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner spinner;
    private ImageView home, account;
    private Button next;
    private LinearLayout linearLayout;
    private int item;
    private Boolean validateCheck; //flag

    EditText textViewList[];

    private ArrayList<Integer> spinnerQty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_num_guest_booking);
        home = (ImageView) findViewById(R.id.homeImage);
        account = (ImageView) findViewById(R.id.acctImage);
        spinner = (Spinner) findViewById(R.id.spinnerGuests);
        next = (Button) findViewById(R.id.btnNext);
        linearLayout = (LinearLayout) findViewById(R.id.guestLinearList);

        validateCheck = false;

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        spinnerQty = new ArrayList<Integer>();
        spinnerQty.add(0);
        spinnerQty.add(1);
        spinnerQty.add(2);
        spinnerQty.add(3);
        spinnerQty.add(4);
        spinnerQty.add(5);
        spinnerQty.add(6);


        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,
                android.R.layout.simple_spinner_item, spinnerQty);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddGuestsToBooking.this, MainActivity.class));
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddGuestsToBooking.this, UserAccount.class));
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(AddGuestsToBooking.this);
                sharedPref.edit().clear().apply();
                SharedPreferences.Editor editor = sharedPref.edit();

                int spin = (int) spinner.getSelectedItem();
                validateGuestFields();

                editor.putInt("guestCount", spin);
                editor.apply();

                if (validateCheck) {
                    startActivity(new Intent(AddGuestsToBooking.this, ConfirmBooking.class));
                }
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout
                .LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // On selecting a spinner item
        item = (int) parent.getItemAtPosition(position);
        textViewList = new EditText[item];
        linearLayout.removeAllViews();

        for(int i = 0; i < item; i++)
        {
            textViewList[i] = new EditText(this);
            textViewList[i].setId(i);
            textViewList[i].setMaxLines(1);
            textViewList[i].setTextSize(22);
            textViewList[i].setInputType(InputType.TYPE_CLASS_TEXT);
            textViewList[i].setBackground(getResources().getDrawable(R.drawable.border));
            textViewList[i].setHint("Enter Full Name");
            layoutParams.setMargins(20, 0, 20, 25);
            textViewList[i].setPadding(20, 15, 20, 15);
            textViewList[i].setLayoutParams(layoutParams);
            textViewList[i].setHintTextColor(getResources().getColor(R.color.grey));
            linearLayout.addView(textViewList[i]);
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        next.setClickable(false);
    }

    public void validateGuestFields() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(AddGuestsToBooking.this);
        sharedPref.edit().clear().apply();
        SharedPreferences.Editor editor = sharedPref.edit();
        String[] editList = new String[item];
        int count = 0;

        if (item == 0) {
            editor.putString("No Guests", "0");
            editor.apply();
            validateCheck = true;
            count++;
        }

        for(EditText text : textViewList)
        {
            if(text.getText().toString().isEmpty())
            {
                text.setError("Name Is Required");
                text.requestFocus();
                validateCheck = false;
            }
            else {
                editList[count] = text.getText().toString();

                editor.putString(String.valueOf(count), editList[count]);
                editor.apply();
                validateCheck = true;
                count++;
            }
        }
    }
}
