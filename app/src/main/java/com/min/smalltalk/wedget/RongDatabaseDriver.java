package com.min.smalltalk.wedget;

import android.content.Context;

import com.facebook.stetho.inspector.database.DatabaseConnectionProvider;
import com.facebook.stetho.inspector.database.DatabaseFilesProvider;
import com.facebook.stetho.inspector.database.SqliteDatabaseDriver;

/**
 * Created by Min on 2016/12/20.
 */

public class RongDatabaseDriver extends SqliteDatabaseDriver {
    public RongDatabaseDriver(Context context) {
        super(context);
    }

    public RongDatabaseDriver(Context context, DatabaseFilesProvider databaseFilesProvider, DatabaseConnectionProvider databaseConnectionProvider) {
        super(context, databaseFilesProvider, databaseConnectionProvider);
    }
}
