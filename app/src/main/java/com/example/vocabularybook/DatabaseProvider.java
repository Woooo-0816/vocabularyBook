package com.example.vocabularybook;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.vocabularybook.MyDatabaseHelper;

public class DatabaseProvider extends ContentProvider {

    public static final int Word_DIR=0;
    public static final int Word_ITEM=1;
    public static final String AUTHORITY = "com.example.vocabularybook.provider";
    private static UriMatcher uriMatcher;
    private MyDatabaseHelper dbHelper;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY,"word",Word_DIR);
        uriMatcher.addURI(AUTHORITY,"word/*",Word_ITEM);
    }

    public DatabaseProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int deletedRows = 0;
        switch (uriMatcher.match(uri)){
            case Word_DIR:
                deletedRows = db.delete("Word",selection,selectionArgs);
                break;
            case Word_ITEM:
                String ENG = uri.getPathSegments().get(1);
                deletedRows = db.delete("Word","ENG = ?",new String[]{ENG});
                break;
            default:
                break;
        }
        return deletedRows;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case Word_DIR:
                return "vnd.android.cursor.dir/vnd.com.example.vocabularybook.provider.word";
            case Word_ITEM:
                return "vnd.android.cursor.item/vnd.com.example.vocabularybook.provider.word";
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri uriReturn = null;
        switch (uriMatcher.match(uri)){
            case Word_DIR:
            case Word_ITEM:
                long newWordId = db.insert("Word",null,values);
                uriReturn = Uri.parse("content://"+AUTHORITY+"/word/"+newWordId);
                break;
        }
        return uriReturn;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new MyDatabaseHelper(getContext(),"WordBook.db",null,2);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        switch (uriMatcher.match(uri)){
            case Word_DIR:
                cursor = db.query("Word",projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case Word_ITEM:
                String ENG=uri.getPathSegments().get(1);
                cursor = db.query("Word",projection,"ENG = ?",new String[]{ENG},null,null,sortOrder);
                break;
            default:
                break;
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int updateRows = 0;
        switch (uriMatcher.match(uri)){
            case Word_DIR:
                updateRows = db.update("Word",values,selection,selectionArgs);
                break;
            case Word_ITEM:
                String ENG = uri.getPathSegments().get(1);
                updateRows = db.update("Word",values,"ENG = ?",new String[]{ENG});
                break;
            default:
                break;
        }
        return updateRows;
    }
}
