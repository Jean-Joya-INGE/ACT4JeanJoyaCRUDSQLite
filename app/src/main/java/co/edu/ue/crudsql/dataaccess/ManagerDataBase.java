package co.edu.ue.crudsql.dataaccess;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ManagerDataBase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "db_users";
    private static final int VERSION = 1;
    private static final   String TABLE_USERS= "user";
    private SQLiteDatabase db;

    public ManagerDataBase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_USER ="CREATE TABLE "+ TABLE_USERS+
                "(use_document INTEGER PRIMARY KEY, use_name varchar(200) NOT NULL,use_lastname varchar(200) NOT NULL, " +
                "use_user varchar(35) NOT NULL, " +
                "use_pass varchar(35) NOT NULL, use_status varchar(1));";// tres primera letras
        db.execSQL(CREATE_USER);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ TABLE_USERS);
        onCreate(sqLiteDatabase); //cambio de db
    }
}
