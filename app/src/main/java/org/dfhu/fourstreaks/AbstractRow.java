package org.dfhu.fourstreaks;


import android.content.ContentValues;

import java.util.HashMap;
import java.util.Map;

abstract class AbstractRow {

    // maps column names to column values
    ContentValues insertValues = new ContentValues();

    public String get(String key) {
        return (String) insertValues.get(key);
    }

    public void set(String key, String value) {
        insertValues.put(key, value);
    }

    public void set(String key, Integer value) {
        insertValues.put(key, value);
    }

    public void set(String key, Float value) {
        insertValues.put(key, value);
    }

    public void set(String key, Boolean value) {
        String vv;
        vv = (value) ? "1" : "0";
        set(key, vv);
    }

    public ContentValues getContentValues () {
        return insertValues;
    }
}
