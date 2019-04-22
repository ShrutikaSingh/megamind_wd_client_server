package com.megamind.abdul.system;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.StatFs;
import android.telephony.TelephonyManager;

import org.json.JSONException;
import org.json.JSONObject;


class GetProperties {
    private static Context mCtx;
    private static GetProperties mInstance;

    private GetProperties(Context context) {
        mCtx = context;
    }

    public static synchronized GetProperties getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new GetProperties(context);
        }
        return mInstance;
    }

    @SuppressLint("HardwareIds")
    public String getMAC() {
        WifiManager manager = (WifiManager) mCtx.getSystemService(Context.WIFI_SERVICE);
        final WifiInfo info = manager.getConnectionInfo();
        return info.getMacAddress();
    }

    @SuppressLint("HardwareIds")
    public String getIMEI() {
        TelephonyManager tm = (TelephonyManager) mCtx.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();

    }

    JSONObject getAllDetails() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("SERIAL", Build.SERIAL);
        jsonObject.put("CPU_ABI", Build.CPU_ABI);
        jsonObject.put("BOARD", Build.BOARD);
        jsonObject.put("BOOTLOADER", Build.BOOTLOADER);
        jsonObject.put("BRAND", Build.BRAND);
        jsonObject.put("DEVICE", Build.DEVICE);
        jsonObject.put("DISPLAY", Build.DISPLAY);
        jsonObject.put("FINGERPRINT", Build.FINGERPRINT);
        jsonObject.put("RadioVersion", Build.getRadioVersion());
        jsonObject.put("HARDWARE", Build.HARDWARE);
        jsonObject.put("HOST", Build.HOST);
        jsonObject.put("ID", Build.ID);
        jsonObject.put("MANUFACTURER", Build.MANUFACTURER);
        jsonObject.put("MODEL", Build.MODEL);
        jsonObject.put("TYPE", Build.TYPE);
        jsonObject.put("PRODUCT", Build.PRODUCT);
        jsonObject.put("USER", Build.USER);
        jsonObject.put("VERSION_CODENAME", Build.VERSION.CODENAME);
        jsonObject.put("VERSION_RELEASE", Build.VERSION.RELEASE);
        jsonObject.put("VERSION_INCREMENTAL", Build.VERSION.INCREMENTAL);
        jsonObject.put("VERSION_CODES_BASE", Build.VERSION_CODES.BASE);
        jsonObject.put("VERSION_SDK", Build.VERSION.SDK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            jsonObject.put("VERSION_BASE_OS", Build.VERSION.BASE_OS);
        jsonObject.put("INTERNAL_STORAGE_PATH", System.getenv("EXTERNAL_STORAGE"));
        jsonObject.put("EXTERNAL_STORAGE_PATH", System.getenv("SECONDARY_STORAGE"));

        try {
            jsonObject.put("RAM_SIZE", convertToString(Runtime.getRuntime().totalMemory()));
        } catch (JSONException ignored) {
        }

        BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
        String deviceName = myDevice.getName();

        jsonObject.put("BLUETOOTH_NAME", deviceName);

        try {
            jsonObject.put("INTERNAL_SPACE_AVAILABLE", getSize("available", System.getenv("EXTERNAL_STORAGE")));
            jsonObject.put("EXTERNAL_SPACE_AVAILABLE", getSize("available", System.getenv("SECONDARY_STORAGE")));
            jsonObject.put("INTERNAL_SPACE", getSize("total", System.getenv("EXTERNAL_STORAGE")));
            jsonObject.put("EXTERNAL_SPACE", getSize("total", System.getenv("SECONDARY_STORAGE")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private static String getSize(String type, String path) {

        StatFs stat = new StatFs(path);
        long blockSize = stat.getBlockSize();
        long totalBlocks;
        if (type == "available")
            totalBlocks = stat.getAvailableBlocks();
        else
            totalBlocks = stat.getBlockCount();
        long size = totalBlocks * blockSize;

        return convertToString(size);
    }

    private static String convertToString(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

}
