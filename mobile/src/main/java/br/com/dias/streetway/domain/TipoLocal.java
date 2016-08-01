package br.com.dias.streetway.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by FernandoDias on 17/07/16.
 */
public class TipoLocal implements Serializable {

    private Integer id;
    private int imgTipo;
    private String nomeTipo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getImgTipo() {
        return imgTipo;
    }

    public void setImgTipo(int imgTipo) {
        this.imgTipo = imgTipo;
    }

    public String getNomeTipo() {
        return nomeTipo;
    }

    public void setNomeTipo(String nomeTipo) {
        this.nomeTipo = nomeTipo;
    }

    public static List<TipoLocal> getListaTipoLocal() {

        List<TipoLocal> lista = new ArrayList<>();

        for(TipoLocalEnum tipo : TipoLocalEnum.values()) {

            TipoLocal tl = new TipoLocal();
            tl.setId(tipo.id);
            tl.setImgTipo(tipo.logo);
            tl.setNomeTipo(tipo.nome);

            lista.add(tl);
        }

        return lista;

    }
}
