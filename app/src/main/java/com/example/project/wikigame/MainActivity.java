package com.example.project.wikigame;

// LIBRERIAS
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

public class MainActivity extends AppCompatActivity {
    String word;
    TextView texto;
    ImageButton btnGenerate, btnCheck, btnClue;
    LinearLayout pantalla;
    ImageView imag, vidasIMG;
    EditText editText;
    TextView puntos;
    String result, resultiMAGEN;
    String carpetaFuente = "fonts/LinLibertine_R.ttf";
    String nombre;
    String TYPE_GAME = "TYPE_GAME";
    boolean trig = true;
    boolean amd = true;
    boolean record = false;
    String tipoDeJuego;
    int puntuacion = 0;
    int vidas = 6;
    int contadorReload = 3;
    TextView puntosF;
    Button try_again, main_menu;
    int OLD_puntuacion;
    ArrayList<String> oldOne;
    String lang;

    LottieAnimationView animationView;
    MediaPlayer mp;

    int contador = 0;

    private static final String PREFS_FILE = "com.example.sharedpreferences.preferences";
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mSharedPreferences;
    private GoogleApiClient client2;
    SharedPreferences pref;

    // Si le das al botón de volver atrás genera un cuadro para confirmar
    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(R.string.r_u_sure)
                .setMessage(R.string.leave)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(getApplicationContext(), Menu.class));
                            }
                        })
                .setNegativeButton(R.string.resume,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        android.app.AlertDialog alert = builder.create();
        alert.show();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean full = pref.getBoolean("full_pref",false);
        if(full) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_main);
        mp= MediaPlayer.create(this, R.raw.bing);
        lang = Locale.getDefault().getLanguage();
        oldOne = new ArrayList<String>();
        oldOne.add("prueba");

        if(lang.equals("es")){

        }else{

        }

        pantalla = (LinearLayout) findViewById(R.id.pantalla);
        animationView = (LottieAnimationView) findViewById(R.id.animation_view);

        mSharedPreferences = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        texto = (TextView) findViewById(R.id.textView2);
        btnGenerate = (ImageButton) findViewById(R.id.button);
        btnCheck = (ImageButton) findViewById(R.id.button2);
        btnClue = (ImageButton) findViewById(R.id.button3);
        imag = (ImageView) findViewById(R.id.imageView2);
        puntos = (TextView) findViewById(R.id.textView4);
        vidasIMG = (ImageView) findViewById(R.id.vidas);

        final ImageView vidaIcono = (ImageView) findViewById(R.id.imageViewHh);
        final TextView generate = (TextView) findViewById(R.id.badge_textView);

        puntosF = (TextView) findViewById(R.id.textView6);

        editText = (EditText) findViewById(R.id.editText);
        Typeface fuente = Typeface.createFromAsset(getAssets(), carpetaFuente);
        texto.setTypeface(fuente);

        // Botón generar
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
                contador++;
                Log.v("eee",contador+"");
                contadorReload--;

                if (contadorReload >= 0) {
                    generate.setText(contadorReload + "");
                    if (contadorReload == 0) {
                        generate.setVisibility(View.INVISIBLE);
                        vidaIcono.setVisibility(View.VISIBLE);
                    }
                } else {

                    vidas--;
                    comprobarVidas();
                }

                generaPalabra();
            }
        });

        tipoDeJuego = getIntent().getStringExtra(TYPE_GAME);

        // Botón comprobar
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });

        // Botón pista
        btnClue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (trig & amd) {
                    imag.setVisibility(View.VISIBLE);
                    Picasso.with(getApplicationContext()).load("https:" + resultiMAGEN).into(imag);
                    amd = !amd;
                }
            }
        });

        generaPalabra();
        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }

    // Comrpueba si está bien puesta la palabra
    public void validate() {

        String e = stripAccents(editText.getText().toString());

        if (e.toLowerCase().equals(stripAccents(word.toLowerCase()))) {

            animationView.setVisibility(View.VISIBLE);
            animationView.setAnimation("check_pop.json");
            animationView.loop(false);

            ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f)
                    .setDuration(1000);

            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    animationView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animator.start();
            animationView.playAnimation();
            pantalla.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
            pantalla.setVisibility(View.INVISIBLE);

            OLD_puntuacion=puntuacion;

            if (amd) {
                puntuacion = puntuacion + 20;
            } else {
                puntuacion = puntuacion + 10;
            }

            animateTextView(OLD_puntuacion,puntuacion,puntos);

            boolean sou = pref.getBoolean("sound_pref",false);
            if(sou) {
                mp.start();
            }

            editText.setText("");
            generaPalabra();
            if (amd) {
                Picasso.with(getApplicationContext()).load("https:" + resultiMAGEN).into(imag);
            }

        } else {

            animationView.setVisibility(View.VISIBLE);
            animationView.setAnimation("x_pop.json");
            animationView.loop(false);

            ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f)
                    .setDuration(1000);

            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    animationView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animator.start();
            animationView.playAnimation();

            pantalla.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
            pantalla.setVisibility(View.INVISIBLE);


            boolean vib = pref.getBoolean("vib_pref",false);
            if(vib) {
                Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(200);
            }

            if (vidas >= 0 && vidas <= 6) {
                vidas--;
            }
            editText.setText("");
            generaPalabra();
        }

        comprobarVidas();

    }

    // Quita los acentos
    public static String stripAccents(String s)
    {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }

    // Animaciones
    public void animateTextView(int initialValue, int finalValue, final TextView  textview) {

        ValueAnimator valueAnimator = ValueAnimator.ofInt(initialValue, finalValue);
        valueAnimator.setDuration(500);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                textview.setText(valueAnimator.getAnimatedValue().toString());

            }
        });
        valueAnimator.start();

    }

    // Comprueba las vidas restantes
    public void comprobarVidas() {
        switch (vidas) {
            case 0:

                switch (tipoDeJuego) {
                    case "games":
                        int p = mSharedPreferences.getInt("PUNTOS_GAMES", 0);

                        if (p < puntuacion) {
                            mEditor.putInt("PUNTOS_GAMES", puntuacion);
                            record = true;
                            mEditor.apply();
                        }
                        break;
                    case "football":
                        int a = mSharedPreferences.getInt("PUNTOS_FOOTBALL", 0);
                        if (a < puntuacion) {
                            mEditor.putInt("PUNTOS_FOOTBALL", puntuacion);
                            record = true;
                            mEditor.apply();
                        }
                        break;
                    case "celebrities":
                        int o = mSharedPreferences.getInt("PUNTOS_CELEBRITIES", 0);

                        if (o < puntuacion) {
                            mEditor.putInt("PUNTOS_CELEBRITIES", puntuacion);
                            record = true;
                            mEditor.apply();
                        }


                        break;
                    case "music":
                        int t = mSharedPreferences.getInt("PUNTOS_MUSIC", 0);

                        if (t < puntuacion) {
                            mEditor.putInt("PUNTOS_MUSIC", puntuacion);
                            record = true;
                            mEditor.apply();
                        }
                        break;
                }
                showDialog();




                break;
            case 1:
                vidasIMG.setImageResource(R.drawable.unoh);
                break;
            case 2:
                vidasIMG.setImageResource(R.drawable.dosh);
                break;
            case 3:
                vidasIMG.setImageResource(R.drawable.tresh);
                break;
            case 4:
                vidasIMG.setImageResource(R.drawable.cuatroh);
                break;
            case 5:
                vidasIMG.setImageResource(R.drawable.cincoh);
                break;
            case 6:

                break;
        }


    }

    private void showDialog() {
        LayoutInflater linf = LayoutInflater.from(this);
        final View inflator = linf.inflate(R.layout.fragment_layout, null);
        final TextView PU = (TextView) inflator.findViewById(R.id.textView6);
        final TextView newScore = (TextView) inflator.findViewById(R.id.textViewRecord);

        final Button main = (Button) inflator.findViewById(R.id.button5);
        final Button try_again = (Button) inflator.findViewById(R.id.button4);


        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        PU.setText("" + puntuacion);


        if (record) {
            newScore.setVisibility(View.VISIBLE);
        }
        alert.setView(inflator);
        alert.setCancelable(false);
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent llamada = new Intent(getApplicationContext(), Menu.class);
                startActivity(llamada);
            }
        });


        try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent llamada = new Intent(getApplicationContext(), MainActivity.class);
                llamada.putExtra(TYPE_GAME, tipoDeJuego);
                startActivity(llamada);
            }
        });


        alert.show();


    }

    public void generaPalabraAleatoria() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://api.wordnik.com/v4/words.json/randomWords?hasDictionaryDef=true&minCorpusCount=0&minLength=5&maxLength=15&limit=1&api_key=a2a73e7b926c924fad7001ca3111acd55af2ffabf50eb4ae5\n")
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {


            @Override
            public void onFailure(Request request, IOException e) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Response response) throws IOException {

                try {
                    String jsonData = response.body().string();
                    Log.v("syso", jsonData);
                    if (response.isSuccessful()) {
                        JSONArray json = new JSONArray(jsonData);
                        JSONObject J = json.getJSONObject(0);

                        word = J.getString("word");
                        Log.v("ooo", "el resultado es ------>" + word);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                buscaWiki(word);

                            }
                        });
                    } else {
                    }
                } catch (IOException e) {
                    Log.e("syso", "Exception caught: ", e);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    // Genera la palabra aleatoria y la pone a minusculas y sin acentos
    public void generaPalabra() {
        trig = false;
        InputStream inputStream = new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        };
        switch (tipoDeJuego) {
            case "games":
                inputStream = getApplicationContext().getResources().openRawResource(R.raw.games);
                break;
            case "football":
                inputStream = getApplicationContext().getResources().openRawResource(R.raw.football);
                break;
            case "celebrities":
                inputStream = getApplicationContext().getResources().openRawResource(R.raw.celebrities);
                break;
            case "music":
                inputStream = getApplicationContext().getResources().openRawResource(R.raw.singers);
                break;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int ctr;
        try {
            ctr = inputStream.read();
            while (ctr != -1) {
                byteArrayOutputStream.write(ctr);
                ctr = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // Parse the data into jsonobject to get original data in form of json.
            JSONArray array = new JSONArray(byteArrayOutputStream.toString());
            boolean b = true;
            boolean x = false;
            int randomnumber=0;
            while(b) {
                randomnumber = Math.round((float) (Math.random() * array.length()) - 1);
                List<String> l = oldOne;
                for (String s:l) {
                    Log.v("hulk", array.getJSONObject(randomnumber).getString("title") +"||"+s);
                    if(array.getJSONObject(randomnumber).getString("title").equals(s)){
                        x=false;
                    }else{
                        x=true;
                    }
                }
                if(x){
                    b=false;
                }

                }

            nombre = array.getJSONObject(randomnumber).getString("title");
            oldOne.add(nombre);
            String n = "";
            for (int i = 0; i < oldOne.size(); i++) {
                 n = n + " "+oldOne.get(i).toString() + "|| ("+i+")" ;

            }

            Log.v("array",n);
            buscaWiki(nombre);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    // Busca en la API de Wikipedia la palabra
    public void buscaWiki(String w) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://"+lang+".wikipedia.org/w/api.php?action=query&list=search&srsearch=" + w.replaceAll(" ", "_") + "&srwhat=text&srprop=timestamp&format=json&continue=")
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {


            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {

                try {
                    String jsonData = response.body().string();
                    Log.v("syso", jsonData);
                    if (response.isSuccessful()) {
                        JSONObject json = new JSONObject(jsonData);
                        JSONObject J = json.getJSONObject("query").getJSONArray("search").getJSONObject(0);
                        word = J.getString("title");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                generaWiki(word);

                            }
                        });
                    } else {
                    }
                } catch (IOException e) {
                    Log.e("syso", "Exception caught: ", e);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    // Guarda la imagen y el primer parrafo de la wikipedia de cada palabra
    public void generaWiki(String w) {
        imag.setVisibility(View.INVISIBLE);

        //Log.v("aaa", "el resultado es ------>" + w);
        word = w;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://"+lang+".wikipedia.org/wiki/" + w.replaceAll(" ", "_"))
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Response response) throws IOException {

                try {
                    String html = response.body().string();
                    Log.v("syso", html);
                    if (response.isSuccessful()) {
                        try {
                            Document doc = DocumentBuilderFactory.newInstance()
                                    .newDocumentBuilder().parse(new InputSource(new StringReader(html)));

                            XPathExpression xpath = XPathFactory.newInstance()
                                    .newXPath().compile("//div[@id='mw-content-text']/p[1]");

                            XPathExpression imagenEXP = XPathFactory.newInstance()
                                    .newXPath().compile("//a[@class='image']/img/@src");

                            result = (String) xpath.evaluate(doc, XPathConstants.STRING);
                            resultiMAGEN = (String) imagenEXP.evaluate(doc, XPathConstants.STRING);


                        } catch (Exception e) {


                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.v("www", result);
                                if (result.contains("may refer to:")) {
                                    generaPalabra();
                                } else {
                                    String textoNuevo = result.replaceAll("\\[[0-9]\\]", "");
                                    textoNuevo = textoNuevo.replaceAll("\\(.*?\\)", "");
                                    textoNuevo = textoNuevo.replaceAll("\\[.*?\\]", "");
                                    String inicio, fin;

                                    String delimitadores = " ";
                                    String[] palabrasSeparadas = word.split(delimitadores);

                                    inicio = palabrasSeparadas[0];
                                    fin = palabrasSeparadas[palabrasSeparadas.length - 1];


                               /* for(int i=0;i<word.length();i++){
                                    vac= vac+vac;
                                }*/
                                    int a = word.length();
                                    String repeated = new String(new char[a]).replace("\0", "█ ");
                                    String n = "";

                                    for (int i = 0; i < word.length(); i++) {
                                        char b = word.charAt(i);
                                        if (b == ' ') {
                                            n += "  ";
                                        } else {
                                            n += "█ ";
                                        }

                                    }
                                    textoNuevo = textoNuevo.replaceAll("(" + inicio + " .*?" + fin + ")", n);
                                    if (inicio.length() > 2) {
                                        textoNuevo = textoNuevo.replaceAll("(?i)" + inicio, new String(new char[inicio.length()]).replace("\0", "█ "));
                                    }
                                    if (fin.length() > 2) {
                                        textoNuevo = textoNuevo.replaceAll("(?i)" + fin, new String(new char[fin.length()]).replace("\0", "█ "));
                                    }
                                    textoNuevo = textoNuevo.replaceAll("(?i)" + word, n);
                                    texto.setText(textoNuevo);
                                    pantalla.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
                                    pantalla.setVisibility(View.VISIBLE);


                                    trig = true;
                                    amd = true;
                                }
                            }
                        });
                    } else {
                    }
                } catch (IOException e) {
                    Log.e("syso", "Exception caught: ", e);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page")
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2.connect();
        AppIndex.AppIndexApi.start(client2, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();
        AppIndex.AppIndexApi.end(client2, getIndexApiAction());
        client2.disconnect();
    }
}
