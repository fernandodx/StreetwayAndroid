package br.com.dias.streetway.domain;

import java.io.Serializable;

/**
 * Created by FernandoDias on 04/07/16.
 */
public class Avaliacao implements Serializable {

    private String id;
    private String uidUsuario;
    private Double avaliacao;

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

    public Double getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Double avaliacao) {
        this.avaliacao = avaliacao;
    }
}
