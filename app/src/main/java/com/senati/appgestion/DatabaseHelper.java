package com.senati.appgestion;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "gestor_tareas.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_TAREAS = "tareas";

    private static final String ID = "id";
    private static final String TITULO = "titulo";
    private static final String DESCRIPCION = "descripcion";
    private static final String ESTADO = "estado";
    private static final String FECHA_VENCIMIENTO = "fecha_vencimiento";
    private static final String FECHA_CREACION = "fecha_creacion";
    private static final String USUARIO = "usuario";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String crearTabla = "CREATE TABLE " + TABLE_TAREAS + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TITULO + " TEXT NOT NULL, " +
                DESCRIPCION + " TEXT, " +
                ESTADO + " TEXT NOT NULL, " +
                FECHA_VENCIMIENTO + " TEXT, " +
                FECHA_CREACION + " TEXT, " +
                USUARIO + " TEXT)";

        db.execSQL(crearTabla);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAREAS);
        onCreate(db);
    }

    // INSERTAR
    public long insertarTarea(Tarea tarea) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(TITULO, tarea.getTitulo());
        values.put(DESCRIPCION, tarea.getDescripcion());
        values.put(ESTADO, tarea.getEstado());
        values.put(FECHA_VENCIMIENTO, tarea.getFechaVencimiento());
        values.put(FECHA_CREACION, tarea.getFechaCreacion());
        values.put(USUARIO, tarea.getUsuario());

        long resultado = db.insert(TABLE_TAREAS, null, values);

        db.close();

        return resultado;
    }

    // OBTENER TODAS LAS TAREAS
    public ArrayList<Tarea> obtenerTareas() {

        ArrayList<Tarea> lista = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_TAREAS +
                        " ORDER BY " + ID + " DESC",
                null
        );

        if (cursor.moveToFirst()) {

            do {

                Tarea tarea = new Tarea();

                tarea.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ID)));
                tarea.setTitulo(cursor.getString(cursor.getColumnIndexOrThrow(TITULO)));
                tarea.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPCION)));
                tarea.setEstado(cursor.getString(cursor.getColumnIndexOrThrow(ESTADO)));
                tarea.setFechaVencimiento(cursor.getString(cursor.getColumnIndexOrThrow(FECHA_VENCIMIENTO)));
                tarea.setFechaCreacion(cursor.getString(cursor.getColumnIndexOrThrow(FECHA_CREACION)));
                tarea.setUsuario(cursor.getString(cursor.getColumnIndexOrThrow(USUARIO)));

                lista.add(tarea);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return lista;
    }

    // OBTENER POR ESTADO
    public ArrayList<Tarea> obtenerTareasPorEstado(String estado) {

        ArrayList<Tarea> lista = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_TAREAS +
                        " WHERE " + ESTADO + " = ? ORDER BY " + ID + " DESC",
                new String[]{estado}
        );

        if (cursor.moveToFirst()) {

            do {

                Tarea tarea = new Tarea();

                tarea.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ID)));
                tarea.setTitulo(cursor.getString(cursor.getColumnIndexOrThrow(TITULO)));
                tarea.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPCION)));
                tarea.setEstado(cursor.getString(cursor.getColumnIndexOrThrow(ESTADO)));
                tarea.setFechaVencimiento(cursor.getString(cursor.getColumnIndexOrThrow(FECHA_VENCIMIENTO)));
                tarea.setFechaCreacion(cursor.getString(cursor.getColumnIndexOrThrow(FECHA_CREACION)));
                tarea.setUsuario(cursor.getString(cursor.getColumnIndexOrThrow(USUARIO)));

                lista.add(tarea);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return lista;
    }

    // ACTUALIZAR
    public boolean actualizarTarea(Tarea tarea) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(TITULO, tarea.getTitulo());
        values.put(DESCRIPCION, tarea.getDescripcion());
        values.put(ESTADO, tarea.getEstado());
        values.put(FECHA_VENCIMIENTO, tarea.getFechaVencimiento());
        values.put(FECHA_CREACION, tarea.getFechaCreacion());
        values.put(USUARIO, tarea.getUsuario());

        int resultado = db.update(
                TABLE_TAREAS,
                values,
                ID + " = ?",
                new String[]{String.valueOf(tarea.getId())}
        );

        db.close();

        return resultado > 0;
    }

    // ELIMINAR
    public boolean eliminarTarea(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        int resultado = db.delete(
                TABLE_TAREAS,
                ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();

        return resultado > 0;
    }
}