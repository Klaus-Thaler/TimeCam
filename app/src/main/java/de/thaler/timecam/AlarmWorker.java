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

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class AlarmWorker extends Worker {
    String TAG = "myLog AlarmWorker";
    public AlarmWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }
    @NonNull
    @Override
    public Result doWork() {
        // Code, der nach ungefähr 9 Stunden ausgeführt wird
        Log.v(TAG, "Worker doWork");
        Logger.add("Worker doWork");

        if (CameraMain.cameraProviderFuture.isCancelled()) CameraMain.startCamera();
        //NotificationManagerCompat.from(getApplicationContext()).cancelAll();

        ContextCompat.startForegroundService(getApplicationContext(),
            new Intent(getApplicationContext(), CameraService.class));

        CustomToast.showToast(getApplicationContext(), "start loop", 1);
        return Result.success();
    }
    @Override
    public void onStopped() {
        super.onStopped();
        Log.v(TAG, "worker is stopped");
    }
}
