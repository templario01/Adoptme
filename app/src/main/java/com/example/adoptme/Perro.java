package com.example.adoptme;

public class Perro {
    private double lat;
    private double lng;
    private String tamano;
    private String color;
    private String fecha;
    private String hora;
    private String idfoto;

    public Perro(double lat, double lng, String tamano, String color, String fecha, String hora, String idfoto) {
        this.lat = lat;
        this.lng = lng;
        this.tamano = tamano;
        this.color = color;
        this.fecha = fecha;
        this.hora = hora;
        this.idfoto = idfoto;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getTamano() {
        return tamano;
    }

    public void setTamano(String tamano) {
        this.tamano = tamano;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getIdfoto() {
        return idfoto;
    }

    public void setIdfoto(String id) {
        this.idfoto = idfoto;
    }
}
