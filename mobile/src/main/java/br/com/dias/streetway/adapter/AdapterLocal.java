package br.com.dias.streetway.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import br.com.dias.streetway.R;
import br.com.dias.streetway.domain.Local;
import br.com.dias.streetway.util.CirculoTransform;
import br.com.dias.streetway.util.FireBaseUtil;
import br.com.dias.streetway.util.Util;

/**
 * Created by FernandoDias on 21/04/16.
 */
public class AdapterLocal extends RecyclerView.Adapter<AdapterLocal.LocalViewHolder> {

    private List<Local> listaLocal;
    private Context context;
    private OnClickGaleriaLocal onClickGaleriaLocal;
    private String uidUsuarioLogado;

    public AdapterLocal(List<Local> listaLocal, Context context, String uidUsuarioLogado, OnClickGaleriaLocal onClickGaleriaLocal) {
        this.listaLocal = listaLocal;
        this.context = context;
        this.uidUsuarioLogado = uidUsuarioLogado;
        this.onClickGaleriaLocal = onClickGaleriaLocal;
    }

    @Override
    public LocalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.adapter_local_item, parent, false);
        LocalViewHolder holder = new LocalViewHolder(view);

        return holder;

    }

    @Override
    public void onBindViewHolder(final LocalViewHolder holder, final int position) {

        final Local local = listaLocal.get(position);

        holder.nomeLocalTextView.setText(local.getNome().toUpperCase());
        holder.fotoLocalImageView.setImageBitmap(Util.StringBase64ToBitmap(local.getImagem()));
        Picasso.with(context).load(local.getImgUsuario()).transform(new CirculoTransform()).into(holder.fotoUsuarioImageView);
        holder.textoIncluidoPorTextView.setText(context.getString(R.string.label_incluido_por,local.getNomeUsuario()));

        holder.textoDataInclusaoTextView.setText(context.getString(R.string.label_em, Util.formatarData(local.getDatainclusao())));
        holder.likeImageView.setColorFilter(ContextCompat.getColor(context, R.color.colorDisibled));

        Firebase localFire = FireBaseUtil.getInstance().getLocais().child(local.getId());

        Firebase likesFire = localFire.child("LIKE");
        likesFire.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long qtd = dataSnapshot.getChildrenCount();
                holder.qtdLikesTextView.setText(qtd.toString());

                if(dataSnapshot.getValue() != null) {
                    HashMap<String, String> curtida = (HashMap) dataSnapshot.getValue();
                    for (String key : curtida.keySet()){
                        if(curtida.get(key).equals(uidUsuarioLogado)){
                            local.setIdCurtir(key);
                            holder.likeImageView.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        Firebase comentarFire = localFire.child("COMENTARIOS");
        comentarFire.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long qtd = dataSnapshot.getChildrenCount();
                holder.qtdComentariosTextView.setText(qtd.toString());

                if(dataSnapshot.getValue() != null) {
                    HashMap<String, Object> comentario = (HashMap) dataSnapshot.getValue();
                    for (String key : comentario.keySet()){
                        HashMap<String, Object> comentarioHash = (HashMap<String, Object>) comentario.get(key);

                        if(comentarioHash.get("idUsuario").equals(uidUsuarioLogado)){
                            holder.comentarImageView.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        final Firebase avaliacaoFire = localFire.child("AVALIACOES");
        avaliacaoFire.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Long qtd = dataSnapshot.getChildrenCount();
                Double somaPontos = 0D;

                holder.qtdAvaliacoesTextView.setText(qtd.toString());

                if(dataSnapshot.getValue() != null) {

                    HashMap<String, Object> avaliacoes = (HashMap) dataSnapshot.getValue();
                    for (String key : avaliacoes.keySet()){
                        HashMap<String, Object> avaliacaoHash = (HashMap<String, Object>) avaliacoes.get(key);

                        if(avaliacaoHash.get("uidUsuario").equals(uidUsuarioLogado)){
                            holder.editarClassificacaoImageView.setImageResource(R.mipmap.ic_editar_classificacao);
                        }
                        if(avaliacaoHash.get("avaliacao") != null) {
                            Double avaliacao = (Double) avaliacaoHash.get("avaliacao");
                            somaPontos += avaliacao;
                        }

                    }

                    Double mediaClassificacao = somaPontos / qtd;

                    holder.classificacaoRatingBar.setRating(mediaClassificacao.floatValue());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        holder.fotoLocalImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickGaleriaLocal.clickLocal(v, position);
            }
        });

        holder.likeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String idCurtir = listaLocal.get(position).getIdCurtir();

                if(idCurtir != null) {
                    listaLocal.get(position).setIdCurtir(null);
                }

                onClickGaleriaLocal.clickCurti(v, position, idCurtir);
            }
        });

        holder.comentarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickGaleriaLocal.clickComentar(v, position);
            }
        });

        holder.editarClassificacaoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickGaleriaLocal.clickAvaliacao(v, position);
            }
        });

        holder.maisOpcoesImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.inflate(R.menu.menu_popup_local);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        String uri = Util.gerarUrlLocation(new LatLng(local.getLatitude(), local.getLongitude()), local.getNome());
                        context.startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));

                        return true;
                    }
                });
                popupMenu.show();





            }
        });


    }

    @Override
    public int getItemCount() {
        return listaLocal == null ? 0 : listaLocal.size();
    }

    public static class LocalViewHolder extends RecyclerView.ViewHolder {

        private TextView nomeLocalTextView;
        private ImageView maisOpcoesImageView;
        private ImageView fotoLocalImageView;
        private RatingBar classificacaoRatingBar;
        private ImageView fotoUsuarioImageView;
        private TextView textoIncluidoPorTextView;
        private TextView textoDataInclusaoTextView;
        private TextView qtdLikesTextView;
        private TextView qtdComentariosTextView;
        private TextView qtdAvaliacoesTextView;
        private ImageView likeImageView;
        private ImageView comentarImageView;
        private ImageView editarClassificacaoImageView;

        public LocalViewHolder(View view) {
            super(view);

            nomeLocalTextView = (TextView) view.findViewById(R.id.nome_local);
            maisOpcoesImageView = (ImageView) view.findViewById(R.id.img_mais_opcoes);
            fotoLocalImageView = (ImageView) view.findViewById(R.id.foto_local);
            classificacaoRatingBar = (RatingBar) view.findViewById(R.id.classificacao_ratingBar);
            fotoUsuarioImageView = (ImageView) view.findViewById(R.id.foto_usuario);
            textoIncluidoPorTextView = (TextView) view.findViewById(R.id.texto_incluido_por);
            textoDataInclusaoTextView = (TextView) view.findViewById(R.id.texto_data_inclusao);
            likeImageView = (ImageView) view.findViewById(R.id.curtir_img);
            comentarImageView = (ImageView) view.findViewById(R.id.comentar_img);
            qtdLikesTextView = (TextView) view.findViewById(R.id.qtd_likes);
            qtdComentariosTextView = (TextView) view.findViewById(R.id.qtd_comentarios);
            qtdAvaliacoesTextView = (TextView) view.findViewById(R.id.qtd_avaliacoes);
            editarClassificacaoImageView = (ImageView) view.findViewById(R.id.editar_avaliacao);

        }
    }

    public interface OnClickGaleriaLocal {
        void clickLocal(View view, int position);
        void clickCurti(View view, int position, String idCurtir);
        void clickComentar(View view, int position);
        void clickAvaliacao(View view, int position);
    }
}
