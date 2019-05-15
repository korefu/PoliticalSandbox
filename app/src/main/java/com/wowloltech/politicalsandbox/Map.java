package com.wowloltech.politicalsandbox;

import android.graphics.Color;

import java.util.LinkedList;

public class Map {
    private static int width;
    private static int height;
    private static Province[][] provinces;

    public static int getWidth() {
        return width;
    }

    public static void setWidth(int width) {
        Map.width = width;
    }

    public static int getHeight() {
        return height;
    }

    public static void setHeight(int height) {
        Map.height = height;
    }

    public static Province[][] getProvinces() {
        return provinces;
    }

    public static void setProvinces(Province[][] provinces) {
        Map.provinces = provinces;
    }

    public static int getColor(int idOfPlayer) {
        switch (idOfPlayer) {
            case 0:
                return Color.BLUE;
            case 1:
                return Color.RED;
            case 2:
                return Color.GREEN;
            case 3:
                return Color.YELLOW;
            case 4:
                return Color.CYAN;
            default:
                return Color.argb(255, (int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
        }
    }

    public static Province findProvinceByID(int id) {
        try {
            return Map.provinces[id / Map.width][id % Map.width];
        } catch (Exception e) {
        }
        return null;
    }

    public static void nextTurn() {

    }

    public static LinkedList<Province> getNeighbours(Province location) {
        LinkedList<Province> neighbours = new LinkedList<>();
        if (location.getX() < width - 1)
            if (provinces[location.getY()][location.getX() + 1].getType() != Province.Type.VOID)
                neighbours.add(provinces[location.getY()][location.getX() + 1]);
        if (location.getX() > 0)
            if (provinces[location.getY()][location.getX() - 1].getType() != Province.Type.VOID)
                neighbours.add(provinces[location.getY()][location.getX() - 1]);
        if (location.getY() > 0)
            if (provinces[location.getY() - 1][location.getX()].getType() != Province.Type.VOID)
                neighbours.add(provinces[location.getY() - 1][location.getX()]);
        if (location.getY() < height - 1)
            if (provinces[location.getY() + 1][location.getX()].getType() != Province.Type.VOID)
                neighbours.add(provinces[location.getY() + 1][location.getX()]);
        if (location.getY() % 2 == 1) {
            if (location.getX() < width - 1 && location.getY() > 0)
                if (provinces[location.getY() - 1][location.getX() + 1].getType() != Province.Type.VOID)
                    neighbours.add(provinces[location.getY() - 1][location.getX() + 1]);
            if (location.getX() < width - 1 && location.getY() < height - 1)
                if (provinces[location.getY() + 1][location.getX() + 1].getType() != Province.Type.VOID)
                    neighbours.add(provinces[location.getY() + 1][location.getX() + 1]);
        } else {
            if (location.getX() > 0 && location.getY() > 0)
                if (provinces[location.getY() - 1][location.getX() - 1].getType() != Province.Type.VOID)
                    neighbours.add(provinces[location.getY() - 1][location.getX() - 1]);
            if (location.getX() > 0 && location.getY() < height - 1)
                if (provinces[location.getY() + 1][location.getX() - 1].getType() != Province.Type.VOID)
                    neighbours.add(provinces[location.getY() + 1][location.getX() - 1]);
        }
        return neighbours;
    }
}

