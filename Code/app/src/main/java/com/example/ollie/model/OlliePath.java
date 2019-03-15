package com.example.ollie.model;

import java.util.ArrayList;
import java.util.List;

public class OlliePath {

    private List<Position> positions = new ArrayList<>();
    private static final int MAX_DIST = 50;


    public void addPosition(Position p) {
        //System.out.println("New pos: X:" + p.getX() + ", Y: " + p.getY());
        positions.add(p);
    }


    public void clear()
    {
        positions.clear();
    }

    public int size() { return positions.size(); }


    public float getAngle(int i) {
        float angle;

        Position A = positions.get(i);
        Position B = positions.get(i+1);

        angle = (float) Math.acos(Math.abs(A.getY() - B.getY()) / Math.sqrt(Math.pow(A.getX() - B.getX(), 2) + Math.pow(A.getY()-B.getY(), 2)));

        // Convert to degree
        angle = (float) Math.toDegrees(angle);

        // Modif de l'angle en fonction de l'orientation

        // Direction haut/gauche
        if(B.getX() < A.getX() && B.getY() < A.getX()) {
            angle -= angle * 2;
        }

        // Direction bas/droite
        if(B.getX() > A.getX() && B.getY() > A.getY()) {
            angle = 180 - angle;
        }


        // Direction bas/gauche
        if(B.getX() < A.getX() && B.getY() > A.getY()) {
            angle += 180;
        }




        return angle;
    }

    public float getVelocity(int i) {
        float vitesse;

        Position A = positions.get(i);
        Position B = positions.get(i+1);

        double dist = Math.sqrt(Math.pow(A.getX() - B.getX(), 2) + Math.pow(A.getY()-B.getY(), 2));

        // Convertion d'une distance (en nbre de pixels) en vitesse
        vitesse = (float) Math.min(dist * 1.0 / MAX_DIST, 1);


        return vitesse;
    }

}
