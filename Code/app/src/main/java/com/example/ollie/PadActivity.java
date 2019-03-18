package com.example.ollie;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.support.constraint.ConstraintLayout;
import android.text.style.DrawableMarginSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

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
    private static int REAL_TIME_DELAY = 150;

    private ConvenienceRobot ollie = RobotHandler.getRobot();
    private OlliePath olliePath;
    private Timer moveTimer;
    private Position lastPosition;
    private boolean isStopped = true;

    private PaintView paintView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        paintView = new PaintView(this);

        setContentView(R.layout.activity_pad);

        // Récupération de la vue
        ConstraintLayout maVue = findViewById(R.id.padView);
        maVue.addView(paintView, new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // Création et ajout du bouton permettant la récalibration du Ollie
        Button calibrateButton = new Button(this);
        calibrateButton.setText(R.string.calibrationButton);
        calibrateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ollie.setZeroHeading();
            }
        });

        ConstraintLayout.LayoutParams constraintLayout = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        constraintLayout.bottomToBottom = R.id.padView;
        constraintLayout.rightToRight = R.id.padView;
        constraintLayout.leftToLeft = R.id.padView;
        constraintLayout.bottomMargin = 20;

        maVue.addView(calibrateButton, constraintLayout);



        // Création et ajout du SeekBar permettant le choix de la vitesse du Ollie
        SeekBar seekBar = new SeekBar(this);
        seekBar.setMax(250);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                REAL_TIME_DELAY = progress + 50;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ConstraintLayout.LayoutParams constraintLayoutSeekBar = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        constraintLayout.topToTop = R.id.padView;
        constraintLayout.rightToRight = R.id.padView;
        constraintLayout.leftToLeft = R.id.padView;
        constraintLayout.topMargin = 40;

        maVue.addView(seekBar, constraintLayoutSeekBar);



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

        moveTimer = new Timer();

    }
    

    private void interpretMotion(MotionEvent event) {
        // On ajoute la position du doigt dans le tableau de positions
        lastPosition = new Position(event.getX(), event.getY());
        olliePath.addPosition(lastPosition);

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            System.out.println("action down");

            // Créer un nouveau chemin vide
            paintView.clearPath();

            if(!isStopped) {
                stopRobot();
            }


           // Affichage du chemin
            paintView.moveTo(lastPosition.getX(), lastPosition.getY());
        }


        // Si on enlève notre doigt de l'écran
        if (event.getAction() == MotionEvent.ACTION_UP) {
            System.out.println("action up");

            i = 0;
            moveTimer.scheduleAtFixedRate(createMoveTask(), 0, REAL_TIME_DELAY);
            isStopped = false;
        }

        if(event.getAction() == MotionEvent.ACTION_MOVE) {
            paintView.lineTo(lastPosition.getX(), lastPosition.getY());
        }
    }


    private void moveRobot() {
        //Toast.makeText(this, (i * 100 / olliePath.size()) + "%", Toast.LENGTH_LONG).show();
        if (i >= olliePath.size() - 1) {
            System.out.println("----------- MOVE TERMINE -----------");
            stopRobot();
            return;
        }

        float angle = olliePath.getAngle(i);
        float velocity = olliePath.getVelocity(i);

        ollie.drive(angle, velocity);

        System.out.println(" --------- Angle : " + angle + " -----------");
        System.out.println(" --------- Velocity : " + velocity + " -----------");

        i++;
    }


    private void stopRobot() {
        ollie.stop();

        moveTimer.cancel();
        moveTimer = new Timer();

        olliePath.clear();

        isStopped = true;
    }



    private TimerTask createMoveTask() {
        return new TimerTask() {
            @Override
            public void run() {
                moveRobot();
            }
        };
    }

}
