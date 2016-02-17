package com.chiri.riberappprototype;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.chiri.riberappprototype.notification_service.AlarmSetter;
import com.chiri.riberappprototype.preferences.Preferences;
import com.chiri.riberappprototype.utils.Notificacion;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener{

    DbManager db;
    NotifAdapterRecycler notifAdapter;
    List<Notificacion> notifList;

    SwipeRefreshLayout swipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_layout);

        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        bar.setIcon(R.drawable.logo);

        //Gestor db
        db = new DbManager(this);

        //Consulta las notificaciones antiguas
        notifList = new ArrayList<>();
        Notificacion [] notifArray;
        notifArray = db.getAllNotif();

        for(Notificacion notIt:notifArray){
            notifList.add(notIt);
        }

        //Actualizador
        swipe = (SwipeRefreshLayout)findViewById(R.id.swipe);
        swipe.setOnRefreshListener(this);

        //Set recycler
        notifAdapter = new NotifAdapterRecycler(notifList, db);

        RecyclerView recycler = (RecyclerView)findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(notifAdapter);



    }


    @Override
    protected void onStart() {
        super.onStart();

        //Actualiza listview y db
        updateList();
    }

    @Override
    protected void onStop() {
        super.onStop();

        //Settea la alarma forzando receiver
        Intent intent = new Intent(AlarmSetter.ACTION_INIT);
        sendBroadcast(intent);

        //SetEnbaled receiver
        ComponentName receiver = new ComponentName(this, AlarmSetter.class);
        PackageManager pm = getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        //Cierra la base de datos
        db.close();
    }

    private void updateList() {
        //Actualizamos bbdd
        QueryNotifsUpdateUiAsyncTask con = new QueryNotifsUpdateUiAsyncTask(db, this, notifAdapter);
        con.execute(getString(R.string.url_get_notif_by_date) + db.getLastDate().replace(" ", "+"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.info_centro:
                Intent intent = new Intent(MainActivity.this, ActivityInfoCentro.class);
                startActivity(intent);
                break;
            case R.id.preferences:
                startActivity(new Intent(this, Preferences.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        QueryNotifsUpdateUiAsyncTask con = new QueryNotifsUpdateUiAsyncTask(db, this, notifAdapter, swipe);
        con.execute(getString(R.string.url_get_notif_by_date) + db.getLastDate().replace(" ", "+"));
    }



    @Override
    public void onClick(View v) {

        Intent openNotif = new Intent(v.getContext(), ActivityNotificacion.class);
        openNotif.putExtra(ActivityNotificacion.DATE, ((TextView) v.findViewById(R.id.textview_notificacion_date)).getText());
        openNotif.putExtra(ActivityNotificacion.TITLE, ((TextView) v.findViewById(R.id.textview_notificacion_title)).getText());
        openNotif.putExtra(ActivityNotificacion.DESRIPTION, ((TextView) v.findViewById(R.id.textview_notificacion_description)).getText());
        openNotif.putExtra(ActivityNotificacion.LINK_WEB, ((TextView) v.findViewById(R.id.textview_notificacion_url)).getText());
        openNotif.putExtra(ActivityNotificacion.ID_TWITTER, ((TextView) v.findViewById(R.id.textview_notificacion_twitter)).getText());


        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, new Pair<>(v.findViewById(R.id.textview_notificacion_date),
                        getString(R.string.transition_name_date)),
                new Pair<>(v.findViewById(R.id.textview_notificacion_title),
                        getString(R.string.transition_name_name)));

        ActivityCompat.startActivity(this, openNotif, options.toBundle());

    }

}
