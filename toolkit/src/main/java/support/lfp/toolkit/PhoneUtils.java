package support.lfp.toolkit;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import androidx.annotation.RequiresPermission;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.List;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.SEND_SMS;

/**
 * <pre>
 * Tip:
 *      手机设备信息相关工具
 * Function:
 *      isPhone()               :判断设备是否为手机
 *      isTablet()              :判断设备是否为平板
 *      getDeviceId()           :获得唯一的设备id
 *      getSerial()             :获得设备的序列号
 *      getIMEI()               :获得设备的IMEI
 *      getMEID()               :获得设备的MEID
 *      getIMSI()               :获得设备的IMSI
 *      getPhoneType()          :获得设备类型
 *      isSimCardReady()        :判断sim卡状态是否就绪
 *      getSimOperatorName()    :获得sim操作符名称
 *      getSimOperatorByMnc()   :用mnc获得sim操作符
 *      getPhoneStatus()        :获得设备状态
 *      dial()                  :转到拨号
 *      call()                  :直接拨号
 *      sendSms()               :转到发送短信
 *      sendSmsSilent()         :直接发送短信
 *      getPhoneOnlyTag()       :通过设备信息计算的唯一标识
 *
 * Created by LiFuPing on 2018/6/1.
 * </pre>
 */
public class PhoneUtils {

    private PhoneUtils() {
    }


    /**
     * Return whether the device is phone.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isPhone() {
        TelephonyManager tm =
                (TelephonyManager) AppUtils.getApp().getSystemService(Context.TELEPHONY_SERVICE);
        return tm != null && tm.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
    }

    /**
     * Return whether device is tablet.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isTablet() {
        return (AppUtils.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * Return the unique device id.
     * <p>Must hold
     * {@code <uses-permission android:name="android.permission.READ_PHONE_STATE" />}</p>
     *
     * @return the unique device id
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    public static String getDeviceId() {
        TelephonyManager tm =
                (TelephonyManager) AppUtils.getApp().getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (tm == null) return "";
            String imei = tm.getImei();
            if (!TextUtils.isEmpty(imei)) return imei;
            String meid = tm.getMeid();
            return TextUtils.isEmpty(meid) ? "" : meid;

        }
        return tm != null ? tm.getDeviceId() : "";
    }

    /**
     * Return the serial of device.
     *
     * @return the serial of device
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    public static String getSerial() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Build.getSerial();
        } else {
            return Build.SERIAL;
        }
    }

    /**
     * Return the IMEI.
     * <p>Must hold
     * {@code <uses-permission android:name="android.permission.READ_PHONE_STATE" />}</p>
     *
     * @return the IMEI
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    public static String getIMEI() {
        TelephonyManager tm =
                (TelephonyManager) AppUtils.getApp().getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return tm != null ? tm.getImei() : "";
        }
        return tm != null ? tm.getDeviceId() : "";
    }

    /**
     * Return the MEID.
     * <p>Must hold
     * {@code <uses-permission android:name="android.permission.READ_PHONE_STATE" />}</p>
     *
     * @return the MEID
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    public static String getMEID() {
        TelephonyManager tm =
                (TelephonyManager) AppUtils.getApp().getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return tm != null ? tm.getMeid() : "";
        } else {
            return tm != null ? tm.getDeviceId() : "";
        }
    }

    /**
     * Return the IMSI.
     * <p>Must hold
     * {@code <uses-permission android:name="android.permission.READ_PHONE_STATE" />}</p>
     *
     * @return the IMSI
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    public static String getIMSI() {
        TelephonyManager tm =
                (TelephonyManager) AppUtils.getApp().getSystemService(Context.TELEPHONY_SERVICE);
        return tm != null ? tm.getSubscriberId() : "";
    }

    /**
     * Returns the current phone type.
     *
     * @return the current phone type
     * <ul>
     * <li>{@link TelephonyManager#PHONE_TYPE_NONE}</li>
     * <li>{@link TelephonyManager#PHONE_TYPE_GSM }</li>
     * <li>{@link TelephonyManager#PHONE_TYPE_CDMA}</li>
     * <li>{@link TelephonyManager#PHONE_TYPE_SIP }</li>
     * </ul>
     */
    public static int getPhoneType() {
        TelephonyManager tm =
                (TelephonyManager) AppUtils.getApp().getSystemService(Context.TELEPHONY_SERVICE);
        return tm != null ? tm.getPhoneType() : -1;
    }

