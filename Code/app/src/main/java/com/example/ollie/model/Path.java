package com.example.ollie.model;

import java.util.ArrayList;
import java.util.List;

public class Path {

    private List<Position> positions = new ArrayList<>();

    public void addPosition(Position p) {
        positions.add(p);
    }

    public Position getPosition(int index) {
        return positions.get(index);
    }

    public void clear() {
        positions.clear();
    }

    public int size() { return positions.size(); }

    public List<Float> getAngles() {
        List<Float> angles = new ArrayList<>();

        for(int i=0; i<positions.size() - 1; i++) {
            Position A = positions.get(i);
            Position B = positions.get(i+1);

            double angle = Math.acos(Math.abs(A.getY() - B.getY()) / Math.sqrt(Math.pow(A.getX() - B.getX(), 2) + Math.pow(A.getY()-B.getY(), 2)));

            angles.add((float)angle);
        }

        return angles;
    }

    public List<Float> getDistances() {
        List<Float> distances = new ArrayList<>();

        for(int i=0; i<positions.size() - 1; i++) {
            Position A = positions.get(i);
            Position B = positions.get(i+1);

            double dist = Math.sqrt(Math.pow(A.getX() - B.getX(), 2) + Math.pow(A.getY()-B.getY(), 2));

            distances.add((float)dist);
        }

        return distances;
    }

}
