package br.com.dias.streetway.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import br.com.dias.streetway.R;
import br.com.dias.streetway.util.CirculoTransform;

/**
 * Created by FernandoDias on 24/02/16.
 */
//public class BaseActivity extends FirebaseLoginBaseActivity {
public class BaseActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView nomeUsuarioTextView;
    private TextView emailUsuarioTextView;
    private ImageView usuarioImageView;
    private static String nomeUsuario;
    private static String emailUsuario;
    private static String imgUsuario;
    private static String uidUsuario;
    private Button loginButton;

    @Override
    protected void onStart() {
        super.onStart();
        // All providers are optional! Remove any you don't want.

    }


    protected void ativarActionBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if(toolbar != null) {
            configurarActionBar();
        }

    }

    private void configurarActionBar() {
        toolbar.setTitle("Streetway");
        toolbar.setNavigationIcon(R.drawable.cast_ic_notification_0);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.item_pesquisa) {
                    Toast.makeText(BaseActivity.this, "Pesquisar", Toast.LENGTH_LONG).show();
                }
                if (id == R.id.item_atualizar) {
                    Toast.makeText(BaseActivity.this, "Atualizar", Toast.LENGTH_LONG).show();
                }

                return true;
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        toolbar.inflateMenu(R.menu.menu_padrao);

        setSupportActionBar(toolbar);
    }

    protected void trocarFragment(Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_container, fragment, tag).commit();
    }

    public void adicionarFragment(Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().add(R.id.nav_container, fragment, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    protected void removeFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }

    public Fragment getCurrentFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
        Fragment currentFragment = getSupportFragmentManager()
                .findFragmentByTag(fragmentTag);
        return currentFragment;
    }

    public boolean isUsuarioLogado() {
        return uidUsuario != null;
    }


    public String getNomeUsuario() {
        return nomeUsuario;
    }

    protected void atualizarDadosUsuario() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(nomeUsuario != null) {

                    nomeUsuarioTextView.setText(nomeUsuario);
                    emailUsuarioTextView.setText(emailUsuario);
                    Picasso.with(BaseActivity.this).load(imgUsuario).transform(new CirculoTransform()).into(usuarioImageView);
                    loginButton.setText("SAIR");


                }else{

                    nomeUsuarioTextView.setText("");
                    emailUsuarioTextView.setText("");
                    Picasso.with(BaseActivity.this).load(R.mipmap.ic_user_default).transform(new CirculoTransform()).into(usuarioImageView);
                    loginButton.setText("LOGIN");

                }

            }
        });
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public String getImgUsuario() {
        return imgUsuario;
    }

    public void setImgUsuario(String imgUsuario) {
        this.imgUsuario = imgUsuario;
    }

    public String getUidUsuario() {
        return uidUsuario;
    }

    public void setUidUsuario(String uidUsuario) {
        this.uidUsuario = uidUsuario;
    }

    public TextView getNomeUsuarioTextView() {
        return nomeUsuarioTextView;
    }

    public void setNomeUsuarioTextView(TextView nomeUsuarioTextView) {
        this.nomeUsuarioTextView = nomeUsuarioTextView;
    }

    public TextView getEmailUsuarioTextView() {
        return emailUsuarioTextView;
    }

    public void setEmailUsuarioTextView(TextView emailUsuarioTextView) {
        this.emailUsuarioTextView = emailUsuarioTextView;
    }

    public ImageView getUsuarioImageView() {
        return usuarioImageView;
    }

    public void setUsuarioImageView(ImageView usuarioImageView) {
        this.usuarioImageView = usuarioImageView;
    }

    public Button getLoginButton() {
        return loginButton;
    }

    public void setLoginButton(Button loginButton) {
        this.loginButton = loginButton;
    }



}




