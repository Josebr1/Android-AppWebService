package br.com.treinaweb.appwebservice;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by jose on 25/06/2016.
 */
public class ClienteProvider extends ContentProvider {

    public static final String AUTHORITY = "br.com.treinaweb.clientes";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String ID_PATH = "id/*";

    public static final int CLIENTES = 1;
    public static final int BY_ID = 2;

    protected ClienteDbHelper dbHelper;

    static final UriMatcher mather = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        mather.addURI(AUTHORITY, null, CLIENTES);
        mather.addURI(AUTHORITY, ID_PATH, BY_ID);

        mather.addURI(AUTHORITY, "#", CLIENTES);
    }

    @Override
    public boolean onCreate() {

        dbHelper = new ClienteDbHelper(getContext());
        if(dbHelper == null){
            return false;
        }else{
            return true;
        }

    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String order = null;
        Cursor result = null;

        if(sortOrder != null){}
            order = sortOrder;
        int match = mather.match(uri);

        try{
            switch (match){
                case CLIENTES:
                    result = db.query(ClienteDbHelper.TABLE, projection, selection, selectionArgs, null, null, order );
                    break;
                case BY_ID:
                    result = db.query(ClienteDbHelper.TABLE, projection, ClienteDbHelper.C_ID + "=?",
                            new String[]{uri.getLastPathSegment()}, null, null, order);
                    break;
            }

        }catch (Exception e){
            Log.i("Log: Pro. query+ ", e.getMessage());
        }

        return result;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        int match = mather.match(uri);

        if(match == 1){
            return "vnd.android.cursor.dir/vnd.treinaweb.clientes";
        }else{
            return "vnd.android.cursor.item/vnd.treinaweb.cliente";
        }

    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int match = mather.match(uri);

        long newId = 0;
        if(match != 1){
            throw new IllegalArgumentException("Wrong Uri " + uri.toString());
        }
        if(contentValues != null){
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            newId = db.insert(ClienteDbHelper.TABLE, null, contentValues);
            return Uri.withAppendedPath(uri, String.valueOf(newId));
        }else{
            return null;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = mather.match(uri);

        if(match == 1){
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            return db.delete(ClienteDbHelper.TABLE, selection, selectionArgs);
        }else{
            return 0;
        }

    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int result = 0;

        try{
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            result = db.update(ClienteDbHelper.TABLE, contentValues, selection, selectionArgs);
        }catch (Exception e){
            Log.i("Log: Pro. update  ", e.getMessage());
        }
        return result;
    }
}
