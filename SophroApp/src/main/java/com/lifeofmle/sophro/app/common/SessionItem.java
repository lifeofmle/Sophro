package com.lifeofmle.sophro.app.common;

import java.util.Date;
import java.util.UUID;

/**
 * Created by mle on 2014-04-27.
 */
public class SessionItem {

    private String id;
    private Date createdDate;

    public SessionItem() {
        setId(getGeneratedId());
        setCreatedDate(new Date());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getGeneratedId(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
