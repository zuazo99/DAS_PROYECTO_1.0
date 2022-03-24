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
import com.example.proyecto_das.models.Esqui;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class EsquiAdapter extends RecyclerView.Adapter<EsquiAdapter.ViewHolder>{

    private Context context;
    private List<Esqui> modelos;
    private int layout;
    private OnItemClickListener itemClickListener;
    private OnButtonClickListener buttonClickListener;


    public EsquiAdapter(List<Esqui> modelos, int layout, OnItemClickListener itemClickListener, OnButtonClickListener buttonClickListener) {
        this.modelos = modelos;
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
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(modelos.get(position), itemClickListener, buttonClickListener);
    }

    @Override
    public int getItemCount() {
        return modelos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView esquiImagen;
        public TextView esquiNombreMarca;
        public TextView esquiNombreModelo;
        public TextView esquiPrecio;
        public TextView stars;
        public Button btnDelete;
        public Button btnEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            esquiImagen = itemView.findViewById(R.id.imageViewEsqui);
            esquiNombreMarca = itemView.findViewById(R.id.textViewEsquiMarca);
            esquiNombreModelo = itemView.findViewById(R.id.textViewEsquiNombreModelo);
            esquiPrecio = itemView.findViewById(R.id.textViewPrecioEsqui);
            stars = itemView.findViewById(R.id.textViewStars);
            btnDelete = itemView.findViewById(R.id.buttonDeleteEsqui);
            btnEdit = itemView.findViewById(R.id.buttonEditEsqui);
        }

        public void bind(final Esqui esqui, final OnItemClickListener itemListener, final OnButtonClickListener btnListener) {
            esquiNombreMarca.setText(esqui.getNombreMarca());
            esquiNombreModelo.setText(esqui.getNombreProd());
            esquiPrecio.setText((int) esqui.getPrecio());
            stars.setText((int)esqui.getStars());
            // Picasso.get().load(categoria.getImagen()).placeholder(R.drawable.placeholder).fit().into(categoriaFoto); //Libreria usada para el manejo de imagenes

            if (esqui.getFromGalery()){
                Picasso.get().load(new File(esqui.getImagen())).placeholder(R.drawable.placeholder).fit().into(esquiImagen, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(context, "Error!!! "+ esqui.getImagen(), Toast.LENGTH_SHORT).show();

                    }
                });

            }else{
                Picasso.get().load(esqui.getImagen()).placeholder(R.drawable.placeholder).fit().into(esquiImagen);

            }


            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnListener.onButtonDeleteClick(esqui, getAdapterPosition());
                }
            });

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnListener.onButtonEditClick(esqui, getAdapterPosition());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemListener.onItemClick(esqui, getAdapterPosition());
                }
            });

        }


    }

    public interface OnItemClickListener {
        void onItemClick(Esqui esqui, int position);
    }

    public interface OnButtonClickListener{
        void onButtonDeleteClick(Esqui esqu, int position);
        void onButtonEditClick(Esqui esqu, int position);
    }



}
