package com.chiri.riberappprototype;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chiri.riberappprototype.utils.Notificacion;

import java.util.List;

/**
 * Created by Chiri on 15/12/2015.
 */
public class NotifAdapterRecycler extends RecyclerView.Adapter<NotifAdapterRecycler.ViewHolder> {


    private List<Notificacion> dataset;
    private DbManager db;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public ViewHolder(View v) {
            super(v);
            this.view = v;
        }
    }


    public NotifAdapterRecycler(List<Notificacion> dataset, DbManager db) {
        this.dataset = dataset;
        this.db = db;
    }


    @Override
    public NotifAdapterRecycler.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_notificacion, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Notificacion notActual = dataset.get(position);
        View v = holder.view;

        final TextView titulo = (TextView)v.findViewById(R.id.textview_notificacion_title);
        final TextView date = (TextView)v.findViewById(R.id.textview_notificacion_date);

        TextView descripcion = (TextView)v.findViewById(R.id.textview_notificacion_description);
        TextView url = (TextView)v.findViewById(R.id.textview_notificacion_url);
        TextView twitter = (TextView)v.findViewById(R.id.textview_notificacion_twitter);


        titulo.setText(notActual.getTitulo());
        date.setText(notActual.getDate());
        descripcion.setText(notActual.getDescripcion());
        url.setText(notActual.getLink());
        twitter.setText(notActual.getTwitter());

        /*Añade listener*/
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openNotif = new Intent(v.getContext(), ActivityNotificacion.class);
                openNotif.putExtra(ActivityNotificacion.DATE, notActual.getDate());
                openNotif.putExtra(ActivityNotificacion.TITLE, notActual.getTitulo());
                openNotif.putExtra(ActivityNotificacion.DESRIPTION, notActual.getDescripcion());
                openNotif.putExtra(ActivityNotificacion.LINK_WEB, notActual.getLink());
                openNotif.putExtra(ActivityNotificacion.ID_TWITTER, notActual.getTwitter());


                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        (Activity)v.getContext(), new Pair<>(v.findViewById(R.id.textview_notificacion_date),
                                v.getContext().getResources().getString(R.string.transition_name_date)),
                        new Pair<>(v.findViewById(R.id.textview_notificacion_title),
                                Resources.getSystem().getString(R.string.transition_name_name)));

                ActivityCompat.startActivity((Activity)v.getContext(), openNotif, options.toBundle());

                //Marca como leido
                DbManager db = new DbManager(v.getContext());
                db.setRead(notActual);
                notActual.setRead(true);

            }
        });


        //Listener a boton eliminer

        ImageButton bRemove = (ImageButton)v.findViewById(R.id.remove_button);
        bRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Eliminar de la base de datos
                db.deleteNotif(notActual.getDate());



                dataset.remove(notActual);
                //Notifica cambio
                notifyDataSetChanged();
            }
        });

    }




    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.size();
    }


    public void insert (Notificacion notif, int position){
        this.dataset.add(position, notif);
    }
}
