package com.example.ollie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.example.ollie.model.Path;
import com.example.ollie.model.Position;
import com.example.ollie.model.RobotHandler;
import com.orbotix.ConvenienceRobot;

import java.util.List;
import java.util.Timer;

public class PadActivity extends BaseActivity {
    private ConvenienceRobot ollie = RobotHandler.getRobot();
    private Path path;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pad);

        // Configuration du path
        path = new Path();

        findViewById(R.id.padView).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                interpretMotion(event);

                return true;
            }
        });


    }

    private void interpretMotion(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {

        }

        // Si on enlève notre doigt de l'écran
        if (event.getAction() == MotionEvent.ACTION_UP) {

            moveRobot(path);

            path.clear();

            return;
        }

        // Sinon on ajoute la position du doigt dans le tableau de positions
        Position pos = new Position(event.getX(), event.getY());

        path.addPosition(pos);

    }

    private void moveRobot(Path path) {
        List<Float> distances = path.getDistances();
        List<Float> angles = path.getAngles();

        for(int i=0; i<angles.size(); i++) {
            ollie.drive(angles.get(i), distances.get(i));
        }

    }
}
