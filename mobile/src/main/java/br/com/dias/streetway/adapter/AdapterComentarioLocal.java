package br.com.dias.streetway.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.dias.streetway.R;
import br.com.dias.streetway.domain.Comentario;
import br.com.dias.streetway.util.CirculoTransform;
import br.com.dias.streetway.util.Util;

/**
 * Created by FernandoDias on 21/04/16.
 */
public class AdapterComentarioLocal extends RecyclerView.Adapter<AdapterComentarioLocal.ComentarioViewHolder> {

    private List<Comentario> listaComentario;
    private Context context;
    private String uidUsuarioLogado;

    public AdapterComentarioLocal(List<Comentario> listaLocal, Context context) {
        this.listaComentario = listaLocal;
        this.context = context;
    }

    @Override
    public ComentarioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.adapter_comentario, parent, false);
        ComentarioViewHolder holder = new ComentarioViewHolder(view);

        return holder;

    }

    @Override
    public void onBindViewHolder(final ComentarioViewHolder holder, final int position) {

        final Comentario comentario = listaComentario.get(position);

        holder.nomeUsuarioTextView.setText(comentario.getNomeUsuario());
        holder.comentarioTextView.setText(comentario.getComentario());
        holder.dataInclusaoTextView.setText(Util.formatarData(comentario.getDataInclusao()));

        Picasso.with(context).load(comentario.getImgUsuario()).transform(new CirculoTransform()).into(holder.usuarioImageView);

    }

    @Override
    public int getItemCount() {
        return listaComentario == null ? 0 : listaComentario.size();
    }

    public static class ComentarioViewHolder extends RecyclerView.ViewHolder {
        private ImageView usuarioImageView;
        private TextView comentarioTextView;
        private TextView nomeUsuarioTextView;
        private TextView dataInclusaoTextView;

        public ComentarioViewHolder(View view) {
            super(view);

            usuarioImageView = (ImageView) view.findViewById(R.id.foto_usuario);
            nomeUsuarioTextView = (TextView) view.findViewById(R.id.nome_usuario);
            comentarioTextView = (TextView) view.findViewById(R.id.comentario_local);
            dataInclusaoTextView = (TextView) view.findViewById(R.id.data_comentario);


        }
    }
}
