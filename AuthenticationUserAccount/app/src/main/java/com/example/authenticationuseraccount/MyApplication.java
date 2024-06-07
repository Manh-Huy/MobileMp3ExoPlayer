package com.example.authenticationuseraccount;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.authenticationuseraccount.activity.MainActivity;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.utils.DataLocalManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.stripe.android.PaymentConfiguration;

public class MyApplication extends Application {

    public static final String CHANNEL_ID = "Music Channel";
    public static final String CHANNEL_ID_2 = "Notification Channel";

    @Override
    public void onCreate() {
        super.onCreate();
        DataLocalManager.init(getApplicationContext());
        createNotificationChannel();
        createPaymentChannel();
        registerFCM();
    }

    private void registerFCM() {
        FirebaseMessaging.getInstance().subscribeToTopic("AdouTinTran")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed";
                        if (!task.isSuccessful()) {
                            msg = "Subscribe failed";
                        }
                        LogUtils.ApplicationLogI(msg);
                    }
                });
    }

    private void createPaymentChannel() {
        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_51POBbu1FjeTWqyK8DTko7WHq8pIlvPMVbvF7Aa43CUEMrNmaHWHQYGBCiLd3gIVQB4DdF6gv0SIom4mCFDkr8oiT00NAbc4OAd"
        );
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Music Control Channel", importance);
            channel.setSound(null, null);

            Uri uri2 = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.rhyderrrr);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            NotificationChannel channel2 = new NotificationChannel(CHANNEL_ID_2, "Notification From Firebase", importance);
            channel.setSound(uri2, audioAttributes);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            notificationManager.createNotificationChannel(channel2);

        }
    }
}
