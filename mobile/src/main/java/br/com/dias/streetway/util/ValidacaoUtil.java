package br.com.dias.streetway.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import br.com.dias.streetway.R;


/**
 * Created by FernandoDias on 04/04/16.
 */
public class ValidacaoUtil {


    public static boolean validarCampoObrigatorio(EditText view) {

        if(view.getText() == null || view.getText().toString().isEmpty()){
            Animation shake = AnimationUtils.loadAnimation(view.getContext(), R.anim.shake);
            view.startAnimation(shake);
            view.setError(view.getContext().getString(R.string.msg_campo_obrigatorio));
            return false;
        }

        return true;

    }


    public static void animarCampoMsg(EditText editText, int resourceMsg) {
        Animation shake = AnimationUtils.loadAnimation(editText.getContext(), R.anim.shake);
        editText.startAnimation(shake);
        editText.setError(editText.getContext().getString(resourceMsg));
    }

    public static void animarCampoMsg(View... views) {

        for(View view : views) {
            Animation shake = AnimationUtils.loadAnimation(view.getContext(), R.anim.shake);
            view.startAnimation(shake);
        }

    }

    public static void animarView(View view) {
        Animation shake = AnimationUtils.loadAnimation(view.getContext(), R.anim.shake);
        view.startAnimation(shake);
    }

    public static boolean validarCampoObrigatorio(EditText... editTexts) {

        boolean retorno = true;

        for(EditText view : editTexts) {
            if(view.getText() == null || view.getText().toString().isEmpty()){
                Animation shake = AnimationUtils.loadAnimation(view.getContext(), R.anim.shake);
                view.startAnimation(shake);
                view.setError(view.getContext().getString(R.string.msg_campo_obrigatorio));
                retorno = false;
            }
        }

        return retorno;
    }



}
