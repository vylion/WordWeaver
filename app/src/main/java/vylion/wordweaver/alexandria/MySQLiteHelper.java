package vylion.wordweaver.alexandria;

/**
 * MySQLiteHelper
 * Created by pr_idi on 10/11/16.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_LANGS = "languages";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_AUTHOR = "author";
    public static final String COLUMN_CATEGORIES= "categories";
    public static final String COLUMN_RULES ="rewrite_rules";
    public static final String COLUMN_SYLLABLES = "syllable_types";
    public static final String COLUMN_DROPOFF = "dropoff_type";
    public static final String COLUMN_CUSTOM_DROPOFF = "dropoff_custom_val";
    public static final String COLUMN_SYL_PROB = "syllable_probability";
    public static final String COLUMN_CUSTOM_SYL_PROB = "syllable_probability_custom_val";
    public static final String COLUMN_MONOSYL_PROB = "monosyllabic_probability";
    public static final String COLUMN_CUSTOM_MONOSYL_PROB = "monosyllabic_probability_custom_val";

    private static final String DATABASE_NAME = "langs.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table " + TABLE_LANGS + "( "
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_NAME + " text not null, "
            + COLUMN_AUTHOR + " text not null, "
            + COLUMN_CATEGORIES + " text not null, "
            + COLUMN_RULES + " text, "
            + COLUMN_SYLLABLES + " text not null, "
            + COLUMN_DROPOFF + " text, "
            + COLUMN_CUSTOM_DROPOFF + " double, "
            + COLUMN_SYL_PROB + " text, "
            + COLUMN_CUSTOM_SYL_PROB + " double, "
            + COLUMN_MONOSYL_PROB + " text, "
            + COLUMN_CUSTOM_MONOSYL_PROB + " double"
            + ");";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LANGS);
        onCreate(db);
    }
}