package com.example.teamsport.presentation.fragments.dialogFragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.teamsport.R;

import java.util.Calendar;

public class TimePickerDialogFragment extends DialogFragment {

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		Calendar calendar = Calendar.getInstance();
		TextView timeTextView = getActivity().findViewById(R.id.time_textView);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);

		return new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute1) {
				view.setIs24HourView(true);
				timeTextView.setText(hourOfDay + ":" + minute1);
				timeTextView.setTextColor(ContextCompat.getColor(TimePickerDialogFragment.this.getContext(), R.color.green));
			}
		},
				hour, minute, DateFormat.is24HourFormat(getActivity()));
	}
}
