package edu.iit.cs442.team15.ehome.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import edu.iit.cs442.team15.ehome.model.User;

public final class ApartmentDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "offline_apartments";
    private static final int DB_VERSION = 1;

    private static ApartmentDatabaseHelper sInstance; // singleton instance

    private final Context context;

    private ApartmentDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;

        setupDatabase();
    }

    public static synchronized ApartmentDatabaseHelper getInstance(Context context) {
        if (sInstance == null)
            sInstance = new ApartmentDatabaseHelper(context.getApplicationContext());
        return sInstance;
    }

    private void setupDatabase() {
        getReadableDatabase(); // creates an empty database file

        try {
            InputStream input = context.getAssets().open(DB_NAME + ".db");
            OutputStream output = new FileOutputStream(context.getDatabasePath(DB_NAME));

            //copy bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0)
                output.write(buffer, 0, length);

            output.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            context.deleteDatabase(DB_NAME);
            setupDatabase();
        }
    }

    public long addUser(User newUser) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Users.KEY_EMAIL, newUser.email);
        values.put(Users.KEY_PASSWORD, newUser.password);
        values.put(Users.KEY_NAME, newUser.name);
        values.put(Users.KEY_ADDRESS, newUser.address);
        values.put(Users.KEY_PHONE, newUser.phone);

        return db.insert(Users.TABLE_NAME, null, values);
    }

    @Nullable
    public User getUser(final String email, final String password) {
        SQLiteDatabase db = getReadableDatabase();

        final String sqlQuery = "SELECT * FROM " + Users.TABLE_NAME + " WHERE " + Users.KEY_EMAIL + "=? AND " + Users.KEY_PASSWORD + "=?";
        Cursor result = db.rawQuery(sqlQuery, new String[]{email, password});

        User user = null;

        if (result.moveToFirst()) {
            // User exists
            user = new User()
                    .setId(result.getInt(result.getColumnIndex(Users.KEY_ID)))
                    .setEmail(email)
                    .setPassword(password)
                    .setName(result.getString(result.getColumnIndex(Users.KEY_NAME)))
                    .setAddress(result.getString(result.getColumnIndex(Users.KEY_ADDRESS)))
                    .setPhone(result.getString(result.getColumnIndex(Users.KEY_PHONE)));
        }

        result.close();
        db.close();

        return user;
    }

    public static class Users {
        public static final String TABLE_NAME = "users";
        public static final String KEY_ID = "id";
        public static final String KEY_EMAIL = "email";
        public static final String KEY_PASSWORD = "password";
        public static final String KEY_NAME = "name";
        public static final String KEY_ADDRESS = "address";
        public static final String KEY_PHONE = "phone";
    }

}