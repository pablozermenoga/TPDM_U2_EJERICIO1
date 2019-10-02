package mx.edu.ittepic.tpdm_u2_ejercicio1

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDatos(
    context: Context?, //el activity
    name: String?, //nombre de la base de datos
    factory: SQLiteDatabase.CursorFactory?, //null, cursor es un objeto que obtiene una respuesta de un select
    version: Int //version de la base de datos construida
) : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase?) {
        //se ejecuta LA PRIMERA VEZ QUE SE EJECUTA LA APP
        //y se dedica a construir en el TELÉFONO / TABLETA
        //las tablas que se usarán en la app
        db?.execSQL("CREATE TABLE PERSONA(ID INTEGER, NOMBRE VARCHAR(200), DOMICILIO VARCHAR(200))")
        //TAREA: HACER DECLARACIONES DE VARIABLES EN EL ACTIVITY Y LOS OYENTES
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) { //p1 -> oldVersion, p2 -> newVersion
        //SE EJECUTA SI Y SOLO SI,LA VERSION DE LA BASE DE DATOS DE SQLITE
        //Y LA VERSION DE LA BASE DE DATOS DE LA APLICACIÓN DIFIERE
    }

}