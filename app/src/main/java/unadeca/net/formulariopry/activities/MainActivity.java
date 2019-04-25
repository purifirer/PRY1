package unadeca.net.formulariopry.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import unadeca.net.formulariopry.R;
import unadeca.net.formulariopry.database.models.Person;

public class MainActivity extends AppCompatActivity {
    private ListView lista;
    private CoordinatorLayout view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lista = findViewById(R.id.lista);
        view = findViewById(R.id.contenidoMain);

        ArrayAdapter<String> adaptador = new ArrayAdapter<>(this,android.R.layout.simple_list_item_2,getPerson());

        lista.setAdapter(adaptador);

        //Mostrar adapter
        setAdapter();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Person test = SQLite.select().from(Person.class).querySingle();
                //Snackbar.make(view, test.nombre + " " + test.apellido, Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                mostrarDialogo();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String[] getPerson(){
        List<Person> listado = SQLite.select().from(Person.class).queryList();
        String[] array = new String[listado.size()];
        for(int c =0; c < listado.size(); c++){
            array[c] = listado.get(c).toString();
        }
        return array;
    }

    public List<Person> getListPerson(){
        return SQLite.select().from(Person.class).queryList();
    }

    private void setAdapter(){
        //lista.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getPerson()));
        lista.setAdapter(new CustomAdapter(getListPerson(), getApplicationContext()));
    }

    public void mostrarDialogo(){
        //Inflador
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View formmulario = layoutInflater.inflate(R.layout.formulario,null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(formmulario);

        //VARIABLES
        final TextInputLayout nombre = formmulario.findViewById(R.id.txtNombre);
        final TextInputLayout apellido = formmulario.findViewById(R.id.txtApellido);
        final TextInputLayout edad = formmulario.findViewById(R.id.txtEdad);
        final TextInputLayout pais = formmulario.findViewById(R.id.txtPais);
        final TextInputLayout ocupacion = formmulario.findViewById(R.id.txtOcupacion);

        builder.setMessage("Llena el formulario y qeuda participando!")
                .setTitle("Partcipa hoy mismo")
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    validacionBD(nombre, apellido, edad, pais, ocupacion);
                    guardarBD(nombre, apellido, edad, pais, ocupacion);


                }catch(Exception e){ Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                }
                dialog.dismiss();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialogo = builder.create();
        dialogo.show();
    }

    private void validacionBD(TextInputLayout n, TextInputLayout a, TextInputLayout e, TextInputLayout p, TextInputLayout o) throws Exception{
        if(n.getEditText().getText().toString().isEmpty()) throw  new Exception("Se ocupa el Nombre de la persona");
        if(a.getEditText().getText().toString().isEmpty()) throw  new Exception("Se ocupa el Apellido de la persona");
        if(e.getEditText().getText().toString().isEmpty()) throw  new Exception("Se ocupa la Edad de la parsona");
        if(p.getEditText().getText().toString().isEmpty()) throw  new Exception("Se ocupa la Nacionalidd de la persona");
        if(o.getEditText().getText().toString().isEmpty()) throw  new Exception("Se necesit la ocupación de la persona");
    }

    private void guardarBD (TextInputLayout n, TextInputLayout a, TextInputLayout e, TextInputLayout p, TextInputLayout o){
        Person person = new Person();
        person.nombre = n.getEditText().getText().toString();
        person.apellido = n.getEditText().getText().toString();
        person.edad =  Integer.parseInt(e.getEditText().getText().toString());
        person.pais = p.getEditText().getText().toString();
        person.ocupacion = o.getEditText().getText().toString();
        person.save();

        Snackbar.make(view, "Guardado con éxito", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        setAdapter();

    }

    private void eliminarBD(){
        //LayoutInflater elimInflater = LayoutInflater.from(this);


        final AlertDialog.Builder elim = new AlertDialog.Builder(this);
        elim.setMessage("¿Está seguro que desea eliminar la persona?").setTitle("Eliminar Persona")
                .setCancelable(false)
                .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setAdapter();
                        Delete.table(Person.class);
                        Snackbar.make(view, "Se eliminado el usuario", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        dialog.dismiss();
                    }
                }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

    }

    private void verDB(){


        setAdapter();
        List<Person> personList = SQLite.select().from(Person.class).queryList();

        Snackbar.make(view, "Cargado con éxito", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }



}
