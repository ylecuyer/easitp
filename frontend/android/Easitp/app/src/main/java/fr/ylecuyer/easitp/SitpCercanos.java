package fr.ylecuyer.easitp;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.rest.spring.annotations.RestService;

@EActivity(R.layout.activity_sitp_cercanos)
public class SitpCercanos extends AppCompatActivity  implements OnMapReadyCallback, AdapterView.OnItemClickListener {

    private GoogleMap map;

    @RestService
    MyRestClient myRestClient;

    @Extra("position")
    public LatLng start;

    @Extra("destino")
    public LatLng destino;

    private SitpList lines;
    private LineAdapter adapter;

    @ViewById
    public GridView gridView;

    private StationList stations;

    @AfterViews
    void init() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        adapter = new LineAdapter(this);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);

        map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.start_marker))
                .anchor(0.5f, 0.5f).position(start).title("incio"));
        map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.destino_marker))
                .anchor(0.5f, 0.5f).position(destino).title("destino"));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(destino);
        builder.include(start);

        LatLngBounds bounds = builder.build();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100);

        map.moveCamera(cameraUpdate);

        downloadLines(start, destino);
    }

    @Background
    public void downloadLines(LatLng position, LatLng destino) {
        lines = myRestClient.getLines(position.latitude, position.longitude, destino.latitude, destino.longitude);
        updateDisplay();
    }

    @UiThread
    void updateDisplay() {
        adapter.setLines(lines);
    }

    @Background
    public void downloadStations(long id) {
        stations = myRestClient.getStations(id);
        updateMap();
    }

    @UiThread
    public void updateMap() {
        map.clear();

        map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.start_marker))
                .anchor(0.5f, 0.5f).position(start).title("incio"));
        map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.destino_marker))
                .anchor(0.5f, 0.5f).position(destino).title("destino"));


        for (Station station : stations) {
            map.addMarker(new MarkerOptions().position(new LatLng(station.getLatitude(), station.getLongitude())).title(station.getStation()));
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        downloadStations(l);
    }
}


