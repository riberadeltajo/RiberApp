package com.chiri.riberappprototype.notification_service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.chiri.riberappprototype.MainActivity;
import com.chiri.riberappprototype.R;
import com.chiri.riberappprototype.location.InRadiusChecker;
import com.chiri.riberappprototype.utils.Notificacion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chiri on 25/11/2015.
 */
public class QueryNewNotifsAsyncTask extends AsyncTask<String, Void, Void> {

    public static final String TAG = "QueryNewNotifAsyncTask";

    private static int notificationId = 0x000001;

    Context context;

    JSONArray objects;


    public QueryNewNotifsAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        InputStream inputStream = null;
        objects = new JSONArray();

        //Log.i(TAG, "Buscando nuevas notificaciones");


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

            String line = null;
            while ((line = bReader.readLine()) != null) {
                sBuilder.append(line + "\n");
            }

            objects = new JSONArray(sBuilder.toString());

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
        try {

            Notificacion[] notificaciones = new Notificacion[objects.length()];

            for (int i = 0; i <= objects.length() - 1; i++) {

                JSONObject object = objects.getJSONObject(i);
                notificaciones[i] = new Notificacion(object);
            }
            notifyNotification(notificaciones);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    private void notifyNotification(Notificacion[] notificacions){

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Comprobacion localizacion
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        InRadiusChecker checker = new InRadiusChecker(this.context);
        String location = pref.getString(context.getResources().getString(R.string.options_location_key), context.getResources().getString(R.string.no_notif));


        if(location.equals(context.getResources().getString(R.string.no_notif)) || //Si Activado no recibir notificaciones
                (location.equals(context.getResources().getString(R.string.only_center)) && !checker.isInsideRadius())){ // O solo en el centro y no esta en el
            return;
        }

        //Recorre las notificaciones, comprobando el radio establecido por mediahub

        List<Notificacion> notifsList = new ArrayList<>();

        for(Notificacion notIt:notificacions){
            if(checker.isInsideRadius(notIt.getRango())){
                notifsList.add(notIt);
            }
        }


        //En caso que solo haya una notificacion nueva
        if(notifsList.size()==1) {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this.context);

            builder.setContentTitle("Noticias IES Ribera del Tajo")
                    .setContentText(notifsList.get(0).getTitulo())
                    .setSmallIcon(R.drawable.logo)
                    .setAutoCancel(true);

            Intent intent = new Intent(context, MainActivity.class);

            PendingIntent resultPendingIntent = PendingIntent.getActivity(this.context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);


            mNotificationManager.notify(notificationId, builder.build());

        //En caso de varias notificaciones
        }else if(notifsList.size()>1){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this.context);

            builder.setContentTitle("Noticias IES Ribera del Tajo")
                    .setContentText(" Tiene " + notifsList.size() + " nuevas noticias ")
                    .setSmallIcon(R.drawable.logo)
                    .setAutoCancel(true);

            Intent intent = new Intent(context, MainActivity.class);

            PendingIntent resultPendingIntent = PendingIntent.getActivity(this.context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);

            mNotificationManager.notify(notificationId, builder.build());
        }
    }
}
