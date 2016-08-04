package com.workingagenda.fissure.PrefHelper;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

/**
 * Created by fen on 8/3/16.
 */
public class DelayPickerPreference extends DialogPreference {

    private int Delay = 0;
    private NumberPicker np= null;

    public DelayPickerPreference(Context context, AttributeSet attrs) {
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

        np.setMaxValue(3000);
        np.setValue(Delay);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {

            Delay = np.getValue();



            if (callChangeListener(String.valueOf(Delay))) {
                persistString(String.valueOf(Delay));
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String DelayValue = null;

        if (restoreValue) {
            if (defaultValue == null) {
                DelayValue = getPersistedString("500");
            } else {
                DelayValue = getPersistedString(defaultValue.toString());
            }
        } else {
            DelayValue = defaultValue.toString();
        }
        Delay = Integer.valueOf(DelayValue);
    }
}
