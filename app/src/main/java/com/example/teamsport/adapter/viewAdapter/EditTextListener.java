package com.example.teamsport.adapter.viewAdapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

import com.example.teamsport.R;

public class EditTextListener {

	public void setListener(EditText[] editTexts, Context context) {
		for (EditText editText: editTexts) {
			if (TextUtils.isEmpty(editText.getText().toString())) {
				editText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)));
			} else {
				editText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.purple_700)));
			}
		}
	}
}
