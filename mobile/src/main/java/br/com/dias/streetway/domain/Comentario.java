package br.com.dias.streetway.domain;

import android.graphics.Bitmap;

import com.firebase.client.DataSnapshot;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import br.com.dias.streetway.util.Util;

/**
 * Created by FernandoDias on 16/04/16.
 */
public class Comentario implements Serializable {

    private String id;
    private String imgUsuario;
    private String comentario;
    private Date dataInclusao;
    private String idUsuario;
    private String nomeUsuario;

    public Comentario() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgUsuario() {
        return imgUsuario;
    }

    public void setImgUsuario(String imgUsuario) {
        this.imgUsuario = imgUsuario;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Date getDataInclusao() {
        return dataInclusao;
    }

    public void setDataInclusao(Date dataInclusao) {
        this.dataInclusao = dataInclusao;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public void carregarImagemUsuarioBitmap(Bitmap bitmap) {
        if(bitmap != null)
        this.imgUsuario = Util.BitmapToBase64(bitmap, Bitmap.CompressFormat.PNG, 100);
    }

    public void setDataInclusao(Long timer) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timer);
        this.dataInclusao = calendar.getTime();
    }

    public static Comentario createLocal(DataSnapshot comentarioData) {

        Comentario comentario = new Comentario();

        comentario.setIdUsuario((String) comentarioData.child("idUsuario").getValue());
        comentario.setImgUsuario((String) comentarioData.child("imgUsuario").getValue());
        comentario.setNomeUsuario((String) comentarioData.child("nomeUsuario").getValue());
        comentario.setComentario((String) comentarioData.child("comentario").getValue());
        comentario.setDataInclusao((Long) comentarioData.child("dataInclusao").getValue());

        comentario.setId(comentarioData.getKey());

        return comentario;
    }
}
