package com.example.teamsport.presentation.fragments.fragment.profileFragment.createProfileFragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.teamsport.R;
import com.example.teamsport.data.db.DataBase;
import com.example.teamsport.data.db.interfaces.FirebaseSuccess;
import com.example.teamsport.data.entity.CurrentUser;
import com.example.teamsport.presentation.activities.TeamSportActivity;

import java.util.HashMap;
import java.util.Map;


public class SetUsersLocationFragment extends Fragment {
	private String address;
	private DataBase.PersonDB personDB;


	public SetUsersLocationFragment() {
	}

	public SetUsersLocationFragment(String address) {
		this.address = address;
	}

	public static SetUsersLocationFragment newInstance() {
		return new SetUsersLocationFragment();
	}

	public static SetUsersLocationFragment newInstanceFromMap(String address) {
		return new SetUsersLocationFragment(address);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		personDB = DataBase.PersonDB.getInstance();

		getLocationPermission();

		TextView location = view.findViewById(R.id.setUsersLocation_locationTextView);
		Button toMapButton = view.findViewById(R.id.setUsersLocation_Button);
		Button nextButton = view.findViewById(R.id.setUsersLocation_nextButton);

		if (address != null) {
			location.setText(address);
		}

		toMapButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.replace(R.id.createProfileFragment_container,
						MapLocationFragment.newInstance())
						.addToBackStack("SetUsersLocationFragment")
						.commit();
			}
		});

		nextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (location.getText() != getResources().getString(R.string.location)) {

					Map<String, Object> personObj = new HashMap<>();
					personObj.put("userAddress", location.getText().toString());

					personDB.createPersonField(CurrentUser.getInstance().getUserId(), personObj, new FirebaseSuccess.OnVoidSuccessListener() {
						@Override
						public void onVoidSuccess() {
							CurrentUser currentUser = CurrentUser.getInstance();
							currentUser.setUserAddress(location.getText().toString());
							startActivity(new Intent(getContext(), TeamSportActivity.class));
							requireActivity().finish();
						}
					});

				} else {
					location.setTextColor(Color.RED);
				}
			}
		});
	}

	private void getLocationPermission() {

		if (ContextCompat.checkSelfPermission(requireContext(),
				android.Manifest.permission.ACCESS_FINE_LOCATION)
				== PackageManager.PERMISSION_GRANTED) {
			boolean locationPermissionGranted = true;
		} else {
			int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
			ActivityCompat.requestPermissions(requireActivity(),
					new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
					PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_set_users_location, container, false);
	}
}