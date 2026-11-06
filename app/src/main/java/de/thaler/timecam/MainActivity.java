package de.thaler.timecam;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
import static de.thaler.timecam.CameraMain.serviceOwner;
import static de.thaler.timecam.CameraService.camLoopThread;
import static de.thaler.timecam.CameraService.running;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * ffmpeg -r 12 -pattern_type glob -i '*.jpg' -c:v libx264 ~/Downloads/test.mp4
 * ffmpeg -r 12 -pattern_type glob -i '*.jpg' -vf scale=w=600:h=-1,transpose=2 -c:v libx264 ~/...
 * -vf scale=w=600:h=-1,transpose=2 // kleiner + drehen
 * -vf 'transpose=1' // drehen 
 */
public class MainActivity extends AppCompatActivity {
    static String TAG = "myLog MainActivity";
    @SuppressLint("StaticFieldLeak")
    public static MainActivity mainActivity;
    public static PreviewView previewView;
    SharedPreferences mPreferenz;
    SharedPreferences.Editor mPreferenzEditor;
    public long startInTime;
    public TextView textViewLoopTime;
    public TextView textViewStartTime;
    String WORKER = "CameraWorkerTag";
    public static final String CHANNEL_ID = "CameraChannel";
    private static final int REQUEST_PERMISSIONS = 100;
    String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    BatteryManager akkuManager;
    boolean needsRequestPermission = false;

