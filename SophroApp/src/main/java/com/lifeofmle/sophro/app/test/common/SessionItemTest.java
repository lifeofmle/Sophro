package com.lifeofmle.sophro.app.test.common;

import android.test.InstrumentationTestCase;

import com.lifeofmle.sophro.app.common.SessionItem;

/**
 * Created by mle on 2014-04-27.
 */
public class SessionItemTest extends InstrumentationTestCase {

    public void testConstructorGeneratedId() throws Exception {
        SessionItem item = new SessionItem();

        assertNotNull(item.getId());
    }

    public void testConstructorGeneratedLoggedDate() throws Exception {
        SessionItem item = new SessionItem();

        assertNotNull(item.getCreatedDate());
    }

    public void testGetGeneratedId() throws Exception {
        SessionItem item = new SessionItem();

        String id = item.getGeneratedId();

        assertNotNull(id);
    }
}
