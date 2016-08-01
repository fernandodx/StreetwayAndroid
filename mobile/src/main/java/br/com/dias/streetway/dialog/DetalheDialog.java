package br.com.dias.streetway.dialog;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
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

import java.io.Serializable;
import java.util.HashMap;

import br.com.dias.streetway.R;
import br.com.dias.streetway.domain.Avaliacao;
import br.com.dias.streetway.domain.Comentario;
import br.com.dias.streetway.domain.Local;
import br.com.dias.streetway.util.CirculoTransform;
import br.com.dias.streetway.util.FireBaseUtil;
import br.com.dias.streetway.util.Util;

/**
 * Created by FernandoDias on 01/04/16.
 */
public class DetalheDialog extends DialogFragment {

    private OnClickDialog onClickDialog;

    private TextView nomeLocalTextView;
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
    private TextView fecharTextView;
    private ImageView enviarGpsImageView;

    private Local local;
    private String uidUsuarioLogado;
    private String imgUsuarioLogado;
    private String nomeUsuarioLogado;
    private boolean isUsuarioLogado;
    private LatLng localizacaoAtual;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NO_TITLE,  android.R.style.Theme_DeviceDefault_Light_Dialog);

        local = (Local) getArguments().getSerializable("LOCAL");
        uidUsuarioLogado = getArguments().getString("UID_USUARIO_LOGADO");
        isUsuarioLogado = getArguments().getBoolean("IS_USUARIO_LOGADO");
        imgUsuarioLogado = getArguments().getString("IMG_USUARIO_LOGADO");
        nomeUsuarioLogado = getArguments().getString("NOME_USUARIO_LOGADO");
        localizacaoAtual = getArguments().getParcelable("LOCALIZACAO_ATUAL");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_detalhe_local, viewGroup, false);

        nomeLocalTextView = (TextView) view.findViewById(R.id.nome_local);
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
        fecharTextView = (TextView) view.findViewById(R.id.fechar);
        enviarGpsImageView = (ImageView) view.findViewById(R.id.img_enviar_gps);



        nomeLocalTextView.setText(local.getNome().toUpperCase());
        fotoLocalImageView.setImageBitmap(Util.StringBase64ToBitmap(local.getImagem()));
        Picasso.with(getContext()).load(local.getImgUsuario()).transform(new CirculoTransform()).into(fotoUsuarioImageView);
        textoIncluidoPorTextView.setText(local.getNomeUsuario());
        textoDataInclusaoTextView.setText(Util.formatarData(local.getDatainclusao()));
        likeImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorDisibled));

        final Firebase localFire = FireBaseUtil.getInstance().getLocais().child(local.getId());

        Firebase likesFire = localFire.child("LIKE");
        likesFire.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long qtd = dataSnapshot.getChildrenCount();
                qtdLikesTextView.setText(qtd.toString());
                likeImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorDisibled));

                if(dataSnapshot.getValue() != null) {
                    HashMap<String, String> curtida = (HashMap) dataSnapshot.getValue();
                    for (String key : curtida.keySet()){
                        if(curtida.get(key).equals(uidUsuarioLogado)){
                            local.setIdCurtir(key);
                            likeImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent));
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
                qtdComentariosTextView.setText(qtd.toString());
                comentarImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorDisibled));

                if(dataSnapshot.getValue() != null) {
                    HashMap<String, Object> comentario = (HashMap) dataSnapshot.getValue();
                    for (String key : comentario.keySet()){
                        HashMap<String, Object> comentarioHash = (HashMap<String, Object>) comentario.get(key);

                        if(comentarioHash.get("idUsuario").equals(uidUsuarioLogado)){
                            comentarImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent));
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

                qtdAvaliacoesTextView.setText(qtd.toString());
                editarClassificacaoImageView.setImageResource(R.mipmap.ic_editar_classificacao_disabled);

                if(dataSnapshot.getValue() != null) {

                    HashMap<String, Object> avaliacoes = (HashMap) dataSnapshot.getValue();
                    for (String key : avaliacoes.keySet()){
                        HashMap<String, Object> avaliacaoHash = (HashMap<String, Object>) avaliacoes.get(key);

                        if(avaliacaoHash.get("uidUsuario").equals(uidUsuarioLogado)){
                            editarClassificacaoImageView.setImageResource(R.mipmap.ic_editar_classificacao);
                        }
                        if(avaliacaoHash.get("avaliacao") != null) {
                            Double avaliacao = (Double) avaliacaoHash.get("avaliacao");
                            somaPontos += avaliacao;
                        }

                    }

                    Double mediaClassificacao = somaPontos / qtd;

                    classificacaoRatingBar.setRating(mediaClassificacao.floatValue());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

//        fotoLocalImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            GaleriaLocalFragment galeriaLocalFragment = new GaleriaLocalFragment();
//            galeriaLocalFragment.setLocalSelecionado(local);
//            ((MainMobileActivity)getActivity()).addFragment(galeriaLocalFragment, GaleriaLocalFragment.TAG, R.menu.main_mobile, "Galeria");
//
//            }
//        });

        likeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            if(isUsuarioLogado){

                Firebase localFire = FireBaseUtil.getInstance().getLocais().child(local.getId());

                String idCurtir = local.getIdCurtir();

                if(idCurtir != null) {
                    local.setIdCurtir(null);
                }

                if(idCurtir != null) {
                    Firebase curtidasFire = localFire.child("LIKE").child(idCurtir);
                    curtidasFire.removeValue();
                }else{
                    Firebase curtidasFire = localFire.child("LIKE");
                    curtidasFire.push().setValue(uidUsuarioLogado);
                }
            }else{
                Util.showAlertErro(getContext(),getString(R.string.alerta_msg_curti_acao_logado, "curti"), R.string.label_ok, null);
            }
            }
        });

        comentarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUsuarioLogado) {

                    Comentario comentarioInit = new Comentario();
                    Bundle paramentros = new Bundle();
                    ComentarioDialog comentarioDialog = new ComentarioDialog();

                    comentarioInit.setIdUsuario(uidUsuarioLogado);
                    comentarioInit.setImgUsuario(imgUsuarioLogado);
                    comentarioInit.setNomeUsuario(nomeUsuarioLogado);

                    paramentros.putSerializable("COMENTARIO_OBJ", comentarioInit);
                    paramentros.putString("ID_LOCAL", local.getId());

                    comentarioDialog.setArguments(paramentros);
                    comentarioDialog.show(getFragmentManager(), "popUpComentario");

                } else {
                    Util.showAlertErro(getContext(), getString(R.string.alerta_msg_curti_acao_logado, "comentar"), R.string.label_ok, null);
                }
            }
        });

        editarClassificacaoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isUsuarioLogado) {

                    Avaliacao avaliacao = new Avaliacao();
                    avaliacao.setUidUsuario(uidUsuarioLogado);

                    Bundle parametros = new Bundle();
                    parametros.putString("ID_LOCAL", local.getId());
                    parametros.putSerializable("AVALIACAO_OBJ", avaliacao);
                    parametros.putString("ID_USUARIO", uidUsuarioLogado);

                    AvaliacaoDialog avaliacaoDialog = new AvaliacaoDialog();
                    avaliacaoDialog.setArguments(parametros);

                    avaliacaoDialog.show(getFragmentManager(), "avaliacao_dialog");

                } else {
                    Util.showAlertErro(getContext(), getString(R.string.alerta_msg_curti_acao_logado, "avaliar"), R.string.label_ok, null);
                }
            }
        });

        fecharTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        enviarGpsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uri = Util.gerarUrlLocation(new LatLng(local.getLatitude(), local.getLongitude()), local.getNome());
                startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();





    }




    public interface OnClickDialog extends Serializable {

        public static final String ON_CLICK_LISTENER_ARG = "OnClickListener";

        void onClickPositive(View v, String tag);
        void onClickNegative(View v, String tag, Object retorno);

    }

    @Override
    public void onDestroyView() {

        if(onClickDialog != null) {
            onClickDialog.onClickNegative(null, getTag(), null);
        }

        super.onDestroyView();
    }

}
