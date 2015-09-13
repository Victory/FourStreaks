package org.dfhu.fourstreaks;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DaysEventHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "days_events";

    public interface C {
        String id = "_id";
        String date_added = "date_added";
        String flag_SOC = "flag_soc";
        String flag_NCH = "flag_nch";
        String flag_NP = "flag_np";
        String flag_KET = "flag_ket";
        String flag_GYM = "flag_gym";
        String flag_no_weekend = "flag_no_weekend";
        String weight = "weight";
        String bf = "bf";
        String date_of_event = "date_of_event";
        String comment = "comment";
        String version = "row_version";
    }

    private static final String END_COL = " , ";


    private final static String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + DB_NAME + " (" +
                    C.id + " INTEGER PRIMARY KEY " + END_COL  +
                    C.date_added + " DATETIME DEFAULT CURRENT_TIMESTAMP " + END_COL +
                    C.flag_SOC + " INTEGER "  + END_COL +
                    C.flag_NCH + " INTEGER "  + END_COL +
                    C.flag_NP + " INTEGER "  + END_COL +
                    C.flag_KET + " INTEGER "  + END_COL +
                    C.flag_GYM + " INTEGER "  + END_COL +
                    C.flag_no_weekend + " INTEGER "  + END_COL +
                    C.weight + " REAL " + END_COL +
                    C.bf + " REAL " + END_COL +
                    C.date_of_event + " TEXT " + END_COL +
                    C.comment + " TEXT " + END_COL +
                    C.version + " INTEGER  DEFAULT '" + DB_VERSION + "'" +
            ");";

    DaysEventHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
