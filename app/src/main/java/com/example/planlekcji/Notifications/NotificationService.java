package com.example.planlekcji.Notifications;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.planlekcji.MainApp.Replacements.GetReplacementsData;
import com.example.planlekcji.MainApp.Replacements.ReplacementList;
import com.example.planlekcji.R;

import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {
    Timer timer;
    TimerTask timerTask;
    String TAG = "ryhc";
    int Your_X_SECS = 60;
    ReplacementList replacementList = null;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);

        startTimer();

        return START_STICKY;
    }


    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        stoptimertask();
        super.onDestroy();
    }

    //we are going to use a handler to be able to run in our TimerTask
    final Handler handler = new Handler();


    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 1000, Your_X_SECS * 1000); //
        //timer.schedule(timerTask, 5000,1000); //
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {

                        //TODO CALL NOTIFICATION FUNC
                        doNotification();

                    }
                });
            }
        };
    }

    private void doNotification() {
        ReplacementList replacementListNew = getDataForReplacements();
        Log.e("Notification", "Zastepstwo loop"+replacementListNew);

        if(replacementListNew != replacementList && !replacementListNew.equals("")) {
            Log.e("Notification", "Nowe zastepstwo");
            replacementList = replacementListNew;

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "notificationChannel")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("Plan Lekcji")
                    .setContentText("Nowe zastępstwo")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            notificationManager.notify(1, builder.build());
        }
    }

    private ReplacementList getDataForReplacements() {
        GetReplacementsData getReplacementsData = new GetReplacementsData();
        Thread thread = new Thread(getReplacementsData);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return getReplacementsData.getReplacementList();
    }
}