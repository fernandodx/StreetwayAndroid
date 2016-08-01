package br.com.dias.streetway.util;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.ui.auth.core.AuthProviderType;
import com.firebase.ui.auth.core.FirebaseLoginBaseActivity;
import com.firebase.ui.auth.core.FirebaseLoginError;

import br.com.dias.streetway.activity.MainMobileActivity;

/**
 * Created by FernandoDias on 16/04/16.
 */
public class FireBaseUtil extends FirebaseLoginBaseActivity {

    private static ServidorEnun servidorAtual;

    static {
        servidorAtual = ServidorEnun.DESENVOLVIMENTO;
    }


    private static FireBaseUtil fireBaseUtil;
    private LoginFireBaseListener loginFireBaseListener;

    private static Firebase firebase;

    public static FireBaseUtil getInstance() {
        if(fireBaseUtil == null) {
            fireBaseUtil = new FireBaseUtil();
            init();
        }

        return fireBaseUtil;
    }

    private static void init() {

        firebase = new Firebase("https://streetway.firebaseio.com");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginFireBaseListener = MainMobileActivity.loginFireBaseListener;

        String acao = null;

        if(getIntent() != null && getIntent().getExtras() != null) {
            acao = getIntent().getExtras().getString("ACAO");
        }

        if(acao.equalsIgnoreCase("INICIAR_LOGIN")) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showFirebaseLoginPrompt();
                }
            }, 1000);
        }

        if(acao.equalsIgnoreCase("LOGOUT")) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    logout();
                }
            }, 1000);
        }




    }

    @Override
    protected void onStart() {
        super.onStart();

        setEnabledAuthProvider(AuthProviderType.FACEBOOK);

    }


    @Override
    protected Firebase getFirebaseRef() {
        return firebase;
    }

    @Override
    protected void onFirebaseLoginProviderError(FirebaseLoginError firebaseLoginError) {
        Log.e("LOGIN", firebaseLoginError.message);
    }

    @Override
    protected void onFirebaseLoginUserError(FirebaseLoginError firebaseLoginError) {
        Log.e("LOGIN", firebaseLoginError.message);
    }

    @Override
    public void onFirebaseLoggedIn(AuthData authData) {

        loginFireBaseListener.onFireBaseLoggedIn(authData);
        finish();
    }

    @Override
    public void onFirebaseLoggedOut() {
        // TODO: Handle logout
        loginFireBaseListener.onFireBaseLoggedOut();
//        finish();

    }

    public Firebase getLocais() {
        return firebase.child(servidorAtual.endereco+"/LOCAIS");
    }

    public Firebase getGaleria() {
        return firebase.child(servidorAtual.endereco+"/GALERIA");
    }

    public static Firebase getFirebase() {
        return firebase;
    }

    public interface LoginFireBaseListener{

        void onFireBaseLoggedIn(AuthData authData);
        void onFireBaseLoggedOut();

    }

    public static enum ServidorEnun {

        PRODUCAO("PRODUCAO"),
        DESENVOLVIMENTO("DESENVOLVIMENTO");

        public String endereco;

        ServidorEnun(String endereco) {
            this.endereco = endereco;
        }
    }

}
