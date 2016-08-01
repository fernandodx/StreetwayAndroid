package br.com.dias.streetway.dialog;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import br.com.dias.streetway.R;
import br.com.dias.streetway.adapter.AdapterComentarioLocal;
import br.com.dias.streetway.domain.Comentario;
import br.com.dias.streetway.util.FireBaseUtil;
import br.com.dias.streetway.util.Util;

/**
 * Created by FernandoDias on 01/04/16.
 */
public class ComentarioDialog extends DialogFragment {

    private OnClickDialog onClickDialog;

    private RecyclerView comentarioRecyclerView;
    private EditText comentarioEditText;
    private ImageView enviarImageView;
    private String idLocal;
    private Comentario comentario;
    private List<Comentario> listaComentario;
    private Firebase comentariosFire;
    private TextView fecharTextView;
    private View layoutNenhumComentario;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NO_TITLE,  android.R.style.Theme_DeviceDefault_Light_Dialog);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_add_comentario, viewGroup, false);

        comentarioRecyclerView = (RecyclerView) view.findViewById(R.id.comentario_lista);
        comentarioEditText = (EditText) view.findViewById(R.id.comentario_local);
        enviarImageView = (ImageView) view.findViewById(R.id.img_enviar);
        fecharTextView = (TextView) view.findViewById(R.id.fechar);
        layoutNenhumComentario = view.findViewById(R.id.layout_nenhum_comentario);

        comentarioRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        comentarioRecyclerView.setAdapter(new AdapterComentarioLocal(getListaComentario(), getContext()));

        comentario = (Comentario) getArguments().getSerializable("COMENTARIO_OBJ");
        idLocal = getArguments().getString("ID_LOCAL");

        enviarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!comentarioEditText.getText().toString().isEmpty()){
                    comentario.setDataInclusao(new Date());
                    comentario.setComentario(comentarioEditText.getText().toString());
                    comentariosFire.push().setValue(comentario);

                    comentarioEditText.setText("");
                }
            }
        });

        fecharTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        comentarioEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count == 0) {
                    enviarImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorDisibled));
                }else{
                    enviarImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        comentariosFire = FireBaseUtil.getInstance().getLocais()
                .child(idLocal)
                .child("COMENTARIOS");

        comentariosFire.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot != null && dataSnapshot.getValue() != null) {
                    HashMap<String, Object> comentarioHash = (HashMap<String, Object>) dataSnapshot.getValue();
                    setListaComentario(null);

                    for(String key : comentarioHash.keySet()) {

                        Comentario comentario = new Comentario();

                        HashMap<String, Object> map = (HashMap<String, Object>) comentarioHash.get(key);
                        comentario.setComentario((String) map.get("comentario"));
                        comentario.setIdUsuario((String) map.get("idUsuario"));
                        comentario.setNomeUsuario((String) map.get("nomeUsuario"));
                        comentario.setImgUsuario((String) map.get("imgUsuario"));
                        comentario.setDataInclusao((Long) map.get("dataInclusao"));
                        comentario.setId(key);

                        getListaComentario().add(comentario);
                    }

                    Collections.sort(getListaComentario(), new Comparator<Comentario>() {
                        @Override
                        public int compare(Comentario c1, Comentario c2) {
                            return c2.getDataInclusao().compareTo(c1.getDataInclusao());
                        }
                    });

                    Util.esconderTecladoMobile(getContext(), comentarioEditText);
                    atualizarLista();
                    layoutNenhumComentario.setVisibility(View.GONE);

                }else{
                    layoutNenhumComentario.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });



    }



    private void atualizarLista() {

        new Handler().post(new Runnable() {
            @Override
            public void run() {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        comentarioRecyclerView.setAdapter(new AdapterComentarioLocal(getListaComentario(), getContext()));
                    }
                });


            }
        });

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

    public List<Comentario> getListaComentario() {
        if(listaComentario == null) {
            listaComentario = new ArrayList<>();
        }
        return listaComentario;
    }

    public void setListaComentario(List<Comentario> listaComentario) {
        this.listaComentario = listaComentario;
    }
}
