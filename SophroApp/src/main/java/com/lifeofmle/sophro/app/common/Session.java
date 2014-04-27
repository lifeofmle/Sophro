package com.lifeofmle.sophro.app.common;

import java.util.Date;

/**
 * Created by mle on 2014-04-27.
 */
public class Session {

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    private Date lastUpdated;
    private Date startDate;
    private Date endDate;
    private boolean isStarted;
    private boolean hadFood;
    private int maximumCount;
    private int remainingCount;
    private String comment;
}
