package org.dfhu.fourstreaks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.concurrent.atomic.AtomicBoolean;

public class DaysEventSource {

    private DaysEventHelper dbHelper;
    private SQLiteDatabase db;
    private AtomicBoolean haveDb = new AtomicBoolean(false);

    public DaysEventSource(Context context) {
        dbHelper = new DaysEventHelper(context);
    }

    public void open() throws SQLException {
        if(haveDb.compareAndSet(false, true)) {
            db = dbHelper.getWritableDatabase();
        }
    }

    public void close() {
        dbHelper.close();
    }

    public long insert(AbstractRow row) {
        open();

        ContentValues vals = row.getContentValues();
        long insertId = db.insert(DaysEventHelper.DB_NAME, null, vals);


        Cursor cursor = db.query(DaysEventHelper.DB_NAME,
                null,
                DaysEventHelper.C.id + " = " + insertId,
                null, null, null, null);

        cursor.moveToFirst();

        return insertId;
    }
}
