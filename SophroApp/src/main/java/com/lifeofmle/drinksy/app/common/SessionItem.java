package com.lifeofmle.drinksy.app.common;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Created by mle on 2014-04-27.
 */
public class SessionItem implements Serializable {

    private String id;
    private Date createdDate;
    private String message;

    private SessionItemType itemType;

    public SessionItem() {
        setItemType(SessionItemType.General);
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

    public SessionItemType getItemType() {
        return itemType;
    }

    public void setItemType(SessionItemType itemType) {
        this.itemType = itemType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SessionItem){
            SessionItem other = (SessionItem)o;
            return this.getId().equals(other.getId());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    public String getGeneratedId(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
