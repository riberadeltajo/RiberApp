package com.chiri.riberappprototype;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.Toast;

import com.chiri.riberappprototype.location.InRadiusChecker;
import com.chiri.riberappprototype.utils.Notificacion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by Chiri on 11/11/2015.
 */
public class QueryNotifsUpdateUiAsyncTask extends AsyncTask<String, Void, Void> {

    private String TAG = "HTTP-AsyncTask";
    private JSONArray objects;

    DbManager db;
    NotifAdapterRecycler notifAdapter;
    SwipeRefreshLayout swipe;
    Context context;

    //Si ha conseguido establecer conexion con el servidor
    private boolean connected;

    public QueryNotifsUpdateUiAsyncTask(DbManager db, Context context, NotifAdapterRecycler notifAdapter){
        this.db = db;
        this.notifAdapter = notifAdapter;
        this.context = context;
    }

    public QueryNotifsUpdateUiAsyncTask(DbManager db, Context context, NotifAdapterRecycler notifAdapter, SwipeRefreshLayout swipe){
        this(db,context, notifAdapter);
        this.swipe = swipe;
        this.swipe.setRefreshing(true);
    }

    @Override
    protected Void doInBackground(String... params) {

        InputStream inputStream = null;
        objects = new JSONArray();

        //Suponemos que establece conexion, se establece a falso en caso de error
        connected = true;

        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("mediahub", "tenorio45".toCharArray());
            }
        });

        try {
            URL url = new URL(params[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setUseCaches(false);


            //Conecta
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(TAG, "The response is: " + response);
            inputStream = conn.getInputStream();

            // Lee el contenido
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
            StringBuilder sBuilder = new StringBuilder();

            String line;
            while ((line = bReader.readLine()) != null) {
                sBuilder.append(line + "\n");
            }



            objects = new JSONArray(new String(sBuilder.toString().getBytes(), "UTF-8"));

        }catch(SocketTimeoutException e) {
            connected = false;
        } catch (JSONException | IOException e) {
            e.printStackTrace();

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //Lee todas las notificaciones a partir de una fecha a ver si hay nuevas
        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {



        if(connected) {//Si ha conectado con el servidor

            try {

                Notificacion notifIt;


                //Comprobacion localizacion
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                InRadiusChecker checker = new InRadiusChecker(this.context);

                String location = pref.getString(context.getResources().getString(R.string.options_location_key), context.getResources().getString(R.string.all_notif));

                boolean notificaciones = (location.equals(context.getResources().getString(R.string.no_notif)) || //Si Activado no recibir notificaciones
                        (location.equals(context.getResources().getString(R.string.only_center)) && !checker.isInsideRadius())); // O solo en el centro y no esta en el



                if(!notificaciones) {

                    //Recorre los objetos, creando notificaciones, si está dentro del rango, la guarda en db
                    for (int i = 0; i <= objects.length() - 1; i++) {

                        JSONObject object = objects.getJSONObject(i);


                        notifIt = new Notificacion(object);

                        if (checker.isInsideRadius(notifIt.getRango())) {

                            db.insertNotif(notifIt);

                            notifAdapter.insert(notifIt, 0);

                        }else{
                            //TODO
                            Log.i(TAG, "Se recibió una notificacion pero no cumple requisitos de mediahub");
                        }

                    }
                }else{
                    Log.i(TAG, "Se recibio una notificacion pero no cumple requisitos de la app");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            notifAdapter.notifyDataSetChanged();
        }else{
            Toast.makeText(this.context, "No se ha podido establecer conexión con el servidor, prueba mas tarde", Toast.LENGTH_SHORT).show();
        }

        if(this.swipe!=null) this.swipe.setRefreshing(false);
    }
}
