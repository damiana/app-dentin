package com.br.dentin.sound;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.br.dentin.R;

public class MediaController {

    private int mCurrentState;

    private MediaBrowserCompat mMediaBrowserCompat;
    private MediaControllerCompat mMediaControllerCompat;

    private Intent intentContext;
    private Activity activity;

    private static final int STATE_PAUSED = 0;
    private static final int STATE_PLAYING = 1;


    public void MediaControoler(Intent intent, Activity activity) {
        this.intentContext = intent;
        this.activity = activity;
    }

    private MediaBrowserCompat.ConnectionCallback mMediaBrowserCompatConnectionCallback = new MediaBrowserCompat.ConnectionCallback() {

        @Override
        public void onConnected() {
            super.onConnected();
            try {
                mMediaControllerCompat = new MediaControllerCompat(activity, mMediaBrowserCompat.getSessionToken());
                mMediaControllerCompat.registerCallback(mMediaControllerCompatCallback);
                MediaControllerCompat.setMediaController(activity,mMediaControllerCompat);
                MediaControllerCompat.getMediaController(activity).getTransportControls().playFromMediaId(String.valueOf(R.raw.here_come_the_weirds_v001), null);

            } catch( RemoteException e ) {

            }
        }
    };

    private MediaControllerCompat.Callback mMediaControllerCompatCallback = new MediaControllerCompat.Callback() {

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
            if( state == null ) {
                return;
            }

            switch( state.getState() ) {
                case PlaybackStateCompat.STATE_PLAYING: {
                    mCurrentState = STATE_PLAYING;
                    break;
                }
                case PlaybackStateCompat.STATE_PAUSED: {
                    mCurrentState = STATE_PAUSED;
                    break;
                }
                default:
                    mCurrentState = STATE_PLAYING;
                    break;
            }
        }
    };

    public MediaBrowserCompat getMediaBrowserCompat(Bundle bundle) {
        //Intent i2=new Intent(Login_2.this,MainActivity.class)


//        mMediaBrowserCompat = new MediaBrowserCompat(this, new ComponentName(activity, BackgroundAudioService.class),
//                mMediaBrowserCompatConnectionCallback, intentContext.));
        return mMediaBrowserCompat;
    }
}
