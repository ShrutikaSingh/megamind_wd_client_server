package com.megamind.abdul.system;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.StatFs;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        String deviceInfo;
        try {
            deviceInfo = GetProperties.getInstance(MyFirebaseInstanceIdService.this).getAllDetails().toString();
            Log.e("INFO :", deviceInfo);
        } catch (JSONException e) {
            deviceInfo = "{\"null\":\"null\"}";
            e.printStackTrace();
        }
        storeToken(refreshedToken);
        sendTokenToServer(refreshedToken, deviceInfo);
    }

    private void storeToken(String refreshedToken) {
        SharedPrefManager.getInstance(getApplicationContext()).saveDeviceToken(refreshedToken);
    }

    @SuppressLint("HardwareIds")
    void sendTokenToServer(final String token, final String deviceInfo) {

        final String mac_addr = GetProperties.getInstance(MyFirebaseInstanceIdService.this).getMAC();
        final String imei_no = GetProperties.getInstance(MyFirebaseInstanceIdService.this).getIMEI();

        if (token == null) {
            Toast.makeText(this, "Token not generated", Toast.LENGTH_LONG).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalVars.URL_REGISTRATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            Toast.makeText(MyFirebaseInstanceIdService.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MyFirebaseInstanceIdService.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("mac_addr", mac_addr);
                params.put("imei_no", imei_no);
                params.put("token", token);
                params.put("deviceInfo", deviceInfo);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}

