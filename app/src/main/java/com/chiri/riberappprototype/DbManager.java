package com.chiri.riberappprototype;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.chiri.riberappprototype.utils.Notificacion;

import java.text.SimpleDateFormat;

/**
 * Created by Chiri on 13/11/2015.
 */
public class DbManager extends SQLiteOpenHelper{

    public static final String TAG = "DBManager";

    private static final int DATABASE_VERSION = 1;
    private Context context;

    private final String PREF_FILE = "DB";
    private final String PREF_LAST_DATE = "lastdate";

    //Para formatear las fechas
    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");

    public DbManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }


    public long insertNotif(Notificacion notif){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(field_date, notif.getDate());
        values.put(field_titulo, notif.getTitulo());
        values.put(field_descripcion, notif.getDescripcion());
        values.put(field_link, notif.getLink());
        values.put(field_twitter, notif.getTwitter());

        //Actualiza la fecha de ultima actualizacion
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREF_LAST_DATE, notif.getDate());
        editor.commit();

        return db.insert(table_name_notificacion, null, values);
    }

    public Notificacion[] getAllNotif(){

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                field_titulo,
                field_descripcion,
                field_date,
                field_link,
                field_twitter,
                field_read
        };

        String sortOrder =
                field_date + " DESC";

        Cursor c = db.query(
                table_name_notificacion,  // The table to query
                projection,                               //
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        //Recorre el cursor y lo añade al array de notifs

        Notificacion [] notificaciones = new Notificacion[c.getCount()];

        if(c.moveToFirst()){
            notificaciones[c.getPosition()] = new Notificacion(
                    c.getString(c.getColumnIndex(field_titulo)),
                    c.getString(c.getColumnIndex(field_descripcion)),
                    c.getString(c.getColumnIndex(field_date)),
                    c.getString(c.getColumnIndex(field_link)),
                    c.getString(c.getColumnIndex(field_twitter)),
                    Boolean.parseBoolean(c.getString(c.getColumnIndex(field_read))), 0);

            while(c.moveToNext()){


                notificaciones[c.getPosition()] = new Notificacion(
                        c.getString(c.getColumnIndex(field_titulo)),
                        c.getString(c.getColumnIndex(field_descripcion)),
                        c.getString(c.getColumnIndex(field_date)),
                        c.getString(c.getColumnIndex(field_link)),
                        c.getString(c.getColumnIndex(field_twitter)),
                        Boolean.parseBoolean(c.getString(c.getColumnIndex(field_read))), 0);
            }
        }

        return notificaciones;

    }


    public String getLastDate(){

        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, 0);
        Log.i(TAG, "Se consulto la ultima fecha" + settings.getString(PREF_LAST_DATE, "0"));
        return settings.getString(PREF_LAST_DATE, "0");



    }

    public void setRead(Notificacion notif){

        SQLiteDatabase db = getWritableDatabase();
        String where = field_date + "=" + notif.getDate();

        ContentValues values = new ContentValues();
        //values.put(field_read, "Calandraca");

        db.rawQuery("UPDATE notificacion SET read=1 WHERE date='" + notif.getDate() + "';", null);
        //db.update(table_name_notificacion, values, where, null);

    }

    public void deleteNotif(String date){

        SQLiteDatabase db = getWritableDatabase();
        db.delete(table_name_notificacion, field_date + "=?", new String[]{date});

    }

    private static final String DATABASE_NAME = "notificacion";

    private static final String table_name_notificacion = "notificacion";
    private static final String field_date = "date";
    private static final String field_titulo = "titulo";
    private static final String field_descripcion = "descripcion";
    private static final String field_link = "link";
    private static final String field_twitter = "twitter";
    private static final String field_read = "read";

    private static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + table_name_notificacion + "(" +
            field_date + " DATETIME NULL," +
            field_titulo + " VARCHAR NOT NULL," +
            field_descripcion + " TEXT NOT NULL, " +
            field_link + " VARCHAR," +
            field_twitter + " VARCHAR, " +
            field_read + " BOOLEAN NOT NULL DEFAULT FALSE);";


    private static String DROP_TABLE = " DROP TABLE IF EXISTS " + table_name_notificacion + ";";


    private static String SELECT_LAST_DATE = "SELECT MAX(date) FROM notificacion;";


}
