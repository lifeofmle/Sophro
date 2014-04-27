package com.lifeofmle.sophro.app.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mle on 2014-04-27.
 */
public class Session implements Serializable {

    private Date lastUpdated;
    private Date startDate;
    private Date endDate;

    private boolean isOverlimit;
    private boolean isStarted;
    private boolean hadFood;
    private int maximumCount;
    private int remainingCount;
    private String comment;
    private List<SessionItem> items;

    public Session() {
        setItems(new ArrayList<SessionItem>());
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isOverlimit() {
        return isOverlimit;
    }

    public void setOverlimit(boolean isOverlimit) {
        this.isOverlimit = isOverlimit;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean isStarted) {
        this.isStarted = isStarted;
    }

    public boolean isHadFood() {
        return hadFood;
    }

    public void setHadFood(boolean hadFood) {
        this.hadFood = hadFood;
    }

    public int getMaximumCount() {
        return maximumCount;
    }

    public void setMaximumCount(int maximumCount) {
        this.maximumCount = maximumCount;
    }

    public int getRemainingCount() {
        return remainingCount;
    }

    public void setRemainingCount(int remainingCount) {
        this.remainingCount = remainingCount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<SessionItem> getItems() {
        return items;
    }

    public void setItems(List<SessionItem> items) {
        this.items = items;
    }

    public void addItem(SessionItem item){
        if (!items.contains(item))
        {
            // Add items to top of list
            items.add(0, item);
        }

        updateCount();
    }

    public void updateCount()
    {
        // Check count of items vs maximum count
        // to calculate remaining count
        int drinkCount = 0;

        for (SessionItem item : items){
            if (item instanceof Drink)
                drinkCount++;
        }

        int remain = maximumCount - drinkCount;

        if (remain < 0 && !isOverlimit)
            setOverlimit(true);

        setRemainingCount(remain);
    }
}
