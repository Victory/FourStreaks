package org.dfhu.fourstreaks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import org.dfhu.fourstreaks.DaysEventHelper.C;

public class DaysEventSource {

    private final DaysEventHelper dbHelper;
    private final SQLiteDatabase db;

    public DaysEventSource(Context context) {
        dbHelper = new DaysEventHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public void open() throws SQLException {
    }

    public void close() {
        dbHelper.close();
    }

    public long insert(AbstractRow row) {
        ContentValues values = row.getContentValues();
        return db.insert(DaysEventHelper.DB_NAME, null, values);
    }

    public Cursor getAll () {
        String[] columns = DaysEventHelper.getOrderedColumns();
        return db.query(DaysEventHelper.DB_NAME,
                columns,
                null, null, null, null,
                C.date_of_event + " DESC");
    }
}
