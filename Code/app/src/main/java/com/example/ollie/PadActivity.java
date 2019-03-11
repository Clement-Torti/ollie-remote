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
import java.util.TimerTask;

// Pour convertir les durées et les distances, nous partons du principe que :
// 10 ms sur le Pad correspond à 100 ms dans la vraie vie ;
// 50 px sur le Pad correspond à 5 cm dans la vraie vie

public class PadActivity extends BaseActivity {

    private int i;
    private static final int VIRTUAL_TIME_DELAY = 10;
    private static final int REAL_TIME_DELAY = 100;

    private ConvenienceRobot ollie = RobotHandler.getRobot();
    private Path path;
    private Timer getMoveTimer;
    private Timer moveTimer;
    private Position lastPosition;
    private TimerTask getMoveTask;
    private TimerTask moveTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pad);

        // Configuration du path
        path = new Path();
        i = 0;

        findViewById(R.id.padView).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                interpretMotion(event);

                return true;
            }
        });

        getMoveTimer = new Timer();
        moveTimer = new Timer();

        // Les tâches de nos Timer
        getMoveTask = new TimerTask() {
            @Override
            public void run() {
                path.addPosition(lastPosition);
            }
        };

        moveTask = new TimerTask() {
            @Override
            public void run() {
                moveRobot();
            }
        };

    }

    private void interpretMotion(MotionEvent event) {
        // On ajoute la position du doigt dans le tableau de positions
        lastPosition = new Position(event.getX(), event.getY());

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            System.out.println("action down");
            getMoveTimer.scheduleAtFixedRate(getMoveTask,0, VIRTUAL_TIME_DELAY);
        }

        // Si on enlève notre doigt de l'écran
        if (event.getAction() == MotionEvent.ACTION_UP) {
            System.out.println("action up");
            getMoveTimer.cancel();
            getMoveTimer = new Timer();
            i = 0;
            moveTimer.scheduleAtFixedRate(moveTask, 0, REAL_TIME_DELAY);
        }
    }

    private void moveRobot() {

        if (i >= path.size() - 1) {
            ollie.stop();
            moveTimer.cancel();
            moveTimer = new Timer();
            path.clear();
            return;
        }

        ollie.drive(path.getAngle(i), path.getVelocity(i));

        i++;
    }
}
