package com.example.ollie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


import com.orbotix.Ollie;
import com.orbotix.Sphero;
import com.orbotix.joystick.api.JoystickEventListener;
import com.orbotix.joystick.api.JoystickView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Vue
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

        // Setup joystick view and event
        setupJoystick();


    }

    private void setupJoystick() {
        // récupère le joystick
        final JoystickView joystick = findViewById(R.id.joystick);

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
            }

            @Override
            public void onJoystickEnded() {
                // Here you can do something when the user stops touching the joystick. For example, we'll make it stop driving.
                //_connectedRobot.stop();
                System.out.println("onJoystickEnded");
            }
        });

    }

}
