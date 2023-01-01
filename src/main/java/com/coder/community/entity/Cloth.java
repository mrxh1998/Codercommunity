package com.coder.community.entity;

public class Cloth {
    String color;
    int size;

    public void setSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String name;

    public Cloth(String color, int size, String name) {
        this.color = color;
        this.size = size;
        this.name = name;
    }
}
