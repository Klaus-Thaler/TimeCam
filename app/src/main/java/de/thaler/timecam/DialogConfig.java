package de.thaler.timecam;

import static de.thaler.timecam.MainActivity.mainActivity;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Objects;

public class DialogConfig extends Dialog {
    String TAG = "myLog DialogConfig";
    public static Dialog dialog;
    protected DialogConfig(@NonNull Context context) {
        super(context);
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_config);
        // create dialog
        Objects.requireNonNull(dialog.getWindow())
                .setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
    }
    public void show () {
        dialog.show();

        // Take the instance of Spinner and apply OnItemSelectedListener on it
        Spinner spinner_value = dialog.findViewById(R.id.spinner_time_value);
        String[] value = new String[60];
        for (int i = 0; i < 60; i++) { value[i] = String.valueOf(i); }
        ArrayAdapter<String> adapterValue = new ArrayAdapter<>(dialog.getContext(), android.R.layout.simple_spinner_item, value);
        adapterValue.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_value.setAdapter(adapterValue);

        // Take the instance of Spinner and apply OnItemSelectedListener on it
        Spinner spinner_unit = dialog.findViewById(R.id.spinner_time_unit);
        String[] unit = new String[]{"seconds", "minutes"};
        ArrayAdapter<String> adapterUnit = new ArrayAdapter<>(dialog.getContext(), android.R.layout.simple_spinner_item, unit);
        adapterUnit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_unit.setAdapter(adapterUnit);

        // select
        long loopTime = mainActivity.mPreferenz.getLong("loopTime", 60 * 1000L);
        //Log.i(TAG, "loopValue: " + loopValue);
        if (loopTime >= 60000) {  // es sind minuten
            spinner_value.setSelection((int) (loopTime / 60000));
            spinner_unit.setSelection(1);
        } else {
            spinner_value.setSelection((int) (loopTime /1000));
            spinner_unit.setSelection(0);
        }

        // Take the instance of Spinner and apply OnItemSelectedListener on it
        Spinner spinner_hour = dialog.findViewById(R.id.spinner_start_hour);
        ArrayList<String> hour = new ArrayList<String>();
        for (int i = 0; i < 12; i++) { hour.add(String.valueOf(i)); }
        ArrayAdapter<String> adapterHour = new ArrayAdapter<>(dialog.getContext(),
                android.R.layout.simple_spinner_item, hour);
        adapterHour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_hour.setAdapter(adapterHour);

        // Take the instance of Spinner and apply OnItemSelectedListener on it
        Spinner spinner_min = dialog.findViewById(R.id.spinner_start_min);
        ArrayList<String> min = new ArrayList<String>();
        min.add("00"); min.add("15"); min.add("30"); min.add("45");
        ArrayAdapter<String> adapterMin = new ArrayAdapter<>(dialog.getContext(),
                android.R.layout.simple_spinner_item, min);
        adapterMin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_min.setAdapter(adapterMin);

        // gespeicherte werte in spinner setzen
        long timeValue = mainActivity.mPreferenz.getLong("startInTime",5*1000L);
        spinner_hour.setSelection((int) (timeValue/(60*60*1000)));
        spinner_min.setSelection((int) ((timeValue%(60*60*1000))/(15*60*1000)));

        //  Take the instance of Spinner and apply OnItemSelectedListener on it
        Spinner spinner_quality = dialog.findViewById(R.id.spinner_quality);
        ArrayList<String> quality = new ArrayList<String>();
        quality.add("20"); quality.add("30"); quality.add("40"); quality.add("50");
        quality.add("60"); quality.add("70"); quality.add("80"); quality.add("90");
        ArrayAdapter<String> adapterQuality = new ArrayAdapter<>(dialog.getContext(),
                android.R.layout.simple_spinner_item, quality);
        adapterQuality.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_quality.setAdapter(adapterQuality);

        // gespeicherte werte in spinner setzen
        int qualityValue = mainActivity.mPreferenz.getInt("JpegQuality",90);
        spinner_quality.setSelection((int) qualityValue/10 - 2);

        //
        // close button
        Button button_close = dialog.findViewById(R.id.button_close);
        button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // save loopValue and show in display
                int quality = spinner_quality.getSelectedItemPosition() * 10 + 20;
                Log.v(TAG, "quality " + quality);
                mainActivity.mPreferenzEditor.putInt("JpegQuality", quality).apply();
                Logger.add(String.format("JpegQuality: %s %%", quality));

                if (spinner_unit.getSelectedItemPosition() == 0) { // in sekunden
                    mainActivity.mPreferenzEditor.putLong("loopTime",
                            spinner_value.getSelectedItemPosition() * 1000L).apply();
                    mainActivity.textViewLoopTime.setText(String.format("Loop %s sec - JPEG %s%%",
                            spinner_value.getSelectedItemPosition(), quality));
                    Logger.add(String.format("Loop: %s seconds.",
                            spinner_value.getSelectedItemPosition()));
                } else { // dann sind es minuten
                    mainActivity.mPreferenzEditor.putLong("loopTime",
                            spinner_value.getSelectedItemPosition() * 60000L).apply();
                    mainActivity.textViewLoopTime.setText(String.format("Loop %s min - JPEG %s%%",
                            spinner_value.getSelectedItemPosition(), quality));
                    Logger.add(String.format("Loop: %s minutes.",
                            spinner_value.getSelectedItemPosition()));
                }

                long startIn = (((long) spinner_hour.getSelectedItemPosition() *60*60*1000) +
                        ((long) spinner_min.getSelectedItemPosition() *15*60*1000));
                //Log.v(TAG, "new start time: + " + startIn + " milli sec");
                mainActivity.mPreferenzEditor.putLong("startInTime", startIn).apply();
                mainActivity.textViewStartTime.setText(String.format("start in %s:%s",
                        ((long) spinner_hour.getSelectedItemPosition()),
                        ((long) spinner_min.getSelectedItemPosition()*15)));
                mainActivity.textViewStartTime.setText(String.format("start: %s",
                        mainActivity.startTimeFormat(System.currentTimeMillis() +
                                mainActivity.mPreferenz.getLong("startInTime",5*1000L))));

                // restart camera with new parameters
                CameraMain.startCamera();

                // close windows
                dialog.dismiss();
            }
        });
    }
}
