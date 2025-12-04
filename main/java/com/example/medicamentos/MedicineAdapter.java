package com.example.medicamentos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.Holder> {

    private List<Medicine> list = new ArrayList<>();
    private final Context ctx;
    private final OnAction callback;
    private final SimpleDateFormat sdf =
            new SimpleDateFormat("HH:mm", Locale.getDefault()); // formato 24h padrão

    public MedicineAdapter(Context ctx, OnAction callback) {
        this.ctx = ctx;
        this.callback = callback;
    }

    // Atualiza a lista
    public void setList(List<Medicine> newList) {
        this.list = new ArrayList<>(newList); // evita referências duplicadas
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_medicine, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Medicine m = list.get(position);

        holder.tvName.setText(m.getName());
        holder.tvDesc.setText(m.getDescription());

        // Proteção: se horário for inválido
        if (m.getTimeMillis() > 0) {
            holder.tvTime.setText(sdf.format(m.getTimeMillis()));
        } else {
            holder.tvTime.setText("--:--"); // horário inválido
        }

        // Ícone correto para "tomado" vs "não tomado"
        holder.ivStatus.setImageResource(
                m.isTaken()
                        ? android.R.drawable.checkbox_on_background
                        : android.R.drawable.checkbox_off_background
        );

        // Clique curto → editar
        holder.itemView.setOnClickListener(v -> callback.onAction(m, 0));

        // Clique no ícone → alternar tomado
        holder.ivStatus.setOnClickListener(v -> callback.onAction(m, 1));

        // Clique longo → deletar
        holder.itemView.setOnLongClickListener(v -> {
            callback.onAction(m, 2);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc, tvTime;
        ImageView ivStatus;

        Holder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivStatus = itemView.findViewById(R.id.ivStatus);
        }
    }

    // Ações:
    // 0 = editar
    // 1 = alternar tomado
    // 2 = deletar
    public interface OnAction {
        void onAction(Medicine m, int action);
    }
}
