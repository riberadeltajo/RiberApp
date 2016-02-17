package com.chiri.riberappprototype.location;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.chiri.riberappprototype.R;

/**
 * Created by Chiri on 10/02/2016.
 */
public class DialogRequestPermissions extends AppCompatActivity {

    public static final int REQUEST_CODE_LOCATION = 0x0000;


    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        setContentView(R.layout.dialog_request_permissions_layout);
    }



    @Override
    public void onStart() {
        super.onStart();
        Log.i("PERMISSSIONS", "ON START");

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {


        if(requestCode==REQUEST_CODE_LOCATION
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                //Deseos concedidos
                Log.i("PERMISSSIONS", "PERMISOS CONCEDIDOS");

        }else{
            TextView text = (TextView) findViewById(R.id.text_aviso);
            text.setText("Si no aceptas los permisos de localización no podrás recibir notificaciones");

        }
    }
}
