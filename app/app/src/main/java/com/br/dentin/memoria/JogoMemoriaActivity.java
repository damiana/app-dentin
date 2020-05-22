package com.br.dentin.memoria;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.br.dentin.R;
import com.br.dentin.memoria.services.ImageAdapter;
import com.br.dentin.memoria.services.MemoriaBoard;
import com.br.dentin.memoria.services.MemoriaService;

import java.util.ArrayList;
import java.util.Stack;

import tyrantgit.explosionfield.ExplosionField;


public class JogoMemoriaActivity extends AppCompatActivity {

    final static int INTERVAL = 1000;
    private int numOfImages;

    // refernceses to front images for this game
    private Integer[] frontImagesReferences;

    // referenceses to all the back images
    private Integer[] backImagesReferences = {
            R.drawable.image1,R.drawable.image2,
            R.drawable.image3,R.drawable.image4,
            R.drawable.image5,R.drawable.image6,
            R.drawable.image7,R.drawable.image8,
            R.drawable.image9,R.drawable.image10,
            R.drawable.image11,R.drawable.image12,
            R.drawable.image13,R.drawable.image14,
            R.drawable.image15,R.drawable.image16,
            R.drawable.image17,R.drawable.image18};

    // refernceses to back images for this game up to the num of images
    private Integer[] currentBackImagesReferences;

    private ArrayList<ImageView> imageViews;
    private ImageAdapter imageAdapter;
    private Animation scaleAnim;
    private Animation offsetAnim;
    private ExplosionField explosionField;

    private MemoriaBoard board;
    private int frontImageId = R.drawable.card_interrogacao; //R.drawable.question;
    private boolean isBusy = false;

    //private String name;
    private int rowNum,colNum;
    private int points;
    private int results;
    private String avatar;

    private ImageView imageViewSelected1;
    private ImageView imageViewSelected2;
    private TextView textTimer;
    //private TextView textName;
    private ConstraintLayout constLayout;
    private GridView myGrid;
    private CountDownTimer mCountDownTimer;
    private ProgressBar progBar;
    private int i = 0;
    private int currentSecond;
    private int time;
    private boolean isBind = false;
    public MemoriaService.SensorBind binder;

    private Stack<ImageView> gameImageView;
    private Intent serviceIntent;
    private boolean state;

