package com.catata.listacomprafiles.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyFormater implements TextWatcher {
    private String mPreviousValue; //Variable que guarda el valor anterior
    private int mCursorPosition; //Variable que guarda la posición del cursor.
    private boolean mRestoringPreviousValueFlag; //Variable lógica que guarda si la cadena anterior cumplia con los requsitos para no entrar en bucle.
    private int mDigitsAfterZero; //Número de digitos que queremos admitir como máximo, se pasa en el constructor.
    private EditText mEditText; //EditText sobre el que queremos hacer las comprobaciones, se pasa en el constructor

    /*
    * Constructor, se asigna el EditText a trabajar y el número de dígitos*/
    public MyFormater(EditText editText, int digitsAfterZero) {
        mDigitsAfterZero = digitsAfterZero;
        mEditText = editText;
        mPreviousValue = "";
        mRestoringPreviousValueFlag = false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //Si la anterior comprobación no tenía 3 digitos entramos y reasignamos variables de nuevo
        if (!mRestoringPreviousValueFlag) {
            mPreviousValue = s.toString();
            mCursorPosition = mEditText.getSelectionStart();
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        //Si la anterior comprobación no tenía 3 digitos entramos a ver si esta los tiene
        if (!mRestoringPreviousValueFlag) {

            if (!isValid(s.toString())) { //no cumple con que como máximo sean 2 dígitos.
                mRestoringPreviousValueFlag = true; //Si no cumple restablecemos el valor, y activamos el flag para evitar bucles.
                restorePreviousValue();
            }

        } else { //La anterior comprobación tenía 3 digitos, como hemos cambiado el valor, volvería a entrar, para evitarlo ponemos el flag a false
            mRestoringPreviousValueFlag = false;
        }
    }

    /*
    * Devuelve al valor anterior, se ejecutará cuando tenga 3 decimales.
    */
    private void restorePreviousValue() {
        mEditText.setText(mPreviousValue);
        mEditText.setSelection(mCursorPosition);
    }

    /**
     * Comprueba mediante una expresión regular si la cadena es un número entero, y tiene como máximo dos decimales
     */
    private boolean isValid(String s) {
        Pattern patternWithDot = Pattern.compile("[0-9]*((\\.[0-9]{0," + mDigitsAfterZero + "})?)||(\\.)?");
        Pattern patternWithComma = Pattern.compile("[0-9]*((,[0-9]{0," + mDigitsAfterZero + "})?)||(,)?");

        Matcher matcherDot = patternWithDot.matcher(s);
        Matcher matcherComa = patternWithComma.matcher(s);

        return matcherDot.matches() || matcherComa.matches();
    }


    /*
    * Funcion estática para formatear un double a dos dígitos y convertirlo en cadena
    */
    public static String DoubleToString2Digits(Double d){
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.UP);
        return df.format(d);
    }


}
