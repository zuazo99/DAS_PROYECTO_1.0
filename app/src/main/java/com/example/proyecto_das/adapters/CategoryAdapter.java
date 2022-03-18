package com.example.proyecto_das.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyecto_das.R;
import com.example.proyecto_das.models.Categoria;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private Context context;
    private List<Categoria> categorias;
    private int layout;
    private OnItemClickListener itemClickListener;


    public CategoryAdapter(List<Categoria> categorias, int layout, OnItemClickListener itemClickListener) {
        this.context = context;
        this.categorias = categorias;
        this.layout = layout;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        context = parent.getContext();
        ViewHolder vh = new ViewHolder(v);
        return vh;    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(categorias.get(position), itemClickListener);
    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView categoriaNombre;
        public TextView categoriaDescripcion;
        public ImageView categoriaFoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoriaNombre = itemView.findViewById(R.id.textViewCategoriaNombre);
            categoriaDescripcion = itemView.findViewById(R.id.textViewCategoriaDescripcion);
            categoriaFoto = itemView.findViewById(R.id.imageViewCategoria);
        }

        public void bind(final Categoria categoria, final OnItemClickListener itemListener) {
            categoriaNombre.setText(categoria.getNombre());
            categoriaDescripcion.setText(categoria.getDescripcion());
            Picasso.get().load(categoria.getImagen()).fit().into(categoriaFoto); //Libreria usada para el manejo de imagenes

        }


        }

    public interface OnItemClickListener {
        void onItemClick(Categoria categoria, int position);
    }
}
