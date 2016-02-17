package com.chiri.riberappprototype.location;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.chiri.riberappprototype.R;

/**
 * Created by Chiri on 23/01/2016.
 */
public class InRadiusChecker implements LocationListener {

    private Context context;
    private Location center;
    private float radius;

    public static final int NOTIFICATION_ID = 0x0000004;

    LocationManager locManager;

    public static final String TAG = "InRadiusChecker";

    public InRadiusChecker(Context context) {
        this.context = context;
        this.center = new Location("");
        this.center.setLatitude(Double.parseDouble(context.getResources().getString(R.string.latitude)));
        this.center.setLongitude(Double.parseDouble(context.getResources().getString(R.string.longitude)));

        locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

    }


    public boolean isInsideRadius() {


        if(context.getPackageManager().checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, context.getPackageName()) != PackageManager.PERMISSION_GRANTED
                && context.getPackageManager().checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, context.getPackageName()) != PackageManager.PERMISSION_GRANTED) {

            notifyRequestPermissions();
            return false;
        }else{
            locManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
            Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(loc==null) return false;
            float radius = Float.parseFloat(context.getResources().getString(R.string.radius));

            Log.i(TAG, String.format("Chequeando radio...\nCentro establecido en: %s\nLocalizacion actual en: %s Radio establecido: %f\n Distancia actual: %f \n  Es menor? %b \n",
                    this.center.toString(), loc.toString(), radius, this.center.distanceTo(loc), this.center.distanceTo(loc) <= radius));


            return this.center.distanceTo(loc) <= radius;
        }

    }


    public boolean isInsideRadius(float radius) {

        if(context.getPackageManager().checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, context.getPackageName()) != PackageManager.PERMISSION_GRANTED
                && context.getPackageManager().checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, context.getPackageName()) != PackageManager.PERMISSION_GRANTED){

            notifyRequestPermissions();
            return false;
        }else {


            locManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
            Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


            if(loc==null) return false;

            Log.i(TAG, String.format("Chequeando radio...\nCentro establecido en: %s\nLocalizacion actual en: %s \n Radio establecido: %f\n Distancia actual: %f \n Es menor? %b \n",
                    this.center.toString(), loc.toString(), radius, this.center.distanceTo(loc), this.center.distanceTo(loc) <= radius));


            return radius==0 || this.center.distanceTo(loc) <= radius;
        }


    }

    private void notifyRequestPermissions(){
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.context);

        builder.setContentTitle("RiberApp necesita permisos de localización para recibir notificaciones")
                .setContentText("Pulsa aquí para aceptar permisos")
                .setSmallIcon(R.drawable.logo)
                .setAutoCancel(true);

        Intent intent = new Intent(context, DialogRequestPermissions.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this.context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "Se ha cambiado la localización: " + location.toString());

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }




}
