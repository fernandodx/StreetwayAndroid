package br.com.dias.streetway.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Date;

import br.com.dias.streetway.R;
import br.com.dias.streetway.activity.MainMobileActivity;
import br.com.dias.streetway.adapter.AdapterTipoLocal;
import br.com.dias.streetway.domain.Avaliacao;
import br.com.dias.streetway.domain.Foto;
import br.com.dias.streetway.domain.Local;
import br.com.dias.streetway.domain.TipoLocal;
import br.com.dias.streetway.util.FireBaseUtil;
import br.com.dias.streetway.util.Util;
import br.com.dias.streetway.util.ValidacaoUtil;

/**
 * Created by FernandoDias on 16/04/16.
 */
public class AddLocalFragment extends BaseMapFragment implements MainMobileActivity.OnActionBarUpdatesListener {

    public static final String TAG = "AddLocalFragment";

    private EditText localEditText;
    private RatingBar classificacaoRatingBar;
    private ImageView localImageView;
    private Marker localmarker;
    private ImageView galeriaImageView;
    private ImageView cameraImageView;
    private EditText comentarioEditText;
    private ViewGroup layoutImgBotoes;
    private ImageView marcarLocalImageView;
    private LatLng pontoSelecionado;
    private Bitmap imagemLocal;
    private String imgUsuarioBase64;
    private String nomeUsuario;
    private boolean isPublicarFacebook;
    private Spinner tipoSpinner;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    protected void onMapaCarregado(GoogleMap mapa) {

        if(pontoSelecionado != null) {

            if(localmarker != null) {
                localmarker.remove();
            }

            localmarker = adicionarMarcador(pontoSelecionado);
            setMapLocation(pontoSelecionado, 16);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_local, container, false);

        super.onCreateViewMap();
        setRetainInstance(true);

        galeriaImageView = (ImageView) view.findViewById(R.id.img_galeria);
        cameraImageView = (ImageView) view.findViewById(R.id.img_camera);
        localEditText = (EditText) view.findViewById(R.id.nome_local);
        comentarioEditText = (EditText) view.findViewById(R.id.comentario_local);
        classificacaoRatingBar = (RatingBar) view.findViewById(R.id.classificacao_ratingBar);
        layoutImgBotoes = (ViewGroup) view.findViewById(R.id.layout_botoes_img);
        localImageView = (ImageView) view.findViewById(R.id.img_local);
        marcarLocalImageView = (ImageView) view.findViewById(R.id.img_marcar_mapa);
        tipoSpinner = (Spinner) view.findViewById(R.id.tipo_spinner);

        marcarLocalImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            MapaFragment mapaFragment = new MapaFragment();
            mapaFragment.setPermitirMarcarMapa(true);
            mapaFragment.setEsconderBootonBar(true);
            mapaFragment.setEsconderAddFab(true);

            MainMobileActivity.onActionMainMobile.addFragment(mapaFragment, "MAPA_SELECAO");

            }
        });


        tipoSpinner.setAdapter(new AdapterTipoLocal(getContext()));

        cameraImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        galeriaImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchOpenGaleryIntent();
            }
        });

        MainMobileActivity.onActionMainMobile.alterarMenuActionbar(R.menu.menu_add_locais, true);
        MainMobileActivity.onActionMainMobile.setOnActionBarUpdatesListener(this);

        MainMobileActivity mainMobileActivity = (MainMobileActivity)getActivity();
        setNomeUsuario(mainMobileActivity.getNomeUsuario());
        setImgUsuarioBase64(mainMobileActivity.getImgUsuario());


        mainMobileActivity.getToolbar().setTitle("Novo Local");
        mainMobileActivity.getFilaMenu().add(new MainMobileActivity.MenuStreeteWay(R.menu.menu_add_locais, "Novo Local"));

        if(savedInstanceState != null) {
            setSavedInstance(savedInstanceState);
            if(savedInstanceState.containsKey("IMG_LOCAL")){
                imagemLocal = savedInstanceState.getParcelable("IMG_LOCAL");
            }
        }

        isPublicarFacebook = true;


        return view;
    }


    @Override
    public void onLocationChanged(Location location) {

        super.onLocationChanged(location);

        if(pontoSelecionado == null) {
            adicionarMarcadorAddLocal(new LatLng(location.getLatitude(), location.getLongitude()));
        }

    }

    public void adicionarMarcadorAddLocal(LatLng localPonto) {

        localmarker = adicionarMarcador(localPonto, null);
        if(localmarker != null) {
            atualizarCameraMapa(localPonto, 16);
            this.pontoSelecionado = localPonto;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            imagemLocal = imageBitmap;

            localImageView.setImageBitmap(imagemLocal);
            layoutImgBotoes.setVisibility(View.GONE);
        }

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == getActivity().RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            imagemLocal = BitmapFactory.decodeFile(picturePath);

            localImageView.setImageBitmap(imagemLocal);
            layoutImgBotoes.setVisibility(View.GONE);
        }
    }

    public void carregarimagem(Bitmap bitmap) {
        imagemLocal = bitmap;
        localImageView.setImageBitmap(imagemLocal);
        layoutImgBotoes.setVisibility(View.GONE);

    }

    private void salvarLocalFirebase() {

        String comentario = comentarioEditText.getText().toString();
        String nomeLocal = localEditText.getText().toString();

        Local local = new Local();
        local.setNome(nomeLocal);
        local.setComentario(comentario);
//        local.setAvaliacao(new Double(classificacaoRatingBar.getRating()));
        local.carregarImagemLocalBitmap(imagemLocal);
        local.carregarCoordenadas(pontoSelecionado);

        local.setImgUsuario(imgUsuarioBase64);
        local.setNomeUsuario(nomeUsuario);
        local.setDatainclusao(new Date());

        local.setIdTipoLocal(((TipoLocal)tipoSpinner.getSelectedItem()).getId());

        Firebase locaisFirebase = FireBaseUtil.getInstance().getLocais();
        Firebase localNovo = locaisFirebase.push();

        localNovo.setValue(local);

        Foto foto = new Foto();
        foto.setUidLocal(localNovo.getKey());
        foto.setUidUsuario(getMainMobile().getUidUsuario());
        foto.setDataInclusao(new Date());
        foto.setImgBase64(Util.BitmapToBase64(imagemLocal, Bitmap.CompressFormat.PNG, 70));

        Firebase galeriaFire = FireBaseUtil.getInstance().getGaleria();
        galeriaFire.push().setValue(foto);

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setUidUsuario(getMainMobile().getUidUsuario());
        avaliacao.setAvaliacao(new Double(classificacaoRatingBar.getRating()));

        Firebase avaliacaoFire = FireBaseUtil.getInstance().getLocais().child(localNovo.getKey()).child("AVALIACOES");
        avaliacaoFire.push().setValue(avaliacao);


    }

    private void salvarPublicarLocal() {

        salvarLocalFirebase();

        if(isPublicarFacebook && nomeUsuario != null) {

            String comentario = comentarioEditText.getText().toString();
            String nomeLocal = localEditText.getText().toString();
            String textoPublicacao = null;

            if(comentario.isEmpty()) {
                textoPublicacao = "Acabei de adicionar o local "+nomeLocal+ " no Streetway, venha conhecer!";
            }else{
                textoPublicacao = nomeLocal + " foi adicionado ao Streetway: " + comentario;
            }

            //COLOCAR EM BACKGROUND
            publicarFotoFacebook(imagemLocal, textoPublicacao);

            ((MainMobileActivity)getActivity()).getFilaMenu().pop();
            getFragmentManager().popBackStack();

        }else if (isPublicarFacebook) {

            Intent telaLogin = new Intent(getContext(), FireBaseUtil.class);
            telaLogin.putExtra("ACAO", "INICIAR_LOGIN");
            startActivity(telaLogin);

        }else{

            ((MainMobileActivity)getActivity()).getFilaMenu().pop();
            getFragmentManager().popBackStack();
        }


        Util.showAlertSucesso(getContext(), R.string.msg_sucesso_local_incluido, R.string.label_ok, null);

    }

    private boolean validarCamposObrigatorios() {

        boolean isOk = true;

        if(localEditText.getText().toString().isEmpty()) {
            ValidacaoUtil.animarCampoMsg(localEditText, R.string.msg_campo_obrigatorio);
            isOk = false;
        }
        if(imagemLocal == null) {
            ValidacaoUtil.animarCampoMsg(cameraImageView, galeriaImageView);
            isOk = false;
        }
        if(classificacaoRatingBar.getRating() <= 0) {
            ValidacaoUtil.animarCampoMsg(classificacaoRatingBar);
            isOk = false;
        }
        if(pontoSelecionado == null) {
            ValidacaoUtil.animarCampoMsg(marcarLocalImageView);
            isOk = false;
        }

        return isOk;
    }

    private boolean validarUsuarioLogado() {
        if(!getMainMobile().isUsuarioLogado()) {

            Util.showAlertErro(getContext(), R.string.msg_erro_usuario_nao_logado, R.string.label_login, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            return false;
        }

        return true;
    }


    @Override
    public void itemMenuSelecionado(MenuItem menuItem) {

        if(menuItem.getItemId() == R.id.salva_local){

            Util.esconderTecladoMobile(getContext(),localEditText, comentarioEditText);
            if(validarUsuarioLogado() && validarCamposObrigatorios()) {
                salvarPublicarLocal();
            }
        }

        if(menuItem.getItemId() == R.id.publicar_facebook) {

            isPublicarFacebook = !isPublicarFacebook;

            if(isPublicarFacebook) {
                menuItem.setIcon(ContextCompat.getDrawable(getContext(), R.mipmap.ic_facebook_ativo));
            }else{
                menuItem.setIcon(ContextCompat.getDrawable(getContext(), R.mipmap.ic_facebook_inativo));
            }
        }
    }

    @Override
    public void itemMenuVoltarClick(View v) {
        voltarFragment();
    }

    @Override
    public void loginFacebookSucess(AuthData authData) {

        String comentario = comentarioEditText.getText().toString();
        String nomeLocal = localEditText.getText().toString();
        String textoPublicacao = null;

        if(comentario.isEmpty()) {
            textoPublicacao = "Acabei de adicionar o local "+nomeLocal+ " no Streetway, venha conhecer!";
        }else{
            textoPublicacao = nomeLocal + " foi adicionado ao Streetway: " + comentario;
        }

        setNomeUsuario((String) authData.getProviderData().get("displayName"));
        setImgUsuarioBase64((String) authData.getProviderData().get("profileImageURL"));

        salvarLocalFirebase();

        //COLOCAR EM BACKGROUND
        publicarFotoFacebook(imagemLocal, textoPublicacao);

//        ((MainMobileActivity)getActivity()).getFilaMenu().pop();
//        getFragmentManager().popBackStack();

    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == CODIGO_PERMISSAO_TIRAR_FOTO) {
            if (!isPermissaoOk(grantResults)) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iniciarCameraFoto();
                    }
                });
            }
        }

        if (requestCode == CODIGO_PERMISSAO_FOTO_GALERIA) {
            if (!isPermissaoOk(grantResults)) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iniciarGaleriaFoto();
                    }
                });
            }
        }

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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putParcelableArrayList("PONTOS", (ArrayList<? extends Parcelable>) getListaPosicaoLocais());
        savedInstanceState.putParcelable("IMG_LOCAL", imagemLocal);
        super.onSaveInstanceState(savedInstanceState);
    }


    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getImgUsuarioBase64() {
        return imgUsuarioBase64;
    }

    public void setImgUsuarioBase64(String imgUsuarioBase64) {
        this.imgUsuarioBase64 = imgUsuarioBase64;
    }
}
