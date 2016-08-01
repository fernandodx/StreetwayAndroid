package br.com.dias.streetway.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;

import br.com.dias.streetway.R;
import br.com.dias.streetway.domain.TipoLocalEnum;

/**
 * Created by FernandoDias on 16/04/16.
 */
public class BaseMapFragment extends BaseFragment
                             implements OnMapReadyCallback, LocationListener,
                                        GoogleApiClient.ConnectionCallbacks,
                                        GoogleApiClient.OnConnectionFailedListener {


    public static final int MAPA_ZOOM_PADRAO = 10;
    public static final int CODIGO_PERMISSAO_MAPA = 1;
    public static LatLng localizacaoAtual;


    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private List<LatLng> listaPosicaoLocais;


    protected void sincronizarMapa() {

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    protected void onCreateViewMap() {

        String[] permissoesMapa = new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        };

        if(verificarAcessoFuncionalidade(CODIGO_PERMISSAO_MAPA,permissoesMapa)){
            sincronizarMapa();
        }

        googleApiClient = new GoogleApiClient.Builder(getContext()).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    private GoogleMap.OnMapClickListener onMapClickListener() {
        return new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                onClickMap(latLng);
            }
        };
    }

    protected GoogleMap.OnMarkerClickListener onMarkerClickListener() {
        return new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Toast.makeText(getContext(), marker.toString(), Toast.LENGTH_LONG).show();


                return true;
            }
        };
    }

    protected void onClickMap(LatLng latLng) {
        Log.e("StreetWay", "Clicou no Mapa.");
    }

    protected void onMapaCarregado(GoogleMap mapa) {
        Log.e("StreetWay", "Mapa carregado.");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.map = googleMap;
        carregarMapa(googleMap);
        onMapaCarregado(googleMap);
        getMap().setOnMapClickListener(onMapClickListener());
        getMap().setOnMarkerClickListener(onMarkerClickListener());


        if(getSavedInstance()!=null){
            if(getSavedInstance().containsKey("PONTOS")){
                List<LatLng> listaPontos = getSavedInstance().getParcelableArrayList("points");
                if(listaPontos!=null){
                  adicionarMarcador(listaPontos.get(0));
                }
            }
        }

    }

    private void carregarMapa(GoogleMap map) {
        try{

            map.setMyLocationEnabled(true);
            Location l = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        }catch (SecurityException e) {
            Log.e(getString(R.string.app_name), e.getLocalizedMessage());
        }
    }

    protected void atualizarCameraMapa(LatLng latLng) {
        atualizarCameraMapa(latLng, MAPA_ZOOM_PADRAO);
    }

    protected void atualizarCameraMapa(LatLng latLng, int zoom) {
        final CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .bearing(0)// Rotação
                .tilt(0) //Ângulo
                .zoom(zoom)
                .build();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        map.moveCamera(cameraUpdate);
    }


    protected void startLocationUpdate() {

        try{

            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(3000);
            locationRequest.setFastestInterval(1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

        }catch (SecurityException e) {
            Log.e(getString(R.string.app_name), e.getLocalizedMessage());
        }
    }

    protected void stopLocationUpdate() {
        if(googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    protected void setMapLocation(Location location) {
        if(map != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, MAPA_ZOOM_PADRAO);
            map.animateCamera(cameraUpdate);
        }
    }

    protected void setMapLocation(LatLng latLng) {
       setMapLocation(latLng, MAPA_ZOOM_PADRAO);
    }

    protected void setMapLocation(LatLng latLng, int zoom) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        map.animateCamera(cameraUpdate);
    }

    protected Marker adicionarMarcador(LatLng latLng, TipoLocalEnum tipoLocal) {

        Marker pontoIcone = null;

        if(map != null && getContext() != null) {
            IconGenerator iconGenerator = new IconGenerator(getContext());

            if(tipoLocal != null) {
                iconGenerator.setStyle(tipoLocal.style);
                ImageView img = new ImageView(getContext());
                img.setImageResource(tipoLocal.logo);
                iconGenerator.setContentView(img);
            }else{
                iconGenerator.setStyle(IconGenerator.STYLE_RED);
                ImageView img = new ImageView(getContext());
                img.setImageResource(R.mipmap.ic_meu_local_atual);
                iconGenerator.setContentView(img);
            }

            if(!getListaPosicaoLocais().contains(latLng) && map != null) {

                pontoIcone =  map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon()))
                        .anchor(iconGenerator.getAnchorU(), iconGenerator.getAnchorV())
                        .flat(false)
                        .draggable(false));

                getListaPosicaoLocais().add(latLng);

            }
        }

        return pontoIcone;
    }

    protected Marker adicionarMarcador(LatLng latLng) {
        return adicionarMarcador(latLng, null);
    }

    @Override
    public void onLocationChanged(Location location) {
        localizacaoAtual = new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("STREETWAY", "GPS Conectado");
        startLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("STREETWAY", "GPS Suspenso");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("STREETWAY", "GPS Falha na conexão");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        stopLocationUpdate();
        googleApiClient.disconnect();
        super.onStop();
    }


    public GoogleMap getMap() {

        if(map == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

        return map;
    }



    public void setMap(GoogleMap map) {
        this.map = map;
    }

    public List<LatLng> getListaPosicaoLocais() {
        if(listaPosicaoLocais == null) {
            listaPosicaoLocais = new ArrayList<>();
        }
        return listaPosicaoLocais;
    }

    public void setListaPosicaoLocais(List<LatLng> listaPosicaoLocais) {
        this.listaPosicaoLocais = listaPosicaoLocais;
    }

}
