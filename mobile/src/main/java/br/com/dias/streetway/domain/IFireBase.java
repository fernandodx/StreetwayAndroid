package br.com.dias.streetway.domain;

import com.firebase.client.DataSnapshot;

/**
 * Created by FernandoDias on 20/04/16.
 */
public interface IFireBase {

    void onDataChange(DataSnapshot snapshot);
    void onDataAdd(DataSnapshot snapshot);
    void onDataRemove(DataSnapshot snapshot);
}
