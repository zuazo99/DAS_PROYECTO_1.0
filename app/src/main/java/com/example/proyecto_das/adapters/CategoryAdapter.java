package com.example.proyecto_das.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyecto_das.R;
import com.example.proyecto_das.models.Categoria;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private Context context;
    private List<Categoria> categorias;
    private int layout;
    private OnItemClickListener itemClickListener;
    private OnButtonClickListener buttonClickListener;


    public CategoryAdapter(List<Categoria> categorias, int layout, OnItemClickListener itemClickListener, OnButtonClickListener buttonClickListener) {
        this.context = context;
        this.categorias = categorias;
        this.layout = layout;
        this.itemClickListener = itemClickListener;
        this.buttonClickListener = buttonClickListener;
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
        holder.bind(categorias.get(position), itemClickListener, buttonClickListener);
    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView categoriaNombre;
        public TextView categoriaDescripcion;
        public ImageView categoriaFoto;
        public Button btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoriaNombre = itemView.findViewById(R.id.textViewCategoriaNombre);
            categoriaDescripcion = itemView.findViewById(R.id.textViewCategoriaDescripcion);
            categoriaFoto = itemView.findViewById(R.id.imageViewCategoria);
            btnDelete = itemView.findViewById(R.id.buttonDeleteCategoria);
        }

        public void bind(final Categoria categoria, final OnItemClickListener itemListener, final OnButtonClickListener btnListener) {
            categoriaNombre.setText(categoria.getNombre());
            categoriaDescripcion.setText(categoria.getDescripcion());
           // Picasso.get().load(categoria.getImagen()).placeholder(R.drawable.placeholder).fit().into(categoriaFoto); //Libreria usada para el manejo de imagenes

            if (categoria.getFromGalery()){
                Toast.makeText(context, "Entra al metodo bind y getFromGaley--> " + categoria.getFromGalery(), Toast.LENGTH_SHORT).show();
                Picasso.get().load(new File(categoria.getImagen())).placeholder(R.drawable.placeholder).fit().into(categoriaFoto, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(context, "Error!!! "+ categoria.getImagen(), Toast.LENGTH_SHORT).show();

                    }
                });

            }else{
                Picasso.get().load(categoria.getImagen()).placeholder(R.drawable.placeholder).fit().into(categoriaFoto);

            }


            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnListener.onButtonClick(categoria, getAdapterPosition());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemListener.onItemClick(categoria, getAdapterPosition());
                }
            });

        }


        }

    public interface OnItemClickListener {
        void onItemClick(Categoria categoria, int position);
    }

    public interface OnButtonClickListener{
        void onButtonClick(Categoria categoria, int position);
    }
}
