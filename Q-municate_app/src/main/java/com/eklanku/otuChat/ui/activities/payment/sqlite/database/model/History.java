package com.eklanku.otuChat.ui.activities.payment.sqlite.database.model;

public class History {
    public static final String TABLE_NAME = "notes";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NUMBER = "number";
    public static final String COLUMN_NOMINAL = "nominal";
    public static final String COLUMN_TRANS = "trans";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private int id;
    private String number;
    private String nominal;
    private String trans;
    private String timestamp;

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NUMBER + " TEXT,"
                    + COLUMN_NOMINAL + " TEXT,"
                    + COLUMN_TRANS + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public History() {
    }

    public History(int id, String number, String nominal, String trans, String timestamp) {
        this.id = id;
        this.number = number;
        this.nominal = nominal;
        this.trans = trans;
        this.timestamp = timestamp;
    }

    public String getTrans() {
        return trans;
    }

    public void setTrans(String trans) {
        this.trans = trans;
    }

    public String getNominal() {
        return nominal;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }

    public int getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
