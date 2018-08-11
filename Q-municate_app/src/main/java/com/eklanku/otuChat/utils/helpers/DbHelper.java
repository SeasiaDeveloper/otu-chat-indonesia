package com.eklanku.otuChat.utils.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.eklanku.otuChat.ui.activities.contacts.ContactsModel;
import com.eklanku.otuChat.ui.activities.contacts.ContactsModelGroup;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "otuchat";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_ACCOUNT = "tblcontacts";

    public static final String KEY_ID = "id";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_FULL_NAME = "full_name";
    public static final String KEY_LOGIN = "login";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_REG_TYPE = "reg_tpe";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String strTabColunms = "(" + KEY_ID + " TEXT," + KEY_USER_ID + " TEXT," + KEY_FULL_NAME + " TEXT," + KEY_LOGIN + " TEXT," + KEY_PHONE + " TEXT," + KEY_REG_TYPE + " TEXT)";
        db.execSQL("CREATE TABLE " + TABLE_ACCOUNT + strTabColunms);
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

    }

    public void insertContact(ContactsModel obj) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, obj.getId());
        values.put(KEY_USER_ID, obj.getId_user());
        values.put(KEY_FULL_NAME, obj.getFullName());
        values.put(KEY_LOGIN, obj.getLogin());
        values.put(KEY_PHONE, obj.getPhone());
        values.put(KEY_REG_TYPE, obj.getIsReg_type());

        database.insert(TABLE_ACCOUNT, null, values);
        database.close();
    }

    public void updateContact(String phoneNumber, int id) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, id);
        values.put(KEY_REG_TYPE, "1");

        //database.insert(TABLE_ACCOUNT, null, values);
        database.update(TABLE_ACCOUNT, values, KEY_LOGIN + " = ?",
                new String[]{String.valueOf(phoneNumber)});
        database.close();
    }

    public boolean isContactsExists() {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "Select * from " + TABLE_ACCOUNT + " LIMIT 1";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public boolean isQbUser(String phoneNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "Select * from " + TABLE_ACCOUNT + " where " + KEY_REG_TYPE + " = '1' AND "+ KEY_LOGIN + " = '" + phoneNumber + "'";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public boolean isPhoneNumberExists(String phoneNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "Select * from " + TABLE_ACCOUNT + " where " + KEY_LOGIN + " = '" + phoneNumber + "'";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public ArrayList<ContactsModel> getContacts() {
        ArrayList<ContactsModel> mArray = new ArrayList<ContactsModel>();
        String selectQuery = "SELECT  * FROM " + TABLE_ACCOUNT + " ORDER BY "+KEY_FULL_NAME+" ASC";
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                mArray.clear();
                do {
                    mArray.add(new ContactsModel(cursor.getString(cursor.getColumnIndex(KEY_LOGIN)),
                            cursor.getString(cursor.getColumnIndex(KEY_FULL_NAME)),
                            cursor.getString(cursor.getColumnIndex(KEY_REG_TYPE)),
                            Integer.valueOf(cursor.getString(cursor.getColumnIndex(KEY_USER_ID)))));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mArray;
    }

    public ArrayList<ContactsModelGroup> getContactsGroup() {
        ArrayList<ContactsModelGroup> mArray = new ArrayList<ContactsModelGroup>();
        String selectQuery = "SELECT  * FROM " + TABLE_ACCOUNT + " ORDER BY "+KEY_FULL_NAME+" ASC";
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                mArray.clear();
                do {
                    mArray.add(new ContactsModelGroup(cursor.getString(cursor.getColumnIndex(KEY_LOGIN)),
                            cursor.getString(cursor.getColumnIndex(KEY_FULL_NAME)),
                            cursor.getString(cursor.getColumnIndex(KEY_REG_TYPE)),
                            Integer.valueOf(cursor.getString(cursor.getColumnIndex(KEY_USER_ID)))));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mArray;
    }

    public ArrayList<ContactsModel> getContactsExcept(List<Integer> arrIds) {
        ArrayList<ContactsModel> mArray = new ArrayList<ContactsModel>();
        String strCon = "";
        if(arrIds.size() > 0) {
            strCon += " AND "+KEY_USER_ID+" NOT IN ("+TextUtils.join(", ", arrIds)+")";
        }
        String selectQuery = "SELECT  * FROM " + TABLE_ACCOUNT+" WHERE "+ KEY_REG_TYPE + " = '1'"+ strCon + " ORDER BY "+KEY_FULL_NAME+" ASC";
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                mArray.clear();
                do {
                    mArray.add(new ContactsModel(cursor.getString(cursor.getColumnIndex(KEY_LOGIN)),
                            cursor.getString(cursor.getColumnIndex(KEY_FULL_NAME)),
                            cursor.getString(cursor.getColumnIndex(KEY_REG_TYPE)),
                            Integer.valueOf(cursor.getString(cursor.getColumnIndex(KEY_USER_ID)))));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mArray;
    }

    public ArrayList<ContactsModelGroup> getContactsExceptGroup(List<Integer> arrIds) {
        ArrayList<ContactsModelGroup> mArray = new ArrayList<ContactsModelGroup>();
        String strCon = "";
        if(arrIds.size() > 0) {
            strCon += " AND "+KEY_USER_ID+" NOT IN ("+TextUtils.join(", ", arrIds)+")";
        }
        String selectQuery = "SELECT  * FROM " + TABLE_ACCOUNT+" WHERE "+ KEY_REG_TYPE + " = '1'"+ strCon + " ORDER BY "+KEY_FULL_NAME+" ASC";
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                mArray.clear();
                do {
                    mArray.add(new ContactsModelGroup(cursor.getString(cursor.getColumnIndex(KEY_LOGIN)),
                            cursor.getString(cursor.getColumnIndex(KEY_FULL_NAME)),
                            cursor.getString(cursor.getColumnIndex(KEY_REG_TYPE)),
                            Integer.valueOf(cursor.getString(cursor.getColumnIndex(KEY_USER_ID)))));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mArray;
    }

    public ArrayList<ContactsModel> getContactsSelected() {
        ArrayList<ContactsModel> mArray = new ArrayList<ContactsModel>();
        String selectQuery = "SELECT  * FROM " + TABLE_ACCOUNT + " where " + KEY_REG_TYPE + " = '1'" + " ORDER BY "+KEY_FULL_NAME+" ASC";
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                mArray.clear();
                do {
                    mArray.add(new ContactsModel(cursor.getString(cursor.getColumnIndex(KEY_LOGIN)),
                            cursor.getString(cursor.getColumnIndex(KEY_FULL_NAME)),
                            cursor.getString(cursor.getColumnIndex(KEY_REG_TYPE)),
                            Integer.valueOf(cursor.getString(cursor.getColumnIndex(KEY_USER_ID)))));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mArray;
    }

    public ArrayList<ContactsModelGroup> getContactsSelectedGroup() {
        ArrayList<ContactsModelGroup> mArray = new ArrayList<ContactsModelGroup>();
        String selectQuery = "SELECT  * FROM " + TABLE_ACCOUNT + " where " + KEY_REG_TYPE + " = '1'" + " ORDER BY "+KEY_FULL_NAME+" ASC";
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                mArray.clear();
                do {
                    mArray.add(new ContactsModelGroup(cursor.getString(cursor.getColumnIndex(KEY_LOGIN)),
                            cursor.getString(cursor.getColumnIndex(KEY_FULL_NAME)),
                            cursor.getString(cursor.getColumnIndex(KEY_REG_TYPE)),
                            Integer.valueOf(cursor.getString(cursor.getColumnIndex(KEY_USER_ID)))));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mArray;
    }

    public String getNamebyNumber(String number) {
        String username = "";
        String selectQuery = "SELECT "+KEY_FULL_NAME+" FROM " + TABLE_ACCOUNT + " WHERE " + KEY_LOGIN + " LIKE '%"+ number + "' LIMIT 1";
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    username = cursor.getString(cursor.getColumnIndex(KEY_FULL_NAME));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return username;
    }

    public void removeAsll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACCOUNT, null, null);
    }
}