    @SuppressLint({"SetTextI18n", "SdCardPath", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mainActivity = this;
        previewView = mainActivity.findViewById(R.id.previewView);

        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissions(new String[]{ Manifest.permission.POST_NOTIFICATIONS },
                    REQUEST_PERMISSIONS);
        }

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                needsRequestPermission = true;
                break; }
        }
        if (needsRequestPermission) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
        } else {
            CameraMain.startCamera();
            Logger.create();
        }

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (!pm.isIgnoringBatteryOptimizations(getPackageName())) {
            @SuppressLint("BatteryLife")
            Intent intent = new Intent(
                    Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                    Uri.parse("package:" + getPackageName())
            );
            startActivity(intent);
        }

        akkuManager = (BatteryManager) getSystemService(BATTERY_SERVICE);

        Logger.create();
        Logger.add("\n --- start ---");
        Log.i(TAG, "SDK " + Build.VERSION.SDK_INT + " Android " + Build.VERSION.RELEASE);

        // start config
        if (getSupportActionBar() != null) { getSupportActionBar().hide(); } // no actionbarr
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); // kein wischen
        setRequestedOrientation(SCREEN_ORIENTATION_UNSPECIFIED); // nicht drehen

        // shared Preference

        mPreferenz = this.getSharedPreferences("MyPref", 0);
        mPreferenzEditor = mPreferenz.edit();

        //umgebung prüfen
        checkWifi();
        checkNfc();
        checkBluetooth();
        // permissions

        createNotificationChannel();

        // default values
        Log.v(TAG, "alle cache daten: " + mPreferenz.getAll());
        Logger.add("start data in cache: " + mPreferenz.getAll());

        startInTime = mPreferenz.getLong("startInTime", 0L); // def 1h
        long loopTime = mPreferenz.getLong("loopTime", 60 * 1000L); // alle minute ein bild
        int jpgQuality = mPreferenz.getInt("JpegQuality",90); // default compression

        // anzeige im display
        // android version
        TextView textViewVersion = findViewById(R.id.textAndroidVersion);
        textViewVersion.setText("Android " + Build.VERSION.RELEASE);
        // time now
        TextView textViewDateTime = findViewById(R.id.textViewDateTime);
        Handler timeHandler = new Handler();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        int delayDate= 1000; //milliseconds
        Runnable runnableDate = new Runnable(){
            public void run(){
                textViewDateTime.setText(simpleDateFormat.format(Calendar.getInstance().getTime()));
                timeHandler.postDelayed(this, delayDate);
            }
        };
        timeHandler.post(runnableDate);
        // akku stand im Display anzeigen
        TextView textViewAKKU = findViewById(R.id.textViewAKKU);
        Handler akkuHandler = new Handler();
        int delayAkku = 10 * 60 * 1000; //milliseconds
        final Runnable runnableAkku = new Runnable(){
            public void run(){
                textViewAKKU.setText(String.format("Akku: %s%% - %s °C",
                        akkuManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY),
                        akkuTemp()));
                Logger.add(String.format("Akku: %s%% - Temp: %s °C",
                        akkuManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY),
                        akkuTemp()));
                akkuHandler.postDelayed(this, delayAkku);
            }
        };
        akkuHandler.post(runnableAkku);

        // start zeit anzeigen
        textViewStartTime = findViewById(R.id.start_time);
        textViewStartTime.setText(String.format("start um %s in %s",
                startTimeFormat(System.currentTimeMillis() +
                mPreferenz.getLong("startInTime",0L)),
                readableMilliSeconds(mPreferenz.getLong("startInTime",0L))));

        // loop zeit anzeigen
        textViewLoopTime = findViewById(R.id.loop);
        textViewLoopTime.setText(String.format("Loop %s sec - JPEG %s%%",
                loopTime /1000, jpgQuality));

        // buttons
        // exit button
        Button buttonExit = findViewById(R.id.buttonExit);
        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.add("end app");
                stopService(new Intent(getApplicationContext(), CameraService.class));
                WorkManager.getInstance(getApplicationContext()).cancelAllWorkByTag(WORKER);
                NotificationManagerCompat.from(getApplicationContext()).cancelAll();
                System.exit(0);
            }
        });

        // start button
        Button buttonStart = findViewById(R.id.buttonStartStop);
        buttonStart.setText("Start");
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(buttonStart.getText() == "Start") {
                    buttonStart.setText("Stop");
                    //cancel all notifications
                    NotificationManagerCompat.from(mainActivity).cancelAll();
                    long startIn = mPreferenz.getLong("startInTime", 0L);
                    Log.i(TAG, "start at: " + startTimeFormat(System.currentTimeMillis() +
                            mPreferenz.getLong("startInTime",0L)));
                    // start Worker
                    OneTimeWorkRequest request =
                            new OneTimeWorkRequest.Builder(AlarmWorker.class)
                                    .setInitialDelay(startIn/1000, TimeUnit.SECONDS)
                                    .addTag(WORKER)
                                    .build();
                    // start observer
                    WorkManager.getInstance(getApplicationContext())
                                .getWorkInfoByIdLiveData(request.getId())
                                .observe(serviceOwner, new Observer<WorkInfo>() {
                                @Override
                                public void onChanged(WorkInfo workInfo) {
                                    if (workInfo != null) {
                                        Log.d(TAG, "worker state: " + workInfo.getState().name());
                                        Logger.add("worker state: " + workInfo.getState().name());
                                    }
                                }
                            });
                    // job in queue
                    WorkManager.getInstance(getApplicationContext())
                            .enqueueUniqueWork(WORKER, ExistingWorkPolicy.KEEP, request);
                    // notification + bubble
                    showNotification(1001, "Timer", "start in "
                                    + startTimeFormat(System.currentTimeMillis() +
                                    mPreferenz.getLong("startInTime",0L)),
                            android.R.drawable.ic_lock_idle_alarm, null);
                    // erste zeile im log
                    Logger.add(String.format("\tStart alarm.\n" +
                                    "\tAkku: %s %%.\n" +
                                    "\tLoop begin at %s.\n" +
                                    "\tLoopTime is %s Seconds.\n" +
                                    "\tJPEG Quality is %s%%.",
                            akkuManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY),
                            startTimeFormat(System.currentTimeMillis() +
                                    mPreferenz.getLong("startInTime",0L)),
                            mPreferenz.getLong("loopTime",0L)/1000,
                            mPreferenz.getInt("JpegQuality", 90)));
                    CustomToast.showToast(getApplicationContext(), "start alarm", 1);
                } else {
                    buttonStart.setText("Start");
                    // end loop
                    WorkManager.getInstance(getApplicationContext()).cancelAllWorkByTag(WORKER);
                    if (running) camLoopThread.quitSafely();
                    // notification stop timer
                    NotificationManagerCompat.from(mainActivity).cancelAll();
                    CustomToast.showToast(mainActivity, "stop alarm", 1);
                    Log.v(TAG, "stop alarm");
                    Logger.add("stop alarm");
                }
            }
        });
        // Buttons Set Dialog
        Dialog dialogConfig = new DialogConfig(this);
        Button buttonSetStart = findViewById(R.id.buttonTimeStart);
        buttonSetStart.setText("Config");
        buttonSetStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfig.show();
            }
        });

        // buttons
        // button Export
        Button buttonExport = findViewById(R.id.buttonExport);
        buttonExport.setText("E");
        buttonExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File cacheRoot = new File(getCacheDir(), "loop");
                if (!cacheRoot.exists()) return;
                File[] files = cacheRoot.listFiles();
                assert files != null;
                Log.i(TAG, "start len " + files.length);
                Logger.add("start export files: " + files.length);
                startExportWorker();
            }
        });
    }
    /**
     * @return return a float as temp of akku
     */
    private float akkuTemp () {
        Intent batteryIntent = registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (batteryIntent != null) {
            int rawTemperature =
                    batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            if (rawTemperature != -1) {
                return rawTemperature / 10.0f;
            }
        }
        return 0;
    }
    public String startTimeFormat(long time) {
        return DateFormat.format("HH:mm", time).toString();
    }

    /**
     * @param requestCode  The request code passed in {@link #requestPermissions}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions which is either
     *                     {@link android.content.pm.PackageManager#PERMISSION_GRANTED} or
     *                     {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                CameraMain.startCamera();
                Logger.create();
            } else {
                CustomToast.showToast(this,
                        "Mindestens eine Berechtigung wurde abgelehnt", 2);
            }
        }
    }
    /**
     * Notification, Bubble
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "CameraService",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setName("Name");
            channel.setDescription("Beschreibung");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
    public void showNotification(int id, String title, String description, int icon, long[] vibrate) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(description)
                .setSmallIcon(icon)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setAutoCancel(true)
                .setVibrate(vibrate);
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
                || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(id, builder.build());
        }
    }
    private void checkBluetooth () {
        BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null) {
            // CustomToast.showToast(this, "No Bluetooth.", 2);
            return;
        }
        BluetoothAdapter adapter = bluetoothManager.getAdapter();
        if (adapter != null && adapter.isEnabled()) {
            CustomToast.showToast(this, "Bluetooth is on!", 2);
        }
    }
    private void checkWifi() {
        WifiManager wifiManager = (WifiManager) getApplicationContext()
                        .getSystemService(Context.WIFI_SERVICE);
        boolean wlanAktiv = wifiManager != null && wifiManager.isWifiEnabled();
        if (wlanAktiv) {
            CustomToast.showToast(this, "WiFi is on!", 2);
        }
    }
    private void checkNfc() {
        NfcAdapter nfcAdapter =
                NfcAdapter.getDefaultAdapter(getApplicationContext());
        if (nfcAdapter == null) {
            //CustomToast.showToast(this, "No NFC.", 2);
            return;
        }
        if (nfcAdapter.isEnabled()) {
            CustomToast.showToast(this, "NFC is on!", 2);
        }
    }
    /**
     * start a background worker
     * copy files from cache into media
     */
    private void startExportWorker() {
        OneTimeWorkRequest request =
                new OneTimeWorkRequest.Builder(Export2Media.class)
                        // Optional: Tag, damit du duplikate vermeiden kannst
                        .addTag("export_camshots")
                        .build();

        WorkManager workManager = WorkManager.getInstance(this);
        // Optional: Duplikate verhindern (nur einen laufenden/exportierenden Worker behalten)
        workManager.enqueueUniqueWork(
                "export_camshots_unique",
                ExistingWorkPolicy.REPLACE,
                request
        );
    }
    private String readableMilliSeconds (long milliseconds) {
        if (milliseconds%3600000 == 0) return String.format("%s",
                milliseconds/3600000);
        if (milliseconds%3600000 > 0) return String.format("%s:%s",
                milliseconds/3600000,
                (milliseconds%3600000)/60000);
        return String.format("%s", milliseconds/1000);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(new Intent(getApplicationContext(), CameraService.class));
        NotificationManagerCompat.from(getApplicationContext()).cancelAll();
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
}
