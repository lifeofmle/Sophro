package com.lifeofmle.sophro.app.test.common;

import android.test.InstrumentationTestCase;

import com.lifeofmle.sophro.app.common.Alert;

/**
 * Created by mle on 2014-04-27.
 */
public class AlertTest extends InstrumentationTestCase {

    public void testSetMessage(){
        Alert alert = new Alert();
        alert.setMessage("B");

        assertEquals("B", alert.getMessage());
    }
}
