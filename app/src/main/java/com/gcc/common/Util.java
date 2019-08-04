package com.gcc.common;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.gcc.R;

import java.net.URI;

public class Util {

    public static void notNull(Object object, String msg) {
        if (object == null) {
            throw new RuntimeException(msg  + " cannot be null");
        }
    }

    public static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }


    public static boolean isBackgroundThread() {
        return Looper.getMainLooper() != Looper.myLooper();
    }

    public static void glide(Context context, String url, ImageView target) {
        try {
            Glide.with(context)
                    .load(Uri.parse(url))
                    .placeholder(R.drawable.logo)
                    .into(target);
        } catch (Exception e) {
            Log.e("GLIDE URL ERROR", url);
        }
    }


    public static void setStatusBarResource(Activity context, int resourceId) {


        if (Build.VERSION.SDK_INT >= 21){

            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(context.getResources().getColor(resourceId));
        }
    }

    public static void enableDrawOverStatus(Activity context) {
        context.getWindow()
                .getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}
