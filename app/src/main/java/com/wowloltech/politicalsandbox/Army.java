package com.wowloltech.politicalsandbox;

import android.content.ContentValues;

import java.util.Iterator;

public class Army {
    private int strength;
    private Province location;
    private int speed = 0;
    private Player owner;
    private int id;


    public Army(int strength, Province location, Player owner, int id) {
        this.strength = strength;
        this.location = location;
        this.owner = owner;
        this.id = id;
    }

    public Army(int strength, Province location, Player owner, int id, int speed) {
        this.strength = strength;
        this.location = location;
        this.speed = speed;
        this.owner = owner;
        this.id = id;
    }

    public static void remove(Army army) {
        army.getOwner().getArmies().remove(army);
        army.getLocation().getArmies().remove(army);
        Tools.dbHelper.getDb().delete("armies", "_id = ?", new String[]{String.valueOf(army.getId())});
    }

    public static void remove(Army army, Iterator i) {
        Tools.dbHelper.getDb().delete("armies", "_id = ?", new String[]{String.valueOf(army.getId())});
        i.remove();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        ContentValues cv = new ContentValues();
        cv.put("_id", id);
        Tools.dbHelper.getDb().update("armies", cv, "_id = ?", new String[]{String.valueOf(id)});
        this.id = id;


    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
        ContentValues cv = new ContentValues();
        cv.put("strength", strength);
        Tools.dbHelper.getDb().update("armies", cv, "_id = ?", new String[]{String.valueOf(id)});
    }

    public Province getLocation() {
        return location;
    }

    public void setLocation(Province location) {
        this.location = location;
        ContentValues cv = new ContentValues();
        cv.put("location", location.getId());
        Tools.dbHelper.getDb().update("armies", cv, "_id = ?", new String[]{String.valueOf(id)});
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "Численность: " + strength;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
        ContentValues cv = new ContentValues();
        cv.put("speed", speed);
        Tools.dbHelper.getDb().update("armies", cv, "_id = ?", new String[]{String.valueOf(id)});
    }
}
