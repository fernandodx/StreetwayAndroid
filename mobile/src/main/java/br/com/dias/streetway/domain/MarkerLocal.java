package br.com.dias.streetway.domain;

import com.google.android.gms.maps.model.Marker;

import java.io.Serializable;

/**
 * Created by FernandoDias on 23/07/16.
 */
public class MarkerLocal implements Serializable {

    private Marker marker;
    private Local local;

    public MarkerLocal(Marker marker, Local local) {
        this.marker = marker;
        this.local = local;
    }

    public MarkerLocal() {
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }
}
