package unadeca.net.formulariopry.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import unadeca.net.formulariopry.R;
import unadeca.net.formulariopry.database.models.Person;

public class CustomAdapter extends ArrayAdapter<Person> {

    private List<Person> dataSet;
    Context mContext;
    CoordinatorLayout view;

    // View lookup cache
    private static class ViewHolder {
        TextView txitem;
        ImageView imdelete;
        ImageView imupdate;
    }

    public CustomAdapter(List<Person> data, Context context) {
        super(context, R.layout.item, data);
        this.dataSet = data;
        this.mContext = context;
        //this.view = l;

    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Person dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item, parent, false);
            viewHolder.txitem = convertView.findViewById(R.id.textoItem);
            viewHolder.imdelete = convertView.findViewById(R.id.imgDelete);
            viewHolder.imupdate = convertView.findViewById(R.id.imgUpdate);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txitem.setText(dataModel.toString());

        viewHolder.txitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verDB(dataModel);

            }
        });


        viewHolder.imupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialog(dataModel);
            }
        });
        viewHolder.imdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataModel.delete();
                dataSet.remove(dataModel);
                notifyDataSetChanged();
                Toast.makeText(getContext(), "Se elimino la persona ", Toast.LENGTH_LONG).show();

            }
        });
        // Return the completed view to render on screen
        return convertView;
    }

    public void mostrarDialog(final Person person) {
        //Inflador
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View formmulario = layoutInflater.inflate(R.layout.formulario, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(formmulario);

        //vARIABLES
        final TextInputLayout nombre = formmulario.findViewById(R.id.txtNombre);
        nombre.getEditText().setText(person.nombre);
        final TextInputLayout apellido = formmulario.findViewById(R.id.txtApellido);
        nombre.getEditText().setText(person.apellido);
        final TextInputLayout edad = formmulario.findViewById(R.id.txtEdad);
        nombre.getEditText().setText(person.edad);
        final TextInputLayout pais = formmulario.findViewById(R.id.txtPais);
        nombre.getEditText().setText(person.pais);
        final TextInputLayout ocupacion = formmulario.findViewById(R.id.txtOcupacion);
        nombre.getEditText().setText(person.ocupacion);

        builder.setMessage("Complete la informacion")
                .setTitle("Añadir persona")
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            validacionBD(nombre, apellido, edad, pais, ocupacion);
                            guardarBD(nombre, apellido, edad, pais, ocupacion);


                        } catch (Exception e) {
                            Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG)
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

    private void validacionBD(TextInputLayout n, TextInputLayout a, TextInputLayout e, TextInputLayout p, TextInputLayout o) throws Exception {
        if (n.getEditText().getText().toString().isEmpty())
            throw new Exception("OCUPAMOS TU NOMBRE");
        if (a.getEditText().getText().toString().isEmpty())
            throw new Exception("OUPAMOS TU APELLIDO");
        if (e.getEditText().getText().toString().isEmpty())
            throw new Exception("Se ocupa la Edad de la parsona");
        if (p.getEditText().getText().toString().isEmpty())
            throw new Exception("Se ocupa la Nacionalidd de la persona");
        if (o.getEditText().getText().toString().isEmpty())
            throw new Exception("Se necesita la ocupación de la persona");
    }

    private void guardarBD(TextInputLayout n, TextInputLayout a, TextInputLayout e, TextInputLayout p, TextInputLayout o) {
        Person person = new Person();
        person.nombre = n.getEditText().getText().toString();
        person.apellido = n.getEditText().getText().toString();
        person.edad = Integer.parseInt(e.getEditText().getText().toString());
        person.pais = p.getEditText().getText().toString();
        person.ocupacion = o.getEditText().getText().toString();
        person.save();

        Snackbar.make(view, "Estas participando desde ahora", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        notifyDataSetChanged();

    }

    private void eliminarBD() {
        //LayoutInflater elimInflater = LayoutInflater.from(this);


        final AlertDialog.Builder elim = new AlertDialog.Builder(mContext);
        elim.setMessage("¿Está seguro que desea eliminar la persona?").setTitle("Eliminar Persona")
                .setCancelable(false)
                .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Delete.table(Person.class);
                        Snackbar.make(view, "Eliminaste tu participacion", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        dialog.dismiss();
                    }
                }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        elim.show();
    }

    private void verDB(Person dataModel) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View item = layoutInflater.inflate(R.layout.item, null);

        final TextView txtITEM = item.findViewById(R.id.textoItem);

        List<Person> personList = SQLite.select().from(Person.class).queryList();

        Snackbar.make(view, "Cargado con éxito", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}