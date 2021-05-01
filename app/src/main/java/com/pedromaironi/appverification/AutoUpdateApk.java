package com.pedromaironi.appverification;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceActivity;
import androidx.preference.PreferenceManager;
import android.util.Log;

import androidx.preference.Preference;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A fairly simple non-Market app update checker. Give it a URL pointing to a JSON file
 * and it will compare its version (from the manifest file) to the versions listed in the JSON.
 * If there are newer version(s), it will provide the changelog between the installed version
 * and the latest version. The updater checks against the versionCode, but displays the versionName.
 *
 * While you can create your own OnAppUpdateListener to listen for new updates, OnUpdateDialog is
 * a handy implementation that displays a Dialog with a bulleted list and a button to do the upgrade.
 *
 * The JSON format looks like this:
 * <pre>
 {
 "package": {
 "downloadUrl": "http://locast.mit.edu/connects/lcc.apk"
 },

 "1.4.3": {
 "versionCode": 6,
 "changelog": ["New automatic update checker", "Improved template interactions"]
 },
 "1.4.2": {
 "versionCode": 5,
 "changelog": ["fixed crash when saving cast"]
 }
 }
 * </pre>
 *
 * @author <a href="pedromaironi@gmail.com">Pedro Toribio</a>
 *
 */
public class AutoUpdateApk {
    private static String packageName;
    private final static String TAG = AutoUpdateApk.class.getSimpleName();

    public static String SHARED_PREFERENCES_NAME = "";
    public static final String
            PREF_ENABLED = "enabled",
            PREF_MIN_INTERVAL = "min_interval",
            PREF_LAST_UPDATED = "last_checked";

    private final String mVersionListUrl;
    private int currentAppVersion;

    private JSONObject pkgInfo;
    private final Context mContext;

    private final OnAppUpdateListener mUpdateListener;
    private SharedPreferences mPrefs;
    private static final int MILLISECONDS_IN_MINUTE = 60000;

    /**
     * @param context
     * @param versionListUrl URL pointing to a JSON file with the update list.
     * @param updateListener
     */

