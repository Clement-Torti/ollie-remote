package com.example.ollie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AccueilActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);
    }


    /*
     * Quand l'utilisateur choisit la page infos
     */
    public void infoClick(View view) {
        goToActivity(InfosActivity.class, new HashMap<String, Serializable>());
    }

    /*
     * Quand l'utilisateur choisit le joystick
     */
    public void joystickClick(View view) {
        goToActivity(JoystickActivity.class, new HashMap<String, Serializable>());
    }

    /*
     * Quand l'utilisateur choisit le pad
     */
    public void padClick(View view) {
        goToActivity(PadActivity.class, new HashMap<String, Serializable>());
    }

    /*
     * Quand l'utilisateur choisit la connection
     */
    public void connectionClick(View view) {
        goToActivity(ConnectionActivity.class, new HashMap<String, Serializable>());
    }
}
