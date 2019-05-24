package com.wowloltech.politicalsandbox.models;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.util.Log;

import com.wowloltech.politicalsandbox.Game;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private int id;
    private List<Province> provinces;
    private List<Army> armies;
    private double money;
    private int color;
    private int recruits;
    private boolean isHuman;
    private String name;
    private Game game;

    public String getName() {
        return name;
    }

    public Player(int id, double money, int recruits, int color, String name, Game game) {
        this.game = game;
        this.id = id;
        this.money = money;
        this.recruits = recruits;
        this.name = name;
        this.color = color;
        armies = new ArrayList<>();
        provinces = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public Game getGame() {
        return game;
    }

    private void addArmy(Army a) {
        armies.add(a);
        a.getLocation().getArmies().add(a);
        game.addArmyToDb(a);
    }

    public int getColor() {
        return color;
    }

    public List<Province> getProvinces() {
        return provinces;
    }

    public List<Army> getArmies() {
        return armies;
    }

    public double getMoney() {
        return money;
    }

    void setMoney(double money) {
        this.money = money;
        game.updatePlayerDb(getId(), "money", String.valueOf(money));
        //Log.d("myLog", String.valueOf(money) + " setmoney");
    }

    public int getRecruits() {
        return recruits;
    }

    public void setName(String name) {
        this.name = name;
        game.updatePlayerDb(getId(),"name", name);
    }

    public void setColor(int color) {
        this.color = color;
        game.updatePlayerDb(getId(), "color", String.valueOf(color));
    }

    void setRecruits(int recruits) {
        this.recruits = recruits;
        game.updatePlayerDb(getId(), "recruits", String.valueOf(recruits));
    }

    void setIsHuman(boolean isHuman) {
        this.isHuman = isHuman;
    }

    public boolean isHuman() {
        return isHuman;
    }

    @NonNull
    @Override
    public String toString() {
        return name + ", игрок № " + getId();
    }

    public double getMoneyIncome() {
        double income = 0;
        for (int i = 0; i < this.provinces.size(); i++)
            income += provinces.get(i).getIncome();
        for (int i = 0; i < this.armies.size(); i++)
            income -= (double) armies.get(i).getStrength() / 100;
        return income;
    }

    public int getRecruitsIncome() {
        int income = 0;
        for (int i = 0; i < this.provinces.size(); i++)
            income += provinces.get(i).getRecruits();
        return income;
    }


    public void divideArmy(int strength1, int strength2, Army parentArmy) {
        parentArmy.getOwner().addArmy(new Army(strength1, parentArmy.getLocation(), parentArmy.getOwner(), game.getIdCounter(), parentArmy.getSpeed(), game));
        parentArmy.getOwner().addArmy(new Army(strength2, parentArmy.getLocation(), parentArmy.getOwner(), game.getIdCounter() + 1, parentArmy.getSpeed(), game));
        game.setIdCounter(game.getIdCounter() + 2);
        game.removeArmy(parentArmy);
    }

    public void pickMilitary(int strength, Province province) {
        Log.d("myLog", ""+getName()+" "+strength);
        Army army = new Army(strength, province, this, game.getIdCounter(), game);
        addArmy(army);
        game.setIdCounter(game.getIdCounter() + 1);
        setRecruits(getRecruits() - strength);
        setMoney(getMoney() - (double) strength / 50);
    }

    public void uniteArmy(Province selectedProvince) {
        int summaryArmy = 0;
        int summarySpeed = 10;
        //      Log.d("myLog", selectedProvince.getArmies().toString());
        for (; selectedProvince.getArmies().size() > 0; ) {
            Army cArmy = selectedProvince.getArmies().get(0);
            summaryArmy += cArmy.getStrength();
            if (summarySpeed > cArmy.getSpeed())
                summarySpeed = cArmy.getSpeed();
            cArmy.getOwner().armies.remove(cArmy);
            game.removeArmy(cArmy);
        }
        //       Log.d("myLog", "" + summaryArmy);
        if (summaryArmy != 0) {
            selectedProvince.getOwner().addArmy(new Army(summaryArmy, selectedProvince, selectedProvince.getOwner(), game.getIdCounter(), summarySpeed, game));
            game.setIdCounter(game.getIdCounter() + 1);
        }

    }

    public void moveArmy(Army army, Province province) {
        army.setSpeed(army.getSpeed() - 1);
        army.getLocation().getArmies().remove(army);
        army.setLocation(province);
        province.getArmies().add(army);
        game.updateScreen();
    }

    public void nextTurn() {
        double income = 0;
        int newRecruits = 0;
        for (int i = 0; i < this.provinces.size(); i++) {
            income += provinces.get(i).getIncome();
            newRecruits += provinces.get(i).getRecruits();
        }
        for (Army a : getArmies())
            income -= (double) a.getStrength() / 100;
        setMoney(getMoney() + income);
        setRecruits(getRecruits() + newRecruits);
        if (getMoney() < 0) {
            setMoney(0.1);
            int totalStrength = 0;
            double lIncome = getMoneyIncome();
            for (Army a : getArmies())
                totalStrength += a.getStrength();
            for (Army a : getArmies())
                a.setStrength(a.getStrength() * (int) (-lIncome * 100) / totalStrength);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Player p = (Player) obj;
        return p.getId() == getId();
    }
}
