package br.com.dias.streetway.domain;

import com.google.maps.android.ui.IconGenerator;

import br.com.dias.streetway.R;

/**
 * Created by FernandoDias on 18/07/16.
 */
public enum TipoLocalEnum {

    SKATEPARK(R.mipmap.ic_skate_park, "Skate Park", 1, IconGenerator.STYLE_GREEN),
    RAMPA_45(R.mipmap.ic_45, "45˚", 2, IconGenerator.STYLE_PURPLE),
    CAIXOTE(R.mipmap.ic_caixote, "Caixote", 3, IconGenerator.STYLE_BLUE),
    CORRIMAO(R.mipmap.ic_corrimao, "Corrimão", 4, IconGenerator.STYLE_BLUE),
    QUARTER(R.mipmap.ic_quarter, "Quarter", 5, IconGenerator.STYLE_PURPLE),
    BOWL(R.mipmap.ic_bowl, "Bowl", 6, IconGenerator.STYLE_RED),
    JUMP(R.mipmap.ic_jump, "Jump", 7, IconGenerator.STYLE_ORANGE);

    TipoLocalEnum(int logo, String nome, Integer id, int styleIcon) {
        this.logo = logo;
        this.nome = nome;
        this.id = id;
        this.style = styleIcon;
    }

    public Integer id;
    public int logo;
    public String nome;
    public int style;

    public static TipoLocalEnum getEnum(Integer idTipo) {
        if(idTipo==null){
            return null;
        }
        for(TipoLocalEnum localEnum : values()){
            if(idTipo.equals(localEnum.id)){
                return localEnum;
            }
        }

        return null;
    }

}
