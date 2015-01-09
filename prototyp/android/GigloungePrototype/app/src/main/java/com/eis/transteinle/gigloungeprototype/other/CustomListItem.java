package com.eis.transteinle.gigloungeprototype.other;

/**
 * Created by DerOlli on 08.01.15.
 */
public class CustomListItem {

    private String itemTitle = "";
    private String secondTitle = "";

    public CustomListItem(String title,String secondTitle) {
        this.itemTitle = title;
        this.secondTitle = secondTitle;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public String getSecondTitle() {
        return secondTitle;
    }
}
