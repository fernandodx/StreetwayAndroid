package br.com.dias.streetway.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MenuRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import java.util.LinkedList;

import br.com.dias.streetway.R;
import br.com.dias.streetway.fragments.GaleriaLocalFragment;
import br.com.dias.streetway.fragments.LocaisFragment;
import br.com.dias.streetway.fragments.MapaFragment;
import br.com.dias.streetway.util.FireBaseUtil;

public class MainMobileActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static MainMobileActivity instance;

    public static OnActionMainMobile onActionMainMobile;
    public static OnActionBarUpdatesListener onActionBarUpdates;
    public static FireBaseUtil.LoginFireBaseListener loginFireBaseListener;
    private static LinkedList<MenuStreeteWay> filaMenu;
    private static LinkedList<Fragment> filaFragment;
    private FireBaseUtil fireBaseUtil;

    private Toolbar toolbar;

    private LoginButton loginButton;
    private CallbackManager callbackManager;

    private static NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_mobile);

        loginFireBaseListener = getFirebaseLogin();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onActionBarUpdates != null) {
                    onActionBarUpdates.itemMenuVoltarClick(v);
                }
            }
        });

        onActionMainMobile = new OnActionMainMobile() {
            @Override
            public void addFragment(Fragment fragment, String tag) {
                adicionarFragment(fragment,tag);
            }

            @Override
            public void replaceFragment(Fragment fragment, String tag) {
                trocarFragment(fragment, tag);
            }

            @Override
            public void removeFragment(Fragment fragment) {
                removeFragment(fragment);
            }

            @Override
            public void alterarMenuActionbar(@MenuRes final int menuRes, final boolean isNavigationVoltar) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toolbar.getMenu().clear();
                        toolbar.inflateMenu(menuRes);
                        setVisivelBotaoVoltar(isNavigationVoltar);
                    }
                });
            }

            @Override
            public void removerMenuActionbar(final boolean isNavigationVoltar) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toolbar.getMenu().clear();
                        setVisivelBotaoVoltar(isNavigationVoltar);
                    }
                });
            }

            @Override
            public void setOnActionBarUpdatesListener(OnActionBarUpdatesListener onActionBarUpdatesListener) {
                onActionBarUpdates = onActionBarUpdatesListener;
            }
        };



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(1).setChecked(true);

        if(getSupportFragmentManager() == null || getSupportFragmentManager().getFragments() == null || getSupportFragmentManager().getFragments().size() == 0) {
            MapaFragment mapaFragment = new MapaFragment();
            adicionarFragment(mapaFragment, MapaFragment.TAG);
            getFilaMenu().add(new MenuStreeteWay(R.menu.main_mobile, "StreetWay"));
            getFilaFragment().add(mapaFragment);
        }

        //MENU LATERAL
        setLoginButton((Button) navigationView.getHeaderView(0).findViewById(R.id.bt_login));
        setNomeUsuarioTextView((TextView) navigationView.getHeaderView(0).findViewById(R.id.nome_usuario));
        setUsuarioImageView((ImageView) navigationView.getHeaderView(0).findViewById(R.id.img_perfil_facebook));
        setEmailUsuarioTextView((TextView) navigationView.getHeaderView(0).findViewById(R.id.email_usuario));

        getLoginButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(getNomeUsuario() == null) {

                    Intent telaLogin = new Intent(getBaseContext(), FireBaseUtil.class);
                    telaLogin.putExtra("ACAO", "INICIAR_LOGIN");
                    startActivity(telaLogin);

                }else{

                    Intent telaLogin = new Intent(getBaseContext(), FireBaseUtil.class);
                    telaLogin.putExtra("ACAO", "LOGOUT");
                    startActivity(telaLogin);


                }

            }
        });

        Firebase streetWayFire = FireBaseUtil.getInstance().getFirebase();
        streetWayFire.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {

                if(authData != null) {
                    setUidUsuario((String) authData.getAuth().get("uid"));
                    setNomeUsuario((String) authData.getProviderData().get("displayName"));
                    setEmailUsuario((String) authData.getProviderData().get("email"));
                    setImgUsuario((String) authData.getProviderData().get("profileImageURL"));
                }else{
                    setUidUsuario(null);
                    setNomeUsuario(null);
                    setEmailUsuario(null);
                    setImgUsuario(null);
                }

                atualizarDadosUsuario();

            }
        });


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getFilaMenu().removeLast();
            MenuStreeteWay menuAtual = getFilaMenu().getLast();
            toolbar.getMenu().clear();
            toolbar.inflateMenu(menuAtual.menu);
            toolbar.setTitle(menuAtual.titulo);

            if(onActionBarUpdates != null) {
                onActionBarUpdates.itemMenuVoltarClick(null);
            }

        }else{
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(getSupportFragmentManager() == null || getSupportFragmentManager().getFragments() == null || getSupportFragmentManager().getFragments().size() == 0) {
            getMenuInflater().inflate(R.menu.main_mobile, menu);
        }else{
            MenuStreeteWay menuAtual = getFilaMenu().getLast();
            getMenuInflater().inflate(menuAtual.menu, menu);
            toolbar.setTitle(menuAtual.titulo);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(onActionBarUpdates != null) {
            onActionBarUpdates.itemMenuSelecionado(item);
        }
        return true;
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.locais: {

                LocaisFragment locaisFragment = new LocaisFragment();
                addFragment(locaisFragment, LocaisFragment.TAG, R.menu.main_mobile, "Feed de Locais");

                break;
            }
            case R.id.mapa: {

                if(!(getCurrentFragment() instanceof MapaFragment)) {

                    MapaFragment mapaFragment = new MapaFragment();
                    addFragment(mapaFragment, MapaFragment.TAG, R.menu.main_mobile, "Mapa dos locais");

                }

                break;
            }
            case R.id.meus_locais: {

                Bundle parametros = new Bundle();
                parametros.putBoolean("IS_MEUS_LOCAIS", true);

                LocaisFragment meusLocais = new LocaisFragment();
                meusLocais.setArguments(parametros);

                addFragment(meusLocais, LocaisFragment.TAG, R.menu.main_mobile, "Meus Locais");

                break;
            }
            case R.id.minhas_fotos: {

                Bundle parametros = new Bundle();
                parametros.putString("UID_USUARIO", getUidUsuario());

                GaleriaLocalFragment galeriaLocalUsuario = new GaleriaLocalFragment();
                galeriaLocalUsuario.setArguments(parametros);

                addFragment(galeriaLocalUsuario, GaleriaLocalFragment.TAG, R.menu.main_mobile, "Minhas Fotos");

                break;
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setVisivelBotaoVoltar(boolean isVisivel) {
        getSupportActionBar().setDisplayShowHomeEnabled(isVisivel);
        getSupportActionBar().setDisplayHomeAsUpEnabled(isVisivel);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    public LinkedList<MenuStreeteWay> getFilaMenu() {
        if(filaMenu == null) {
            filaMenu = new LinkedList<>();
        }
        return filaMenu;
    }

    public void setFilaMenu(LinkedList<MenuStreeteWay> filaMenu) {
        filaMenu = filaMenu;
    }

    public LinkedList<Fragment> getFilaFragment() {
        if(filaFragment == null) {
            filaFragment = new LinkedList<>();
        }
        return filaFragment;
    }

    public void setFilaFragment(LinkedList<Fragment> filaFragment) {
        filaFragment = filaFragment;
    }

    private FireBaseUtil.LoginFireBaseListener getFirebaseLogin() {

        return new FireBaseUtil.LoginFireBaseListener() {

            @Override
            public void onFireBaseLoggedIn(AuthData authData) {

                if(onActionBarUpdates != null) {
                    onActionBarUpdates.loginFacebookSucess(authData);
                }

                setNomeUsuario((String) authData.getProviderData().get("displayName"));
                setEmailUsuario((String) authData.getProviderData().get("email"));
                setImgUsuario((String) authData.getProviderData().get("profileImageURL"));

                atualizarDadosUsuario();

            }

            @Override
            public void onFireBaseLoggedOut() {

                setNomeUsuario(null);
                setEmailUsuario(null);
                setImgUsuario(null);
                atualizarDadosUsuario();
            }

        };
    }


    public interface OnActionMainMobile {

        void addFragment(Fragment fragment, String tag);
        void replaceFragment(Fragment fragment, String tag);
        void removeFragment(Fragment fragment);
        void alterarMenuActionbar(@MenuRes int menu, boolean isNavigationVoltar);
        void removerMenuActionbar(boolean isNavigationVoltar);
        void setOnActionBarUpdatesListener(OnActionBarUpdatesListener onActionBarUpdatesListener);
    }

    public interface OnActionBarUpdatesListener {
        void itemMenuSelecionado(MenuItem menuItem);
        void itemMenuVoltarClick(View v);
        void loginFacebookSucess(AuthData authData);
    }

    public static class MenuStreeteWay {

        Integer menu;
        String titulo;

        public MenuStreeteWay(Integer menu, String titulo) {
            this.menu = menu;
            this.titulo = titulo;
        }
    }

    private void voltarFragment() {
        Fragment fragmentAnterior = getFilaFragment().pop();

        if(fragmentAnterior instanceof MainMobileActivity.OnActionBarUpdatesListener) {
            MainMobileActivity.onActionMainMobile.setOnActionBarUpdatesListener((MainMobileActivity.OnActionBarUpdatesListener)fragmentAnterior);
        }

        getFragmentManager().popBackStack();
    }

    public void addFragment(Fragment fragment, String tag, int menu, String titulo) {
        adicionarFragment(fragment, tag);
        getFilaMenu().add(new MainMobileActivity.MenuStreeteWay(menu, titulo));
        getFilaFragment().add(fragment);

        if(fragment instanceof OnActionBarUpdatesListener) {
            MainMobileActivity.onActionMainMobile.setOnActionBarUpdatesListener((OnActionBarUpdatesListener) fragment);
        }
    }

    public NavigationView getNavigationView() {
        return navigationView;
    }

    public void setNavigationView(NavigationView navigationView) {
        this.navigationView = navigationView;
    }
}