    /**
     * Return whether sim card state is ready.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isSimCardReady() {
        TelephonyManager tm =
                (TelephonyManager) AppUtils.getApp().getSystemService(Context.TELEPHONY_SERVICE);
        return tm != null && tm.getSimState() == TelephonyManager.SIM_STATE_READY;
    }

    /**
     * Return the sim operator name.
     *
     * @return the sim operator name
     */
    public static String getSimOperatorName() {
        TelephonyManager tm =
                (TelephonyManager) AppUtils.getApp().getSystemService(Context.TELEPHONY_SERVICE);
        return tm != null ? tm.getSimOperatorName() : "";
    }

    /**
     * Return the sim operator using mnc.
     *
     * @return the sim operator
     */
    public static String getSimOperatorByMnc() {
        TelephonyManager tm =
                (TelephonyManager) AppUtils.getApp().getSystemService(Context.TELEPHONY_SERVICE);
        String operator = tm != null ? tm.getSimOperator() : null;
        if (operator == null) return null;
        switch (operator) {
            case "46000":
            case "46002":
            case "46007":
                return "中国移动";
            case "46001":
                return "中国联通";
            case "46003":
                return "中国电信";
            default:
                return operator;
        }
    }

    /**
     * Return the phone status.
     * <p>Must hold
     * {@code <uses-permission android:name="android.permission.READ_PHONE_STATE" />}</p>
     *
     * @return DeviceId = 99000311726612<br>
     * DeviceSoftwareVersion = 00<br>
     * Line1Number =<br>
     * NetworkCountryIso = cn<br>
     * NetworkOperator = 46003<br>
     * NetworkOperatorName = 中国电信<br>
     * NetworkType = 6<br>
     * PhoneType = 2<br>
     * SimCountryIso = cn<br>
     * SimOperator = 46003<br>
     * SimOperatorName = 中国电信<br>
     * SimSerialNumber = 89860315045710604022<br>
     * SimState = 5<br>
     * SubscriberId(IMSI) = 460030419724900<br>
     * VoiceMailNumber = *86<br>
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    public static String getPhoneStatus() {
        TelephonyManager tm =
                (TelephonyManager) AppUtils.getApp().getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) return "";
        String str = "";
        str += "DeviceId(IMEI) = " + tm.getDeviceId() + "\n";
        str += "DeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion() + "\n";
        str += "Line1Number = " + tm.getLine1Number() + "\n";
        str += "NetworkCountryIso = " + tm.getNetworkCountryIso() + "\n";
        str += "NetworkOperator = " + tm.getNetworkOperator() + "\n";
        str += "NetworkOperatorName = " + tm.getNetworkOperatorName() + "\n";
        str += "NetworkType = " + tm.getNetworkType() + "\n";
        str += "PhoneType = " + tm.getPhoneType() + "\n";
        str += "SimCountryIso = " + tm.getSimCountryIso() + "\n";
        str += "SimOperator = " + tm.getSimOperator() + "\n";
        str += "SimOperatorName = " + tm.getSimOperatorName() + "\n";
        str += "SimSerialNumber = " + tm.getSimSerialNumber() + "\n";
        str += "SimState = " + tm.getSimState() + "\n";
        str += "SubscriberId(IMSI) = " + tm.getSubscriberId() + "\n";
        str += "VoiceMailNumber = " + tm.getVoiceMailNumber() + "\n";
        return str;
    }

    /**
     * Skip to dial.
     *
     * @param phoneNumber The phone number.
     */
    public static void dial(final String phoneNumber) {
        AppUtils.getApp().startActivity(IntentUtils.getDialIntent(phoneNumber, true));
    }

    /**
     * Make a phone call.
     * <p>Must hold {@code <uses-permission android:name="android.permission.CALL_PHONE" />}</p>
     *
     * @param phoneNumber The phone number.
     */
    @RequiresPermission(CALL_PHONE)
    public static void call(final String phoneNumber) {
        AppUtils.getApp().startActivity(IntentUtils.getCallIntent(phoneNumber, true));
    }

    /**
     * Send sms.
     *
     * @param phoneNumber The phone number.
     * @param content     The content.
     */
    public static void sendSms(final String phoneNumber, final String content) {
        AppUtils.getApp().startActivity(IntentUtils.getSendSmsIntent(phoneNumber, content, true));
    }

