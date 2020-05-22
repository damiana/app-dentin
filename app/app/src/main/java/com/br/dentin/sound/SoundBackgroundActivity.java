package com.br.dentin.sound;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.br.dentin.R;

public class SoundBackgroundActivity extends AppCompatActivity {
    private static final int STATE_PAUSED = 0;
    private static final int STATE_PLAYING = 1;

    private int mCurrentState;

    private MediaBrowserCompat mMediaBrowserCompat;
    private MediaControllerCompat mMediaControllerCompat;

    private Button mPlayPauseToggleButton;

    private MediaBrowserCompat.ConnectionCallback mMediaBrowserCompatConnectionCallback = new MediaBrowserCompat.ConnectionCallback() {

        @Override
        public void onConnected() {
            super.onConnected();
            try {
                mMediaControllerCompat = new MediaControllerCompat(SoundBackgroundActivity.this, mMediaBrowserCompat.getSessionToken());
                mMediaControllerCompat.registerCallback(mMediaControllerCompatCallback);
                MediaControllerCompat.setMediaController(SoundBackgroundActivity.this,mMediaControllerCompat);
                MediaControllerCompat.getMediaController(SoundBackgroundActivity.this).getTransportControls().playFromMediaId(String.valueOf(R.raw.here_come_the_weirds_v001), null);

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_background);

        mPlayPauseToggleButton = (Button) findViewById(R.id.button);

        mMediaBrowserCompat = new MediaBrowserCompat(this, new ComponentName(this, BackgroundAudioService.class),
                mMediaBrowserCompatConnectionCallback, getIntent().getExtras());

        mMediaBrowserCompat.connect();

        mPlayPauseToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //MediaControllerCompat.getMediaController(SoundBackgroundActivity.this).getTransportControls().prepare();
//                if( mCurrentState == STATE_PAUSED ) {
//                    MediaControllerCompat.getMediaController(SoundBackgroundActivity.this).getTransportControls().play();
//                    mCurrentState = STATE_PLAYING;
//                } else {
//                    if(  MediaControllerCompat.getMediaController(SoundBackgroundActivity.this).getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING ) {
//                        MediaControllerCompat.getMediaController(SoundBackgroundActivity.this).getTransportControls().pause();
//                    }
//
//                    mCurrentState = STATE_PAUSED;
//                }
            }
        });

        MediaControllerCompat.getMediaController(SoundBackgroundActivity.this).getTransportControls().prepare();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(  MediaControllerCompat.getMediaController(SoundBackgroundActivity.this).getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING ) {
            MediaControllerCompat.getMediaController(SoundBackgroundActivity.this).getTransportControls().pause();
        }
        mMediaBrowserCompat.disconnect();
    }
}
