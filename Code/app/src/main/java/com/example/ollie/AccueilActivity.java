package com.example.ollie;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.ollie.model.RobotHandler;
import com.example.ollie.model.VirtualOllie;
import com.orbotix.common.DiscoveryAgent;
import com.orbotix.common.DiscoveryAgentEventListener;
import com.orbotix.common.DiscoveryException;
import com.orbotix.common.Robot;
import com.orbotix.common.RobotChangedStateListener;
import com.orbotix.le.DiscoveryAgentLE;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccueilActivity extends BaseActivity  implements DiscoveryAgentEventListener, RobotChangedStateListener {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 42;
    private DiscoveryAgent discoverRobot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);
    }

    /*
     * Tente d'accéder au bluetooth en faisant une demande utilisateur
     */
    private boolean accessBluetooth() {
        if(PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == -1) {
            String[] permissions = new String[1];
            permissions[0] = (Manifest.permission.ACCESS_COARSE_LOCATION);
            requestPermissions(permissions, REQUEST_CODE_LOCATION_PERMISSION);
            return false;
        }

        return true;
    }

    /*
     * Méthode commancant à chercher les robots disponibles à l'utilisation
     */
    private void setUpDiscovery() {
        try {
            discoverRobot = DiscoveryAgentLE.getInstance();
            discoverRobot.addDiscoveryListener(this);
            discoverRobot.addRobotStateListener(this);
            discoverRobot.startDiscovery(this);
        } catch (DiscoveryException e) {
            System.out.println("Recherche Ollie: Prbl lors de la recherche du robot!!" + e.getMessage());
            System.out.println("ERREUR  " + e.getMessage());
            e.printStackTrace();
        }

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
        if(RobotHandler.getRobot() != null) {
            goToActivity(JoystickActivity.class, new HashMap<String, Serializable>());
        } else {
            System.out.println("Robot non connecté, action impossible");
        }
    }

    /*
     * Quand l'utilisateur choisit le pad
     */
    public void padClick(View view) {
        if(RobotHandler.getRobot() != null) {
            goToActivity(PadActivity.class, new HashMap<String, Serializable>());
        } else {
            System.out.println("Robot non connecté, action impossible");
        }
    }

    /*
     * Quand l'utilisateur choisit la connection
     */
    public void connectionClick(View view) {
        if(accessBluetooth()) {
            setUpDiscovery();
        }

    }

    @Override
    public void handleRobotsAvailable(List<Robot> list) {
        discoverRobot.connect(list.get(0));
    }

    @Override
    public void handleRobotChangedState(Robot robot, RobotChangedStateNotificationType type) {
        switch (type) {
            case Online:
                discoverRobot.stopDiscovery();
                RobotHandler.setRobot(new VirtualOllie(robot));

            case Disconnected:
                // On vérifie que ça soit le bon robot qui se soit déconnecté
                if (robot == RobotHandler.getRobot()) {
                    RobotHandler.deleteRobot();

                    try {
                        discoverRobot.startDiscovery(this);
                    } catch (DiscoveryException e) {
                        e.printStackTrace();
                    }
                }

        }
    }

    /*
     * Appelez lorsque l'utilisateur a répondu à la demande de permission
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // on ne demande la permission que pour la Location, grantResult contient 1 resultat
        if(requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
            setUpDiscovery();
        } else {
            Log.d(permissions[0], "Acces refusé");
        }


    }
}
