package br.com.dias.streetway.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.dias.streetway.R;
import br.com.dias.streetway.activity.MainMobileActivity;

/**
 * Created by FernandoDias on 24/02/16.
 */
public class BaseFragment extends Fragment {

    public static  final int CODIGO_PERMISSAO_TIRAR_FOTO = 100;
    public static  final int CODIGO_PERMISSAO_FOTO_GALERIA = 200;
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int RESULT_LOAD_IMAGE = 2;

    private CallbackManager callbackManager;
    private LoginManager loginManager;
    private Bundle savedInstance;

    private LovelyProgressDialog progressDialog;

    protected void showProgress(int msg) {
        if(progressDialog == null) {

            progressDialog = new LovelyProgressDialog(getContext())
                    .setIcon(R.mipmap.ic_loading_cronometro)
                    .setTitle(msg)
                    .setTopColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        }

        progressDialog.show();
    }

    protected void showProgress() {
        showProgress(R.string.label_aguarde);
    }

    protected void hideProgress() {
        progressDialog.dismiss();
    }



    protected boolean verificarAcessoFuncionalidade(int codigoPermissao, String... acessos) {

        List<String> permissoesNegadas = new ArrayList<>();

        for(String acesso : acessos){
            if (ActivityCompat.checkSelfPermission(getActivity(), acesso) != PackageManager.PERMISSION_GRANTED) {
                permissoesNegadas.add(acesso);
            }
        }

        if(!permissoesNegadas.isEmpty()){
            requestPermissions(permissoesNegadas.toArray(new String[]{}), codigoPermissao);
            return false;
        }

        return true;
    }

    protected void publicarFotoNoFAcebookComLogin(final Bitmap foto, final String msg) {

        callbackManager = CallbackManager.Factory.create();
        List<String> permissionNeeds = Arrays.asList("publish_actions");

        loginManager = LoginManager.getInstance();
        loginManager.logInWithPublishPermissions(this, permissionNeeds);
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                publicarFotoFacebook(foto, msg);
            }

            @Override
            public void onCancel() {
                System.out.println("onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                System.out.println("onError");
            }
        });
    }

    protected boolean isPermissaoOk(int[] permissoes) {
        boolean isPermissaoNegada = false;

        for(int permissao : permissoes) {
            if(permissao < 0 ) isPermissaoNegada = true;
        }
        return isPermissaoNegada;
    }



    public void publicarFotoFacebook(Bitmap foto, String descricao) {

        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(foto)
                .setCaption(descricao)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareApi.share(content, null);
    }




    protected void iniciarCameraFoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    protected void dispatchTakePictureIntent() {

        if(verificarAcessoFuncionalidade(CODIGO_PERMISSAO_TIRAR_FOTO, Manifest.permission.CAMERA)) {
            iniciarCameraFoto();
        }

    }

    protected void iniciarGaleriaFoto() {
        Intent i = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    protected void dispatchOpenGaleryIntent() {

        String[] permissoes = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if(verificarAcessoFuncionalidade(CODIGO_PERMISSAO_FOTO_GALERIA, permissoes)) {
            iniciarGaleriaFoto();
        }
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

    }

    public CallbackManager getCallbackManager() {
        return callbackManager;
    }

    public void setCallbackManager(CallbackManager callbackManager) {
        this.callbackManager = callbackManager;
    }

    public Bundle getSavedInstance() {
        return savedInstance;
    }

    public void setSavedInstance(Bundle savedInstance) {
        this.savedInstance = savedInstance;
    }

    protected MainMobileActivity getMainMobile() {
        return (MainMobileActivity) getActivity();
    }

    protected void voltarFragment() {

        if(!getMainMobile().getFilaFragment().isEmpty()){

            getMainMobile().getFilaFragment().remove(getMainMobile().getFilaFragment().getLast());

            Fragment fragmentAnterior = getMainMobile().getFilaFragment().getLast();
            if(fragmentAnterior instanceof MainMobileActivity.OnActionBarUpdatesListener) {
                MainMobileActivity.onActionMainMobile.setOnActionBarUpdatesListener((MainMobileActivity.OnActionBarUpdatesListener)fragmentAnterior);
            }
        }

        getFragmentManager().popBackStack();
    }

    public void addFragment(Fragment fragment, String tag, int menu, String titulo) {
        getMainMobile().adicionarFragment(fragment, tag);
        getMainMobile().getFilaMenu().add(new MainMobileActivity.MenuStreeteWay(R.menu.main_mobile, "StreetWay"));
        getMainMobile().getFilaFragment().add(fragment);
    }





}
