package com.jacek.simplesmshandler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

/**
 * Created by Jacek on 27.06.2017.
 */

public class SmsReceiver extends BroadcastReceiver {

    public SmsReceiver() {
        super();

    }

    @Override
    public void onReceive(Context context, Intent intent){
        Bundle bundle = intent.getExtras();
        SmsMessage[] messages;
        String finalMessage = "";

        if (bundle != null){
            Object[] pdus = (Object[]) bundle.get("pdus");
            messages = new SmsMessage[pdus != null ? pdus.length : 0];
            for(int i = 0; i < messages.length; ++i){
                messages[i] = SmsMessage.createFromPdu((byte[]) (pdus != null ? pdus[i] : null), "3gpp");
                finalMessage += messages[i].getOriginatingAddress();
                finalMessage += ": ";
                finalMessage += messages[i].getMessageBody();
                finalMessage += ": ";
                finalMessage += messages[i].getMessageBody().length();
                finalMessage += "\n";
            }

            Toast.makeText(context, finalMessage, Toast.LENGTH_LONG).show();

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("SMS_RECEIVED_ACTION");
            broadcastIntent.putExtra("message", finalMessage);
            context.sendBroadcast(broadcastIntent);
        }
    }
}
