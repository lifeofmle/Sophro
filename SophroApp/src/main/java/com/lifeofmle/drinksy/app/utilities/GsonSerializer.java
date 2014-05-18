package com.lifeofmle.drinksy.app.utilities;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Created by mle on 2014-04-29.
 */
public class GsonSerializer {

    public static String objectToString(Object object) throws Exception
    {
        Gson gson = new Gson();

        String json = gson.toJson(object);

        return json;
    }

    public static Object stringToObject(String objectString, Type classType) throws Exception
    {
        Gson gson = new Gson();

        Object object = gson.fromJson(objectString, classType);

        return object;
    }
}
