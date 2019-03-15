package com.example.ollie;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.ollie.model.RobotHandler;
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

public class JoystickActivity extends BaseActivity {

    private JoystickView joystick;
    private ConvenienceRobot ollie = RobotHandler.getRobot();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joystick);

        setupJoystick();

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


    // Permet de calibrer le robot, c-a-d lui dire que son orientation actuelle correspond à l'angle 0
    public void calibrationClick(View view) {
        ollie.setZeroHeading();
    }
}
