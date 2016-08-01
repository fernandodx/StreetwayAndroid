package br.com.dias.streetway.fragments;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

import br.com.dias.streetway.R;
import br.com.dias.streetway.activity.MainMobileActivity;
import br.com.dias.streetway.dialog.DetalheDialog;
import br.com.dias.streetway.domain.Local;
import br.com.dias.streetway.domain.MarkerLocal;
import br.com.dias.streetway.domain.TipoLocalEnum;
import br.com.dias.streetway.util.FireBaseUtil;

/**
 * Created by FernandoDias on 06/04/16.
 */
public class MapaFragment extends BaseMapFragment {

    public static final String TAG = "MapaFragment";
    public static String IS_PERMITIR_MARCAR_LOCAL = "IS_PERMITIR_MARCAR_LOCAL";

    private FloatingActionButton fab;
    private Firebase firebaseLocais;
    private List<Local> listaLocais;
    private boolean isPermitirMarcarMapa;
    private boolean isEsconderBootonBar;
    private boolean isEsconderAddFab;
    private Marker localMarcadoMarker;
    private boolean isLocalEncontrado;
    private LatLng localizacaoAtual;

    private List<MarkerLocal> listaMarkerLocal;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.firebaseLocais = FireBaseUtil.getInstance().getLocais();

    }

    @Override
    public void onClickMap(LatLng latLng) {

        if(localMarcadoMarker != null) {
            localMarcadoMarker.remove();
        }

        if(isPermitirMarcarMapa) {

            AddLocalFragment addLocalFragment = (AddLocalFragment) getFragmentManager().findFragmentByTag(AddLocalFragment.TAG);
            addLocalFragment.adicionarMarcadorAddLocal(latLng);

            getFragmentManager().popBackStack();

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mapa_fragment, container, false);

        setRetainInstance(true);
        super.onCreateViewMap();

        firebaseLocais.addValueEventListener(getValueEventListenerLocais());
        firebaseLocais.addChildEventListener(getChildEventListener());

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AddLocalFragment addLocalFragment = new AddLocalFragment();
                MainMobileActivity.onActionMainMobile.addFragment(addLocalFragment, AddLocalFragment.TAG);
                MainMobileActivity.onActionMainMobile.setOnActionBarUpdatesListener(addLocalFragment);

            }
        });

        if(!isEsconderAddFab) {
            fab.setVisibility(View.VISIBLE);
        }else{
            fab.setVisibility(View.GONE);
        }

        ((MainMobileActivity)getActivity()).getToolbar().setTitle("Locais");

        showProgress(R.string.label_carregando_locais);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private ValueEventListener getValueEventListenerLocais() {

        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                atualizarTela();
                hideProgress();
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

                if(dataSnapshot.getValue() != null) {
                    Local local = Local.createLocal(dataSnapshot);
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

        setListaMarkerLocal(null);

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

                for(Local local : getListaLocais()) {

                    TipoLocalEnum localEnum = TipoLocalEnum.getEnum(local.getIdTipoLocal());
                    Marker marker = adicionarMarcador(new LatLng(local.getLatitude(), local.getLongitude()), localEnum);

                    getListaMarkerLocal().add(new MarkerLocal(marker, local));
                }
            }
        });
    }

    @Override
    protected GoogleMap.OnMarkerClickListener onMarkerClickListener() {
        return new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                for(MarkerLocal markerLocal : getListaMarkerLocal()) {

                    if(markerLocal.getMarker().equals(marker)){

                        Bundle parametros = new Bundle();
                        parametros.putSerializable("LOCAL", markerLocal.getLocal());
                        parametros.putString("UID_USUARIO_LOGADO", getMainMobile().getUidUsuario());
                        parametros.putString("IMG_USUARIO_LOGADO", getMainMobile().getImgUsuario());
                        parametros.putString("NOME_USUARIO_LOGADO", getMainMobile().getNomeUsuario());
                        parametros.putBoolean("IS_USUARIO_LOGADO", getMainMobile().isUsuarioLogado());
                        parametros.putParcelable("LOCALIZACAO_ATUAL", localizacaoAtual);

                        DetalheDialog detalheDialog = new DetalheDialog();
                        detalheDialog.setArguments(parametros);

                        detalheDialog.show(getFragmentManager(),"detalhe_dialog");

                        return true;
                    }
                }


                return false;
            }
        };
    }

    @Override
    public void onLocationChanged(Location location) {
        if(!isLocalEncontrado) {
            setMapLocation(location);
            isLocalEncontrado = true;
        }
        this.localizacaoAtual = new LatLng(location.getLatitude(), location.getLongitude());

        super.onLocationChanged(location);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == CODIGO_PERMISSAO_MAPA) {
            if (!isPermissaoOk(grantResults)) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sincronizarMapa();
                    }
                });
            }
        }
    }


        public List<Local> getListaLocais() {
        if(listaLocais == null) {
            listaLocais = new ArrayList<>();
        }
        return listaLocais;
    }

    public boolean isEsconderBootonBar() {
        return isEsconderBootonBar;
    }

    public void setEsconderBootonBar(boolean esconderBootonBar) {
        isEsconderBootonBar = esconderBootonBar;
    }
    public void setListaLocais(List<Local> listaLocais) {
        this.listaLocais = listaLocais;
    }

    public boolean isEsconderAddFab() {
        return isEsconderAddFab;
    }

    public void setEsconderAddFab(boolean esconderAddFab) {
        isEsconderAddFab = esconderAddFab;
    }

    public boolean isPermitirMarcarMapa() {
        return isPermitirMarcarMapa;
    }

    public void setPermitirMarcarMapa(boolean permitirMarcarMapa) {
        isPermitirMarcarMapa = permitirMarcarMapa;
    }

    public List<MarkerLocal> getListaMarkerLocal() {
        if(listaMarkerLocal == null) {
            listaMarkerLocal = new ArrayList<>();
        }
        return listaMarkerLocal;
    }

    public void setListaMarkerLocal(List<MarkerLocal> listaMarkerLocal) {
        this.listaMarkerLocal = listaMarkerLocal;
    }

    public enum ButtonBarEnum {

        MAPA(R.mipmap.ic_map, "Mapa", 0),
        PESQUISAR(R.mipmap.ic_pesquisar_map, "Pesquisar", 1),
        MEUS_LOCAIS(R.mipmap.ic_meus_mapas, "Meus Locais", 2),
        GALERIA(R.mipmap.ic_galeria, "Galeria", 3);

        private final int imgItem;
        private final String descricao;
        private final int posicao;

        ButtonBarEnum(int imgItem, String descricao, int posicao) {
            this.imgItem = imgItem;
            this.descricao = descricao;
            this.posicao = posicao;
        }

        public int getImgItem() {
            return imgItem;
        }

        public String getDescricao() {
            return descricao;
        }

        public int getPosicao() {
            return posicao;
        }

        public static ButtonBarEnum getEnum(int posicao) {

            for(ButtonBarEnum b : ButtonBarEnum.values()) {

                if(b.getPosicao() == posicao) {
                    return b;
                }
            }

            return null;
        }
    }
}
