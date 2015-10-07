package org.dfhu.fourstreaks;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;

public class DaysEventHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "days_events";
    private static DaysEventHelper mInstance;

    @Retention(RetentionPolicy.RUNTIME)
    public @interface FieldOrder {
        int value();
    }

    public interface C {
        @FieldOrder(0)
        String _id = "_id";
        @FieldOrder(1)
        String date_added = "date_added";
        @FieldOrder(2)
        String flag_SOC = "flag_soc";
        @FieldOrder(3)
        String flag_NCH = "flag_nch";
        @FieldOrder(4)
        String flag_NP = "flag_np";
        @FieldOrder(5)
        String flag_KET = "flag_ket";
        @FieldOrder(6)
        String flag_GYM = "flag_gym";
        @FieldOrder(7)
        String flag_no_weekend = "flag_no_weekend";
        @FieldOrder(8)
        String weight = "weight";
        @FieldOrder(9)
        String bf = "bf";
        @FieldOrder(10)
        String date_of_event = "date_of_event";
        @FieldOrder(11)
        String comment = "comment";
        @FieldOrder(12)
        String row_version = "row_version";
    }

    private static final String END_COL = " , ";


    private final static String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + DB_NAME + " (" +
                    C._id + " INTEGER PRIMARY KEY " + END_COL  +
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
                    C.row_version + " INTEGER  DEFAULT '" + DB_VERSION + "'" +
            ");";

    private DaysEventHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static synchronized DaysEventHelper getInstance (Context context) {
        if (mInstance == null) {
            mInstance = new DaysEventHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static String[] getOrderedColumns() {
        Field[] fields = C.class.getFields();

        Arrays.sort(fields, new Comparator<Field>() {
            @Override
            public int compare(Field lhs, Field rhs) {
                FieldOrder lhsFo = lhs.getAnnotation(FieldOrder.class);
                FieldOrder rhsFo = rhs.getAnnotation(FieldOrder.class);

                if (lhsFo == null || rhsFo == null) {
                    return lhs.getName().compareTo(rhs.getName());
                }

                return lhsFo.value() - rhsFo.value();
            }
        });

        String[] columns = new String[fields.length];
        for (int ii = 0; ii < fields.length; ii++) {
            columns[ii] = fields[ii].getName();
        }

        return columns;
    }

}
