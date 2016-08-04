package com.workingagenda.fissure.PrefHelper;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

public class NumberPickerPreference extends DialogPreference {

    private int Quality = 0;
    private NumberPicker np= null;

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
    }

    @Override
    protected View onCreateDialogView() {
        np = new NumberPicker(getContext());

        return (np);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);

        np.setMaxValue(100);
        np.setValue(Quality);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {

            Quality = np.getValue();



            if (callChangeListener(String.valueOf(Quality))) {
                persistString(String.valueOf(Quality));
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String QualityValue = null;

        if (restoreValue) {
            if (defaultValue == null) {
                QualityValue = getPersistedString("20");
            } else {
                QualityValue = getPersistedString(defaultValue.toString());
            }
        } else {
            QualityValue = defaultValue.toString();
        }
        Quality = Integer.valueOf(QualityValue);
    }

}