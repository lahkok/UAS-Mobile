package com.example.uas_mobile.utils;

import java.util.List;

public class ProfileItem {
    private String title;
    private List<String> subItems;
    private boolean isExpanded;

    public ProfileItem(String title, List<String> subItems) {
        this.title = title;
        this.subItems = subItems;
        this.isExpanded = false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getSubItems() {
        return subItems;
    }

    public void setSubItems(List<String> subItems) {
        this.subItems = subItems;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
