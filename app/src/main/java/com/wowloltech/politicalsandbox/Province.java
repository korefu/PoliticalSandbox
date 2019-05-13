package com.wowloltech.politicalsandbox;

import android.content.ContentValues;

public class Province {
    private double income;
    private int recruits;
    private int id;
    private int x;
    private int y;
    private Player owner;
    private boolean selected = false;

    public Province(int x, int y, int id, int recruits, double income, Player owner) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.recruits = recruits;
        this.income = income;
        this.owner = owner;
    }

    public Province(int id, int x, int y, Game game) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.owner = game.getPlayers().get(0);
        this.income = 1.0;
        this.recruits = 10000;
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
}

