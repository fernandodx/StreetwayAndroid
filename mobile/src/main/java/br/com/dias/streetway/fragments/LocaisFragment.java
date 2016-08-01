package br.com.dias.streetway.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.dias.streetway.R;
import br.com.dias.streetway.activity.MainMobileActivity;
import br.com.dias.streetway.adapter.AdapterLocal;
import br.com.dias.streetway.dialog.AvaliacaoDialog;
import br.com.dias.streetway.dialog.ComentarioDialog;
import br.com.dias.streetway.domain.Avaliacao;
import br.com.dias.streetway.domain.Comentario;
import br.com.dias.streetway.domain.Local;
import br.com.dias.streetway.util.FireBaseUtil;
import br.com.dias.streetway.util.Util;

/**
 * Created by FernandoDias on 24/04/16.
 */
public class LocaisFragment extends BaseFragment implements AdapterLocal.OnClickGaleriaLocal, MainMobileActivity.OnActionBarUpdatesListener {

    public static final String TAG = "LocaisFragment";

    private List<Local> listaLocais;
    private RecyclerView recyclerView;
    private ViewGroup layoutLoading;
    private ViewGroup layoutMsgErro;
    private Firebase firebaselocais;
    private LinearLayoutManager linearLayoutManager;
    private boolean isMeusLocais;
    private Integer posicaoAtual;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.firebaselocais = FireBaseUtil.getInstance().getLocais();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_locais, container, false);

        if(getArguments() != null) {
            isMeusLocais = getArguments().getBoolean("IS_MEUS_LOCAIS", false);
        }

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        layoutLoading = (ViewGroup) view.findViewById(R.id.layout_loading);
        layoutMsgErro = (ViewGroup) view.findViewById(R.id.layout_msg_erro);
        linearLayoutManager = new LinearLayoutManager(getActivity());

        layoutMsgErro.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.VISIBLE);

        firebaselocais.addChildEventListener(getChildEventListener());
        firebaselocais.addValueEventListener(getValueEventListenerLocais());

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);


        return view;
    }

    private ValueEventListener getValueEventListenerLocais() {

        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                atualizarTela();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("FIREBASE", firebaseError.getMessage());
            }
        };
    }

    private ChildEventListener getChildEventListener() {

        return new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String key) {

                Local local = Local.createLocal(dataSnapshot);

                if(isMeusLocais && local.getIdCurtir() != null && local.getIdCurtir().equals(getMainMobile().getUidUsuario())) {
                    getListaLocais().add(local);
                }else{
                    getListaLocais().add(local);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String key) {
                atualizarTela();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String key) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };
    }


    private void atualizarTela() {

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

                recyclerView.setAdapter(new AdapterLocal(getListaLocais(), getContext(), getMainMobile().getUidUsuario(), LocaisFragment.this));
                layoutLoading.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                if(posicaoAtual != null) {
                    recyclerView.scrollToPosition(posicaoAtual);
                }


            }
        });
    }

    public List<Local> getListaLocais() {
        if(listaLocais == null) {
            listaLocais = new ArrayList<>();
        }
        return listaLocais;
    }

    public void setListaLocais(List<Local> listaLocais) {
        this.listaLocais = listaLocais;
    }

    @Override
    public void clickLocal(View view, int position) {

        Local localSelecionado = listaLocais.get(position);

        GaleriaLocalFragment galeriaLocalFragment = new GaleriaLocalFragment();
        galeriaLocalFragment.setLocalSelecionado(localSelecionado);

        addFragment(galeriaLocalFragment, GaleriaLocalFragment.TAG, R.menu.main_mobile, "Galeria");

        posicaoAtual = position;

    }

    @Override
    public void clickCurti(View view, int position, String idCurtir) {

        if(getMainMobile().isUsuarioLogado()){

            Firebase localFire = FireBaseUtil.getInstance().getLocais().child(listaLocais.get(position).getId());

            if(idCurtir != null) {
                Firebase curtidasFire = localFire.child("LIKE").child(idCurtir);
                curtidasFire.removeValue();
            }else{
                Firebase curtidasFire = localFire.child("LIKE");
                curtidasFire.push().setValue(getMainMobile().getUidUsuario());
            }

            posicaoAtual = position;
        } else {
            Util.showAlertErro(getContext(), getString(R.string.alerta_msg_curti_acao_logado, "curtir"), R.string.label_ok, null);
        }
    }

    @Override
    public void clickComentar(View view, int position) {

        if(getMainMobile().isUsuarioLogado()) {

            Comentario comentarioInit = new Comentario();
            Bundle paramentros = new Bundle();
            ComentarioDialog comentarioDialog = new ComentarioDialog();

            comentarioInit.setIdUsuario(getMainMobile().getUidUsuario());
            comentarioInit.setImgUsuario(getMainMobile().getImgUsuario());
            comentarioInit.setNomeUsuario(getMainMobile().getNomeUsuario());

            paramentros.putSerializable("COMENTARIO_OBJ", comentarioInit);
            paramentros.putString("ID_LOCAL", listaLocais.get(position).getId());

            comentarioDialog.setArguments(paramentros);
            comentarioDialog.show(getFragmentManager(), "popUpComentario" );

            posicaoAtual = position;

        } else {
            Util.showAlertErro(getContext(), getString(R.string.alerta_msg_curti_acao_logado, "comentar"), R.string.label_ok, null);
        }
    }

    @Override
    public void clickAvaliacao(View view, int position) {

        if(getMainMobile().isUsuarioLogado()) {

            Avaliacao avaliacao = new Avaliacao();
            avaliacao.setUidUsuario(getMainMobile().getUidUsuario());

            Bundle parametros = new Bundle();
            parametros.putString("ID_LOCAL", listaLocais.get(position).getId());
            parametros.putSerializable("AVALIACAO_OBJ", avaliacao);
            parametros.putString("ID_USUARIO", getMainMobile().getUidUsuario());

            AvaliacaoDialog avaliacaoDialog = new AvaliacaoDialog();
            avaliacaoDialog.setArguments(parametros);

            avaliacaoDialog.show(getFragmentManager(), "avaliacao_dialog");

            posicaoAtual = position;

        } else {
            Util.showAlertErro(getContext(), getString(R.string.alerta_msg_curti_acao_logado, "avaliar"), R.string.label_ok, null);
        }

    }

    @Override
    public void itemMenuSelecionado(MenuItem menuItem) {

    }

    @Override
    public void itemMenuVoltarClick(View v) {
        voltarFragment();
    }

    @Override
    public void loginFacebookSucess(AuthData authData) {

    }
}
