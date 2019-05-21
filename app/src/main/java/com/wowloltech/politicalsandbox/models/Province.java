package com.wowloltech.politicalsandbox.models;

import android.support.annotation.NonNull;

import com.wowloltech.politicalsandbox.Game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Province {
    private double income;
    private int recruits;
    private int id;
    private int x;
    private int y;
    private Player owner;
    private boolean selected = false;
    private List<Army> armies;
    private  Type type;
    private List<Province> neighbours;
    private Game game;

    public Province(int x, int y, int id) {
        this.x = x;
        this.y = y;
        this.id = id;
        type = Type.VOID;
        owner = null;
        income = 0;
        recruits = 0;
        armies = null;
        neighbours = null;
        game = null;
    }

    Integer getNumberOfFriendlyProvinces(Player player) {
        Integer numberOfFriendlyProvinces=0;
        for (Province province: neighbours)
            if (player==province.getOwner())
                numberOfFriendlyProvinces++;
            return numberOfFriendlyProvinces;
    }

    public List<Province> getNeighbours() {
        return neighbours;
    }

    public Type getType() {
        return type;
    }

    public List<Army> getArmies() {
        return armies;
    }

    public Province(int x, int y, int id, int recruits, double income, Player owner, Type type, Game game) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.recruits = recruits;
        this.income = income;
        this.owner = owner;
        armies = new LinkedList<>();
        neighbours = new ArrayList<>();
        this.type = type;
        this.game = game;
    }


    public int getId() {
        return id;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @NonNull
    @Override
    public String toString() {
        return "\nx=" + x +
                " y=" + y +
                " owner= " + getOwner().getName();
    }

    public double getIncome() {
        return income;
    }

    public int getRecruits() {
        return recruits;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
        game.updateMapDb(getId(), "owner", String.valueOf(owner.getId()));
    }

    public enum Type {
        VOID(0), PLAIN(1);
        int value;

        Type(int i) {
            value = i;
        }

        public static Type valueOf(int type) {
            for (Type t : values())
                if (t.value == type)
                    return t;
            return null;
        }
    }
}

