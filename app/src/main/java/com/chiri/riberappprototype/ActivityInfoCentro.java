package com.chiri.riberappprototype;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by Chiri on 18/12/2015.
 */
public class ActivityInfoCentro extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_info_centro_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        bar.setIcon(R.drawable.logo);
        bar.setTitle("Información del Centro");
        bar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onClick(View v) {

        Intent i;
        switch (v.getId()){

            case R.id.boton_info_web:
                String url = "http://www.riberadeltajo.es";
                i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;

            case R.id.boton_info_twitter:
                try {
                    // si existe app twitter
                    this.getPackageManager().getPackageInfo("com.twitter.android", 0);
                    i = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=397908242"));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                } catch (Exception e) {
                    // Si noe xiste la app de twitter abre navegador
                    i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/intent/user?user_id=397908242"));
                }
                startActivity(i);
                break;

            case R.id.boton_info_mail:

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","45005471.ies@edu.jccm.es", null));
                startActivity(Intent.createChooser(emailIntent, "Mandar e-mail..."));

                break;



        }

    }
}
