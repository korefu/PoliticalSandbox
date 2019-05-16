package com.wowloltech.politicalsandbox;

import android.util.Log;
import java.util.LinkedList;
import java.util.List;

public class AIPlayer extends Player {

    public AIPlayer(int id, double money, int recruits, int color) {
        super(id, money, recruits, color);
        this.setIsHuman(false);
    }

    @Override
    boolean acceptAlliance(Event e) {
        return false;
    }

    @Override
    boolean acceptPeace(Event e) {
        return false;
    }

    @Override
    public void nextTurn() {
        super.nextTurn();
        List<Province> borderProvinces = new LinkedList<>();
        int recruitableArmy;
        if (getMoneyIncome() * 100 <= getMoney() * 50)
            recruitableArmy = (int) (getMoneyIncome() * 100);
        else recruitableArmy = (int) (getMoney() * 50);
        recruitableArmy-=200;
        for (Province p : getProvinces()) combineArmy(p);
        for (Province p : getProvinces())
            for (Province n : Map.getNeighbours(p))
                if (n.getOwner() != p.getOwner()) {
                    borderProvinces.add(p);
                    break;
                }

        for (int i = 0; i < getArmies().size(); i++) {
            Army a = getArmies().get(i);
            for (Province p : Map.getNeighbours(a.getLocation())) {
                if (p.getOwner().getId() != a.getOwner().getId()) {
                    int sum = 0;
                    for (Army enemy : p.getOwner().getArmies()) {
                        if (enemy.getLocation() == p) sum += enemy.getStrength();
                    }
                    if (a.getStrength()-sum>=50 && a.getSpeed()>0) {
//                        Log.d("myLog1", " sum " + sum + " " + a.getStrength() + " army loc xy " + a.getLocation().getX()+a.getLocation().getY() +
//                                " prov xy " + p.getX()+p.getY() +
//                                " prov owner " + p.getOwner() + " army owner " + a.getOwner() + "ID " + a.getId());
                        attackProvince(a, p);
                    }
                }
            }
        }
        int gthreat = 0;
        int[] threats = new int[borderProvinces.size()];
        for (int i = 0; i < borderProvinces.size(); i++) {
            Province p = borderProvinces.get(i);
            for (Province n : Map.getNeighbours(p)) {
                if (n.getOwner() != p.getOwner()) {
                    threats[i]+=100;
                    gthreat+=100;
                }
                for (Army a : n.getArmies())
                    if (a.getOwner() != p.getOwner()) {
                        threats[i] += a.getStrength()+100;
                        gthreat += a.getStrength()+100;
                    }
            }
        }
        for (int i = 0; i < borderProvinces.size(); i++) {
            //Log.d("myLog", toString()+" "+threats[i]+" "+"gthread "+gthreat+" "+recruitableArmy);
            int defense = 0;
            for (Army a: borderProvinces.get(i).getArmies())
                defense+=a.getStrength();
            if (gthreat > 0 && threats[i] > 0 && recruitableArmy > 0 && (recruitableArmy * threats[i])/gthreat-defense>0)
                pickMilitary((recruitableArmy * threats[i])/gthreat-defense, borderProvinces.get(i));

        }
    }

    @Override
    public String toString() {
        return "ИИ, №" + this.getId();
    }
}
