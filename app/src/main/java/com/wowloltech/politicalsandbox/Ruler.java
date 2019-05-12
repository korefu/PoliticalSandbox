package com.wowloltech.politicalsandbox;

public class Ruler {
    private int managementTalents;
    private int diplomaticTalents;
    private int militaryTalents;

    public Ruler(int managementTalents, int diplomaticTalents, int militaryTalents) {
        this.managementTalents = managementTalents;
        this.diplomaticTalents = diplomaticTalents;
        this.militaryTalents = militaryTalents;
    }

    public int getManagementTalents() {
        return managementTalents;
    }

    public void setManagementTalents(int managementTalents) {
        this.managementTalents = managementTalents;
    }

    public int getDiplomaticTalents() {
        return diplomaticTalents;
    }

    public void setDiplomaticTalents(int diplomaticTalents) {
        this.diplomaticTalents = diplomaticTalents;
    }

    public int getMilitaryTalents() {
        return militaryTalents;
    }

    public void setMilitaryTalents(int militaryTalents) {
        this.militaryTalents = militaryTalents;
    }

    @Override
    public String toString() {
        return " " + managementTalents + " " + diplomaticTalents + " " + militaryTalents;
    }
}
