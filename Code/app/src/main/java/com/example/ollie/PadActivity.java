package com.example.ollie;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.example.ollie.View.PaintView;
import com.example.ollie.model.OlliePath;
import com.example.ollie.model.Position;
import com.example.ollie.model.RobotHandler;
import com.orbotix.ConvenienceRobot;

import java.util.Timer;
import java.util.TimerTask;

// Pour convertir les durées et les distances, nous partons du principe que :
// 10 ms sur le Pad correspond à 100 ms dans la vraie vie ;
// 50 px sur le Pad correspond à 5 cm dans la vraie vie

public class PadActivity extends BaseActivity {

    private int i;
    private static final int VIRTUAL_TIME_DELAY = 50;
    private static final int REAL_TIME_DELAY = 200;

    private ConvenienceRobot ollie = RobotHandler.getRobot();
    private OlliePath olliePath;
    private Timer getMoveTimer;
    private Timer moveTimer;
    private Position lastPosition;
    private boolean newValue = false;

    private PaintView paintView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        paintView = new PaintView(this);

        setContentView(paintView);

        // Configuration du olliePath
        olliePath = new OlliePath();
        i = 0;

        paintView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                interpretMotion(event);

                return true;
            }
        });

        getMoveTimer = new Timer();
        moveTimer = new Timer();

    }
    

    private void interpretMotion(MotionEvent event) {
        // On ajoute la position du doigt dans le tableau de positions
        lastPosition = new Position(event.getX(), event.getY());
        newValue = true;

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            System.out.println("action down");
            getMoveTimer.scheduleAtFixedRate(createGetMoveTask(),0, VIRTUAL_TIME_DELAY);

            // Créer un nouveau chemin vide
            paintView.clearPath();

            paintView.moveTo(lastPosition.getX(), lastPosition.getY());
        }

        // Si on enlève notre doigt de l'écran
        if (event.getAction() == MotionEvent.ACTION_UP) {
            System.out.println("action up");

            getMoveTimer.cancel();
            getMoveTimer = new Timer();

            i = 0;
            moveTimer.scheduleAtFixedRate(createMoveTask(), 0, REAL_TIME_DELAY);
        }

        if(event.getAction() == MotionEvent.ACTION_MOVE) {
            paintView.lineTo(lastPosition.getX(), lastPosition.getY());
        }
    }


    private void moveRobot() {

        if (i >= olliePath.size() - 1) {
            System.out.println("----------- MOVE TERMINE -----------");
            ollie.stop();

            moveTimer.cancel();
            moveTimer = new Timer();

            olliePath.clear();
            return;
        }

        float angle = olliePath.getAngle(i);
        float velocity = olliePath.getVelocity(i);

        ollie.drive(angle, velocity);

        System.out.println(" --------- Angle : " + angle + " -----------");
        System.out.println(" --------- Velocity : " + velocity + " -----------");

        i++;
    }




    private TimerTask createGetMoveTask() {
        return new TimerTask() {
            @Override
            public void run() {
                if(newValue) {
                    olliePath.addPosition(lastPosition);
                    newValue = false;
                }
            }
        };
    }


    private TimerTask createMoveTask() {
        return new TimerTask() {
            @Override
            public void run() {
                moveRobot();
            }
        };
    }

    public void calibrationPadClick(View view) {
        ollie.setZeroHeading();
    }
}
