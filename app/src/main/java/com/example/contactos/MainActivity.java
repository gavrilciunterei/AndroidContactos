package com.example.contactos;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase db=null;
    private DataBaseHelper usdbh;
    private Button buttonAnadir;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        buttonAnadir = (Button)findViewById(R.id.buttonAnadir);
        buttonAnadir.setOnClickListener(new newAnadir());


        usdbh = new DataBaseHelper(this, "DBcontactos", null, 1);
        SQLiteDatabase db= usdbh.getWritableDatabase();


        this.listView = (ListView) findViewById(R.id.lsvContactos);
        this.listView.setAdapter(new Adaptador(this, getContacto()));


    }
    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        //Refresh your stuff here
    }

    private class newAnadir implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            Intent intent = new Intent(MainActivity.this, Anadir.class);
            startActivity(intent);

        }
    }


    public ArrayList<Contacto> getContacto() {
        ArrayList<Contacto> contacto = new ArrayList<>();

        String sql = "SELECT * FROM CONTACTO ORDER BY NOMBRE ASC";
        SQLiteDatabase db = usdbh.getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);

        while (c.moveToNext()){
            Contacto con = new Contacto(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getBlob(4));
            contacto.add(con);
        }

        return contacto;
    }

}