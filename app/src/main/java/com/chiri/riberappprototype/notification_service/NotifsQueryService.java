package com.chiri.riberappprototype.notification_service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.chiri.riberappprototype.DbManager;
import com.chiri.riberappprototype.R;

public class NotifsQueryService extends Service {


    private static String TAG = "NotifsQueryService";
    private DbManager db;

    public NotifsQueryService() {
        db = new DbManager(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //TODO Log.i(TAG, " iniciado.");
        //Consigue la fecha de la ultima notificacion
        QueryNewNotifsAsyncTask query = new QueryNewNotifsAsyncTask(this);
        query.execute(getString(R.string.url_get_notif_by_date) + db.getLastDate().replace(" ", "+"));
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
