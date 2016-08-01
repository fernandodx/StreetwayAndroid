package br.com.dias.streetway.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import br.com.dias.streetway.R;

/**
 * Created by FernandoDias on 19/04/16.
 */
public class Util {

    public static String BitmapToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap StringBase64ToBitmap(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static  String formatarData(Date data) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        return format.format(data);
    }

    public static void esconderTecladoMobile(Context context, EditText... editTexts) {
        for(EditText editText : editTexts) {
            if(editText != null) {
                ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        }
    }


    public static void showAlertErro(Context context, int msg, int labelPositivo, int labelNegativo, View.OnClickListener negativeButton, View.OnClickListener positiveButton) {
        new LovelyStandardDialog(context)
                .setTopColorRes(R.color.color_alert)
                .setButtonsColorRes(R.color.colorPrimaryDark)
                .setIcon(R.mipmap.ic_erro)
                .setTitle(R.string.label_erro)
                .setMessage(msg)
                .setPositiveButton(labelPositivo, positiveButton)
                .setNegativeButton(labelNegativo, negativeButton).show();
    }

    public static void showAlertErro(Context context, int msg, int labelPositivo, View.OnClickListener positiveButton) {
        new LovelyStandardDialog(context)
                .setTopColorRes(R.color.color_alert)
                .setButtonsColorRes(R.color.colorPrimaryDark)
                .setIcon(R.mipmap.ic_erro)
                .setTitle(R.string.label_erro)
                .setMessage(msg)
                .setPositiveButton(labelPositivo, positiveButton)
                .show();
    }

    public static void showAlertErro(Context context, String msg, int labelPositivo, View.OnClickListener positiveButton) {
        new LovelyStandardDialog(context)
                .setTopColorRes(R.color.color_alert)
                .setButtonsColorRes(R.color.colorPrimaryDark)
                .setIcon(R.mipmap.ic_erro)
                .setTitle(R.string.label_erro)
                .setMessage(msg)
                .setPositiveButton(labelPositivo, positiveButton)
                .show();
    }

    public static void showAlertSucesso(Context context, int msg, int labelPositivo, View.OnClickListener positiveButton) {
        new LovelyStandardDialog(context)
                .setTopColorRes(R.color.color_info)
                .setButtonsColorRes(R.color.colorPrimaryDark)
                .setIcon(R.mipmap.ic_sucesso)
                .setTitle(R.string.label_sucesso)
                .setMessage(msg)
                .setPositiveButton(labelPositivo, positiveButton)
                .show();
    }

    public static String gerarUrlLocation(LatLng destino, String nome) {
        return String.format(Locale.getDefault(), "geo:0,0?q=") + android.net.Uri.encode(String.format("%s@%f,%f", nome, destino.latitude, destino.longitude), "UTF-8");
    }

}
