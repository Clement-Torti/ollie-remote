package com.example.ollie;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.textclassifier.TextClassification;
import android.view.textclassifier.TextLinks;
import android.widget.Button;
import android.widget.TextView;


import com.orbotix.ConvenienceRobot;
import com.orbotix.Ollie;
import com.orbotix.Sphero;
import com.orbotix.common.DiscoveryAgent;
import com.orbotix.common.DiscoveryAgentEventListener;
import com.orbotix.common.DiscoveryException;
import com.orbotix.common.Robot;
import com.orbotix.common.RobotChangedStateListener;
import com.orbotix.joystick.api.JoystickEventListener;
import com.orbotix.joystick.api.JoystickView;
import com.orbotix.le.DiscoveryAgentLE;
import com.orbotix.macro.cmd.Calibrate;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements DiscoveryAgentEventListener,
                                                                RobotChangedStateListener{
    private static int REQUEST_CODE_LOCATION_PERMISSION = 42;
    private JoystickView joystick;
    private DiscoveryAgent discoverRobot;
    private ConvenienceRobot ollie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Vue
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

        // Access au bluetooth
        if(PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == -1) {
            String[] permissions = new String[1];
            permissions[0] = (Manifest.permission.ACCESS_COARSE_LOCATION);
            requestPermissions(permissions, REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            setUpDiscovery();
        }


    }

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

    // Permet de récupérer tous les robots avec lesquels on peut se connecter
    @Override
    public void handleRobotsAvailable(List<Robot> list) {
        discoverRobot.connect(list.get(0));
    }


    @Override
    public void handleRobotChangedState(Robot robot, RobotChangedStateNotificationType type) {
        switch (type) {
            case Online:
                System.out.println("Robot online");
                discoverRobot.stopDiscovery();

                ollie = new Ollie(robot);
                // Setup joystick view and event
                setupJoystick();
            case Connected:
                System.out.println("Robot connecté");

            case Disconnected:
                /*
                System.out.println("Robot deconnecté");
                ollie = null;

                try {
                    discoverRobot.startDiscovery(this);
                } catch (DiscoveryException e) {
                    e.printStackTrace();
                }*/
        }
    }

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
                // Here you can do something when the user starts using the joystick.
                System.out.println("onJoystickBegan");
            }

            @Override
            public void onJoystickMoved(double distanceFromCenter, double angle) {
                // Here you can use the joystick input to drive the connected robot. You can easily do this with the
                // ConvenienceRobot#drive() method
                // Note that the arguments do flip here from the order of parameters
                //_connectedRobot.drive((float)angle, (float)distanceFromCenter);
                System.out.println("onJoystickMoved: " + distanceFromCenter + " " + angle);
                if(ollie != null)
                    ollie.drive((float) angle, (float) distanceFromCenter);
            }

            @Override
            public void onJoystickEnded() {
                // Here you can do something when the user stops touching the joystick. For example, we'll make it stop driving.
                //_connectedRobot.stop();
                System.out.println("onJoystickEnded");
                if(ollie != null)
                    ollie.stop();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        System.out.println("onRequest Methods");
        setUpDiscovery();

    }


}
