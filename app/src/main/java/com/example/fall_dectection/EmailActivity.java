package com.example.fall_dectection;


/**********************************************************
 *author: Haojue Wang
 *student number:  S1936286
 *
 *Description:In this activity, it mainly has the following functions
 *          1. Use Google Mail to send mail
 *          2. Configure for Google email smtp
 *          3. Set the default EditText for user convenience
 * Layoutfile:activity_main.xml
 * */

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.os.Message;
import javax.mail.Session;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import javax.mail.Message;
import javax.mail.MessagingException;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.Properties;


public class EmailActivity extends AppCompatActivity {

    EditText EditText_Email_To,EditText_Email_Subject,EditText_Email_Message;
    Button Button_Email_Send;
    String String_Account_Address,String_Account_Password;
    private int Send_Trigger = 0;
    private double Send_user_lat;
    private double Send_user_long;
    private String Send_user_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        //Edit the text
        EditText_Email_To = findViewById(R.id.EditText_Email_To);
        EditText_Email_Subject = findViewById(R.id.EditText_Email_Subject);
        EditText_Email_Message = findViewById(R.id.EditText_Email_Message);
        Button_Email_Send = findViewById(R.id.Button_Email_Send);

        //Sender Email Set
        String_Account_Address = "edwirelessgroup@gmail.com ";
        String_Account_Password = "Edwireless.";

        //Send mail manually
        Button_Email_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View View) {
                Send_Email();
            }
        });

        //Send mail automatically
        Intent_data_share intent_data_share = (Intent_data_share) getIntent().getSerializableExtra("intent_share");
        Send_Trigger = getIntent().getIntExtra("Fall_Down_Trigger", 0);

        //Set the default Edit text
        if (Send_Trigger == 30 ){
            //receive the data from main activity by using Serializable
            Send_user_lat = intent_data_share.get_share_lat();
            Send_user_long = intent_data_share.get_share_long();
            Send_user_address = intent_data_share.get_share_address();
            //set the send information
            SharedPreferenceActivity sharedPreferenceActivity = new SharedPreferenceActivity();
            String Data_email = sharedPreferenceActivity.return_Carer_email();

            EditText_Email_To.setText(Data_email);
            EditText_Email_Subject.setText("Fall Down! Help!");
            EditText_Email_Message.setText("Fall down in Latlong" + Send_user_lat+ "," + Send_user_long +
                    "In address of:" +Send_user_address);
            Send_Email();
            Send_Trigger = 0;
        }
    }

    //Email smtp configuration
    private void Send_Email() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.starttls.enable","true");
        properties.put("mail.smtp.host","smtp.gmail.com");
        properties.put("mail.smtp.port","587");

        //initialize session
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication(){
                return  new PasswordAuthentication(String_Account_Address,String_Account_Password);
            }
        });
        //initial email content
        MimeMessage message = new MimeMessage(session);
        try {
            //Sender email
            message.setFrom(new InternetAddress(String_Account_Address));
            //Recipient email
            message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(EditText_Email_To.getText().toString().trim()));
            //Email subject
            message.setSubject(EditText_Email_Subject.getText().toString().trim());
            //Email message
            message.setText(EditText_Email_Message.getText().toString().trim());
            //Send email
            new SendMail().execute(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    private class SendMail extends AsyncTask<Message,String,String> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = ProgressDialog.show(EmailActivity.this,
                    "Please Wait","Sending EMail",true,false);
        }
        @Override
        protected String doInBackground(Message... messages) {
            try{
                Transport.send(messages[0]);
                return "Success";
            }catch (MessagingException e){
                e.printStackTrace();
                return "Error";
            }
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            //Dismiss progress dialog
            progressDialog.dismiss();
            //when success
            if(s.equals("Success")){
                //Initialize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(EmailActivity.this);
                builder.setCancelable(false);
                builder.setTitle(Html.fromHtml("<font color = '#509324'>Success</font>"));
                builder.setMessage("Mail send Successfully.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                //show alert dialog
                builder.show();
            }else{
                Toast.makeText(getApplicationContext(),"Something Wrong",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
