package com.example.ollie;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.ollie.model.VirtualOllie;
import com.orbotix.ConvenienceRobot;
import com.orbotix.common.DiscoveryAgent;
import com.orbotix.common.DiscoveryAgentEventListener;
import com.orbotix.common.DiscoveryException;
import com.orbotix.common.Robot;
import com.orbotix.common.RobotChangedStateListener;
import com.orbotix.joystick.api.JoystickEventListener;
import com.orbotix.joystick.api.JoystickView;
import com.orbotix.le.DiscoveryAgentLE;

import java.util.List;

public class JoystickActivity extends BaseActivity implements DiscoveryAgentEventListener,
        RobotChangedStateListener {

    private static int REQUEST_CODE_LOCATION_PERMISSION = 42;
    private JoystickView joystick;
    private DiscoveryAgent discoverRobot;
    private ConvenienceRobot ollie;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joystick);

        // accessBluetooth();
    }



    /*
     * Tente d'accéder au bluetooth en faisant une demande utilisateur
     */
    private void accessBluetooth() {
        if(PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == -1) {
            String[] permissions = new String[1];
            permissions[0] = (Manifest.permission.ACCESS_COARSE_LOCATION);
            requestPermissions(permissions, REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            // On a deja acces au bluetooth, on lance la recherche de robots
            setUpDiscovery();
        }
    }

    /*
     * Méthode commancant à chercher les robots disponbiles à l'utilisation
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

    // Permet de récupérer tous les robots avec lesquels on peut se connecter, on choisi le 1er (Qui doit correspondre au plus proche)
    @Override
    public void handleRobotsAvailable(List<Robot> list) {
        discoverRobot.connect(list.get(0));
    }


    // Lorsque le robot qui nous intéresse change d'état
    @Override
    public void handleRobotChangedState(Robot robot, RobotChangedStateListener.RobotChangedStateNotificationType type) {
        switch (type) {
            case Online:
                discoverRobot.stopDiscovery();
                ollie = new VirtualOllie(robot);
                // Setup joystick view and event
                setupJoystick();
            case Disconnected:
                // On vérifie que ça soit le bon robot qui ce soit déconnecté
                if (robot == ollie.getRobot()) {
                    ollie = null;

                    try {
                        discoverRobot.startDiscovery(this);
                    } catch (DiscoveryException e) {
                        e.printStackTrace();
                    }
                }

        }
    }


    /*
     * Met en place le code pour le fonctionnement du joystickView:
     * La récupération des évènements touchEvent et le traitement associé pour faire avancer le robot
     */
    private void setupJoystick() {
        // récupère le joystick
        joystick = findViewById(R.id.joystick);

        // Redirige les event de la vue vers ce GUI
        findViewById(R.id.entireView).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                joystick.interpretMotionEvent(event);
                return true;
            }
        });

        // Effectue une action en fonction de l'évènement reçu pour le joystick
        joystick.setJoystickEventListener(new JoystickEventListener() {

            @Override
            public void onJoystickBegan() {
            }

            @Override
            public void onJoystickMoved(double distanceFromCenter, double angle) {
                if(ollie != null)
                    ollie.drive((float) angle, (float) distanceFromCenter);
            }

            @Override
            public void onJoystickEnded() {
                if(ollie != null)
                    ollie.stop();
            }
        });

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
