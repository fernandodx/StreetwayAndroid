package br.com.dias.streetway.domain;

import com.firebase.client.DataSnapshot;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by FernandoDias on 14/07/16.
 */
public class Foto implements Serializable {

    private String id;
    private String uidLocal;
    private String uidUsuario;
    private String imgBase64;
    private Date dataInclusao;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUidUsuario() {
        return uidUsuario;
    }

    public void setUidUsuario(String uidUsuario) {
        this.uidUsuario = uidUsuario;
    }

    public String getUidLocal() {
        return uidLocal;
    }

    public void setUidLocal(String uidLocal) {
        this.uidLocal = uidLocal;
    }

    public String getImgBase64() {
        return imgBase64;
    }

    public void setImgBase64(String imgBase64) {
        this.imgBase64 = imgBase64;
    }

    public Date getDataInclusao() {
        return dataInclusao;
    }

    public void setDataInclusao(Date datainclusao) {
        this.dataInclusao = datainclusao;
    }

    public void setDataInclusao(Long timer) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timer);
        this.dataInclusao = calendar.getTime();
    }



    public static Foto createFoto(DataSnapshot fotoData) {

        Foto foto = new Foto();
        foto.setImgBase64((String) fotoData.child("imgBase64").getValue());
        foto.setDataInclusao((Long) fotoData.child("dataInclusao").getValue());
        foto.setUidUsuario((String) fotoData.child("uidUsuario").getValue());
        foto.setId(fotoData.getKey());

        return foto;
    }
}
