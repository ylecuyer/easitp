package fr.ylecuyer.easitp;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.Debug;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.annotations.sharedpreferences.SharedPref;
import org.androidannotations.rest.spring.annotations.RestService;
import org.w3c.dom.Text;

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
    private ProgressDialog progressDialog;

    @ViewById(R.id.emptyview)
    TextView emptyview;

    @Pref
    MyPrefs_ prefs;

    @AfterViews
    void init() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        SmartLocation.with(this).location()
                .start(this);

        listview.setEmptyView(emptyview);

        adapter = new TuLlaveAdapter(this);
        listview.setAdapter(adapter);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        //Show bogota
        LatLng position = new LatLng(4.66956771,-74.07378649);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 12));

        map.setMyLocationEnabled(true);

        progressDialog = ProgressDialog.show(this, "", "Buscando su ubicacion", true, false);
    }

    @Override
    public void onLocationUpdated(Location location) {

        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 14));

        progressDialog.dismiss();

        progressDialog = ProgressDialog.show(this, "", "Un momento", true, false);

        downloadPuntos(position);
    }

    @Background
    public void downloadPuntos(LatLng position) {

        puntos = myRestClient.getPuntos(position.latitude, position.longitude, prefs.distance_tullave().get());

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
        progressDialog.dismiss();
    }
}