    /**
     * Send sms silently.
     * <p>Must hold {@code <uses-permission android:name="android.permission.SEND_SMS" />}</p>
     *
     * @param phoneNumber The phone number.
     * @param content     The content.
     */
    @RequiresPermission(SEND_SMS)
    public static void sendSmsSilent(final String phoneNumber, final String content) {
        if (Utils.isEmpty(content)) return;
        PendingIntent sentIntent = PendingIntent.getBroadcast(AppUtils.getApp(), 0, new Intent("send"), 0);
        SmsManager smsManager = SmsManager.getDefault();
        if (content.length() >= 70) {
            List<String> ms = smsManager.divideMessage(content);
            for (String str : ms) {
                smsManager.sendTextMessage(phoneNumber, null, str, sentIntent, null);
            }
        } else {
            smsManager.sendTextMessage(phoneNumber, null, content, sentIntent, null);
        }
    }

    /**
     * 获得设备唯一标记
     *
     * @return 设备唯一标记
     */
    public static String getPhoneOnlyTag() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(Build.BOARD);
        buffer.append(Build.BRAND);
        buffer.append(Build.CPU_ABI);
        buffer.append(Build.DEVICE);
        buffer.append(Build.DISPLAY);
        buffer.append(Build.HOST);
        buffer.append(Build.ID);
        buffer.append(Build.MANUFACTURER);
        buffer.append(Build.MODEL);
        buffer.append(Build.PRODUCT);
        buffer.append(Build.TAGS);
        buffer.append(Build.TYPE);
        buffer.append(Build.USER);
        return EncryptUtils.encryptMD5ToString(buffer.toString());
    }

    /**
     * 获得设备信息
     *
     * @return String
     */
    public static String getPhoneInfo() {
        return MessageFormat.format("设备厂商:{0}\n设备名称:{1}\nAPI:{2} {3}",
                Build.BOARD + "  " + Build.MANUFACTURER,
                Build.MODEL,
                SDKUtiles.getSdkVersion(),
                SDKUtiles.getSdkVersionName());
    }


    /*---- 系统检测 ---*/


    /**
     * 系统ROM
     */
    public enum ROM {
        /**
         * 小米
         */
        MIUI
        /** 索尼 */
        , SONY
        /** 三星 */
        , SAMSUNG
        /** LG */
        , LG
        /** HTC */
        , HTC
        /** 华为 NOVA */
        , NOVA
        /** OPPO */
        , OPPO
        /** 乐视 */
        , LEMOBILE
        /** VIVO */
        , VIVO
        /** 华为 */
        , HUAWEI
        /** 魅族 */
        , Flyme

    }

    /**
     * 获得Rom
     *
     * @return the Rom
     */
    public static ROM getRom() {
        if (Build.MANUFACTURER.equalsIgnoreCase("xiaomi")) {
            return ROM.MIUI; /*小米*/
        } else if (Build.MANUFACTURER.equalsIgnoreCase("sony")) {
            return ROM.SONY;  /*索尼*/
        } else if (Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
            return ROM.SAMSUNG;  /*三星*/
        } else if (Build.MANUFACTURER.equalsIgnoreCase("lg")) {
            return ROM.LG;  /*LG*/
        } else if (Build.MANUFACTURER.equalsIgnoreCase("htc")) {
            return ROM.HTC;  /*HTC*/
        } else if (Build.MANUFACTURER.equalsIgnoreCase("nova")) {
            return ROM.NOVA; /*NOVA*/
        } else if (Build.MANUFACTURER.equalsIgnoreCase("OPPO")) {
            return ROM.OPPO; /*Oppo*/
        } else if (Build.MANUFACTURER.equalsIgnoreCase("LeMobile")) {
            return ROM.LEMOBILE; /*乐视*/
        } else if (Build.MANUFACTURER.equalsIgnoreCase("vivo")) {
            return ROM.VIVO; /*Vivo*/
        } else if (Build.MANUFACTURER.equalsIgnoreCase("HUAWEI") || Build.BRAND.equals("Huawe")) {
            return ROM.HUAWEI; /*华为*/
        } else if (Build.BRAND.equalsIgnoreCase("meizu")) {
            return ROM.Flyme; /*Vivo*/
        }
        return null;
    }


    /*获得Rom版本*/
    private static String getRomVersion(String name) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + name);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return line;
    }


}
