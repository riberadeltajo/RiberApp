package com.chiri.riberappprototype;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class ActivityNotificacion extends AppCompatActivity implements View.OnClickListener{

    public static final String DATE = "date";
    public static final String TITLE = "title";
    public static final String DESRIPTION = "description";
    public static final String LINK_WEB = "link";
    public static final String ID_TWITTER = "twitter";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacion_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        bar.setIcon(R.drawable.logo);
        bar.setTitle("Noticia");
        bar.setDisplayHomeAsUpEnabled(true);


        TextView titulo = (TextView)findViewById(R.id.textView_title);
        TextView date = (TextView)findViewById(R.id.textView_date);
        TextView description = (TextView) findViewById(R.id.textView_descripcion);

        //Botones

        FloatingActionButton fabTwitter = (FloatingActionButton)findViewById(R.id.boton_twitter);
        FloatingActionButton fabWeb = (FloatingActionButton)findViewById(R.id.boton_web);


        //Hace visibles los botones si tiene link a twitter o web


        if(!getIntent().getStringExtra(LINK_WEB).equals("")){
            fabWeb.setVisibility(View.VISIBLE);
        }

        if(!getIntent().getStringExtra(ID_TWITTER).equals("")){
            fabTwitter.setVisibility(View.VISIBLE);
        }

        description.setMovementMethod(new ScrollingMovementMethod());


        System.out.println(titulo);
        date.setText(getIntent().getStringExtra(DATE));
        titulo.setText(getIntent().getStringExtra(TITLE));
        description.setText(getIntent().getStringExtra(DESRIPTION));

    }

    @Override
    public void onClick(View v) {
        Intent i;

        switch (v.getId()){
            case R.id.boton_twitter:
                try {
                    // si existe app twitter

                    System.out.println("Accediendo a twitter" + "https://twitter.com/iesriberatajo/statuses/" + getIntent().getStringExtra(ID_TWITTER));
                    this.getPackageManager().getPackageInfo("com.twitter.android", 0);
                    i = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://status?status_id=" + getIntent().getStringExtra(ID_TWITTER)));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                } catch (Exception e) {
                    // Si noe xiste la app de twitter abre navegador
                    i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/iesriberatajo/statuses/" + getIntent().getStringExtra(ID_TWITTER)));
                }
                startActivity(i);
                break;


            case R.id.boton_web:

                String url = getIntent().getStringExtra(LINK_WEB);
                Intent inUrl = new Intent(Intent.ACTION_VIEW);
                inUrl.setData(Uri.parse(url));
                startActivity(inUrl);
                break;

            case R.id.boton_share:


                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT,getResources().getString(R.string.share_header) + getIntent().getStringExtra(TITLE));
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, "Compartir vía..."));

                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notification_menu, menu);

        return true;
    }
}
