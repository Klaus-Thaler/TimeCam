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
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CustomToast extends MainActivity {
    private static final String TAG = "myLog CustomToast";

    /**
     * <p>it is a toast message</p>
     *
     *     <li> 1 = normal custom</li>
     *     <li> 2 = warnung</li>
     *     <li> 0* ist default Toast</li>
     *
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    public static void showToast(Context context, String message, int style) {
        Toast toast = new Toast(context.getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);

        LayoutInflater li = mainActivity.getLayoutInflater();
        View layout = li.inflate(R.layout.custom_toast,
                mainActivity.findViewById(R.id.linearLayoutToast));
        //layout.setRotation(0);
        TextView text = layout.findViewById(R.id.textToast);
        text.setText(message);
        layout.setPaddingRelative(10,10, 10, 10);

        // je nachdem als normal oder warnung
        switch(style) {
            case 1:
                layout.setBackground(context.getDrawable(R.drawable.button_shapes));
                toast.setGravity(Gravity.BOTTOM|Gravity.START,42,600);
                toast.setView(layout); //setting the view of custom toast layout
                break;
            case 2:
                layout.setBackground(context.getDrawable(R.drawable.button_shapes_on));
                toast.setGravity(Gravity.BOTTOM|Gravity.START,42,600);
                //ImageView image = layout.findViewById(R.id.imageToast);
                //image.setImageDrawable(mContext.getDrawable(drawable.ic_stat_name));
                toast.setView(layout); //setting the view of custom toast layout
                break;
            default:
                toast.setText(message);
        }
        toast.show();
    }
}