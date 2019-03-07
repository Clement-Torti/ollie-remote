package com.example.ollie.model;

import java.util.ArrayList;
import java.util.List;

public class Path {

    private List<Position> positions = new ArrayList<>();

    private List<Integer> times = new ArrayList<>();

    public void addPosition(Position p, int time) {

        positions.add(p);
        times.add(time);
    }

    public Position getPosition(int index) {
        return positions.get(index);
    }

    public Integer getTime(int index) {
        return times.get(index);
    }

    public void clear()
    {
        positions.clear();
        times.clear();
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

            // Convertir les pixels en mètre
            dist = dist / 100;

            distances.add((float)dist);
        }

        return distances;
    }

}
