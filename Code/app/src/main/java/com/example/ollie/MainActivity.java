package com.example.ollie;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
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

import java.util.List;


public class MainActivity extends AppCompatActivity implements DiscoveryAgentEventListener,
                                                                RobotChangedStateListener{

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
        //requestPermissions(getApplicationContext(), new String[]{Manifest.permission.BLUETOOTH}, 1);

        // Setup joystick view and event
        setupJoystick();

    }

    private void setUpDiscovery() {
        try {
            discoverRobot = DiscoveryAgentLE.getInstance();
            discoverRobot.addDiscoveryListener(this);
            discoverRobot.addRobotStateListener(this);
            discoverRobot.startDiscovery(this);
        } catch (DiscoveryException e) {
            Log.d("Recherche Ollie", "Prbl lors de la recherche du robot!!" + e.getMessage());
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
                discoverRobot.stopDiscovery();
                joystick.setEnabled(true);
                ollie = new Ollie(robot);
            case Disconnected:
                ollie = null;
                joystick.setEnabled(false);
                try {
                    discoverRobot.startDiscovery(this);
                } catch (DiscoveryException e) {
                    e.printStackTrace();
                }
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
                ollie.drive((float) angle, (float) distanceFromCenter);
            }

            @Override
            public void onJoystickEnded() {
                // Here you can do something when the user stops touching the joystick. For example, we'll make it stop driving.
                //_connectedRobot.stop();
                System.out.println("onJoystickEnded");
                ollie.stop();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Si access au bluetooth
        //setUpDiscovery();
    }
}
