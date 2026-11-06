package de.thaler.timecam;

/*
 * Copyright 2026 Mathias Uebel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import static de.thaler.timecam.MainActivity.CHANNEL_ID;
import static de.thaler.timecam.MainActivity.mainActivity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.concurrent.Executors;

public class CameraService extends Service {
    static String TAG = "myLog CameraService";
    static HandlerThread camLoopThread;
    static Handler bgHandler;
    static Runnable loop;
    public static boolean running = false;
    public static boolean photoIsSave = true;
    private static final int NOTIFICATION_ID = 1002;

    @Override
    public void onCreate() {
        super.onCreate();
        // notification stop
        NotificationManagerCompat.from(mainActivity).cancel(1001);
        // Notification Channel einmalig anlegen
        createNotificationChannel();
        // doze, immer im vordergrund halten
        Notification notification =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("Kamera aktiv")
                        .setContentText("Die Kamera läuft im Hintergrund")
                        .setSmallIcon(R.drawable.ic_camera_foreground)
                        .setOngoing(true)
                        .setCategory(NotificationCompat.CATEGORY_SERVICE)
                        .setPriority(NotificationCompat.PRIORITY_LOW)
                        .build();
        startForeground(NOTIFICATION_ID, notification);

        // Separaten Thread für Kameraaufnahmen vorbereiten
        if (CameraMain.cameraExecutor == null) {
            CameraMain.cameraExecutor = Executors.newSingleThreadExecutor();
            Logger.add("CameraMain.cameraExecutor == null");
        }
        // own lifecycle
        if (CameraMain.serviceOwner == null) {
            CameraMain.serviceOwner = new CameraMain.ServiceLifecycleOwner();
            Logger.add("CameraMain.serviceOwner == null");
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // create handler
        camLoopThread = new HandlerThread("CamLoopTimer");
        camLoopThread.start();
        bgHandler = new Handler(camLoopThread.getLooper());

        running = true;
        long loopTime = mainActivity.mPreferenz.getLong("loopTime", 60 * 1000L);
        Log.v(TAG, "in loop - value: " + loopTime + " millisec");
        //Log.i(TAG, String.format("running: %s photoIsSave: %s", running, photoIsSave));

        Log.i(TAG, "service on start -> " + CameraMain.cameraProviderFuture.isCancelled());

        loop = new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                // einen moment warten
                if (CameraMain.cameraProviderFuture.isCancelled()) {
                    CameraMain.startCamera();
                    bgHandler.postDelayed(loop, 250); // kurz warten
                    return;
                }
                //Log.i(TAG, String.format("running: %s photoIsSave: %s", running, photoIsSave));
                //fileLog.add(String.format("running: %s photoIsSave: %s", running, photoIsSave));
                if (!running) return;
                if (!photoIsSave) return;
                CameraMain.takeOnePhoto(String.valueOf(System.currentTimeMillis()));
                bgHandler.postDelayed(loop, loopTime);
            }
        };
        bgHandler.removeCallbacks(loop);
        bgHandler.post(loop);
        return START_STICKY;
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "CameraServiceService",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onDestroy() {
        running = false;
        if (loop != null) camLoopThread.quitSafely();
        if (CameraMain.cameraProvider != null) {
            CameraMain.cameraProvider.unbindAll();
            CameraMain.cameraProvider = null;
        }
        stopForeground(STOP_FOREGROUND_REMOVE);
        //cameraExecutor.shutdownNow();
        super.onDestroy();
    }
}
