package fr.ylecuyer.easitp;

import android.location.Location;
import android.os.Debug;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.rest.spring.annotations.RestService;

import java.util.ArrayList;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

@EActivity(R.layout.activity_puntos_recargas)
public class PuntosRecargas extends AppCompatActivity implements OnLocationUpdatedListener, OnMapReadyCallback {

    @RestService
    MyRestClient myRestClient;

    private GoogleMap map;

    private ArrayList<TuLLave> puntos = new ArrayList<TuLLave>();

    @ViewById(R.id.listView)
    public ListView listview;

    private TuLlaveAdapter adapter;

    @AfterViews
    void init() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        SmartLocation.with(this).location()
                .start(this);

        adapter = new TuLlaveAdapter(this);
        listview.setAdapter(adapter);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
    }

    @Override
    public void onLocationUpdated(Location location) {

        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 14));

        downloadPuntos(position);
    }

    @Background
    public void downloadPuntos(LatLng position) {
        puntos = myRestClient.getPuntos(position.latitude, position.longitude);
        updateDisplay();
    }

    @UiThread
    void updateDisplay() {

        map.clear();

        for(TuLLave punto : puntos) {
            Log.d("EASITP", "Adding marker");
            MarkerOptions marker = new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.tullave_marker))
                    .anchor(0.5f, 0.5f)
                    .position(new LatLng(punto.getLatitude(), punto.getLongitude()))
                    .title(punto.getName());
            map.addMarker(marker);
        }

        adapter.setPuntos(puntos);
    }
}
