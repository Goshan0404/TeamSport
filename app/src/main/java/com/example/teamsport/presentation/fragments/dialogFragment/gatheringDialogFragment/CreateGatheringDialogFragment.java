package com.example.teamsport.presentation.fragments.dialogFragment.gatheringDialogFragment;

import android.annotation.SuppressLint;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.teamsport.presentation.fragments.fragment.gatheringFragment.CreateGatheringFragment;
import com.example.teamsport.R;
import com.example.teamsport.adapter.parcelable.MarkersLocationParcelable;

import java.util.List;

public class CreateGatheringDialogFragment extends DialogFragment implements View.OnClickListener {

	private List<Address> addresses;
	private double latitude;
	private double longitude;

	public static CreateGatheringDialogFragment newInstance(List<Address> addresses, double latitude,
															double longitude) {
		CreateGatheringDialogFragment fragment = new CreateGatheringDialogFragment();
		Bundle args = new Bundle();
		args.putParcelable("Address", new MarkersLocationParcelable(addresses, latitude, longitude));
		fragment.setArguments(args);

		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		return inflater.inflate(R.layout.dialog_fragment_create_gathering, container, false);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			savedInstanceState = getArguments();
			MarkersLocationParcelable markersLocationParcelable = savedInstanceState.getParcelable("Address");
			addresses = markersLocationParcelable.getAddresses();
//			Log.d("address", "onMarkClick: " + addresses.get(0).getAddressLine(0));
			latitude = markersLocationParcelable.getLatitude();
			longitude = markersLocationParcelable.getLongitude();

		}
	}

	@SuppressLint("SetTextI18n")
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		Button toCreateGatheringButton = view.findViewById(R.id.to_create_gathering_button);
		ImageButton closeImageButton = view.findViewById(R.id.dialogGatheringDetails_closeImageButton);
		TextView gatheringAddressTextView = view.findViewById(R.id.gathering_address_createDialogFragment_textView);

		gatheringAddressTextView.setText( addresses.get(0).getAddressLine(0) +
				addresses.get(0).getLocality());

		toCreateGatheringButton.setOnClickListener(this);
		closeImageButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.to_create_gathering_button) {
			if (v.getId() == R.id.to_create_gathering_button) {
				dismiss();
				FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				Handler handler = new Handler();
				handler.post(() -> fragmentTransaction.replace(R.id.teamSportFragment_container,
						CreateGatheringFragment.newInstance(addresses, latitude, longitude))
						.addToBackStack("CreateGatheringDialogFragment")
						.commit());
			}
		} else if (v.getId() == R.id.dialogGatheringDetails_closeImageButton) {
			dismiss();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_fragment_background);
	}
}
