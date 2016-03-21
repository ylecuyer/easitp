package fr.ylecuyer.easitp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Station {

    @JsonProperty
    double latitude;

    @JsonProperty
    double longitude;

    @JsonProperty
    String address;

    @JsonProperty
    String station;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }
}
