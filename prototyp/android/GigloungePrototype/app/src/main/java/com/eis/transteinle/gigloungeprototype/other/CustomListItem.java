package com.eis.transteinle.gigloungeprototype.other;

/**
 * Created by DerOlli on 08.01.15.
 */
public class CustomListItem {

    private String itemTitle = "";
    private String secondTitle = "";
    private String id = "";

    public CustomListItem(String id,String title,String secondTitle) {
        this.id = id;
        this.itemTitle = title;
        this.secondTitle = secondTitle;
    }

    public String getId() {
        return id;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public String getSecondTitle() {
        return secondTitle;
    }
}
