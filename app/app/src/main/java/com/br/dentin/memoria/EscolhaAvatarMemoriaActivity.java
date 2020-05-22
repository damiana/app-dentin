package com.br.dentin.memoria;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.br.dentin.MainActivity;
import com.br.dentin.R;

public class EscolhaAvatarMemoriaActivity extends AppCompatActivity {

    ImageButton imgButtonAvatarDentin;
    ImageButton imgButtonFioDental;
    ImageButton imgButtonEscova;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escolha_avatar);

        addListenerOnButtonAvatar();
    }

    public void addListenerOnButtonAvatar() {

        imgButtonAvatarDentin = (ImageButton) findViewById(R.id.imgBtn_dentin);
        imgButtonFioDental = (ImageButton) findViewById(R.id.imgBtn_fio_dental_ninja);
        imgButtonEscova = (ImageButton) findViewById(R.id.imgBtn_escova_lady);

        imgButtonAvatarDentin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(EscolhaAvatarMemoriaActivity.this, JogoMemoriaMenuActivity.class);
                i.putExtra("avatar", "dentin");
                startActivity(i);
            }
        });

        imgButtonFioDental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(EscolhaAvatarMemoriaActivity.this, JogoMemoriaMenuActivity.class);
                i.putExtra("avatar", "fioDental");
                startActivity(i);
               // finish();
            }
        });

        imgButtonEscova.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(EscolhaAvatarMemoriaActivity.this, JogoMemoriaMenuActivity.class);
                i.putExtra("avatar", "escovaLady");
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
