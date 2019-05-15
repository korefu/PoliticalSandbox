package com.wowloltech.politicalsandbox;

import android.content.ContentValues;

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

    public Province(int x, int y, int id) {
        this.x = x;
        this.y = y;
        this.id = id;
        type = Type.VOID;
        owner = null;
        income = 0;
        recruits = 0;
        armies = null;
    }

    public Type getType() {
        return type;
    }

    public List<Army> getArmies() {
        return armies;
    }

    public Province(int x, int y, int id, int recruits, double income, Player owner, Type type) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.recruits = recruits;
        this.income = income;
        this.owner = owner;
        armies = new LinkedList<>();
        this.type = type;
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

    @Override
    public String toString() {
        return "income " + income +
                "\n  recruits " + recruits +
                "\n  id " + id +
                "\n  x " + x +
                "\n  y " + y +
                "\n  owner " + owner + "\n";
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
        ContentValues cv = new ContentValues();
        cv.put("owner", owner.getId());
        Tools.dbHelper.getDb().update("map", cv, "_id = ?", new String[]{String.valueOf(getId() + 1)});
    }

    enum Type {
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

