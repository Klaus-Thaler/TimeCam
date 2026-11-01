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

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.work.WorkerParameters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Export2Media extends androidx.work.Worker {
    static String TAG = "myLog Export2Media";
    public Export2Media(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    /**
     * worker -> do work
     * @return Result.success()
     */
    @SuppressLint("SdCardPath")
    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();

        File cacheRoot = new File(context.getCacheDir(), "loop");
        if (!cacheRoot.exists()) return Result.success();

        File[] files = cacheRoot.listFiles();
        assert files != null;
        Log.i(TAG, "export " + files.length + " files from cache");
        Logger.add("export " + files.length + " files from cache");

        for (File f : files) {
            if (!f.isFile()) continue;
            copyFile(f.getAbsolutePath(),"/sdcard/" + f.getName());
            Log.i(TAG, "export " + f.getName());
            Logger.add("export " + f.getName());
        }
        Log.i(TAG, "export end");
        Logger.add("export end");
        return Result.success();
    }
    /**
     * <p>copy a file</p>
     * <p>if it doesn't work, then show a toast</p>
     */
    public static void copyFile (String src, String dst) {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "copy fail" + e);
            Logger.add("copy fail");
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private boolean insertFileToMediaStore(Context context, File file) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, file.getName());
        values.put(MediaStore.Images.Media.SIZE, file.length());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/loop");
        values.put(MediaStore.Images.Media.IS_PENDING, 1);

        ContentResolver resolver = context.getContentResolver();
        Uri collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);

        Uri itemUri = resolver.insert(collection, values);
        if (itemUri == null) {
            Log.e(TAG, "insert collection=" + collection);
            Log.e(TAG, "values=" + values);
            Log.e(TAG, "itemUri=" + itemUri);
            return false;
        }

        try (InputStream in = new FileInputStream(file);
             OutputStream out = resolver.openOutputStream(itemUri)) {

            if (out == null) return false;

            byte[] buffer = new byte[1024 * 1024];
            int read;
            while ((read = in.read(buffer)) != -1) out.write(buffer, 0, read);
            out.flush();

        } catch (Exception e) {
            Log.e(TAG, "error: " + e);
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues done = new ContentValues();
            done.put(MediaStore.Images.Media.IS_PENDING, 0);
            resolver.update(itemUri, done, null, null);
        }
        return true;
    }
}
