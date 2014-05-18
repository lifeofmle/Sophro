package com.lifeofmle.drinksy.app.common;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by mle on 2014-05-01.
 */
public class SessionRepository implements Serializable {
    private ArrayList<Session> sessions;

    public SessionRepository(){
        sessions = new ArrayList<Session>();
    }

    public ArrayList<Session> getSessions() {
        return sessions;
    }

    public void setSessions(ArrayList<Session> sessions) {
        this.sessions = sessions;
    }
}
