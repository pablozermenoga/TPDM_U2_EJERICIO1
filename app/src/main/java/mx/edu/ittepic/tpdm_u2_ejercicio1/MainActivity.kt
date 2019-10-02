package mx.edu.ittepic.tpdm_u2_ejercicio1

import android.app.Dialog
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var idEt : EditText?=null
    var nombre : EditText?=null
    var domicilio : EditText?=null
    var buscar : Button?=null
    var insertar : Button?=null
    var eliminar : Button?=null
    var actualizar : Button?=null
    var etiqueta : TextView?=null
    var basedatos = BaseDatos(this,"ejercicio1",null,1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        idEt = findViewById(R.id.id)
        nombre = findViewById(R.id.nombre)
        domicilio = findViewById(R.id.domicilio)
        buscar = findViewById(R.id.buscar)
        insertar = findViewById(R.id.insertar)
        eliminar = findViewById(R.id.eliminar)
        actualizar = findViewById(R.id.actualizar)
        etiqueta = findViewById(R.id.etiqueta)

        insertar?.setOnClickListener {
            insertar()
        }
        buscar?.setOnClickListener {
            pedirId(buscar?.text.toString())
        }
        actualizar?.setOnClickListener {
            if(actualizar?.text.toString().startsWith("ACTUALIZAR")){
                pedirId(actualizar?.text.toString())
            }else{
                alertaAplicarCambios()
            }
        }
        eliminar?.setOnClickListener {
            pedirId(eliminar?.text.toString())
        }
    }
    fun alertaAplicarCambios(){
        AlertDialog.Builder(this).setTitle("ATENCION").setMessage("ESTAS SEGURO DE QUE DESEAS APLICAR PERMANENTEMENTE LOS CAMBIOS")
            .setNeutralButton("NO ACTUALIZAR"){Dialog, which-> desbloquear()}
            .setPositiveButton("SI, ACTUALIZAR"){Dialog,which->actualizar()}
            .show()
    }
    fun desbloquear(){
        actualizar?.setText("ACTUALIZAR")
        idEt?.isEnabled=true
        insertar?.isEnabled=true
        buscar?.isEnabled=true
        eliminar?.isEnabled=true
        limpiarCampos()
    }
    fun actualizar(){
        try {
            var transaction = basedatos.writableDatabase
            var SQL = "UPDATE PERSONA SET NOMBRE='camponom', DOMICILIO='campodom' WHERE ID=campoid"
            if (validarCampos()==false){
                mensaje("ERROR","ALGUN CAMPO ESTA VACIO")
                return
            }
            SQL = SQL.replace("ID",idEt?.text.toString())
            SQL = SQL.replace("NOMBRE",nombre?.text.toString())
            SQL = SQL.replace("DOMICILIO",domicilio?.text.toString())
            transaction.execSQL(SQL)
            transaction.close()
            desbloquear()
            mensaje("EXITO","SE ACTUALIZO CORRECTAMENTE")
        }catch (err:SQLiteException){
            mensaje("ERROR", "NO SE ACTUALIZO")
        }
    }

    fun pedirId(etiqueta: String){
        var campo = EditText(this)
        campo.inputType = InputType.TYPE_CLASS_NUMBER
        AlertDialog.Builder(this).setTitle("ATENCIÓN")
            .setMessage("Escriba el ID A ${etiqueta}: ").setView(campo)
            .setNeutralButton("CANCELAR"){dialog,which->
                return@setNeutralButton
            }
            .setPositiveButton("BUSCAR") { dialog, which ->
                if (validarCampo(campo) == false) {
                    Toast.makeText(this@MainActivity, "ERROR CAMPO VACÍO", Toast.LENGTH_LONG).show()
                    return@setPositiveButton
                }
                buscar(campo.text.toString(), etiqueta)

            }.show()

    }
    fun buscar(id: String, botonEtiqueta: String){
        try{
            var transaccion = basedatos.readableDatabase
            var SQL = "SELECT * FROM PERSONA WHERE ID="+id
            var resultado = transaccion.rawQuery(SQL,null)  //solo select
            if(resultado.moveToFirst()==true){
                var cadena ="NOMBRE: "+resultado.getString(1)+
                        "\nDOMICILIO: "+resultado.getString(2)
                if(botonEtiqueta.startsWith("Buscar")){
                        etiqueta?.setText(cadena)
                }
                if(botonEtiqueta.startsWith("Eliminar")){
                    var alerta = AlertDialog.Builder(this)
                    alerta.setTitle("ATENCIÓN").setMessage(cadena)
                        .setNeutralButton("No"){dialog,which->
                            return@setNeutralButton
                        }
                        .setPositiveButton("Sí"){dialog,which->
                            eliminar(id)
                        }
                        .show()
                }
                if(botonEtiqueta.startsWith("Actualizar")){
                    idEt?.setText(resultado.getString(0))
                    nombre?.setText(resultado.getString(1))
                    domicilio?.setText(resultado.getString(2))
                    actualizar?.setText("Aplicar cambios")
                    insertar?.setEnabled(false)
                    eliminar?.setEnabled(false)
                    buscar?.setEnabled(false)
                }
            }else{
                mensaje("ATENCIÓN","AL PARECER NO ENCONTRÉ EL ID.")
            }
            transaccion.close()
        }catch(err:SQLiteException){
            mensaje("ERROR","NO SE PUDO REALIZAR EL SELECT.")
        }
    }

    fun eliminar(id:String){
        try{
            var transaccion = basedatos.writableDatabase
            var SQL = "DELETE FROM PERSONA WHERE ID="+id
            transaccion.execSQL(SQL)
            transaccion.close()
            mensaje("ÉXITO","SE ELIMINÓ CORRECTAMENTE.")

        }catch (err:SQLiteException){
            mensaje("ERROR","NO SE PUDO ELIMINAR.")
        }
    }


    fun insertar(){
        try {
            //CÓDIGO SQL
            var transaccion = basedatos.writableDatabase
            var SQL = "INSERT INTO PERSONA VALUES(ID,'NOMBRE','DOMICILIO')"
            //var SQL = "INSERT INTO PERSONA VALUES(${id?.text.toString()},'${nombre?.text.toString()}','${domicilio?.text.toString()}')"
            //tarea, validar campos -> if
           if(validarCampos()==false){
                mensaje("ERROR","ALGÚN CAMPO ESTÁ VACÍO.")
                return
            }
            SQL = SQL.replace("ID",idEt?.text.toString())
            SQL = SQL.replace("NOMBRE",nombre?.text.toString())
            SQL = SQL.replace("DOMICILIO",domicilio?.text.toString())
            transaccion.execSQL(SQL) //insert, update, delete
            transaccion.close()
            limpiarCampos()
            mensaje("ÉXITO","SE INSERTÓ CORRECTAMENTE.")
        }catch(err: SQLiteException){
            mensaje("ERROR","NO SE PUDO INSERTAR.")
        }
    }

    fun mensaje(titulo:String,texto:String){
        AlertDialog.Builder(this).setTitle(titulo).setMessage(texto).setPositiveButton("Ok"){dialog,which->}.show()
    }
    fun  validarCampos():Boolean{
        if((idEt?.text.toString().toString().isEmpty()) || (nombre?.text.toString().isEmpty()) || (domicilio?.text.toString().isEmpty())){
            return false
        }else{
            return true
        }
    }
    fun validarCampo(campo: EditText): Boolean{
        if(campo.text.toString().isEmpty()){
            return false
        }else{
            return true
        }
    }
    fun limpiarCampos(){
        idEt?.setText("")
        nombre?.setText("")
        domicilio?.setText("")
    }
}
