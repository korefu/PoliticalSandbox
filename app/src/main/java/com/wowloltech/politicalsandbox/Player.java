package com.wowloltech.politicalsandbox;

import android.content.ContentValues;
import android.graphics.Color;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class Player {
    private int id;
    private List<Province> provinces;
    private List<Player> wars;
    private List<Event> events;
    private List<Army> armies;
    private double money;
    private int color;
    private int recruits;
    private boolean isHuman;

    public Player(int id) {
        this.id = id;
        provinces = new ArrayList<>();
        wars = new LinkedList<>();
        events = new LinkedList<>();
        armies = new ArrayList<>();
        this.money = 0;
        this.recruits = 1000;
    }

    public Player(int id, double money, int recruits, int color) {
        this.id = id;
        this.money = money;
        this.recruits = recruits;
        this.color = color;
        armies = new ArrayList<>();
        provinces = new ArrayList<>();
        wars = new LinkedList<>();
        events = new LinkedList<>();
    }


    public int getId() {
        return id;
    }

    public void addArmy(Army a) {
        armies.add(a);
        a.getLocation().getArmies().add(a);
        ContentValues cv = new ContentValues();
        cv.put("strength", a.getStrength());
        cv.put("location", a.getLocation().getId());
        cv.put("speed", a.getSpeed());
        cv.put("owner", a.getOwner().getId());
        cv.put("_id", a.getId());
        Tools.dbHelper.getDb().insert("armies", null, cv);
    }

    public int getColor() {
        return color;
    }

    public List<Province> getProvinces() {
        return provinces;
    }

    public List<Player> getWars() {
        return wars;
    }

    public void setWars(List<Player> wars) {
        this.wars = wars;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public List<Army> getArmies() {
        return armies;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
        ContentValues cv = new ContentValues();
        cv.put("money", money);
        Tools.dbHelper.getDb().update("players", cv, "_id = ?", new String[]{String.valueOf(getId() + 1)});
        //Log.d("myLog", String.valueOf(money) + " setmoney");
    }

    public int getRecruits() {
        return recruits;
    }

    public void setRecruits(int recruits) {
        this.recruits = recruits;
        ContentValues cv = new ContentValues();
        cv.put("recruits", recruits);
        Tools.dbHelper.getDb().update("players", cv, "_id = ?", new String[]{String.valueOf(getId() + 1)});
    }

    public void setIsHuman(boolean isHuman) {
        this.isHuman = isHuman;
        ContentValues cv = new ContentValues();
        if (isHuman) {
            cv.put("is_human", 1);
            Tools.dbHelper.getDb().update("players", cv, "_id = ?", new String[]{String.valueOf(getId() + 1)});
        } else {
            cv.put("is_human", 0);
            Tools.dbHelper.getDb().update("players", cv, "_id = ?", new String[]{String.valueOf(getId() + 1)});
        }
    }

    public boolean isHuman() {
        return isHuman;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                '}';
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
        parentArmy.getOwner().addArmy(new Army(strength1, parentArmy.getLocation(), parentArmy.getOwner(), Tools.getIdCounter(), parentArmy.getSpeed()));
        parentArmy.getOwner().addArmy(new Army(strength2, parentArmy.getLocation(), parentArmy.getOwner(), Tools.getIdCounter() + 1, parentArmy.getSpeed()));
        Tools.setIdCounter(Tools.getIdCounter() + 2);
        Army.remove(parentArmy);
    }

    abstract boolean acceptAlliance(Event e);

    abstract boolean acceptPeace(Event e);

    public void pickMilitary(int strength, Province province) {
        Army army = new Army(strength, province, this, Tools.getIdCounter());
        addArmy(army);
        Tools.setIdCounter(Tools.getIdCounter() + 1);
        setRecruits(getRecruits() - strength);
        setMoney(getMoney() - (double) strength / 50);
    }

    public void combineArmy(Province selectedProvince) {
        int summaryArmy = 0;
        int summarySpeed = 10;
        //      Log.d("myLog", selectedProvince.getArmies().toString());
        for (Iterator<Army> i = selectedProvince.getArmies().iterator(); i.hasNext(); ) {
            Army cArmy = i.next();
            summaryArmy += cArmy.getStrength();
            if (summarySpeed > cArmy.getSpeed())
                summarySpeed = cArmy.getSpeed();
            cArmy.getOwner().armies.remove(cArmy);
            Army.remove(cArmy, i);
        }
        //       Log.d("myLog", "" + summaryArmy);
        if (summaryArmy != 0) {
            selectedProvince.getOwner().addArmy(new Army(summaryArmy, selectedProvince, selectedProvince.getOwner(), Tools.getIdCounter(), summarySpeed));
            Tools.setIdCounter(Tools.getIdCounter() + 1);
        }

    }

    public void attackProvince(Army army, Province province) {
        if (province.getOwner() != army.getOwner()) {
            Iterator<Army> i = province.getArmies().iterator();
            while (i.hasNext()) {
                Army a = i.next();
//            Log.d("myLog", "" + army.getLocation().getX() + " " + army.getLocation().getY() + " " + army.getOwner() + " "
//            + army.getStrength() + "\n" + province.getX() + " " + province.getY());
                if (province.getOwner() != army.getOwner()) {
                    if (a.getStrength() > army.getStrength()) {
                        a.setStrength(a.getStrength() - army.getStrength());
                        Army.remove(army);
                        return;
                    } else if (a.getStrength() == army.getStrength()) {
                        Army.remove(army);
                        Army.remove(a, i);
                        a.getOwner().armies.remove(a);
                        return;
                    } else {
                        army.setStrength(army.getStrength() - a.getStrength());
                        Army.remove(a, i);
                        a.getOwner().armies.remove(a);
                    }
                }
            }
            province.getOwner().getProvinces().remove(province);
            army.getOwner().getProvinces().add(province);
            province.setOwner(army.getOwner());
        }
        army.setSpeed(army.getSpeed() - 1);
        army.getLocation().getArmies().remove(army);
        army.setLocation(province);
        province.getArmies().add(army);
        try {
            updateScreen();
        } catch (Exception e) {
            Log.e("myLog", "oops");
            throw  e;
        }
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
            setMoney(0);
            for (Iterator<Army> i = getArmies().iterator(); i.hasNext(); ) {
                Army a = i.next();
                a.getLocation().getArmies().remove(a);
                Army.remove(a, i);
            }
        }
    }

    private void updateScreen() {
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            Runnable rn = new Runnable() {
                @Override
                public void run() {
                    Tools.game.getActivity().updateScreen();
                    synchronized (this) {
                        this.notify();
                    }
                }
            };
            synchronized (rn) {
                Tools.game.getActivity().runOnUiThread(rn);
                try {
                    rn.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
