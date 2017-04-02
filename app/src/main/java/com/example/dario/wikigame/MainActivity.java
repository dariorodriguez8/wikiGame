package com.example.dario.wikigame;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Arrays;

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
    Button btnGenerate,btnCheck,btnClue;
    ImageView imag, vidasIMG;
    EditText editText;
    TextView puntos;
    String result, resultiMAGEN;
    String carpetaFuente = "fonts/LinLibertine_R.ttf";
    String nombre;
    String TYPE_GAME = "TYPE_GAME";
    boolean trig=true;
    boolean amd=true;
    String tipoDeJuego;
    int puntuacion;
    int vidas = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        texto = (TextView) findViewById(R.id.textView2);
        btnGenerate = (Button) findViewById(R.id.button);
        btnCheck = (Button) findViewById(R.id.button2);
        btnClue = (Button) findViewById(R.id.button3);
        imag = (ImageView) findViewById(R.id.imageView2);
        puntos = (TextView) findViewById(R.id.textView4);
        vidasIMG = (ImageView) findViewById(R.id.vidas);


        editText = (EditText) findViewById(R.id.editText);
        Typeface fuente = Typeface.createFromAsset(getAssets(), carpetaFuente);
        texto.setTypeface(fuente);
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generaPalabra();
            }
        });
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });
        btnClue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(trig & amd) {
                    imag.setVisibility(View.VISIBLE);
                    Picasso.with(getApplicationContext()).load("https:"+resultiMAGEN).into(imag);
                    amd = !amd;
                }
                }
        });


        tipoDeJuego = getIntent().getStringExtra(TYPE_GAME);


        generaPalabra();
    }

    public void validate() {

        String e = editText.getText().toString();

        if(e.toLowerCase().equals(word.toLowerCase())){
            Toast.makeText(this, "Correcto", Toast.LENGTH_SHORT).show();

            if(vidas > 0 && vidas < 6){
                vidas++;
            }

            if (amd){
                puntuacion = puntuacion + 20;
            } else{
                puntuacion = puntuacion + 10;
            }
            puntos.setText("" + puntuacion);
            editText.setText("");
            generaPalabra();
            if(amd){
                Picasso.with(getApplicationContext()).load("https:"+resultiMAGEN).into(imag);
            }

        }else{
            Toast.makeText(this, "Incorrecto", Toast.LENGTH_SHORT).show();
            if(vidas >= 0 && vidas <= 6){
                vidas--;
            }
        }

        comprobarVidas();


    }

    public void comprobarVidas(){
        switch (vidas){
            case 0:

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



    public void generaPalabraAleatoria(){
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
                        Log.v("ooo","el resultado es ------>"+word);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                buscaWiki(word);

                            }
                        });
                    } else {
                    }
                }
                catch (IOException e) {
                    Log.e("syso", "Exception caught: ", e);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public void generaPalabra() {
        trig=false;
        InputStream inputStream=new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        };
        switch (tipoDeJuego){
            case"games":
                inputStream = getResources().openRawResource(R.raw.games);
                break;
            case"football":
                 inputStream = getResources().openRawResource(R.raw.football);
                break;
            case"celebrities":
                 inputStream = getResources().openRawResource(R.raw.celebrities);
                break;
            case"music":
                 inputStream = getResources().openRawResource(R.raw.singers);
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

            int randomnumber = Math.round((float) (Math.random( )*array.length()) -1);

            nombre = array.getJSONObject(randomnumber).getString("title");

            Log.v("eee",nombre);
            buscaWiki(nombre);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void buscaWiki(String w){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch="+w.replaceAll(" ","_")+"&srwhat=text&srprop=timestamp&format=json&continue=")
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
                }
                catch (IOException e) {
                    Log.e("syso", "Exception caught: ", e);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    public void generaWiki(String w){
        imag.setVisibility(View.INVISIBLE);

        Log.v("aaa","el resultado es ------>"+w);
        word=w;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://en.wikipedia.org/wiki/"+w.replaceAll(" ","_"))
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

                         Log.v("syso","el resultado es ------>" + result);
                         Log.v("pepe","el resultado es ------>" + resultiMAGEN);

                     } catch (Exception e){


                     }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.v("www",result);
                                if(result.contains("may refer to:")){
                                    generaPalabra();
                                }
                                else{
                                String textoNuevo = result.replaceAll("\\[[0-9]\\]","");
                                    textoNuevo = textoNuevo.replaceAll("\\(.*?\\)","");
                                    textoNuevo = textoNuevo.replaceAll("\\[.*?\\]","");
                                    String inicio, fin;

                                    String delimitadores= " ";
                                    String[] palabrasSeparadas = word.split(delimitadores);

                                    inicio = palabrasSeparadas[0];
                                    fin = palabrasSeparadas[palabrasSeparadas.length - 1];


                                String vac ="_";
                                    Log.v("ttt", word);
                                    Log.v("ttt", Arrays.toString(palabrasSeparadas));

                               /* for(int i=0;i<word.length();i++){
                                    vac= vac+vac;
                                }*/
                                int a = word.length();
                                String  repeated = new String(new char[a]).replace("\0", "_ ");
                                    String n="";

                                    for(int i =0; i < word.length();i++){
                                    char b = word.charAt(i);
                                    if(b==' '){
                                        n+="  ";
                                    } else{
                                        n+="_ ";
                                    }

                                    }
                                    textoNuevo = textoNuevo.replaceAll("(" + inicio + " .*?" + fin + ")",n);
                                    if(inicio.length()>2) {
                                        textoNuevo = textoNuevo.replaceAll("(?i)" + inicio, new String(new char[inicio.length()]).replace("\0", "_ "));
                                    }
                                    if(fin.length() > 2) {
                                        textoNuevo = textoNuevo.replaceAll("(?i)" + fin, new String(new char[fin.length()]).replace("\0", "_ "));
                                    }
                                textoNuevo = textoNuevo.replaceAll("(?i)"+word,n);
                                texto.setText(textoNuevo);
                                trig=true;
                                    amd=true;
                                }

                            }
                        });
                    } else {
                    }
                }
                catch (IOException e) {
                    Log.e("syso", "Exception caught: ", e);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }
}
