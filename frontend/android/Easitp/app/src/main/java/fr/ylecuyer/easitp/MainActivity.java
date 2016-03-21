package fr.ylecuyer.easitp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @Click(R.id.puntos_recarga)
    void goToPuntosRecarga() {
        PuntosRecargas_.intent(this).start();
    }

    @Click(R.id.sitp_cercanos)
    void goToWhere() {
        WhereActivity_.intent(this).start();
    }
}
