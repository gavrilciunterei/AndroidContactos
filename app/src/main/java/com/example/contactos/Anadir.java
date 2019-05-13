package com.example.contactos;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Anadir extends AppCompatActivity {

    private final String []opciones={"FIJO","MOVIL"};
    private DataBaseExecute dbe;


    private EditText editTextNombre, editTextApodo, editTextEmpresa, editTextTelefono, editTextEmail, editTextNotas;
    private EditText editTextCalle, editTextPiso, editTextNumero, editTextPuerta, editTextCodigo, editTextCiudad;
    private Button buttonAnadir, buttonAbrirGaleria;
    private ImageView imageViewimg;
    private Spinner spinnerTipo, spinnerProvincia;
    private long backPressedTime;
    private ArrayAdapter<String> adapterSpinner, adapterSpinnerPronvincia;
    private boolean editar = false;



    //listviews dinamicos telefonos
    private Button buttonAddTelefono;
    private ArrayList<Telefono> arrayListTelefonos;
    private NonScrollListView list_telefono;
    private Adaptador_Telefono at;


    //listviw correo
    private Button buttonAddCorreo;
    private ArrayList<String> arrayListCorreo;
    private NonScrollListView list_correo;
    private ArrayAdapter<String> arrayAdapterCorreo;


    //listviw notas
    private Button buttonAddNotas;
    private ArrayList<String> arrayListNotas;
    private NonScrollListView list_Notas;
    private ArrayAdapter<String> arrayAdapterNotas;

    private static final int REQUEST_SELECT_PHOTO = 1;
    private Bitmap bmp;

    private Contacto conV;
    private Direccion dirV;
    private ArrayList<String> emailsV;
    private ArrayList<String> notasV;
    private ArrayList<Telefono> telefonosV;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anadir);
        getSupportActionBar().hide();

        dbe = new DataBaseExecute(this);

        editTextNombre = (EditText) findViewById(R.id.editTextNombre);
        editTextApodo = (EditText) findViewById(R.id.editTextApodo);
        editTextEmpresa = (EditText) findViewById(R.id.editTextEmpresa);
        editTextTelefono = (EditText) findViewById(R.id.editTextTelefono);
        editTextEmail = (EditText) findViewById(R.id.editTextCorreo);
        editTextNotas = (EditText) findViewById(R.id.editTextNotas);
        editTextCalle = (EditText) findViewById(R.id.editTextCalle);
        editTextPiso = (EditText) findViewById(R.id.editTextPiso);
        editTextNumero = (EditText) findViewById(R.id.editTextNumero);
        editTextPuerta = (EditText) findViewById(R.id.editTextPuerta);
        editTextCodigo = (EditText) findViewById(R.id.editTextCodigo);
        editTextCiudad = (EditText) findViewById(R.id.editTextCiudad);


        imageViewimg = (ImageView) findViewById(R.id.imageViewimg);
        buttonAbrirGaleria = (Button) findViewById(R.id.buttonAbrirGaleria);
        buttonAbrirGaleria.setOnClickListener(new abrirGaleria());



        spinnerTipo = (Spinner) findViewById(R.id.spinnerTipo);
        adapterSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, opciones);
        spinnerTipo.setAdapter(adapterSpinner);

        Provincias provincias = new Provincias();
        spinnerProvincia = (Spinner) findViewById(R.id.spinnerProvincia);
        adapterSpinnerPronvincia = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, provincias.getProvincias());
        spinnerProvincia.setAdapter(adapterSpinnerPronvincia);


        //Anadir telefonos a un listView y borrar manteniendo pulsado
        buttonAddTelefono = (Button) findViewById(R.id.buttonAddTelefono);
        buttonAddTelefono.setOnClickListener(new addViewTelefono());

        arrayListTelefonos = new ArrayList<>();
        list_telefono = (NonScrollListView) findViewById(R.id.list_telefono);
        at = new Adaptador_Telefono(this, arrayListTelefonos);
        list_telefono.setAdapter(at);
        list_telefono.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                arrayListTelefonos.remove(position);
                at.notifyDataSetChanged();
                return true;

            }
        });

        //Anadir correo
        buttonAddCorreo = (Button) findViewById(R.id.buttonAddEmail);
        buttonAddCorreo.setOnClickListener(new addViewCorreo());

        arrayListCorreo = new ArrayList<>();
        list_correo = (NonScrollListView) findViewById(R.id.list_correo);
        arrayAdapterCorreo = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, arrayListCorreo);
        list_correo.setAdapter(arrayAdapterCorreo);

        list_correo.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                arrayAdapterCorreo.remove(arrayListCorreo.get(position));
                arrayAdapterCorreo.notifyDataSetChanged();
                return true;

            }
        });

        //Anadir notas
        buttonAddNotas = (Button) findViewById(R.id.buttonAddNotas);
        buttonAddNotas.setOnClickListener(new addViewNotas());

        arrayListNotas= new ArrayList<>();
        list_Notas = (NonScrollListView) findViewById(R.id.list_notas);
        arrayAdapterNotas = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, arrayListNotas);
        list_Notas.setAdapter(arrayAdapterNotas);

        list_Notas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                arrayAdapterNotas.remove(arrayListNotas.get(position));
                arrayAdapterNotas.notifyDataSetChanged();
                return true;

            }
        });


        //Apartado edicion
        Bundle bundle=getIntent().getExtras();

        if(bundle != null) {
            conV = (Contacto) bundle.getSerializable("CONTACTO");
            dirV = (Direccion) bundle.getSerializable("DIRECCION");
            notasV = bundle.getStringArrayList("NOTA");
            emailsV = bundle.getStringArrayList("EMAIL");
            telefonosV= (ArrayList<Telefono>) bundle.getSerializable("TELEFONO");

            llenarCamposEditar();
            editar = true;
        }

        buttonAnadir = (Button) findViewById(R.id.buttonAnadir);
        if(editar) {
            buttonAnadir.setOnClickListener(new editarContacto());
        }else{
            buttonAnadir.setOnClickListener(new anadirContacto());
    }



    }

    private void llenarCamposEditar(){

        Contacto con = conV;
        editTextNombre.setText(con.getNombre());
        editTextApodo.setText(con.getApodo());
        editTextEmpresa.setText(con.getEmpresa());

        byte[] bytes = con.getImg();
        imageViewimg.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
        Direccion dir = dirV;

        editTextCiudad.setText(dir.getCiudad());
        editTextCalle.setText(dir.getCalle());
        editTextCodigo.setText(dir.getCodigoPostal());
        editTextPiso.setText(dir.getPiso());
        editTextPuerta.setText(dir.getPuerta());
        editTextNumero.setText(dir.getNumero());


        List<String> list = new ArrayList<String>();
        list.add(dir.getProvincia());
        adapterSpinnerPronvincia = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,list);
        spinnerProvincia.setAdapter(adapterSpinnerPronvincia);


        arrayListTelefonos.addAll(telefonosV);
        at.notifyDataSetChanged();

        arrayListCorreo.addAll(emailsV);
        arrayAdapterCorreo.notifyDataSetChanged();

        arrayListNotas.addAll(notasV);
        arrayAdapterNotas.notifyDataSetChanged();

    }

    private class addViewTelefono implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if(arrayListTelefonos.size() <3) {
                if(editTextTelefono.getText().toString().trim().length() > 0) {
                    Telefono t = new Telefono(editTextTelefono.getText().toString(), spinnerTipo.getSelectedItem().toString());
                    arrayListTelefonos.add(t);
                    at.notifyDataSetChanged();
                }
            }else{
                maxCamp();
            }
        }
    }

    private class addViewCorreo implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if(arrayListCorreo.size() <3) {
                if(editTextEmail.getText().toString().trim().length() > 0) {

                    arrayListCorreo.add(editTextEmail.getText().toString());
                    arrayAdapterCorreo.notifyDataSetChanged();
                }
            }else{
                maxCamp();
            }
        }
    }
    private class addViewNotas implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if(arrayListNotas.size() <3) {
                if(editTextNotas.getText().toString().trim().length() > 0) {

                    arrayListNotas.add(editTextNotas.getText().toString());
                    arrayAdapterNotas.notifyDataSetChanged();
                }
            }else{
                maxCamp();
            }
        }
    }



    private class abrirGaleria implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent ii = new Intent(Intent.ACTION_PICK) ;
            ii.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    MediaStore.Images.Media.CONTENT_TYPE) ;
            startActivityForResult(ii,REQUEST_SELECT_PHOTO) ;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_SELECT_PHOTO && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            imageViewimg.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }
    }

    private byte[] devolverImagen(){

        BitmapDrawable drawable = (BitmapDrawable) imageViewimg.getDrawable();
        bmp = drawable.getBitmap();

        ByteArrayOutputStream baos = new ByteArrayOutputStream(20480);
        bmp.compress(Bitmap.CompressFormat.PNG, 0 , baos);
        byte[] blob = baos.toByteArray();

        return blob;
    }

    private class anadirContacto implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            int maxid = dbe.getLastId();


            ContentValues nuevoRegistroContacto = new ContentValues();
            Contacto contacto = new Contacto(maxid, editTextNombre.getText().toString(), editTextApodo.getText().toString(), editTextEmpresa.getText().toString(), devolverImagen());
            nuevoRegistroContacto.put("ID", contacto.getId());
            nuevoRegistroContacto.put("NOMBRE", contacto.getNombre());
            nuevoRegistroContacto.put("APODO", contacto.getApodo());
            nuevoRegistroContacto.put("EMPRESA", contacto.getEmpresa());
            nuevoRegistroContacto.put("IMG", contacto.getImg());
            dbe.anadirContacto(nuevoRegistroContacto, "CONTACTO");




            for(int i = 0; i < arrayListCorreo.size(); i++) {

                ContentValues nuevoRegistroEmail = new ContentValues();
                nuevoRegistroEmail.put("ID", maxid);
                nuevoRegistroEmail.put("EMAIL", arrayListCorreo.get(i).toString());
                dbe.anadirContacto(nuevoRegistroEmail, "EMAIL");

            }

            for(int i = 0; i < arrayListNotas.size(); i++) {
                ContentValues nuevoRegistroNotas = new ContentValues();
                nuevoRegistroNotas.put("ID", maxid);
                nuevoRegistroNotas.put("NOTA", arrayListNotas.get(i).toString());
                dbe.anadirContacto(nuevoRegistroNotas, "NOTAS");
            }

            for(int i = 0; i < arrayListTelefonos.size(); i++) {
                ContentValues nuevoRegistroTelefono = new ContentValues();

                nuevoRegistroTelefono.put("ID", maxid);
                nuevoRegistroTelefono.put("TIPO", arrayListTelefonos.get(i).getTipo());
                nuevoRegistroTelefono.put("TELEFONO", arrayListTelefonos.get(i).getTelefono());

                dbe.anadirContacto(nuevoRegistroTelefono, "TELEFONO");
            }


            ContentValues nuevoRegistroDireccion = new ContentValues();
            Direccion direccion = new Direccion(maxid, editTextCalle.getText().toString(), editTextNumero.getText().toString(),
                    editTextPiso.getText().toString(), editTextPuerta.getText().toString(), editTextCodigo.getText().toString(),
                    editTextCiudad.getText().toString(), spinnerProvincia.getSelectedItem().toString());


            nuevoRegistroDireccion.put("ID", direccion.getId());
            nuevoRegistroDireccion.put("CALLE", direccion.getCalle());
            nuevoRegistroDireccion.put("NUMERO", direccion.getNumero());
            nuevoRegistroDireccion.put("PISO", direccion.getPiso());
            nuevoRegistroDireccion.put("PUERTA", direccion.getPuerta());
            nuevoRegistroDireccion.put("CODIGO", direccion.getCodigoPostal());
            nuevoRegistroDireccion.put("CIUDAD", direccion.getCiudad());
            nuevoRegistroDireccion.put("PROVINCIA", direccion.getProvincia());
            dbe.anadirContacto(nuevoRegistroDireccion, "DIRECCION");


            mensajeNormalContacto();


        }
    }

    private class editarContacto implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            Contacto contactoNuevo = new Contacto(conV.getId(), editTextNombre.getText().toString(), editTextApodo.getText().toString(), editTextEmpresa.getText().toString(), devolverImagen());
            String idEdit = Integer.toString(contactoNuevo.getId());

            if(contactoNuevo.compareTo(conV) != 0){

                ContentValues nuevoRegistroContacto = new ContentValues();
                nuevoRegistroContacto.put("NOMBRE", contactoNuevo.getNombre());
                nuevoRegistroContacto.put("APODO", contactoNuevo.getApodo());
                nuevoRegistroContacto.put("EMPRESA", contactoNuevo.getEmpresa());
                nuevoRegistroContacto.put("IMG", contactoNuevo.getImg());

               dbe.editarContacto(nuevoRegistroContacto, "CONTACTO", idEdit);


            }

            Direccion direccionNueva = new Direccion(dirV.getId(),editTextCalle.getText().toString(), editTextNumero.getText().toString(),
                    editTextPiso.getText().toString(), editTextPuerta.getText().toString(), editTextCodigo.getText().toString(),
                    editTextCiudad.getText().toString(), spinnerProvincia.getSelectedItem().toString());

            if(direccionNueva.compareTo(dirV) != 0){


                ContentValues nuevoRegistroDireccion = new ContentValues();

                nuevoRegistroDireccion.put("CALLE", direccionNueva.getCalle());
                nuevoRegistroDireccion.put("NUMERO", direccionNueva.getNumero());
                nuevoRegistroDireccion.put("PISO", direccionNueva.getPiso());
                nuevoRegistroDireccion.put("PUERTA", direccionNueva.getPuerta());
                nuevoRegistroDireccion.put("CODIGO", direccionNueva.getCodigoPostal());
                nuevoRegistroDireccion.put("CIUDAD", direccionNueva.getCiudad());
                nuevoRegistroDireccion.put("PROVINCIA", direccionNueva.getProvincia());
                dbe.editarContacto(nuevoRegistroDireccion, "DIRECCION", idEdit);

            }

            if(!arrayListTelefonos.equals(telefonosV)){
                dbe.borrarPorID(idEdit, "TELEFONO");
                for(int i = 0; i < arrayListTelefonos.size(); i++) {

                    ContentValues nuevoRegistroTelefono = new ContentValues();
                    nuevoRegistroTelefono.put("ID", idEdit);
                    nuevoRegistroTelefono.put("TIPO", arrayListTelefonos.get(i).getTipo());
                    nuevoRegistroTelefono.put("TELEFONO", arrayListTelefonos.get(i).getTelefono());

                    dbe.anadirContacto(nuevoRegistroTelefono, "TELEFONO");
                }
            }
            if(!arrayListNotas.equals(notasV)){
                dbe.borrarPorID(idEdit, "NOTAS");
                for(int i = 0; i < arrayListNotas.size(); i++) {
                    ContentValues nuevoRegistroNotas = new ContentValues();
                    nuevoRegistroNotas.put("ID", idEdit);
                    nuevoRegistroNotas.put("NOTA", arrayListNotas.get(i).toString());
                    dbe.anadirContacto(nuevoRegistroNotas, "NOTAS");
                }

            }

            if(!arrayListCorreo.equals(emailsV)){

                dbe.borrarPorID(idEdit, "EMAIL");
                for(int i = 0; i < arrayListCorreo.size(); i++) {

                    ContentValues nuevoRegistroEmail = new ContentValues();
                    nuevoRegistroEmail.put("ID", idEdit);
                    nuevoRegistroEmail.put("EMAIL", arrayListCorreo.get(i).toString());
                    dbe.anadirContacto(nuevoRegistroEmail, "EMAIL");

                }

            }
            mensajeContactoEditado();


        }
    }

    public void mensajeContactoEditado()
    {
        Toast mensaje =
                Toast.makeText(getApplicationContext(),
                        "Contacto editado correctamente", Toast.LENGTH_SHORT);
        mensaje.show();
    }



    public void mensajeNormalContacto()
    {
        Toast mensaje =
                Toast.makeText(getApplicationContext(),
                        "Contacto añadido correctamente", Toast.LENGTH_SHORT);
        mensaje.show();
    }


    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            finish();
        } else {
            Toast.makeText(this, "Pulsa otra vez para salir.", Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();
    }

    public void maxCamp() {

            Toast.makeText(this, "Solo se pueden añadir 3 campos", Toast.LENGTH_SHORT).show();

    }



}