    private ImageView dentin;
    private ImageView fioDental;
    private ImageView escovaLady;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jogo_memoria);

        getParametersByIntent();
        showAvatars(avatar);

        frontImagesReferences = new Integer[numOfImages];
        currentBackImagesReferences = new Integer[numOfImages/2]; // num Of Images/2 - every image appear 2 times
        board = new MemoriaBoard(numOfImages);
        gameImageView = new Stack<>();

        initCurrentBackImages();
        initFrontImages();
        addImagesToGameBoard();
        board.shuffle();

        bindUI();
        currentSecond = time/INTERVAL;
        int height = constLayout.getLayoutParams().height;
        int width = constLayout.getLayoutParams().width;
        offsetAnim = AnimationUtils.loadAnimation(this,R.anim.offset_anim);
        explosionField = ExplosionField.attach2Window(this);
        //textName.setText(name);
        myGrid.setNumColumns(rowNum);
        imageAdapter = new ImageAdapter(this,height,width,rowNum,colNum,frontImagesReferences);
        myGrid.setAdapter(imageAdapter);

        myGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                ImageView img = (ImageView)v;
                checkBackImagesMatching(img,position);
            }
        });

        textTimer.setText(""+currentSecond);
        progBar.setProgress(i);

        mCountDownTimer = new CountDownTimer(time,INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                currentSecond--;
                i++;
                int timer = i*100/(time/INTERVAL);
                progBar.setProgress(timer);
                textTimer.setText(""+currentSecond);
            }

            @Override
            public void onFinish() {
                i++;
                stopService(serviceIntent);
                progBar.setProgress(100);
                textTimer.setText(""+0);
                doAnimationTimeOver();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },5000);
            }
        };

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCountDownTimer.start();
            }
        },900);
        serviceIntent = new Intent(this, MemoriaService.class);
        bindService(serviceIntent, bindService, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(msg, new IntentFilter(getString(R.string.change)));
        scaleAnim = AnimationUtils.loadAnimation(this,R.anim.scale);

    } //onCreate

    public void doAnimationTimeOver(){
        imageViews = imageAdapter.getImageViews();
        for(int i=0;i<imageViews.size();i++){
            //explosionField.explode(textName);
            explosionField.explode(textTimer);
            explosionField.explode(progBar);
            imageViews.get(i).startAnimation(offsetAnim);
        }
        JogoMemoriaActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(JogoMemoriaActivity.this,
                        "Tempo esgotado! Por favor, tente novamente.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private ServiceConnection bindService = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder = (MemoriaService.SensorBind) iBinder;
            binder.notifyService(getString(R.string.listen_message));
            isBind = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBind = false;
        }
    };

    private BroadcastReceiver msg = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            state = intent.getBooleanExtra(getString(R.string.move), true);
            if (state) {
                return;
            }
            reverseImage();
        }
    };

    public void getParametersByIntent() {
        Intent intent = getIntent();
        rowNum = intent.getIntExtra("row",-1);
        colNum = intent.getIntExtra("col", -1);
        //name = intent.getStringExtra("name");
        time = intent.getIntExtra("time",-1);
        points = intent.getIntExtra("point",-1);
        avatar = intent.getStringExtra("avatar");

        numOfImages = rowNum*colNum;
    }

    private void showAvatars(String avatar) {

        try {
            dentin = findViewById(R.id.back_avatar_dentin);
            fioDental = findViewById(R.id.back_avatar_fioDental);
            escovaLady = findViewById(R.id.back_avatar_escovaLady);

            if (avatar.equals("dentin")) {
                dentin.setVisibility(View.VISIBLE);
            }
            else if (avatar.equals("fioDental")) {
                fioDental.setVisibility(View.VISIBLE);
            }
            else if (avatar.equals("escovaLady")) {
                escovaLady.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            //incluir TOAST pra error
        }
    }

    public void bindUI(){
        constLayout = findViewById(R.id.constraint_board);
        textTimer   = findViewById(R.id.txt_timer);
        //textName   = findViewById(R.id.txt_name);
        progBar     = findViewById(R.id.progressBar);
        myGrid      = findViewById(R.id.gridview);
    }

    /**
     * This function check if 2 selected images on the board are match.
     * Checks if the player win.
     * @param img
     * @param position
     */
    public void checkBackImagesMatching(ImageView img,int position){

        if(isBusy)
            return;

        if(!board.isImageSelected(0)) {
            board.setImageSelectedFirstByIndex(position);
            if(!board.flipFirst()) {
                imageViewSelected1 = img;
                imageViewSelected1.setImageResource(board.getDrawableId(0));
            }
            else return;
        }
        if(!board.isImageSelected(1)) {
            board.setImageSelectedSecondByIndex(position);
            if (!board.flipSecond()) {
                results -= points/4; // not matching
                imageViewSelected2 = img;
                imageViewSelected2.setImageResource(board.getDrawableId(1));
            }
            else return;

            if (board.isSelectedImagesMatching()) {
                results += points; // matching
                gameImageView.push(imageViewSelected1);
                gameImageView.push(imageViewSelected2);
                if (checkWinGame()) {
                    mCountDownTimer.cancel(); // cancel timer
                    Intent resultIntent = new Intent();
                    if(results<0)// only postive points should be in the table
                        results=0;
                    resultIntent.putExtra("result", results+"");
                    setResult(RESULT_OK, resultIntent);
                    doAnimationWin();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    },2700);
                }
            } else {
                // do it in the background ;
                isBusy = true;
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        board.resetSelectedImages();
                        imageViewSelected1.setImageResource(frontImageId);
                        imageViewSelected2.setImageResource(frontImageId);
                        isBusy = false;
                    }
                },800);
            }
        }
    }

    public void doAnimationWin(){
        imageViews = imageAdapter.getImageViews();
        for(int i=0;i<imageViews.size();i++){
            imageViews.get(i).startAnimation(scaleAnim);
        }
        JogoMemoriaActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(JogoMemoriaActivity.this, "Você ganhou! Sua pontuação é: " + results, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void reverseImage(){
        ImageView imageView1, imageView2;

        if(!gameImageView.isEmpty()) {
            board.flipBack();
            imageView2 = gameImageView.pop();
            imageView1 = gameImageView.pop();

            imageView1.setImageResource(frontImageId);
            imageView2.setImageResource(frontImageId);

            if(results > 0){
                results = results - 5;
            }
        }
    }

    public boolean checkWinGame(){
        return board.isAllImagesMatch();
    }

    public void addImagesToGameBoard(){
        int countCurrentBackImages = 0;
        for(int i = 0; i < numOfImages; i ++){
            if(countCurrentBackImages == currentBackImagesReferences.length)
                countCurrentBackImages = 0;
            board.addImage(currentBackImagesReferences[countCurrentBackImages]);
            countCurrentBackImages++;
        }
    }

    public void initCurrentBackImages(){
        for(int i = 0; i < currentBackImagesReferences.length; i ++){
            currentBackImagesReferences[i] = backImagesReferences[i];
        }
    }

    // This function initialize refernces to front image by num of images.
    public void initFrontImages(){
        for(int i = 0 ; i < numOfImages; i ++)
            frontImagesReferences[i] = frontImageId;
    }

    public void onBackPressed() {
        mCountDownTimer.cancel();
        stopService(serviceIntent);
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (bindService != null) {
            unbindService(bindService);
        }
    }
}
