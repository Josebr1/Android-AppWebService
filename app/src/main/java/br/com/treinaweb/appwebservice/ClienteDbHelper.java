package br.com.treinaweb.appwebservice;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by jose on 25/06/2016.
 */
public class ClienteDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "clientes.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE = "cliente";
    public static final String C_ID = "_id";
    public static final String C_NOME = "nome";
    public static final String C_EMAIL = "email";

    public ClienteDbHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + TABLE
                + "( " + C_ID + " integer primary key autoincrement, "
                + C_NOME + " text, "
                + C_EMAIL + " text)";
        try{
            db.execSQL(sql);
        }catch (Exception e){
            Log.i("Log: SQLHelper", e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        try {
            db.execSQL("drop table if exist " + TABLE);
            onCreate(db);
        }catch (Exception e){
            Log.i("Log: SQLHelper", e.getMessage());
        }

    }
}
