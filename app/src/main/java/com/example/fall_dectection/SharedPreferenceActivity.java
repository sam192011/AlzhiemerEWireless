package com.example.fall_dectection;

/**********************************************************
 *author: Haojue Wang
 *student number:  S1936286
 *
 *Description:In this activity, it mainly has the following functions
 *              1. Using the SharedPreferences to store and load the data
 *              2. This module store 4 parameter, home address,  User weight, Carer email and phone
 * Layoutfile:activity_shared_preference.xml
 * */

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SharedPreferenceActivity extends AppCompatActivity {

    private TextView text_Home_address;
    private TextView text_User_weight;
    private TextView text_Carer_email;
    private TextView text_Carer_phone;

    private EditText EditText_Home_address;
    private EditText EditText_User_weight;
    private EditText EditText_Carer_email;
    private EditText EditText_Carer_phone;

    private Button Button_message_Saved;
    private Button Button_message_Applied;

    private Switch Switch_Home_address;
    private Switch Switch_User_weight;
    private Switch Switch_Carer_email;
    private Switch Switch_Carer_phone;


    public static final String SHARED_Home_address = "initial_SHARED_Home_address";
    public static final String SHARED_User_weight  = "initial_SHARED_User_weight";
    public static final String SHARED_Carer_email  = "initial_SHARED_Carer_email";
    public static final String SHARED_Carer_phone  = "initial_SHARED_Carer_phone";


    public static final String TEXT_Home_address = "initial_Home_address";
    public static final String TEXT_User_weight  = "initial_User_weight";
    public static final String TEXT_Carer_email  = "initial_Carer_email";
    public static final String TEXT_Carer_phone  = "initial_Carer_phone";

    public static final String SWITCH_Home_address = "Home_address_Saved";
    public static final String SWITCH_User_weight = "User_weight_Saved";
    public static final String SWITCH_Carer_email = "Carer_email_Saved";
    public static final String SWITCH_Carer_phone = "Carer_phone_Saved";

    private String Home_address = "London";
    private String User_weight = "80";
    private String Carer_email = "s1936286@ed.ac.uk";
    private String Carer_phone = "";

    private static String return_Home_address  = "London";
    private static String return_User_weight  = "80";
    private static String return_Carer_email  = "s1936286@ed.ac.uk";
    private static String return_Carer_phone  = "";

    private boolean switchOnOff_Home_address;
    private boolean switchOnOff_User_weight;
    private boolean switchOnOff_Carer_email;
    private boolean switchOnOff_Carer_phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_preference);

        text_Home_address= (TextView) findViewById(R.id.text_Home_address);
        text_User_weight = (TextView) findViewById(R.id.text_User_weight);
        text_Carer_email = (TextView) findViewById(R.id.text_Carer_email);
        text_Carer_phone = (TextView) findViewById(R.id.text_Carer_phone);

        EditText_Home_address = (EditText) findViewById(R.id.EditText_Home_address);
        EditText_User_weight = (EditText) findViewById(R.id.EditText_User_weight);
        EditText_Carer_email = (EditText) findViewById(R.id.EditText_Carer_email);
        EditText_Carer_phone = (EditText) findViewById(R.id.EditText_Carer_phone);

        Button_message_Saved = (Button) findViewById(R.id.Button_message_Saved);
        Button_message_Applied = (Button) findViewById(R.id.Button_message_Applied);

        Switch_Home_address = (Switch) findViewById(R.id.Switch_Home_address);
        Switch_User_weight = (Switch) findViewById(R.id.Switch_User_weight);
        Switch_Carer_email = (Switch) findViewById(R.id.Switch_Carer_email);
        Switch_Carer_phone = (Switch) findViewById(R.id.Switch_Carer_phone);

        Button_message_Applied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View View) {
                text_Home_address.setText(EditText_Home_address.getText().toString());
                text_User_weight.setText(EditText_User_weight.getText().toString());
                text_Carer_email.setText(EditText_Carer_email.getText().toString());
                text_Carer_phone.setText(EditText_Carer_phone.getText().toString());
            }
        });

        Button_message_Saved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View View) {
                Data_save();
            }
        });
        Data_load();
        Data_updata();
    }

    //this module is used to store the data in the SharedPreferences
    private void Data_save() {
        SharedPreferences SHARED_Home_address_haredPreferences = getSharedPreferences(SHARED_Home_address, MODE_PRIVATE);
        SharedPreferences SHARED_User_weight_haredPreferences = getSharedPreferences(SHARED_User_weight, MODE_PRIVATE);
        SharedPreferences SHARED_Carer_email_haredPreferences = getSharedPreferences(SHARED_Carer_email, MODE_PRIVATE);
        SharedPreferences SHARED_Carer_phone_haredPreferences = getSharedPreferences(SHARED_Carer_phone, MODE_PRIVATE);

        SharedPreferences.Editor SHARED_Home_address_editor = SHARED_Home_address_haredPreferences.edit();
        SharedPreferences.Editor SHARED_User_weight_editor = SHARED_User_weight_haredPreferences.edit();
        SharedPreferences.Editor SHARED_Carer_email_editor = SHARED_Carer_email_haredPreferences.edit();
        SharedPreferences.Editor SHARED_Carer_phone_editor = SHARED_Carer_phone_haredPreferences.edit();


        SHARED_Home_address_editor.putString(TEXT_Home_address, text_Home_address.getText().toString());
        SHARED_User_weight_editor.putString(TEXT_User_weight, text_User_weight.getText().toString());
        SHARED_Carer_email_editor.putString(TEXT_Carer_email, text_Carer_email.getText().toString());
        SHARED_Carer_phone_editor.putString(TEXT_Carer_phone, text_Carer_phone.getText().toString());

        SHARED_Home_address_editor.putBoolean(SWITCH_Home_address, Switch_Home_address.isChecked());
        SHARED_User_weight_editor.putBoolean(SWITCH_User_weight, Switch_User_weight.isChecked());
        SHARED_Carer_email_editor.putBoolean(SWITCH_Carer_email, Switch_Carer_email.isChecked());
        SHARED_Carer_phone_editor.putBoolean(SWITCH_Carer_phone, Switch_Carer_phone.isChecked());


        SHARED_Home_address_editor.apply();
        SHARED_Home_address_editor.apply();
        SHARED_User_weight_editor.apply();
        SHARED_Carer_email_editor.apply();
        SHARED_Carer_phone_editor.apply();

        Toast.makeText(this, "Data up load data", Toast.LENGTH_SHORT).show();
    }

    //this module is used to load the data
    private void Data_load() {
        SharedPreferences SHARED_Home_address_haredPreferences = getSharedPreferences(SHARED_Home_address, MODE_PRIVATE);
        SharedPreferences SHARED_User_weight_haredPreferences = getSharedPreferences(SHARED_User_weight, MODE_PRIVATE);
        SharedPreferences SHARED_Carer_email_haredPreferences = getSharedPreferences(SHARED_Carer_email, MODE_PRIVATE);
        SharedPreferences SHARED_Carer_phone_haredPreferences = getSharedPreferences(SHARED_Carer_phone, MODE_PRIVATE);

        Home_address= SHARED_Home_address_haredPreferences.getString(TEXT_Home_address, "");
        User_weight= SHARED_User_weight_haredPreferences.getString(TEXT_User_weight, "");
        Carer_email = SHARED_Carer_email_haredPreferences.getString(TEXT_Carer_email, "");
        Carer_phone = SHARED_Carer_phone_haredPreferences.getString(TEXT_Carer_phone, "");

        switchOnOff_Home_address = SHARED_Home_address_haredPreferences.getBoolean(SWITCH_Home_address, false);
        switchOnOff_User_weight = SHARED_User_weight_haredPreferences.getBoolean(SWITCH_User_weight, false);
        switchOnOff_Carer_email = SHARED_Carer_email_haredPreferences.getBoolean(SWITCH_Carer_email, false);
        switchOnOff_Carer_phone = SHARED_Carer_phone_haredPreferences.getBoolean(SWITCH_Carer_phone, false);

    }

    //this module is used to upload the data
    private void Data_updata() {
        text_Home_address.setText(Home_address);
        text_User_weight.setText(User_weight);
        text_Carer_email.setText(Carer_email);
        text_Carer_phone.setText(Carer_phone);

        return_Home_address  = Home_address;
        return_User_weight  = User_weight;
        return_Carer_email  = Carer_email;
        return_Carer_phone  = Carer_phone;

        Switch_Home_address.setChecked(switchOnOff_Home_address);
        Switch_User_weight.setChecked(switchOnOff_User_weight);
        Switch_Carer_email.setChecked(switchOnOff_Carer_email);
        Switch_Carer_phone.setChecked(switchOnOff_Carer_phone);
    }

    //this module is used to return the home address
    public String return_Home_address(){
        String return_address = return_Home_address;
        return return_address;
    }

    //this module is used to return the user weight
    public String return_User_weight(){
        String return_weight = return_User_weight;
        return return_weight;
    }

    //this module is used to return the email address
    public String return_Carer_email(){
        String return_email = return_Carer_email;
        return return_email;
    }

    //this module is used to return the phone number
    public String return_Carer_phone(){
        String return_phone = return_Carer_phone;
        return return_phone;
    }
}
