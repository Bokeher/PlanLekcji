package com.example.planlekcji.Tools;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    public static void showToast(final Context context, final String message, boolean closeApplication) {
        if (!(context instanceof Activity)) { return; }

        final Activity activity = (Activity) context;
        activity.runOnUiThread(() -> {
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            if(closeApplication) activity.finish();
        });
    }
}
