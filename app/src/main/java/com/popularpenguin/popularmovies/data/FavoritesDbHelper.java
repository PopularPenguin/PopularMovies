package com.popularpenguin.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.popularpenguin.popularmovies.data.FavoritesContract.FavoritesEntry;

public class FavoritesDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "favorites.db";
    private static final int DB_VERSION = 1;

    public FavoritesDbHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE " + FavoritesEntry.TABLE_NAME + " (" +
                FavoritesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavoritesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL UNIQUE, " +
                FavoritesEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_MOVIE_RATING + " REAL NOT NULL, " +
                FavoritesEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL" +
                ");";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // not being upgraded yet, so just drop the table
        db.execSQL("DROP TABLE IF EXISTS " + FavoritesEntry.TABLE_NAME);
        onCreate(db);
    }
}
