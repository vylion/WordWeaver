package vylion.wordweaver.alexandria;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import vylion.wordweaver.R;
import vylion.wordweaver.arachne.Language;
import vylion.wordweaver.arachne.Weaver;

public class Scribe {

    // Database fields
    private SQLiteDatabase database;

    // Helper to manipulate table
    private MySQLiteHelper dbHelper;

    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_NAME, MySQLiteHelper.COLUMN_AUTHOR,
            MySQLiteHelper.COLUMN_CATEGORIES, MySQLiteHelper.COLUMN_RULES,
            MySQLiteHelper.COLUMN_SYLLABLES,
            MySQLiteHelper.COLUMN_DROPOFF, MySQLiteHelper.COLUMN_CUSTOM_DROPOFF,
            MySQLiteHelper.COLUMN_SYL_PROB, MySQLiteHelper.COLUMN_CUSTOM_SYL_PROB,
            MySQLiteHelper.COLUMN_MONOSYL_PROB, MySQLiteHelper.COLUMN_CUSTOM_MONOSYL_PROB
    };

    public String defaultAuthor;
    public String defaultDropoff;
    public double defaultDropoffVal;
    public String defaultSylProb;
    public double defaultSylProbVal;
    public String defaultMonosylProb;
    public double defaultMonosylProbVal;

    public Scribe(Context context) {
        dbHelper = new MySQLiteHelper(context);
        Resources res = context.getResources();

        defaultAuthor = res.getString(R.string.default_author);
        defaultDropoff = res.getString(R.string.default_dropoff);
        defaultSylProb = res.getString(R.string.default_syl_prob);
        defaultMonosylProb = res.getString(R.string.default_monosyl_prob);

        defaultDropoffVal = 0.5;
        defaultSylProbVal = 0.5;
        defaultMonosylProbVal = 0.5;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void read() throws SQLException {
        database = dbHelper.getReadableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    private Language createLang(String name, String author, String categories, String rules, String syllables, String dropoff,
                                Double dropoff_val, String syl_prob, Double syl_prob_val, String monosyl_prob, Double monosyl_prob_val) {

        //default values
        if(author == null) author = defaultAuthor;
        if(dropoff == null) dropoff = defaultDropoff;
        if(dropoff_val == null) dropoff_val = defaultDropoffVal;
        if(syl_prob == null) syl_prob = defaultSylProb;
        if(syl_prob_val == null) syl_prob_val = defaultSylProbVal;
        if(monosyl_prob == null) monosyl_prob = defaultMonosylProb;
        if(monosyl_prob_val == null) monosyl_prob_val = defaultMonosylProbVal;

        ContentValues values = new ContentValues();
        Log.d("Creating", "Creating " + name + " by " + author);

        values.put(MySQLiteHelper.COLUMN_NAME, name);
        values.put(MySQLiteHelper.COLUMN_AUTHOR, author);
        values.put(MySQLiteHelper.COLUMN_CATEGORIES, categories);
        values.put(MySQLiteHelper.COLUMN_RULES, rules);
        values.put(MySQLiteHelper.COLUMN_SYLLABLES, syllables);
        values.put(MySQLiteHelper.COLUMN_DROPOFF, dropoff);
        values.put(MySQLiteHelper.COLUMN_CUSTOM_DROPOFF, dropoff_val);
        values.put(MySQLiteHelper.COLUMN_SYL_PROB, syl_prob);
        values.put(MySQLiteHelper.COLUMN_CUSTOM_SYL_PROB, syl_prob_val);
        values.put(MySQLiteHelper.COLUMN_MONOSYL_PROB, monosyl_prob);
        values.put(MySQLiteHelper.COLUMN_CUSTOM_MONOSYL_PROB, monosyl_prob_val);

        // Actual insertion of the data using the values variable
        long insertId = database.insert(MySQLiteHelper.TABLE_LANGS, null,
                values);

        // Main activity calls this procedure to create a new book
        // and uses the result to update the listview.
        // Therefore, we need to get the data from the database
        // (you can use this as a query example)
        // to feed the view.

        Cursor cursor = database.query(MySQLiteHelper.TABLE_LANGS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Language newLang = cursorToLang(cursor);

        // Do not forget to close the cursor
        cursor.close();

        // Return the book
        return newLang;
    }

    public Language createLang(String name, String author, Weaver w) {
        return createLang(name, author, w.categoriesToString(), w.rulesToString(), w.syllablesToString(),
                w.dropoffToString(), w.getDropoffCustom(), w.sylProbToString(), w.getSylProbCustom(),
                w.monosylProbToString(), w.getMonosylProbCustom());
    }

    public Language createLang(Language l) {
        return createLang(l.getName(), l.getAuthor(), l.getWeaver());
    }

    public void deleteLang(Language lang) {
        long id = lang.getId();
        System.out.println("Language deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_LANGS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Language> getAllLangs() {
        List<Language> langs = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_LANGS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Language lang = cursorToLang(cursor);
            langs.add(lang);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return langs;
    }

    private Language cursorToLang(Cursor cursor) {
        Weaver weaver = new Weaver(cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getDouble(7),
                cursor.getString(8),
                cursor.getDouble(9),
                cursor.getString(10),
                cursor.getDouble(11));
        Language lang = new Language(cursor.getLong(0),
                cursor.getString(1),
                cursor.getString(2),
                weaver);

        return lang;
    }
}