package com.skeleton.mvp.model;

/**
 * Created by rajatdhamija
 * 28/06/18.
 */

public class ContactsList {
    private String name;
    private String data;
    private boolean isSelected;

    public ContactsList(String name, String data,boolean isSelected) {
        this.name = name;
        this.data = data;
        this.isSelected=isSelected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
