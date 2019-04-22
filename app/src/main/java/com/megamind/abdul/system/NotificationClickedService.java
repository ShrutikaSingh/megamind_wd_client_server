package com.megamind.abdul.system;

import android.app.IntentService;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;


public class NotificationClickedService extends IntentService {

    public NotificationClickedService() {
        super("NotificationClickedService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        JSONObject jo_resp = new JSONObject();
        try {
            jo_resp.put("action_name", "notification");
            jo_resp.put("action", "notification clicked");

            ActionResponseSender.Action_Response(NotificationClickedService.this, jo_resp);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        NotificationClickedService.super.stopSelf();
    }

}
