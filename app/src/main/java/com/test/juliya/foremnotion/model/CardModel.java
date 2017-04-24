package com.test.juliya.foremnotion.model;

/**
 * Created by juliya on 23.04.2017.
 */
public class CardModel {
    private String titleSource;
    private String titleDestination;
    private int valueDistance;

    public String getTitleSource() {
        return titleSource;
    }

    public String getTitleDestination() {
        return titleDestination;
    }

    public int getValueDistance() {
        return valueDistance;
    }

    public void setTitleSource(String titleSource) {
        this.titleSource = titleSource;
    }

    public void setTitleDestination(String titleDestination) {
        this.titleDestination = titleDestination;
    }

    public void setValueDistance(int valueDistance) {
        this.valueDistance = valueDistance;
    }
}
