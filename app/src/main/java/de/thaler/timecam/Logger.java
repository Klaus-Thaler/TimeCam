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

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * create Log file
 * Errors from the `try` block are not acknowledged.
 * In that case, there is no log file.
 */
public class Logger extends MainActivity {
    static String TAG = "myLog Logger";
    private static final ContentResolver resolver
            = mainActivity.getApplicationContext().getContentResolver();
    static File logFile;
    protected static void create() {
        String formatFileName = DateFormat.format("yyyy-MM-dd",
                System.currentTimeMillis()).toString();
        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "log");
        logFile = new File(dir + File.separator + "log" + formatFileName + ".txt");
        if (!dir.isDirectory()) { boolean b = dir.mkdirs(); Log.v(TAG, "mkdir: " + b); }
        if (!logFile.exists()) {
            try {
                if (logFile.createNewFile()) {
                    throw new IOException("failed to create log file");
                }
            } catch (Exception ignored) { /*Log.v(TAG, "failed to create log file");*/
            } finally {
                add("\n +++ new Log File +++");
                add("SDK " + Build.VERSION.SDK_INT + " Android " + Build.VERSION.RELEASE);
            }
        }
    }
    public static synchronized void add(String message) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());
        byte[] bytes = (time + " " + message + System.lineSeparator())
                .getBytes(StandardCharsets.UTF_8);
        try (ParcelFileDescriptor parcelFileDescriptor = resolver.openFileDescriptor(
                Uri.fromFile(logFile), "wa")) {
            if (parcelFileDescriptor == null) {
                throw new IOException("unable to open log file");
            }
            try (FileOutputStream out = new FileOutputStream(parcelFileDescriptor.getFileDescriptor())) {
                out.write(bytes);
                out.flush();
            }
        } catch (Exception ignore) { /*Log.v(TAG, "unable to open log file");*/ }
    }
    public void del() {
        if (logFile.exists()) {
            if (!logFile.delete()) Log.v(TAG, "delete " + logFile + " fail");
        }
    }
}