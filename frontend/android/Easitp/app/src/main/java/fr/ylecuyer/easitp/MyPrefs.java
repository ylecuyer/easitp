package fr.ylecuyer.easitp;

import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref(SharedPref.Scope.UNIQUE)
public interface MyPrefs {

    @DefaultInt(1000)
    int distance_tullave();

    @DefaultInt(200)
    int distance_from_sitp();

    @DefaultInt(500)
    int distance_destino_sitp();

}
