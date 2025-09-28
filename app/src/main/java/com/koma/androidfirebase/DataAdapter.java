package com.koma.androidfirebase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataViewHolder> {

    private List<DataModel> dataList;

    public DataAdapter(List<DataModel> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_data, parent, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        DataModel data = dataList.get(position);
        holder.tvNombre.setText("Nombre: " + data.getNombre());
        holder.tvCorreo.setText("Correo: " + data.getCorreo());
        holder.tvMensaje.setText("Mensaje: " + data.getMensaje());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void updateData(List<DataModel> newData) {
        this.dataList = newData;
        notifyDataSetChanged();
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCorreo, tvMensaje;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvCorreo = itemView.findViewById(R.id.tvCorreo);
            tvMensaje = itemView.findViewById(R.id.tvMensaje);
        }
    }
}