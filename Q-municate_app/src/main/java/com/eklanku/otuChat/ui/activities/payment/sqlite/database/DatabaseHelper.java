package com.eklanku.otuChat.ui.activities.payment.sqlite.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.eklanku.otuChat.ui.activities.payment.sqlite.database.model.History;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "otu_db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(History.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + History.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertHistory(String number, String nominal, String trans) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(History.COLUMN_NUMBER, number);
        values.put(History.COLUMN_NOMINAL, nominal);
        values.put(History.COLUMN_TRANS, trans);

        // insert row
        long id = db.insert(History.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public History getHistory(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(History.TABLE_NAME,
                new String[]{History.COLUMN_ID, History.COLUMN_NUMBER, History.COLUMN_NOMINAL, History.COLUMN_TRANS, History.COLUMN_TIMESTAMP},
                History.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare number object
        History note = new History(
                cursor.getInt(cursor.getColumnIndex(History.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(History.COLUMN_NUMBER)),
                cursor.getString(cursor.getColumnIndex(History.COLUMN_NOMINAL)),
                cursor.getString(cursor.getColumnIndex(History.COLUMN_TRANS)),
                cursor.getString(cursor.getColumnIndex(History.COLUMN_TIMESTAMP)));

        // close the db connection
        cursor.close();

        return note;
    }

    public List<History> getAllHistory() {
        List<History> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + History.TABLE_NAME + " ORDER BY " +
                History.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                History history = new History();
                history.setId(cursor.getInt(cursor.getColumnIndex(History.COLUMN_ID)));
                history.setNumber(cursor.getString(cursor.getColumnIndex(History.COLUMN_NUMBER)));
                history.setTimestamp(cursor.getString(cursor.getColumnIndex(History.COLUMN_TIMESTAMP)));

                notes.add(history);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return notes;
    }

    public int getHistoryCount() {
        String countQuery = "SELECT  * FROM " + History.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    public int updateHistory(History number) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(History.COLUMN_NUMBER, number.getNumber());

        // updating row
        return db.update(History.TABLE_NAME, values, History.COLUMN_ID + " = ?",
                new String[]{String.valueOf(number.getId())});
    }

    public void deleteHistory(History number) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(History.TABLE_NAME, History.COLUMN_ID + " = ?",
                new String[]{String.valueOf(number.getId())});
        db.close();
    }
}
