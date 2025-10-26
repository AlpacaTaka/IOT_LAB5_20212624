package com.example.lab5_20212624;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CursosAdapter extends RecyclerView.Adapter<CursosAdapter.CursoViewHolder> {

    private Context context;
    private List<Curso> listaCursos;
    private OnCursoClickListener listener;

    public interface OnCursoClickListener {
        void onCursoClick(Curso curso);
        void onCursoDelete(Curso curso, int position);
    }

    public CursosAdapter(Context context, List<Curso> listaCursos, OnCursoClickListener listener) {
        this.context = context;
        this.listaCursos = listaCursos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CursoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_curso, parent, false);
        return new CursoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CursoViewHolder holder, int position) {
        Curso curso = listaCursos.get(position);
        holder.bind(curso, position);
    }

    @Override
    public int getItemCount() {
        return listaCursos.size();
    }

    public void updateList(List<Curso> nuevaLista) {
        this.listaCursos = nuevaLista;
        notifyDataSetChanged();
    }

    class CursoViewHolder extends RecyclerView.ViewHolder {

        private CardView cardCurso;
        private TextView tvNombreCurso;
        private TextView tvCategoriaCurso;
        private TextView tvFrecuenciaCurso;
        private TextView tvProximaSesion;
        private TextView tvAccionSugerida;
        private ImageButton btnEliminar;

        public CursoViewHolder(@NonNull View itemView) {
            super(itemView);

            cardCurso = itemView.findViewById(R.id.card_curso);
            tvNombreCurso = itemView.findViewById(R.id.tv_nombre_curso);
            tvCategoriaCurso = itemView.findViewById(R.id.tv_categoria_curso);
            tvFrecuenciaCurso = itemView.findViewById(R.id.tv_frecuencia_curso);
            tvProximaSesion = itemView.findViewById(R.id.tv_proxima_sesion);
            tvAccionSugerida = itemView.findViewById(R.id.tv_accion_sugerida);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar);
        }

        public void bind(Curso curso, int position) {

            tvNombreCurso.setText(curso.getNombre());


            tvCategoriaCurso.setText(curso.getCategoriaEmoji() + " " + curso.getCategoria());


            tvFrecuenciaCurso.setText(curso.getFrecuenciaTexto());


            tvProximaSesion.setText("📅 " + curso.getProximaSesionTexto());


            if (curso.getAccionSugerida() != null && !curso.getAccionSugerida().isEmpty()) {
                tvAccionSugerida.setText("💡 " + curso.getAccionSugerida());
                tvAccionSugerida.setVisibility(View.VISIBLE);
            } else {
                tvAccionSugerida.setVisibility(View.GONE);
            }

            // Color del card según categoría
            int colorFondo = getCategoriaColor(curso.getCategoria());
            cardCurso.setCardBackgroundColor(context.getResources().getColor(colorFondo));

            // Click en el card para ver detalles
            cardCurso.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCursoClick(curso);
                }
            });

            // Click en botón eliminar
            btnEliminar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCursoDelete(curso, position);
                }
            });
        }

        private int getCategoriaColor(String categoria) {
            switch (categoria.toLowerCase()) {
                case "teorico":
                case "teórico":
                    return R.color.categoria_teorico;
                case "laboratorio":
                    return R.color.categoria_laboratorio;
                case "electivo":
                    return R.color.categoria_electivo;
                default:
                    return R.color.categoria_otros;
            }
        }
    }
}