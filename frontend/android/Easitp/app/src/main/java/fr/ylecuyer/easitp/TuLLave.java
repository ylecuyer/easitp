package fr.ylecuyer.easitp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TuLLave {

    @JsonProperty
    double latitude;

    @JsonProperty
    double longitude;

    @JsonProperty
    String name;

    @JsonProperty
    String address;

    @JsonProperty
    String horario_week;

    @JsonProperty
    String horario_sabado;

    @JsonProperty
    String horario_domingo_festivo;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHorario_week() {
        return horario_week;
    }

    public void setHorario_week(String horario_week) {
        this.horario_week = horario_week;
    }

    public String getHorario_sabado() {
        return horario_sabado;
    }

    public void setHorario_sabado(String horario_sabado) {
        this.horario_sabado = horario_sabado;
    }

    public String getHorario_domingo_festivo() {
        return horario_domingo_festivo;
    }

    public void setHorario_domingo_festivo(String horario_domingo_festivo) {
        this.horario_domingo_festivo = horario_domingo_festivo;
    }

    @Override
    public String toString() {
        return "TuLLave{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", horario_week='" + horario_week + '\'' +
                ", horario_sabado='" + horario_sabado + '\'' +
                ", horario_domingo_festivo='" + horario_domingo_festivo + '\'' +
                '}';
    }
}


