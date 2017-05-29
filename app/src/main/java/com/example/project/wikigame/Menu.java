package com.example.project.wikigame;

// LIBRERIAS
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
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
    FloatingActionButton fb;
    private static final String PREFS_FILE = "com.example.sharedpreferences.preferences";
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mSharedPreferences;
    SharedPreferences pref;

    @Override
    public void onBackPressed() {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mSharedPreferences = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);

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

        fb = (FloatingActionButton) findViewById(R.id.myFAB);

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

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),SettingsActivity.class);
                startActivity(i);
            }
        });

    }

    // Comienzar un nuevo juego (selecionando cada categoria)
    protected void empezar(String op){
        Intent llamada = new Intent(this, MainActivity.class);
        llamada.putExtra(TYPE_GAME,op);
        startActivity(llamada);
    }

    // Asignar el valor al record de cada categoría
    public void muestraRecord(View v){
        int puntos=0;
        switch (v.getId()){
            case R.id.buttonf:
                puntos = mSharedPreferences.getInt("PUNTOS_FOOTBALL",0);
                break;
            case R.id.buttonm:
                puntos = mSharedPreferences.getInt("PUNTOS_MUSIC",0);
                break;
            case R.id.buttong:
                puntos = mSharedPreferences.getInt("PUNTOS_GAMES",0);
                break;
            case R.id.buttonc:
                puntos = mSharedPreferences.getInt("PUNTOS_CELEBRITIES",0);
                break;
        }
        showDia(puntos);
    }

    // Mostrar el record de cada categoria en el menú principal
    public void showDia(int p) {

        LayoutInflater linf = LayoutInflater.from(this);
        View inflator = linf.inflate(R.layout.fragment_layout, null);
        TextView PU = (TextView) inflator.findViewById(R.id.textView6);
        TextView TI = (TextView) inflator.findViewById(R.id.textView5);

        Button main = (Button) inflator.findViewById(R.id.button5);
        Button try_again = (Button) inflator.findViewById(R.id.button4);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        PU.setText("" + p);
        TI.setText("RECORD");

        alert.setView(inflator);
        alert.setCancelable(true);
        main.setVisibility(View.INVISIBLE);
        try_again.setVisibility(View.INVISIBLE);

        alert.show();

    }

}
