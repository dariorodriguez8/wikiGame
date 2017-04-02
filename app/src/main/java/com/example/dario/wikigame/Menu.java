package com.example.dario.wikigame;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Menu extends AppCompatActivity {
    String TYPE_GAME = "TYPE_GAME";
    String carpetaFuenteArial = "fonts/arial.ttf";
    String carpetaFuenteRock = "fonts/ROCK.TTF";

    TextView fut,mus,gam,cel;
    TextView futT,musT,gamT,celT;


    Button btnFut, btnCel, btnMus, btnGam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        fut = (TextView) findViewById(R.id.buttonFot);
        mus = (TextView) findViewById(R.id.buttonMus);
        gam = (TextView) findViewById(R.id.buttonGam);
        cel = (TextView) findViewById(R.id.buttonCel);

        futT = (TextView) findViewById(R.id.foottext);
        musT = (TextView) findViewById(R.id.mustext);
        gamT = (TextView) findViewById(R.id.gamtext);
        celT = (TextView) findViewById(R.id.celtext);

        Typeface fuenteA = Typeface.createFromAsset(getAssets(), carpetaFuenteArial);
        Typeface fuenteC = Typeface.createFromAsset(getAssets(), carpetaFuenteRock);
        fut.setTypeface(fuenteA);
        mus.setTypeface(fuenteA);
        gam.setTypeface(fuenteA);
        cel.setTypeface(fuenteA);

        futT.setTypeface(fuenteC);
        musT.setTypeface(fuenteC);
        gamT.setTypeface(fuenteC);
        celT.setTypeface(fuenteC);

        btnCel = (Button) findViewById(R.id.buttonCel);
        btnFut = (Button) findViewById(R.id.buttonFot);
        btnGam = (Button) findViewById(R.id.buttonGam);
        btnMus = (Button) findViewById(R.id.buttonMus);

        btnCel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                empezar("celebrities");
            }
        });

        btnFut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                empezar("football");
            }
        });

        btnGam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                empezar("games");
            }
        });

        btnMus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                empezar("music");
            }
        });

    }

    protected void empezar(String op){
        Intent llamada = new Intent(this, MainActivity.class);
        llamada.putExtra(TYPE_GAME,op);
        startActivity(llamada);
    }

}
