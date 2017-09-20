package com.jacek.simplesmshandler;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText number_editText;
    private EditText message_editText;
    private Button send_button;
    private Button longMessage_button;
    private IntentFilter intentFilter;
    private BroadcastReceiver broadcastReceiver;
    private TextView incomingMessage_textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        number_editText = (EditText) findViewById(R.id.number_editText);
        message_editText = (EditText) findViewById(R.id.message_editText);
        send_button = (Button) findViewById(R.id.send_button);
        longMessage_button = (Button) findViewById(R.id.longMessage_button);

        intentFilter = new IntentFilter("SMS_RECEIVED_ACTION");
        incomingMessage_textView = (TextView) findViewById(R.id.incomingMessage_textView);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                incomingMessage_textView.setText(String.valueOf(intent.getStringExtra("message")));
            }
        };

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = number_editText.getText().toString();
                String message = message_editText.getText().toString();
                sendSMS(number, message);
            }
        });

        longMessage_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = number_editText.getText().toString();
                sendSMS(number, generateLongMessage(200));
            }
        });

    }

    public void sendSMS(String number, String message){
        String sent = "MESSAGE SENT";
        String delivered = "MESSAGE RECEIVED";

        PendingIntent sentPi = PendingIntent.getBroadcast(this, 0, new Intent(sent), 0);
        PendingIntent deliveredPi = PendingIntent.getBroadcast(this, 0, new Intent(delivered), 0);


        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(sent));

        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(delivered));

        SmsManager smsManager = SmsManager.getDefault();
        if(message.length() > 160){
            ArrayList<String> splitMessages = smsManager.divideMessage(message);
            smsManager.sendMultipartTextMessage(number, null, splitMessages, null, null);
        }
        smsManager.sendTextMessage(number, null, message, sentPi, deliveredPi);
    }

    public String generateLongMessage(int numberOfChars){
        String longMessage = "";
        for (int i = 0; i<numberOfChars; i++){
            longMessage += "a";
        }
        return longMessage;
    }

    @Override
    protected void onResume(){
        registerReceiver(broadcastReceiver, intentFilter);
        super.onResume();
    }

    @Override
    protected void onPause(){
        unregisterReceiver(broadcastReceiver);
        super.onPause();
    }
}
