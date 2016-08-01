package br.com.dias.streetway.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.dias.streetway.R;
import br.com.dias.streetway.domain.TipoLocal;

/**
 * Created by FernandoDias on 17/07/16.
 */
public class AdapterTipoLocal extends ArrayAdapter<TipoLocal> {

    private Context context;

    public AdapterTipoLocal(Context context) {
        super(context, R.layout.adapter_tipo_local);
        this.context = context;
        addAll(TipoLocal.getListaTipoLocal());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_tipo_local, parent, false);
        TextView nomeTipoLocal=(TextView) view.findViewById(R.id.nome_tipo_local);
        ImageView imgTipoLocal = (ImageView) view.findViewById(R.id.logo_tipo_local);
        TipoLocal tipoLocal=getItem(position);
        nomeTipoLocal.setText(tipoLocal.getNomeTipo());
        imgTipoLocal.setImageResource(tipoLocal.getImgTipo());
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_tipo_local, parent, false);
        TextView nomeTipoLocal=(TextView) view.findViewById(R.id.nome_tipo_local);
        ImageView imgTipoLocal = (ImageView) view.findViewById(R.id.logo_tipo_local);
        TipoLocal tipoLocal=getItem(position);
        nomeTipoLocal.setText(tipoLocal.getNomeTipo());
        imgTipoLocal.setImageResource(tipoLocal.getImgTipo());
        return view;
    }

}
