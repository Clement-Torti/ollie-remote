package com.example.ollie.model;

import android.util.Log;

import com.orbotix.Ollie;
import com.orbotix.common.Robot;

public class VirtualOllie extends Ollie {
    public VirtualOllie(Robot robot) {
        super(robot);
    }

    @Override
    public void drive(float heading, float velocity) {
        Log.d("Robot " + this, "J'avance (angle " + heading + ", vitesse " + velocity + ")");
        super.drive(heading, velocity);
    }

    @Override
    public void rotate(float heading) {
        Log.d("Robot " + this, "Je tourne (angle " + heading + ")");
        super.rotate(heading);
    }

    @Override
    public void stop() {
        Log.d("Robot " + this, "Je m'arrete");
        super.stop();
    }
}
