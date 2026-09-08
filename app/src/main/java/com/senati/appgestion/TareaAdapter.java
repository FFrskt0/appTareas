package com.senati.appgestion;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.TareaViewHolder> {

    private Context context;
    private ArrayList<Tarea> listaTareas;
    private DatabaseHelper databaseHelper;

    public TareaAdapter(Context context, ArrayList<Tarea> listaTareas) {

        this.context = context;
        this.listaTareas = listaTareas;
        this.databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public TareaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_tarea, parent, false);

        return new TareaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TareaViewHolder holder, int position) {

        Tarea tarea = listaTareas.get(position);

        holder.txtTitulo.setText(tarea.getTitulo());
        holder.txtDescripcion.setText(tarea.getDescripcion());
        holder.txtEstado.setText("Estado: " + tarea.getEstado());
        holder.txtFecha.setText("Vencimiento: " + tarea.getFechaVencimiento());
        holder.txtFechaCreacion.setText("Creada: " + tarea.getFechaCreacion());
        holder.txtUsuario.setText("Usuario: " + tarea.getUsuario());

        holder.btnEditar.setOnClickListener(v -> {

            Intent intent = new Intent(context, FormularioTareaActivity.class);

            intent.putExtra("id", tarea.getId());
            intent.putExtra("titulo", tarea.getTitulo());
            intent.putExtra("descripcion", tarea.getDescripcion());
            intent.putExtra("estado", tarea.getEstado());
            intent.putExtra("fecha_vencimiento", tarea.getFechaVencimiento());
            intent.putExtra("fecha_creacion", tarea.getFechaCreacion());
            intent.putExtra("usuario", tarea.getUsuario());

            context.startActivity(intent);
        });

        holder.btnEliminar.setOnClickListener(v -> {

            new AlertDialog.Builder(context)
                    .setTitle("Eliminar tarea")
                    .setMessage("¿Deseas eliminar esta tarea?")
                    .setPositiveButton("Sí", (dialog, which) -> {

                        databaseHelper.eliminarTarea(tarea.getId());

                        listaTareas.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, listaTareas.size());
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return listaTareas.size();
    }

    public static class TareaViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitulo;
        TextView txtDescripcion;
        TextView txtEstado;
        TextView txtFecha;
        TextView txtFechaCreacion;
        TextView txtUsuario;

        Button btnEditar;
        Button btnEliminar;
        public TareaViewHolder(@NonNull View itemView) {

            super(itemView);

            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcion);
            txtEstado = itemView.findViewById(R.id.txtEstado);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            txtFechaCreacion = itemView.findViewById(R.id.txtFechaCreacion);
            txtUsuario = itemView.findViewById(R.id.txtUsuario);

            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}