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

import static android.view.Surface.ROTATION_0;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraMain extends MainActivity {
    static String TAG = "myLog Camera Main";
    static ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    static Camera camera;
    static ProcessCameraProvider cameraProvider;
    static CameraControl cameraControl;
    static ImageCapture imageCapture;
    public static ExecutorService cameraExecutor;
    static LifecycleOwner serviceOwner;
    static String subDir = "timeCam";
    public static Float maxZoom;
    public static volatile boolean photoIsSave = false;
    public static int z = 0;
    public static void startCamera() {
        Log.i(TAG, "startCameraX");

        if (CameraMain.cameraExecutor == null)
            CameraMain.cameraExecutor = Executors.newSingleThreadExecutor();
        // own lifecycle
        if (CameraMain.serviceOwner == null)
            CameraMain.serviceOwner = new CameraMain.ServiceLifecycleOwner();

        cameraProviderFuture =
                ProcessCameraProvider.getInstance(mainActivity.getApplicationContext());
        cameraProviderFuture.addListener(() -> {
            try {
                if (cameraProvider != null) cameraProvider.unbindAll();
                cameraProvider = ProcessCameraProvider.getInstance(mainActivity).get();
                // preview
                Preview preview = new Preview.Builder().build();

                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setTargetRotation(ROTATION_0)
                        .setOutputFormat(ImageCapture.OUTPUT_FORMAT_JPEG)
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setJpegQuality(mainActivity.mPreferenz.getInt("JpegQuality", 90)) // TODO: 27.10.26 testen, ob der kompressionsgrad akku spart
                        .build();
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();
                camera = cameraProvider.bindToLifecycle(serviceOwner,
                        cameraSelector, preview, imageCapture);
                cameraControl = camera.getCameraControl();
                CameraInfo cameraInfo = camera.getCameraInfo();
                if (cameraInfo.getZoomState().getValue() != null) {
                    maxZoom = cameraInfo.getZoomState().getValue().getMaxZoomRatio();
                } else { maxZoom = null; } Log.v(TAG, "max Zoom: " + maxZoom);
                // seekbar
                setSeekBarZoom();
            } catch (ExecutionException | InterruptedException e) {
                CustomToast.showToast(mainActivity, "Fehler beim Starten der Kamera", 2);
            }
        }, ContextCompat.getMainExecutor(mainActivity.getApplicationContext()));

        //takeOnePhoto("00001test.jpg"); // test und initialisieren todo ???
    }
    private static void setSeekBarZoom() {
        int scall = 10; // scallierung erhoehen
        CameraControl cameraControl = camera.getCameraControl();
        cameraControl.setZoomRatio((float) mainActivity.mPreferenz.getInt("lastZoom", 0) / scall);

        SeekBar seekBar = mainActivity.findViewById(R.id.seekBarZoom);
        seekBar.setMin(0);
        seekBar.setMax((int) (maxZoom * 10));
        seekBar.setProgress(mainActivity.mPreferenz.getInt("lastZoom", 0));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                cameraControl.setZoomRatio((float) progress / scall);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mainActivity.mPreferenzEditor.putInt("lastZoom", seekBar.getProgress()).apply();
            }
        });
    }
    public static void takeOnePhoto(String fileName) {
        // check camera
        if (imageCapture == null) { Log.e(TAG, "imageCapture == null"); return; }
        if (cameraExecutor == null) { Log.e(TAG, "cameraExecutor == null"); return; }
        if (cameraControl == null) { Log.e(TAG, "cameraControl == null"); return; }
        if (camera == null) { Log.e(TAG, "camera == null"); return; }

        if (Build.VERSION.SDK_INT >= 29) {
            takePictureToMediasStore(fileName);
            Log.v(TAG, "take in Media");
            //mainActivity.fileLog.add("take in Media");
        } else {
            takePictureToFile(fileName);
            Log.v(TAG, "take in File");
            //mainActivity.fileLog.add("take in File");
        }
        //takePictureInCache(); Log.v(TAG, "take in Cache");
    }
    private static void takePictureToMediasStore(String fileName) {
        // media store
        //String fileName = System.currentTimeMillis() + ".jpg";
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName + ".jpg");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        //contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/bmp");
        contentValues.put(MediaStore.Images.Media.ARTIST, "Kanalmatrose");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Lucky Pics");
        contentValues.put(MediaStore.Images.Media.GENRE, "Fun");
        // MediaStore.VOLUME_EXTERNAL_PRIMARY  ??
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH,
                "Pictures/" + subDir);
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions
                .Builder(mainActivity.getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                .build();
        imageCapture.takePicture(
                outputOptions,
                cameraExecutor,
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        photoIsSave = true; z++;
                        Logger.add(String.format("%s %s save in MediaStore", z, fileName));
                        Log.v(TAG, String.format("%s %s save in MediaStore", z, fileName));
                        mainActivity.runOnUiThread(() ->
                                CustomToast.showToast(mainActivity,
                                        "save " + fileName, 1)
                        );
                    }
                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        //Log.v(TAG, "Error saving with MediaStore.");
                        Logger.add("Error saving with MediaStore.");
                        // dsnn direkt
                        takePictureToFile(fileName);
                    }
                }
        );
    }
    private static void takePictureToFile(String fileName) {
        // direct in file
        //String fileName = System.currentTimeMillis() + ".jpg";
        String imageDirPath = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + File.separator
                + subDir;
        File imageDir = new File(imageDirPath);
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(
                new File(imageDir + File.separator + fileName + ".jpg")).build();
        imageCapture.takePicture(
                outputOptions,
                cameraExecutor,
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        photoIsSave = true; z++;
                        Log.v(TAG, String.format("%s %s save direct in %s", z, fileName, imageDir.getAbsolutePath()));
                        Logger.add(String.format("%s %s save direct in %s", z, fileName, imageDir.getAbsolutePath()));
                        mainActivity.runOnUiThread(() ->
                                CustomToast.showToast(mainActivity, "save " + fileName, 1)
                        );
                    }
                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.v(TAG, "file no save directly.");
                        Logger.add("file no save directly.");
                        takePictureInCache(fileName);
                    }
                }
        );
    }
    @SuppressLint("RestrictedApi")
    public static void takePictureInCache(String fileName) {
        // in cache
        File outDir = new File(mainActivity.getCacheDir(), "loop");
        try {
            outDir.mkdirs();
        } catch (RuntimeException ignored) { Log.v(TAG, "catch cache mkdir"); }
        //File fileName = new File(outDir, System.currentTimeMillis() + ".jpg");
        ImageCapture.OutputFileOptions outputOptions =
                new ImageCapture.OutputFileOptions.Builder(new File(fileName + ".jpg")).build();
        imageCapture.takePicture(
                outputOptions,
                cameraExecutor,
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        photoIsSave = true; z++;
                        Log.v(TAG, String.format("%s %s cashed in %s", z, fileName, outDir.getName()));
                        Logger.add(String.format("%s %s cashed in %s", z, fileName, outDir.getName()));
                        mainActivity.runOnUiThread(() ->
                                CustomToast.showToast(mainActivity,fileName + " save in cache/loop", 1)
                        );
                    }
                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        photoIsSave = false;
                        Log.v(TAG, "file no save in cache: " + exception);
                        Logger.add("file no save in cache: " + exception);
                        CustomToast.showToast(mainActivity, "Check your Permissions!",2);
                    }
                }
        );
    }
    /**
     * Minimal LifecycleOwner für Service-Binding
     */
    static class ServiceLifecycleOwner implements LifecycleOwner {
        private final LifecycleRegistry lifecycleRegistry;
        public ServiceLifecycleOwner() {
            lifecycleRegistry = new LifecycleRegistry(this);
            lifecycleRegistry.setCurrentState(Lifecycle.State.CREATED);
            lifecycleRegistry.setCurrentState(Lifecycle.State.STARTED);
            lifecycleRegistry.setCurrentState(Lifecycle.State.RESUMED);
        }
        @NonNull
        @Override
        public Lifecycle getLifecycle() {
            return lifecycleRegistry;
        }
    }
}
