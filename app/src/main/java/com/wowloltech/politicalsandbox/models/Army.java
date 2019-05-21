package com.wowloltech.politicalsandbox.models;

import android.support.annotation.NonNull;

import com.wowloltech.politicalsandbox.Game;

public class Army {
    private int strength;
    private Province location;
    private int speed = 0;
    private Player owner;
    private int id;
    private Game game;


    public Army(int strength, Province location, Player owner, int id, Game game) {
        this.strength = strength;
        this.location = location;
        this.owner = owner;
        this.id = id;
        this.game = game;
    }

    public Army(int strength, Province location, Player owner, int id, int speed, Game game) {
        this.strength = strength;
        this.location = location;
        this.speed = speed;
        this.owner = owner;
        this.id = id;
        this.game = game;
    }

    public int getId() {
        return id;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
        game.updateArmyDb(getId(), "strength", String.valueOf(strength));
    }

    public Province getLocation() {
        return location;
    }

    void setLocation(Province location) {
        this.location = location;
        game.updateArmyDb(getId(), "location", String.valueOf(location.getId()));
    }

    public Player getOwner() {
        return owner;
    }

    void setOwner(Player owner) {
        this.owner = owner;
    }

    @NonNull
    @Override
    public String toString() {
        return "Численность: " + strength;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
        game.updateArmyDb(getId(), "speed", String.valueOf(speed));
    }
}
