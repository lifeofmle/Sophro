package com.lifeofmle.sophro.app.test.common;

import android.test.InstrumentationTestCase;

import com.lifeofmle.sophro.app.common.Alert;
import com.lifeofmle.sophro.app.common.Drink;
import com.lifeofmle.sophro.app.common.Session;

/**
 * Created by mle on 2014-04-27.
 */
public class SessionTest extends InstrumentationTestCase {

    public void testConstructorEmptyItems(){
        Session session = new Session();

        assertNotNull(session.getItems());
        assertTrue(session.getItems().isEmpty());
    }

    public void testAddItem(){
        Session session = new Session();

        Alert alert1 = new Alert();
        alert1.setMessage("A");

        session.addItem(alert1);

        assertEquals(1, session.getItems().size());
    }

    public void testAddItems() {
        Session session = new Session();

        Alert alert1 = new Alert();
        alert1.setMessage("A");

        Alert alert2 = new Alert();
        alert2.setMessage("B");

        session.addItem(alert1);
        session.addItem(alert2);

        assertEquals(2, session.getItems().size());
        assertEquals("B", ((Alert)session.getItems().get(0)).getMessage());
    }

    public void testAddMixedItems() {
        Session session = new Session();

        Alert alert = new Alert();

        Drink drink = new Drink();

        session.addItem(alert);
        session.addItem(drink);

        assertEquals(2, session.getItems().size());
    }

    public void testRemainingCount() {
        Session session = new Session();

        session.setMaximumCount(5);

        session.addItem(new Drink());
        session.addItem(new Alert());
        session.addItem(new Drink());

        assertEquals(3, session.getRemainingCount());
    }

    public void testOverlimit() {
        Session session = new Session();

        session.setMaximumCount(1);

        session.addItem(new Drink());

        assertFalse(session.isOverlimit());

        session.addItem(new Drink());

        assertTrue(session.isOverlimit());
        assertEquals(-1, session.getRemainingCount());
    }
}
