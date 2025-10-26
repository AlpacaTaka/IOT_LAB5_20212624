package com.example.lab5_20212624;

import java.io.Serializable;

public class Curso implements Serializable {

    private String nombre;
    private String categoria; // obligatorio, electivo, laboratorio, otros
    private int frecuenciaHoras; // cada X horas
    private int frecuenciaDias; // cada X días
    private String fechaInicioProximaSesion; // formato: "dd/MM/yyyy"
    private String horaInicioProximaSesion; // formato: "HH:mm"
    private String accionSugerida; // acción a realizar
    private long proximaNotificacionTimestamp; // timestamp para notificación
    private boolean notificacionActiva;


    public Curso() {
        this.notificacionActiva = true;
    }


    public Curso(String nombre, String categoria, int frecuenciaHoras, int frecuenciaDias,
                 String fechaInicioProximaSesion, String horaInicioProximaSesion, String accionSugerida) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.frecuenciaHoras = frecuenciaHoras;
        this.frecuenciaDias = frecuenciaDias;
        this.fechaInicioProximaSesion = fechaInicioProximaSesion;
        this.horaInicioProximaSesion = horaInicioProximaSesion;
        this.accionSugerida = accionSugerida;
        this.notificacionActiva = true;
        this.proximaNotificacionTimestamp = 0;
    }

    // Getters
    public String getNombre() {
        return nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public int getFrecuenciaHoras() {
        return frecuenciaHoras;
    }

    public int getFrecuenciaDias() {
        return frecuenciaDias;
    }

    public String getFechaInicioProximaSesion() {
        return fechaInicioProximaSesion;
    }

    public String getHoraInicioProximaSesion() {
        return horaInicioProximaSesion;
    }

    public String getAccionSugerida() {
        return accionSugerida;
    }

    public long getProximaNotificacionTimestamp() {
        return proximaNotificacionTimestamp;
    }

    public boolean isNotificacionActiva() {
        return notificacionActiva;
    }

    // Setters
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setFrecuenciaHoras(int frecuenciaHoras) {
        this.frecuenciaHoras = frecuenciaHoras;
    }

    public void setFrecuenciaDias(int frecuenciaDias) {
        this.frecuenciaDias = frecuenciaDias;
    }

    public void setFechaInicioProximaSesion(String fechaInicioProximaSesion) {
        this.fechaInicioProximaSesion = fechaInicioProximaSesion;
    }

    public void setHoraInicioProximaSesion(String horaInicioProximaSesion) {
        this.horaInicioProximaSesion = horaInicioProximaSesion;
    }

    public void setAccionSugerida(String accionSugerida) {
        this.accionSugerida = accionSugerida;
    }

    public void setProximaNotificacionTimestamp(long proximaNotificacionTimestamp) {
        this.proximaNotificacionTimestamp = proximaNotificacionTimestamp;
    }

    public void setNotificacionActiva(boolean notificacionActiva) {
        this.notificacionActiva = notificacionActiva;
    }

    public String getFrecuenciaTexto() {
        if (frecuenciaDias > 0) {
            return "Cada " + frecuenciaDias + (frecuenciaDias == 1 ? " día" : " días");
        } else if (frecuenciaHoras > 0) {
            return "Cada " + frecuenciaHoras + (frecuenciaHoras == 1 ? " hora" : " horas");
        }
        return "Sin frecuencia definida";
    }

    public String getProximaSesionTexto() {
        if (fechaInicioProximaSesion != null && horaInicioProximaSesion != null) {
            return fechaInicioProximaSesion + " a las " + horaInicioProximaSesion;
        }
        return "Fecha no definida";
    }

    public String getCanalNotificacion() {
        switch (categoria.toLowerCase()) {
            case "teorico":
            case "teórico":
                return MainActivity.CHANNEL_TEORICOS;
            case "laboratorio":
                return MainActivity.CHANNEL_LABORATORIOS;
            case "electivo":
                return MainActivity.CHANNEL_ELECTIVOS;
            default:
                return MainActivity.CHANNEL_OTROS;
        }
    }

    public String getCategoriaEmoji() {
        switch (categoria.toLowerCase()) {
            case "teorico":
            case "teórico":
                return "📚";
            case "laboratorio":
                return "🔬";
            case "electivo":
                return "⭐";
            case "obligatorio":
                return "📖";
            default:
                return "📝";
        }
    }

    @Override
    public String toString() {
        return "Curso{" +
                "nombre='" + nombre + '\'' +
                ", categoria='" + categoria + '\'' +
                ", frecuencia=" + getFrecuenciaTexto() +
                ", proximaSesion=" + getProximaSesionTexto() +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Curso curso = (Curso) obj;
        return nombre != null ? nombre.equals(curso.nombre) : curso.nombre == null;
    }

    @Override
    public int hashCode() {
        return nombre != null ? nombre.hashCode() : 0;
    }
}