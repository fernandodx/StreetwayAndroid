package br.com.dias.streetway.domain;

import android.graphics.Bitmap;

import com.firebase.client.DataSnapshot;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import br.com.dias.streetway.util.Util;

/**
 * Created by FernandoDias on 16/04/16.
 */
public class Local implements Serializable {

    private String id;
    private Integer idTipoLocal;
//    private Double avaliacao;
    private String imagem;
    private Double latitude;
    private Double longitude;
    private String nome;
    private String comentario;
    private String imgUsuario;
    private String nomeUsuario;
    private Date datainclusao;
    private String idCurtir;

    public Local() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getIdTipoLocal() {
        return idTipoLocal;
    }

    public void setIdTipoLocal(Integer idTipoLocal) {
        this.idTipoLocal = idTipoLocal;
    }

    public void setIdTipoLocal(Long idTipoLocal) {
        this.idTipoLocal = idTipoLocal.intValue();
    }

//    public Double getAvaliacao() {
//        return avaliacao;
//    }
//
//    public void setAvaliacao(Double avaliacao) {
//        this.avaliacao = avaliacao;
//    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getImgUsuario() {
        return imgUsuario;
    }

    public void setImgUsuario(String imgUsuario) {
        this.imgUsuario = imgUsuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public Date getDatainclusao() {
        return datainclusao;
    }

    public void setDatainclusao(Date datainclusao) {
        this.datainclusao = datainclusao;
    }

    public String getIdCurtir() {
        return idCurtir;
    }

    public void setIdCurtir(String idCurtir) {
        this.idCurtir = idCurtir;
    }

    public void carregarImagemLocalBitmap(Bitmap bitmap) {
        if(bitmap != null)
        this.imagem = Util.BitmapToBase64(bitmap, Bitmap.CompressFormat.PNG, 100);
    }

    public void carregarImagemUsuarioBitmap(Bitmap bitmap) {
        if(bitmap != null)
            this.imgUsuario = Util.BitmapToBase64(bitmap, Bitmap.CompressFormat.PNG, 100);
    }

    public void setDataInclusao(Long timer) {
        if(timer == null) {
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timer);
        this.datainclusao = calendar.getTime();
    }


    public void carregarCoordenadas(LatLng latLng) {
        if(latLng != null) {
            this.latitude = latLng.latitude;
            this.longitude = latLng.longitude;
        }
    }

    public static Local createLocal(DataSnapshot localData) {

        Local local = new Local();

//        local.setAvaliacao((Double) localData.child("avaliacao").getValue());
        local.setImagem((String) localData.child("imagem").getValue());
        local.setLatitude((Double) localData.child("latitude").getValue());
        local.setLongitude((Double) localData.child("longitude").getValue());
        local.setNome((String) localData.child("nome").getValue());
        local.setComentario((String) localData.child("comentario").getValue());

        local.setDataInclusao((Long) localData.child("datainclusao").getValue());
        local.setNomeUsuario((String) localData.child("nomeUsuario").getValue());
        local.setImgUsuario((String) localData.child("imgUsuario").getValue());

        local.setIdTipoLocal((Long) localData.child("idTipoLocal").getValue());

        if(localData.child("LIKE").getValue() != null) {
            HashMap<String,String> mapLike = (HashMap) localData.child("LIKE").getValue();

            for(String  key : mapLike.keySet()) {
                local.setIdCurtir((String) mapLike.get(key));
            }
        }

        local.setId(localData.getKey());

        return local;
    }
}
