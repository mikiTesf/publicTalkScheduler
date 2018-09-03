package com.publictalkgenerator.controller;

public enum MonthName {

    M1  ("Tir"),
    M2  ("Yekatit"),
    M3  ("Megabit"),
    M4  ("Miyazia"),
    M5  ("Ginbot"),
    M6  ("Sene"),
    M7  ("Hamle"),
    M8  ("Nehase"),
    M9  ("Meskerem"),
    M10 ("Tikimt"),
    M11 ("Hidar"),
    M12 ("Tahsas");

    MonthName(String name){
        this.name = name;
    }
    String name;

    @Override
    public String toString() {
        return name;
    }
}
