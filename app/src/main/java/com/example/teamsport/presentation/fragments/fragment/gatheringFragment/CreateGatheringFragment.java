package com.example.teamsport.presentation.fragments.fragment.gatheringFragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.location.Address;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teamsport.R;
import com.example.teamsport.data.db.DataBase;
import com.example.teamsport.data.entity.Gathering;
import com.example.teamsport.presentation.fragments.dialogFragment.TimePickerDialogFragment;
import com.example.teamsport.data.entity.CurrentUser;
import com.example.teamsport.adapter.parcelable.MarkersLocationParcelable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateGatheringFragment extends Fragment implements
		View.OnClickListener {
	private EditText coastEditText;
	private EditText descriptionEditText;
	private TextView dateTextView;
	private TextView timeTextView;
	private TextView addressTextView;
	private CheckBox checkBox;
	private AutoCompleteTextView sportSelector;
	private ImageView coastImageView;

	private String gatheringId;
	private Gathering gathering;
	private final String[] sportListItems;


	private final DataBase.GatheringDB gatheringDB;
	private final DataBase.PersonDB personDB;
	private final CurrentUser currentUser;

	private String sportSelected;
	private List<Address> addresses;
	private double latitude;
	private double longitude;

	public CreateGatheringFragment() {
		sportListItems = new String[]{"Football", "Basketball", "Volleyball", "Tennis"};
		gatheringDB =  DataBase.GatheringDB.getInstance();
		personDB= DataBase.PersonDB.getInstance();
		currentUser = CurrentUser.getInstance();
	}

	public static CreateGatheringFragment newInstance(List<Address> addresses,
													  double latitude, double longitude) {
		CreateGatheringFragment fragment = new CreateGatheringFragment();
		Bundle args = new Bundle();
		args.putParcelable("Address", new MarkersLocationParcelable(addresses, latitude, longitude));
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			savedInstanceState = getArguments();
			MarkersLocationParcelable markersLocationParcelable = savedInstanceState.getParcelable("Address");
			addresses = markersLocationParcelable.getAddresses();
			latitude = markersLocationParcelable.getLatitude();
			longitude = markersLocationParcelable.getLongitude();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_create_gathering, container, false);
	}

	@SuppressLint("SetTextI18n")
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);



		addressTextView = view.findViewById(R.id.create_gathering_address_textView);
		sportSelector = view.findViewById(R.id.auto_complete_text);
		checkBox = view.findViewById(R.id.checkBox);
		coastEditText = view.findViewById(R.id.cash_editText);
		descriptionEditText = view.findViewById(R.id.editTextDescription);
		dateTextView = view.findViewById(R.id.gathering_date_textView);
		dateTextView.setOnClickListener(this);
		timeTextView = view.findViewById(R.id.time_textView);
		timeTextView.setOnClickListener(this);
		coastImageView = view.findViewById(R.id.createGathering_coastImageView);
		Button saveGatheringButton = view.findViewById(R.id.create_gathering_button);
		saveGatheringButton.setOnClickListener(this);


		setSportSelector();

		setCheckBox();

		addressTextView.setText(addresses.get(0).getCountryName()
				+ addresses.get(0).getLocality() + addresses.get(0).getAddressLine(0));
	}

	private void setSportSelector() {
		ArrayAdapter<String> adapterItems = new ArrayAdapter<>(getActivity(), R.layout.sport_list_item, sportListItems);
		sportSelector.setAdapter(adapterItems);

		sportSelector.setOnItemClickListener((parent, view1, position, id) -> {
			sportSelected = (String) parent.getItemAtPosition(position);
			Log.d("SportSelected", "onItemClick: " + sportSelected);
		});
	}

	private void setCheckBox() {
		checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
			if (isChecked) {
				coastImageView.setVisibility(View.VISIBLE);
				coastEditText.setVisibility(View.VISIBLE);
			}
			else {
				coastImageView.setVisibility(View.INVISIBLE);
				coastEditText.setVisibility(View.INVISIBLE);
			}
			});

	}

	@SuppressLint("NonConstantResourceId")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

			case R.id.gathering_date_textView:
				setDatePickerDialog();
				break;

			case R.id.time_textView:
				setTimePicker();
				break;

			case R.id.create_gathering_button:
				createGathering();
				break;
		}
	}

	private void createGathering() {
		if (sportSelected != null
				& dateTextView.getText() != getResources().getString(R.string.set_date)
				& timeTextView.getText() != getResources().getString(R.string.set_time)
				& addressTextView.getText() != null) {

			if (checkBox.isChecked() && TextUtils.isEmpty(coastEditText.getText())) {
				Toast.makeText(getContext(), "Empty fields", Toast.LENGTH_SHORT).show();
				return;
			}

			gatheringId = currentUser.getUserId() + dateTextView.getText().toString()
					+ timeTextView.getText().toString() + sportSelector.getText().toString();

			Map<String, Object> gatheringObj = new HashMap<>();
			setGatheringObj(gatheringId, gatheringObj);

			createGatheringDocument(gatheringId, gatheringObj);

		} else {
			Toast.makeText(getContext(), "Empty fields", Toast.LENGTH_SHORT).show();
		}
	}

	private void setGatheringObj(String gatheringId, Map<String, Object> gatheringObj) {
		gatheringObj.put("id", gatheringId);
		gatheringObj.put("sport", sportSelected);
		gatheringObj.put("date", dateTextView.getText().toString());
		gatheringObj.put("address", addressTextView.getText().toString());
		gatheringObj.put("time", timeTextView.getText().toString());
		gatheringObj.put("latitude", String.valueOf(latitude));
		gatheringObj.put("longitude", String.valueOf(longitude));
		gatheringObj.put("createdBy", currentUser.getUserId());
		gatheringObj.put("city", addresses.get(0).getLocality().trim());
		gatheringObj.put("usersId", new ArrayList<>(Collections.singleton(currentUser.getUserId())));
		gatheringObj.put("amountUsers", 1);
		gatheringObj.put("description", descriptionEditText.getText().toString());
		gatheringObj.put("coast", coastEditText.getText().toString());
	}

	private void createGatheringDocument(String gatheringId, Map<String, Object> gatheringObj) {
		gatheringDB.createGatheringField(gatheringId, gatheringObj, () -> {
			gathering = new Gathering();
			gathering.setAddress(String.valueOf(gatheringObj.get("address")));
			gathering.setAmountUsers((Integer) gatheringObj.get("amountUsers"));
			gathering.setCoast(String.valueOf(gatheringObj.get("coast")));
			gathering.setCreatedBy(String.valueOf(gatheringObj.get("createdBy")));
			gathering.setSport(String.valueOf(gatheringObj.get("sport")));
			gathering.setSport(String.valueOf(gatheringObj.get("city")));
			gathering.setDate(String.valueOf(gatheringObj.get("date")));
			gathering.setTime(String.valueOf(gatheringObj.get("time")));
			gathering.setLatitude(String.valueOf(gatheringObj.get("latitude")));
			gathering.setLongitude(String.valueOf(gatheringObj.get("longitude")));
			gathering.setDescription(String.valueOf(gatheringObj.get("description")));
			gathering.setCreatedBy(String.valueOf(gatheringObj.get("createdBy")));
			gathering.addUsers(currentUser.getUserId());
			addPersonGathering(currentUser.getUserId());
		});
	}

	private void addPersonGathering(String personId) {
		personDB.addArrayItemPersonField(personId, "gatherings", gatheringId, () -> {

			CurrentUser.getInstance().addGathering(gatheringId);

			FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.teamSportFragment_container,
					GatheringDetailsFragment.newInstance(gathering))
					.commit();
		});
	}

	public void setTimePicker() {
		DialogFragment timePicker = new TimePickerDialogFragment();
		timePicker.show(requireActivity().getSupportFragmentManager(), "time Picker");
	}

	public void setDatePickerDialog() {
		Calendar calendar = Calendar.getInstance();

		final int year = calendar.get(Calendar.YEAR);
		final int month = calendar.get(Calendar.MONTH);
		final int day = calendar.get(Calendar.DAY_OF_MONTH);

		DatePickerDialog datePickerDialog = new DatePickerDialog(
				getActivity(), R.style.DatePickerStyle, (view, year1, month1, dayOfMonth) -> {

					Calendar calendar1 = Calendar.getInstance();
					calendar1.set(year1, month1, dayOfMonth);
					@SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
					String dateTime = dateFormat.format(calendar1.getTime());

					dateTextView.setText(dateTime);
					dateTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green));
				}, year, month, day);
		datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
		datePickerDialog.show();
	}
}
