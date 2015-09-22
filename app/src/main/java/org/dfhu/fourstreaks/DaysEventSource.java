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

    public int updateRow (AbstractRow row, int id) {
        return db.update(
                DaysEventHelper.DB_NAME,
                row.getContentValues(),
                C._id + " = " + id,
                null);
    }

    /**
     * get all of the known days events, but only get the latest record for each day
     * @return cursor pointing to query results
     */
    public Cursor getAllTopLevel () {
        String sql = "SELECT * " +
                "FROM " + DaysEventHelper.DB_NAME + " t1 " +
                "WHERE " + C._id + " = " +
                "(SELECT MAX(" + C._id + ") FROM " + DaysEventHelper.DB_NAME +
                    " WHERE " + DaysEventHelper.DB_NAME + "." + C.date_of_event + " = t1." + C.date_of_event + ") " +
                "ORDER BY " + C.date_of_event + " DESC";

        return db.rawQuery(sql, null);
    }
}
