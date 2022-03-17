package com.example.proyecto_das.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_das.R;
import com.example.proyecto_das.models.Categoria;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private Context context;
    private List<Categoria> categorias;
    private int layout;
    private OnItemClickListener itemClickListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
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


    }

    public interface OnItemClickListener {
        void onItemClick(Categoria categoria, int position);
    }
}
