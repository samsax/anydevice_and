package com.parse.anydevice.models;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.concurrent.TimeUnit;

/**
 * Model represents the device and the application it's running
 */
@ParseClassName(Model.PARSE_CLASS_NAME)
public class Model extends ParseObject {
    public static final String PARSE_CLASS_NAME = "Model";
    public static final String DEFAULT = "default";
    public static final String BOARD_TYPE = "boardType";
    public static final String APP_NAME = "appName";
    public static final String ICON = "icon";

    public Model() {}

    public boolean isDefault() {
        return getBoolean(DEFAULT);
    }

    public String getBoardType() {
        return getString(BOARD_TYPE);
    }

    public String getAppName() {
        return getString(APP_NAME);
    }



    /**
     * Helper to create a query with a cache
     *
     * @return Model query
     */
    public static ParseQuery<Model> getQuery() {
        final ParseQuery<Model> query = ParseQuery.getQuery(Model.class);
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(30));
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        return query;
    }
}
