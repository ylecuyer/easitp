package fr.ylecuyer.easitp;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

@EActivity(R.layout.activity_where)
public class WhereActivity extends AppCompatActivity implements OnLocationUpdatedListener, OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private GoogleMap map;
    private LatLng position;
    private LatLng destino;

    @Click(R.id.button)
    public void goToSitpCercanos() {

        SitpCercanos_.intent(this)
                .extra("destino", destino)
                .extra("position", position)
        .start();

    }

    @AfterViews
    void init() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        SmartLocation.with(this).location().start(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        //Show bogota
        LatLng position = new LatLng( 4.66956771,-74.07378649);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 12));

        map.setMyLocationEnabled(true);
        map.setOnMarkerDragListener(this);
    }

    @Override
    public void onLocationUpdated(Location location) {

        position = new LatLng(location.getLatitude(), location.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 14));

        MarkerOptions markerOption = new MarkerOptions()
                .position(position)
                .draggable(true);
        
        map.addMarker(markerOption);
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        //NOP
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        //NOP
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        destino = marker.getPosition();
    }
}
