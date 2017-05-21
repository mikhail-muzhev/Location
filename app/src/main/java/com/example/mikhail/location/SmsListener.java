package com.example.mikhail.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.ArrayList;


public class SmsListener extends BroadcastReceiver {
    public SmsListener() {
       // super();
    }

    private String TAG = SmsListener.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        SmsMessage[] msgs = null;

        String str = "";
        String senderAddress = "";

        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];

            // For every SMS message received
            for (int i=0; i < msgs.length; i++) {
                // Convert Object array
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                // Sender's phone number
                str += "SMS from " + msgs[i].getOriginatingAddress() + " : ";


                // Fetch the text message
                str += msgs[i].getMessageBody();
                if(str.contains("NeedLoc")){
                    senderAddress = msgs[i].getOriginatingAddress();
                }
                // Newline <img draggable="false" class="emoji" alt="🙂" src="https://s.w.org/images/core/emoji/72x72/1f642.png">
                str += "\n";
            }


            if(str.contains("NeedLoc") && senderAddress.equals("")){
               sendSms(senderAddress, Location.makeURLFromLocation(MainActivity.TM));
                System.out.print(true);
            }

            // Display the entire SMS Message
            Log.d(TAG, str);
        }
    }

    public static void sendSms(String address,String msgContent)
    {
        try
        {
            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> smsString = sms.divideMessage(msgContent);
            sms.sendMultipartTextMessage(address, null, smsString, null, null);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


}