    public AutoUpdateApk(Context context, String versionListUrl, OnAppUpdateListener updateListener) {
        mContext = context;
        mVersionListUrl = versionListUrl;
        mUpdateListener = updateListener;
        packageName = mContext.getPackageName();
        SHARED_PREFERENCES_NAME = packageName + "_" + TAG;

        try {
            currentAppVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            Log.e(TAG, String.valueOf(currentAppVersion));
        } catch (final PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Cannot get version for self! Who am I?! What's going on!? I'm so confused :-(");
            return;
        }

        mPrefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        // defaults are kept in the preference file for ease of tweaking
        // TODO put this on a thread somehow
        PreferenceManager.setDefaultValues(mContext, SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE, R.xml.preferences, false);
    }

    public int getMinInterval(){
        return Integer.parseInt(mPrefs.getString(PREF_MIN_INTERVAL, "60"));
    }

    public void setMinInterval(int minutes){
        mPrefs.edit().putString(PREF_MIN_INTERVAL, String.valueOf(minutes)).apply();
    }

    public boolean getEnabled(){
        return mPrefs.getBoolean(PREF_ENABLED, true);
    }

    public void setEnabled(boolean enabled){
        mPrefs.edit().putBoolean(PREF_ENABLED, enabled).apply();
    }

    /**
     * You normally shouldn't need to call this, as {@link #checkForUpdates()} checks it before doing any updates.
     *
     * @return true if the updater should check for updates
     */
    public boolean isStale(){
        return System.currentTimeMillis() - mPrefs.getLong(PREF_LAST_UPDATED, 0) > getMinInterval() * MILLISECONDS_IN_MINUTE;
    }

    /**
     * Checks for updates if updates haven't been checked for recently and if checking is enabled.
     */
    public void checkForUpdates(){
        if (mPrefs.getBoolean(PREF_ENABLED, true) && isStale()){
//            forceCheckForUpdates();
        }
    }
//  "0.2": {
//    "versionCode": 2,
//    "changelog": ["New automatic update checker", "Improved template interactions"]
//  },
    /**
     * Checks for updates regardless of when the last check happened or if checking for updates is enabled.
     */
//    public void forceCheckForUpdates(){
//        Log.d(TAG, "checking for updates...");
//        if (versionTask == null){
//            versionTask = new GetVersionJsonTask();
//            versionTask.execute(mVersionListUrl);
//        }else{
//            Log.w(TAG, "checkForUpdates() called while already checking for updates. Ignoring...");
//        }
//    }
//    // why oh why is the JSON API so poorly integrated into java?
//    @SuppressWarnings("unchecked")
//    private void triggerFromJson(JSONObject jo) throws JSONException {
//
//        final ArrayList<String> changelog = new ArrayList<String>();
//
//        // keep a sorted map of versionCode to the version information objects.
//        // Most recent is at the top.
//        final TreeMap<Integer, JSONObject> versionMap =
//                new TreeMap<Integer, JSONObject>(new Comparator<Integer>() {
//                    public int compare(Integer object1, Integer object2) {
//                        return object2.compareTo(object1);
//                    };
//                });
//
//        for (final Iterator<String> i = jo.keys(); i.hasNext(); ){
//            final String versionName = i.next();
//            if (versionName.equals("package")){
//                pkgInfo = jo.getJSONObject(versionName);
//                continue;
//            }
//            final JSONObject versionInfo = jo.getJSONObject(versionName);
//            versionInfo.put("versionName", versionName);
//
//            final int versionCode = versionInfo.getInt("versionCode");
//            versionMap.put(versionCode, versionInfo);
//        }
//        final int latestVersionNumber = versionMap.firstKey();
//        final String latestVersionName = versionMap.get(latestVersionNumber).getString("versionName");
//        final Uri downloadUri = Uri.parse(pkgInfo.getString("downloadUrl"));
//
//        if (currentAppVersion > latestVersionNumber){
//            Log.d(TAG, "We're newer than the latest published version ("+latestVersionName+"). Living in the future...");
//            mUpdateListener.appUpdateStatus(true, latestVersionName, null, downloadUri);
//            return;
//        }
//
//        if (currentAppVersion == latestVersionNumber){
//            Log.d(TAG, "We're at the latest version ("+currentAppVersion+")");
//            mUpdateListener.appUpdateStatus(true, latestVersionName, null, downloadUri);
//            return;
//        }
//
//        // construct the changelog. Newest entries are at the top.
//        for (final Entry<Integer, JSONObject> version: versionMap.headMap(currentAppVersion).entrySet()){
//            final JSONObject versionInfo = version.getValue();
//            final JSONArray versionChangelog = versionInfo.optJSONArray("changelog");
//            if (versionChangelog != null){
//                final int len = versionChangelog.length();
//                for (int i = 0; i < len; i++){
//                    changelog.add(versionChangelog.getString(i));
//                }
//            }
//        }
//
////        mUpdateListener.appUpdateStatus(false, latestVersionName, changelog, downloadUri);
//    }

//    private class VersionCheckException extends Exception {
//        /**
//         *
//         */
//        private static final long serialVersionUID = 397593559982487816L;
//
//        public VersionCheckException(String msg) {
//            super(msg);
//        }
//    }
//
//    /**
//     * Send off an intent to start the download of the app.
//     */
//    public void startUpgrade(){
//        try {
//            final Uri downloadUri = Uri.parse(pkgInfo.getString("downloadUrl"));
//            mContext.startActivity(new Intent(Intent.ACTION_VIEW, downloadUri));
//        } catch (final JSONException e) {
//            e.printStackTrace();
//        }
//    }
/*

    private GetVersionJsonTask versionTask;
    private class GetVersionJsonTask extends AsyncTask<String, Integer, JSONObject> {
        private String errorMsg = null;

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d(TAG, "update check progress: " + values[0]);
            super.onProgressUpdate(values);
        }
        @Override
        protected JSONObject doInBackground(String... params) {
            publishProgress(0);
            final DefaultHttpClient hc = new DefaultHttpClient();
            final String url = params[0];
            final HttpGet req = new HttpGet(url);
            JSONObject jo = null;
            try {
                publishProgress(50);
                final HttpResponse res = hc.execute(req);

                final StatusLine status = res.getStatusLine();
                final int statusCode = status.getStatusCode();
                if (statusCode == HttpStatus.SC_NOT_FOUND) {
                    throw new VersionCheckException(url + " " + status.getReasonPhrase());
                }
                if (statusCode != HttpStatus.SC_OK){
                    final HttpEntity e = res.getEntity();
                    if (e.getContentType().getValue().equals("text/html") || e.getContentLength() > 40){
                        // long response body. Serving HTML...
                        throw new VersionCheckException("Got a HTML instead of expected JSON.");
                    }
                    throw new VersionCheckException("HTTP " + res.getStatusLine().getStatusCode() + " "+ res.getStatusLine().getReasonPhrase());
                }

                final HttpEntity ent = res.getEntity();

                jo = new JSONObject(StreamUtils.inputStreamToString(ent.getContent()));
                ent.consumeContent();
                mPrefs.edit().putLong(PREF_LAST_UPDATED, System.currentTimeMillis()).apply();

            } catch (final Exception e) {
                //e.printStackTrace();

                errorMsg = e.getClass().getSimpleName() + ": " + e.getLocalizedMessage();
            }finally {
                publishProgress(100);
            }
            return jo;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (result == null){
                Log.e(TAG, errorMsg);
            }else{
                try {
                    triggerFromJson(result);

                } catch (final JSONException e) {
                    Log.e(TAG, "Error in JSON version file.", e);
                }
            }
            versionTask = null; // forget about us, we're done.
        };
    };




*/
















































    /*
    public static final String AUTOUPDATE_CHECKING = "autoupdate_checking";
    public static final String AUTOUPDATE_NO_UPDATE = "autoupdate_no_update";
    public static final String AUTOUPDATE_GOT_UPDATE = "autoupdate_got_update";
    public static final String AUTOUPDATE_HAVE_UPDATE = "autoupdate_have_update";

    public static final String PUBLIC_API_URL = "http://updateapk.pedromaironi.com/checkversion.json";

    public void clearSchedule() {
//        schedule.clear();
    }

    public void addSchedule(int start, int end) {
//        schedule.add(new ScheduleEntry(start, end));
    }

    //
    // ---------- everything below this line is private and does not belong to
    // the public API ----------
    //
    protected final static String TAG = "AutoUpdateApk";

    private final static String ANDROID_PACKAGE = "application/vnd.android.package-archive";

//    protected final String server;
    protected final String apiPath;

    protected static Context context = null;
    protected static SharedPreferences preferences;
    private final static String LAST_UPDATE_KEY = "last_update";
    private static long last_update = 0;

    private static int appIcon = android.R.drawable.ic_popup_reminder;
    private static int versionCode = 0; // as low as it gets
    private static String appName;
    private static int device_id;

    public static final long MINUTES = 60 * 1000;
    public static final long HOURS = 60 * MINUTES;
    public static final long DAYS = 24 * HOURS;

    // 3-4 hours in dev.mode, 1-2 days for stable releases
    private long updateInterval = 3 * HOURS; // how often to check

    private static boolean mobile_updates = false; // download updates over wifi
    // only

    private final static Handler updateHandler = new Handler();
    protected final static String UPDATE_FILE = "update_file";
    protected final static String SILENT_FAILED = "silent_failed";
    private final static String MD5_TIME = "md5_time";
    private final static String MD5_KEY = "md5";

    private static int NOTIFICATION_ID = 0xDEADBEEF;
    private static int NOTIFICATION_FLAGS = Notification.FLAG_AUTO_CANCEL
            | Notification.FLAG_NO_CLEAR;
    private static long WAKEUP_INTERVAL = 500;


    public AutoUpdateApk(Context ctx, String URL) {
        setupVariables(ctx);
        this.apiPath = URL;
    }

    @SuppressLint("HardwareIds")
    private void setupVariables(Context ctx) {
        context = ctx;

        preferences = context.getSharedPreferences(packageName + "_" + TAG,
                Context.MODE_PRIVATE);

        Log.e(TAG, packageName);
        Log.e(TAG, String.valueOf(preferences));

        device_id = crc32(Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID));
        last_update = preferences.getLong("last_update", 0);
        Log.e(TAG, String.valueOf(device_id));
        Log.e(TAG, String.valueOf(last_update));

        NOTIFICATION_ID += crc32(packageName);
        Log.e(TAG, String.valueOf(NOTIFICATION_ID));

        // schedule.add(new ScheduleEntry(0,24));

        ApplicationInfo appinfo = context.getApplicationInfo();
        if (appinfo.icon != 0) {
            appIcon = appinfo.icon;
        } else {
            Log.d(TAG, "unable to find application icon");
        }
        if (appinfo.labelRes != 0) {
            appName = context.getString(appinfo.labelRes);
        } else {
            Log.d(TAG, "unable to find application label");
        }

        if (new File(appinfo.sourceDir).lastModified() > preferences.getLong(
                MD5_TIME, 0)) {
            preferences.edit().putString(MD5_KEY, MD5Hex(appinfo.sourceDir))
                    .apply();
            preferences.edit().putLong(MD5_TIME, System.currentTimeMillis())
                    .apply();

            String update_file = preferences.getString(UPDATE_FILE, "");
            if (update_file != null){
                if (update_file.length() > 0) {
                    if (new File(context.getFilesDir().getAbsolutePath() + "/"
                            + update_file).delete()) {
                        preferences.edit().remove(UPDATE_FILE)
                                .remove(SILENT_FAILED).apply();
                    }
                }
            }
            Log.e(TAG, String.valueOf( update_file));
        }

        Log.e(TAG, String.valueOf(context.getFilesDir().getAbsolutePath()));
        Log.e(TAG, String.valueOf(appinfo.sourceDir));
        raise_notification();

        if (haveInternetPermissions()) {
            context.registerReceiver(connectivity_receiver, new IntentFilter(
                    ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    private String MD5Hex(String filename) {
        final int BUFFER_SIZE = 8192;
        byte[] buf = new byte[BUFFER_SIZE];
        int length;
        try {
            FileInputStream fis = new FileInputStream(filename);
            BufferedInputStream bis = new BufferedInputStream(fis);
            MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            while ((length = bis.read(buf)) != -1) {
                md.update(buf, 0, length);
            }
            bis.close();

            byte[] array = md.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : array) {
                sb.append(Integer.toHexString((b & 0xFF) | 0x100)
                        .substring(1, 3));
            }
            Log.d(TAG, "md5sum: " + sb.toString());
            return sb.toString();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        return "md5bad";
    }

    private boolean haveInternetPermissions() {
        return true;
    }

    private void raise_notification() {

    }

    private static int crc32(String str) {
        byte bytes[] = str.getBytes();
        Checksum checksum = new CRC32();
        checksum.update(bytes, 0, bytes.length);
        return (int) checksum.getValue();
    }

    private Runnable periodicUpdate = new Runnable() {
        @Override
        public void run() {
            checkUpdates(false);
            updateHandler.removeCallbacks(periodicUpdate); // remove whatever
            // others may have
            // posted
            updateHandler.postDelayed(this, WAKEUP_INTERVAL);
        }
    };
    private BroadcastReceiver connectivity_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo currentNetworkInfo = (NetworkInfo) intent
                    .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

            // do application-specific task(s) based on the current network
            // state, such
            // as enabling queuing of HTTP requests when currentNetworkInfo is
            // connected etc.
            boolean not_mobile = !currentNetworkInfo.getTypeName()
                    .equalsIgnoreCase("MOBILE");
            if (currentNetworkInfo.isConnected()
                    && (mobile_updates || not_mobile)) {
                checkUpdates(false);
                updateHandler.postDelayed(periodicUpdate, updateInterval);
            } else {
                updateHandler.removeCallbacks(periodicUpdate); // no network
                // anyway
            }
        }
    };

    private void checkUpdates(boolean b) {


    }
*/
}
