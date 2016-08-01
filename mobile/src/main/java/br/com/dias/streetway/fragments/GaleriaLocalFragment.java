package br.com.dias.streetway.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.dias.streetway.R;
import br.com.dias.streetway.activity.MainMobileActivity;
import br.com.dias.streetway.domain.Foto;
import br.com.dias.streetway.domain.Local;
import br.com.dias.streetway.util.FireBaseUtil;
import br.com.dias.streetway.util.Util;
import it.gmariotti.cardslib.library.extra.staggeredgrid.internal.CardGridStaggeredArrayAdapter;
import it.gmariotti.cardslib.library.extra.staggeredgrid.view.CardGridStaggeredView;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardThumbnail;

/**
 * Created by FernandoDias on 14/06/16.
 */
public class GaleriaLocalFragment extends BaseFragment implements MainMobileActivity.OnActionBarUpdatesListener{

    public static final String TAG = "GaleriaLocalFragment";


    private Local localSelecionado;

    private CardGridStaggeredView galeria;
    private List<Card> listaCardLocal;

    private FloatingActionButton galeriaFab;
    private FloatingActionButton fotoFab;
    private FloatingActionMenu floatingActionMenu;

    private Firebase localGaleriaFirebase;
    private Query galeriaUsuario;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(localSelecionado != null) {
            galeriaUsuario = FireBaseUtil.getInstance().getGaleria().orderByChild("uidLocal").equalTo(localSelecionado.getId());
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_galeria_local, container, false);

        this.galeria = (CardGridStaggeredView) view.findViewById(R.id.card_grid_galeria);
        this.galeriaFab = (FloatingActionButton) view.findViewById(R.id.escolher_galeria_fab);
        this.fotoFab = (FloatingActionButton) view.findViewById(R.id.tirar_foto_fab);
        this.floatingActionMenu = (FloatingActionMenu) view.findViewById(R.id.fab_add_foto);
        this.listaCardLocal = new ArrayList<>();


        galeriaFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchOpenGaleryIntent();
            }
        });

        fotoFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        if(getArguments() != null && getArguments().get("UID_USUARIO") != null) {

            String idUsuario = getArguments().getString("UID_USUARIO");
            galeriaUsuario = FireBaseUtil.getInstance().getGaleria().orderByChild("uidUsuario").equalTo(idUsuario);
            galeriaUsuario.addChildEventListener(getChildEventListener());
            galeriaUsuario.addValueEventListener(getValueEventListenerLocais());

        } else {

            galeriaUsuario.addChildEventListener(getChildEventListener());
            galeriaUsuario.addValueEventListener(getValueEventListenerLocais());
        }

        if(getMainMobile().isUsuarioLogado()) {
            floatingActionMenu.setVisibility(View.VISIBLE);
        }else{
            floatingActionMenu.setVisibility(View.GONE);
        }

        showProgress();

        return view;
    }

    private void addCardToList(String imgCardBase64) {
        SquaredGridCard card = new SquaredGridCard(getContext());
        card.fotoBase64 = imgCardBase64;
        card.thumbHeightId = R.dimen.carddemo_extras_basestaggered_height1;
        card.color = R.color.colorBackgroundCardView;
        card.init();
        listaCardLocal.add(card);
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

                Foto foto = Foto.createFoto(dataSnapshot);
                addCardToList(foto.getImgBase64());

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

                //Set the arrayAdapter
                CardGridStaggeredArrayAdapter mCardArrayAdapter = new CardGridStaggeredArrayAdapter(getActivity(), listaCardLocal);
                galeria.setAdapter(mCardArrayAdapter);
            }
        });
    }


    public class SquaredGridCard extends Card {

        protected String headerTitle;
        protected int color;
        protected int thumbHeightId;
        protected String fotoBase64;

        public SquaredGridCard(Context context) {
            super(context, R.layout.card_adapter_img_local_galeria);

        }

        private void init() {

            SquaredGridThumb thumb = new SquaredGridThumb(getContext(), color);
            thumb.setExternalUsage(true);
            thumb.thumbHeightId = thumbHeightId;

            addCardThumbnail(thumb);

        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {
            //Setup the main content title
             ImageView fotoImageView = (ImageView) view.findViewById(R.id.foto_local_galeria);
             fotoImageView.setImageBitmap(Util.StringBase64ToBitmap(fotoBase64));
        }

    }

    class SquaredGridThumb extends CardThumbnail {

        int color;
        protected int thumbHeightId;

        public SquaredGridThumb(Context context, int color) {
            super(context);
            this.color = color;
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View viewImage) {

            ImageView image = (ImageView) viewImage;

            if(color > 0) {
                image.setBackgroundColor(color);
            }

            image.getLayoutParams().height = (int) getResources().getDimension(thumbHeightId);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            addImageGaleria(imageBitmap);
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

            Bitmap imagemBitmap = BitmapFactory.decodeFile(picturePath);
            addImageGaleria(imagemBitmap);
        }
    }

    private void addImageGaleria(Bitmap bitmap) {

        String imgLocalBase64 = Util.BitmapToBase64(bitmap, Bitmap.CompressFormat.PNG,70);

        Foto foto = new Foto();
        foto.setUidLocal(localSelecionado.getId());
        foto.setUidUsuario(getMainMobile().getUidUsuario());
        foto.setDataInclusao(new Date());
        foto.setImgBase64(imgLocalBase64);

        Firebase galeriaFire = FireBaseUtil.getInstance().getGaleria();
        galeriaFire.push().setValue(foto);

        publicarFotoFacebook(bitmap, "Acabei de adicionar um foto na galeria do "+ localSelecionado.getNome().toUpperCase()+ " via Streetway.");


    }

    public Local getLocalSelecionado() {
        return localSelecionado;
    }

    public void setLocalSelecionado(Local localSelecionado) {
        this.localSelecionado = localSelecionado;
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
