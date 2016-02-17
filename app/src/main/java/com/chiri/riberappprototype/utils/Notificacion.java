package com.chiri.riberappprototype.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Chiri on 14/11/2015.
 */
public class Notificacion {

    private String titulo;
    private String descripcion;
    private String date;
    private String link;
    private String twitter;
    private boolean read;
    private float rango;

    public Notificacion(String titulo, String descripcion, String date, String link, String twitter, boolean read, float rango) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.date = date;
        this.link = link;
        this.twitter = twitter;
        this.read = read;
        this.rango = rango;
    }

    public Notificacion(JSONObject object) throws JSONException {
        this.titulo = object.getString("titulo");
        this.descripcion = object.getString("descripcion");
        this.date = object.getString("fecha");
        this.link = object.getString("joomla_url");
        this.twitter = object.getString("twitter_id");
        this.read = false;
        if(object.getString("rango").equals("null")) this.rango=0;
        else this.rango = (float)object.getDouble("rango");
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public float getRango() {
        return rango;
    }

    public void setRango(float rango) {
        this.rango = rango;
    }
}
