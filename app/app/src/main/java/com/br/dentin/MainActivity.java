package com.br.dentin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import com.br.dentin.memoria.EscolhaAvatarMemoriaActivity;
import com.br.dentin.puzzle.PuzzleMainActivity;
import com.br.dentin.sound.BackgroundAudioService;
import com.br.dentin.sound.SoundBackgroundActivity;

public class MainActivity extends AppCompatActivity {

    ImageButton btnJogoMemoria;
    ImageButton btnQuebraCabeca;

    ImageButton btnVolume;
    private boolean isPlay = false;

    ImageButton btnSobre;

    //ImageButton btnLigue;
    //ImageButton btnVideos;

    Button btnVideos;


    // Sound
    private static final int STATE_PAUSED = 0;
    private static final int STATE_PLAYING = 1;

    private int mCurrentState;

    private MediaBrowserCompat mMediaBrowserCompat;
    private MediaControllerCompat mMediaControllerCompat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mMediaBrowserCompat = new MediaBrowserCompat(this, new ComponentName(this, BackgroundAudioService.class),
                mMediaBrowserCompatConnectionCallback, getIntent().getExtras());

        mMediaBrowserCompat.connect();
        addListenerOnButton();
    }

    public void addListenerOnButton() {
        btnJogoMemoria = (ImageButton) findViewById(R.id.imgBtn_jogo_memoria_menu2);
        btnQuebraCabeca = (ImageButton) findViewById(R.id.imgBtn_quebra_cabeca);

        btnVolume = (ImageButton) findViewById(R.id.imgBtn_volume);
        btnVolume.setBackground(getResources().getDrawable(R.drawable.volume_off_white_36));

        btnSobre = (ImageButton) findViewById(R.id.imgBtn_sobre);

        //btnVideos =(Button) findViewById(R.id.btn_videos);

        btnJogoMemoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(MainActivity.this, EscolhaAvatarMemoriaActivity.class);
                startActivity(i);
               // finish();
            }
        });

        btnQuebraCabeca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(MainActivity.this, PuzzleMainActivity.class);
                startActivity(i);
                // finish();
            }
        });

        btnVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlay) {
                    btnVolume.setBackground(getResources().getDrawable(R.drawable.volume_off_white_36));
                } else {
                    btnVolume.setBackground(getResources().getDrawable(R.drawable.volume_up_white_36));
                }
                isPlay = !isPlay;

                if( mCurrentState == STATE_PAUSED ) {
                    MediaControllerCompat.getMediaController(MainActivity.this).getTransportControls().play();
                    mCurrentState = STATE_PLAYING;
                } else {
                    if(  MediaControllerCompat.getMediaController(MainActivity.this).getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING ) {
                        MediaControllerCompat.getMediaController(MainActivity.this).getTransportControls().pause();
                    }
                    mCurrentState = STATE_PAUSED;
                }
            }
        });

        btnSobre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(i);
                // finish();
            }
        });

        /*btnVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(MainActivity.this, VideosActivity.class);
                startActivity(i);
                finish();
            }
        });*/
        //setupViewAdapter();

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        switch (id){
//            case R.id.about:
//                Intent i = new Intent(MainActivity.this, AboutActivity.class);
//                startActivity(i);
//                Toast.makeText(getApplicationContext(),"Item 1 Selected",Toast.LENGTH_LONG).show();
//                return true;
//            case R.id.item2:
//                Intent ii = new Intent(MainActivity.this, SoundBackgroundActivity.class);
//                startActivity(ii);
//                Toast.makeText(getApplicationContext(),"Item 2 Selected",Toast.LENGTH_LONG).show();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    private MediaBrowserCompat.ConnectionCallback mMediaBrowserCompatConnectionCallback = new MediaBrowserCompat.ConnectionCallback() {

        @Override
        public void onConnected() {
            super.onConnected();
            try {
                mMediaControllerCompat = new MediaControllerCompat(MainActivity.this, mMediaBrowserCompat.getSessionToken());
                mMediaControllerCompat.registerCallback(mMediaControllerCompatCallback);
                MediaControllerCompat.setMediaController(MainActivity.this,mMediaControllerCompat);
                MediaControllerCompat.getMediaController(MainActivity.this).getTransportControls().playFromMediaId(String.valueOf(R.raw.here_come_the_weirds_v001), null);

            } catch( RemoteException e ) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
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
    protected void onDestroy() {
        super.onDestroy();
        if(  MediaControllerCompat.getMediaController(MainActivity.this).getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING ) {
            MediaControllerCompat.getMediaController(MainActivity.this).getTransportControls().pause();
        }
        mMediaBrowserCompat.disconnect();
    }
}
