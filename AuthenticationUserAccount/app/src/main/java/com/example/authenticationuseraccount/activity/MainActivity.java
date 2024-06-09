package com.example.authenticationuseraccount.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.session.MediaController;
import androidx.media3.session.MediaSession;
import androidx.media3.session.SessionToken;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.common.Constants;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.common.PermissionManager;
import com.example.authenticationuseraccount.model.business.Song;
import com.example.authenticationuseraccount.service.BackEventHandler;
import com.example.authenticationuseraccount.service.MediaItemHolder;
import com.example.authenticationuseraccount.service.MusicService;
import com.example.authenticationuseraccount.service.UIThread;
import com.example.authenticationuseraccount.utils.MySharedPreferences;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private UIThread m_vThread;

    private boolean isReceiveNotification;

    private Song mSong;

    public static interface OnMediaControllerConnect {
        void onMediaControllerConnect(MediaController controller);

        void onUpdateUIOnRestar(MediaController mediaController);
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.ApplicationLogE("MainActivity onCreate");
        setContentView(R.layout.activity_main2);

        isReceiveNotification = false;
        mSong = null;

        PermissionManager.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, 100);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            PermissionManager.requestPermission(this, Manifest.permission.FOREGROUND_SERVICE, 100);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            PermissionManager.requestPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE, 100);
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }
        this.m_vThread = new UIThread(this);
        BackEventHandler.getInstance();
        Intent intentFromFCM = getIntent();
        String actionFromNotification = intentFromFCM.getAction();
        if (actionFromNotification != null && actionFromNotification.equals(Constants.NOTIFICATION_ACTION_CLICK)) {
            LogUtils.ApplicationLogI("Receive Action From Notfication");
            Bundle songBundle = intentFromFCM.getExtras();
            Song song = (Song) songBundle.getSerializable(Constants.NOTIFICATION_SONG_OBJECT);
            if (MediaItemHolder.getInstance().getMediaController() != null) {
                LogUtils.ApplicationLogI("Receive Action From Notfication And App Already Open");
                MediaItemHolder.getInstance().getListSongs().clear();
                MediaItemHolder.getInstance().getListSongs().add(song);
                MediaItem mediaItem = MediaItem.fromUri(song.getSongURL());
                MediaItemHolder.getInstance().getMediaController().setMediaItem(mediaItem);
                m_vThread.onUpdateUIOnRestar(MediaItemHolder.getInstance().getMediaController());
                isReceiveNotification = false;
                mSong = null;

            } else {
                isReceiveNotification = true;
                mSong = song;
                LogUtils.ApplicationLogI("Receive Action From Notfication mediaController null: " + song.getName());
            }
        } else {
            LogUtils.ApplicationLogI("No Action From Any Notfication");
        }
    }


    @UnstableApi
    @Override
    protected void onStart() {
        super.onStart();

        LogUtils.ApplicationLogE("MainActivity onStart");
        if (MediaItemHolder.getInstance().getMediaController() != null) {
            LogUtils.ApplicationLogD("MediaItemHolder Instance Not Null");
            if (isReceiveNotification && mSong != null) {
                LogUtils.ApplicationLogI("Receive noti and start playing");
                MediaItemHolder.getInstance().getListSongs().clear();
                MediaItemHolder.getInstance().getListSongs().add(mSong);
                MediaItem mediaItem = MediaItem.fromUri(mSong.getSongURL());
                MediaItemHolder.getInstance().getMediaController().setMediaItem(mediaItem);

                isReceiveNotification = false;
                mSong = null;
            }
            if (MediaItemHolder.getInstance().getMediaController().getMediaMetadata().title != null) {
                LogUtils.ApplicationLogD("MediaMetadata Not Null => App is playing music (pausing / playing)");
                m_vThread.onUpdateUIOnRestar(MediaItemHolder.getInstance().getMediaController());
            } else {
                LogUtils.ApplicationLogD("App not playing music. just restart");
            }
            return;
        }

        SessionToken sessionToken = new SessionToken(MainActivity.this, new ComponentName(MainActivity.this, MusicService.class));
        MediaController.Builder builder = new MediaController.Builder(MainActivity.this, sessionToken);
        ListenableFuture<MediaController> controllerFuture = builder.buildAsync();
        controllerFuture.addListener(() -> {
            try {
                if (MediaItemHolder.getInstance().getMediaController() == null) {
                    LogUtils.ApplicationLogE("OnStart Connect Media Controller");

                    MediaController mediaController = controllerFuture.get();
                    MediaItemHolder.getInstance().setMediaController(mediaController);
                    m_vThread.onMediaControllerConnect(mediaController);

                    if (isReceiveNotification && mSong != null) {
                        LogUtils.ApplicationLogI("Receive noti and start playing");
                        MediaItem mediaItem = MediaItem.fromUri(mSong.getSongURL());
                        MediaItemHolder.getInstance().getMediaController().setMediaItem(mediaItem);
                        MediaItemHolder.getInstance().getListSongs().clear();
                        MediaItemHolder.getInstance().getListSongs().add(mSong);

                        isReceiveNotification = false;
                        mSong = null;
                    }
                }
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, MoreExecutors.directExecutor());

    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.ApplicationLogE("MainActivity onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.ApplicationLogE("MainActivity onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.ApplicationLogE("MainActivity onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtils.ApplicationLogE("MainActivity onRestart");
    }

    @Override
    protected void onDestroy() {
        LogUtils.ApplicationLogE("MainActivity onDestroy");
        if (m_vThread.getListener() != null)
            MediaItemHolder.getInstance().getMediaController().removeListener(m_vThread.getListener());
        m_vThread.release();
        m_vThread = null;
        super.onDestroy();
    }
}