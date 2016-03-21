package fr.ylecuyer.easitp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Sitp {

    @JsonProperty
    long id;

    @JsonProperty
    String line;

    @JsonProperty
    String destino;

    @JsonProperty("short")
    String days;

    @JsonProperty
    String desde;

    @JsonProperty
    String hasta;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHasta() {
        return hasta;
    }

    public void setHasta(String hasta) {
        this.hasta = hasta;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getDesde() {
        return desde;
    }

    public void setDesde(String desde) {
        this.desde = desde;
    }

    @Override
    public String toString() {
        return "Sitp{" +
                "line='" + line + '\'' +
                ", destino='" + destino + '\'' +
                ", days='" + days + '\'' +
                ", desde='" + desde + '\'' +
                ", hasta='" + hasta + '\'' +
                '}';
    }
}
