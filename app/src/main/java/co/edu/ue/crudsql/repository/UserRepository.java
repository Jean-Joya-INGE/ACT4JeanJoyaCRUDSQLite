package co.edu.ue.crudsql.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.sql.SQLDataException;
import java.util.ArrayList;

import co.edu.ue.crudsql.dataaccess.ManagerDataBase;
import co.edu.ue.crudsql.entities.User;

public class UserRepository {
    private ManagerDataBase dataBase;
    private Context context;
    private View view;
    private User user;

    public UserRepository(Context context, View view) {
        this.context = context;
        this.view = view;
        this.dataBase =new ManagerDataBase(context);
    }

    public void insertUser(User user) {
        SQLiteDatabase dataBaseSQL = null;
        try {
            dataBaseSQL = dataBase.getWritableDatabase();
            if (dataBaseSQL != null) {
                ContentValues values = new ContentValues();
                values.put("use_document", user.getDocument());
                values.put("use_name", user.getName());
                values.put("use_lastname", user.getLastName());
                values.put("use_user", user.getUser());
                values.put("use_pass", user.getPass());
                values.put("use_status", "1");
                long response = dataBaseSQL.insert("user", null, values);
                String message = (response >= 1) ? "Se registro correctamente" : "No se registro";
                Snackbar.make(this.view, message, Snackbar.LENGTH_LONG).show();
            }

        } catch (SQLException e) {
            Log.i("Error  en Base de datos", "insertUser: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (dataBaseSQL != null) dataBaseSQL.close();
        }

    }

    public ArrayList<User> getUserList(){
        SQLiteDatabase dataBaseSQL = dataBase.getReadableDatabase();
        String query = "SELECT*FROM user WHERE use_status = 1";
        ArrayList<User>users=new ArrayList<>();
        Cursor cursor = dataBaseSQL.rawQuery(query,null);
        if (cursor.moveToFirst()){
            do{
                User user = new User();
                user.setDocument(cursor.getInt(0));
                user.setName(cursor.getString(1));
                user.setLastName(cursor.getString(2));
                user.setUser(cursor.getString(3));
                user.setPass(cursor.getString(4));
                users.add(user);

            }while(cursor.moveToNext());
        }
        cursor.close();
        dataBaseSQL.close();
        return users;
    }
    // Buscar usuario por documento
    public User getUserByDocument(int document) {
        SQLiteDatabase dataBaseSQL = dataBase.getReadableDatabase();
        String query = "SELECT * FROM user WHERE use_document = ? AND use_status = 1";
        Cursor cursor = dataBaseSQL.rawQuery(query, new String[]{String.valueOf(document)});

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setDocument(cursor.getInt(0));
            user.setName(cursor.getString(1));
            user.setLastName(cursor.getString(2));
            user.setUser(cursor.getString(3));
            user.setPass(cursor.getString(4));
        }
        cursor.close();
        dataBaseSQL.close();
        return user;
    }

    // Actualizar usuario
    public boolean updateUser(User user) {
        SQLiteDatabase dataBaseSQL = dataBase.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("use_name", user.getName());
        values.put("use_lastname", user.getLastName());
        values.put("use_user", user.getUser());
        values.put("use_pass", user.getPass());

        int rowsAffected = dataBaseSQL.update("user", values,
                "use_document = ?", new String[]{String.valueOf(user.getDocument())});
        dataBaseSQL.close();
        return rowsAffected > 0;
    }

    // Eliminación física (borra permanentemente el registro)
    public boolean deleteUser(int document) {
        SQLiteDatabase dataBaseSQL = dataBase.getWritableDatabase();

        try {
            // Ejecuta el DELETE directamente
            int rowsAffected = dataBaseSQL.delete(
                    "user",                     
                    "use_document = ?",
                    new String[]{String.valueOf(document)}
            );

            return rowsAffected > 0;  // Retorna true si se eliminó al menos un registro
        } finally {
            dataBaseSQL.close();  // Asegura que la conexión se cierre
        }
    }
}
