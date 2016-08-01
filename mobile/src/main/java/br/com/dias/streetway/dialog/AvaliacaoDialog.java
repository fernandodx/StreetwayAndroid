package br.com.dias.streetway.dialog;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;

import br.com.dias.streetway.R;
import br.com.dias.streetway.domain.Avaliacao;
import br.com.dias.streetway.util.FireBaseUtil;

/**
 * Created by FernandoDias on 01/04/16.
 */
public class AvaliacaoDialog extends DialogFragment {

    private RatingBar classificacaoRatingBar;
    private Button okButton;
    private String idLocal;
    private String idUsuarioLogado;
    private String idAvaliacaoUsuario;
    private Avaliacao avaliacao;
    private Double avaliacaoUsuario;


    private Query avaliacaoFire;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NO_TITLE,  android.R.style.Theme_DeviceDefault_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_avaliacao, viewGroup, false);

        idLocal = getArguments().getString("ID_LOCAL");
        avaliacao = (Avaliacao) getArguments().getSerializable("AVALIACAO_OBJ");
        idUsuarioLogado = getArguments().getString("ID_USUARIO");

        classificacaoRatingBar = (RatingBar) view.findViewById(R.id.classificacao_ratingBar);
        okButton = (Button) view.findViewById(R.id.bt_ok);

        avaliacaoFire = FireBaseUtil.getInstance().getLocais().child(idLocal).child("AVALIACOES").orderByChild("uidUsuario").equalTo(idUsuarioLogado);

        avaliacaoFire.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot != null && dataSnapshot.getValue() != null) {

                    HashMap mapPai = ((HashMap)dataSnapshot.getValue());
                    HashMap map = (HashMap) dataSnapshot.getChildren().iterator().next().getValue();

                    for (Object key : mapPai.keySet()){
                        idAvaliacaoUsuario = key.toString();
                        break;
                    }

                    avaliacaoUsuario = (Double) map.get("avaliacao");

                    classificacaoRatingBar.setRating(avaliacaoUsuario.floatValue());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(avaliacao != null && avaliacao.getAvaliacao() != null && avaliacao.getAvaliacao() > 0D){

                    if(idAvaliacaoUsuario != null) {
                        Firebase avaliacaoFire = FireBaseUtil.getInstance().getLocais().child(idLocal).child("AVALIACOES").child(idAvaliacaoUsuario);
                        avaliacaoFire.setValue(avaliacao);
                    }else {
                        Firebase avaliacaoFire = FireBaseUtil.getInstance().getLocais().child(idLocal).child("AVALIACOES");
                        avaliacaoFire.push().setValue(avaliacao);
                    }
                }

                dismiss();
            }
        });

        classificacaoRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                avaliacao.setAvaliacao(new Double(rating));
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
