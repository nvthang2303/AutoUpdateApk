package com.pedromaironi.appverification.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.pedromaironi.appverification.ui.MainActivity;
import com.pedromaironi.appverification.R;

public class DownloadWorker extends Worker {

    private NotificationHelper notificationHelper;
    private int current = 0;

    public DownloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        return null;
    }
}