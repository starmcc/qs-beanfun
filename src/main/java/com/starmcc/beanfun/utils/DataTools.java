package com.starmcc.beanfun.utils;

import java.util.Collection;

public class DataTools {

    public static boolean collectionIsEmpty(final Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    public static boolean collectionIsNotEmpty(final Collection<?> coll) {
        return !collectionIsEmpty(coll);
    }

}
