package com.example.teamsport.presentation.fragments.fragment.gatheringFragment;

import android.app.AlertDialog;

import android.graphics.Color;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.teamsport.R;
import com.example.teamsport.data.db.DataBase;
import com.example.teamsport.data.entity.Gathering;
import com.example.teamsport.data.entity.CurrentUser;
import com.example.teamsport.presentation.fragments.fragment.PagerFragment;
import com.example.teamsport.presentation.fragments.fragment.profileFragment.UsersListFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class GatheringDetailsFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {
	private MapView mapView;
	private final Gathering gathering;
	private CurrentUser currentUser;

	private TextView descriptionStaticTextView;
	private TextView sportTextView;
	private TextView addressTextView;
	private TextView timeTextView;
	private TextView coastTextView;
	private TextView coastStaticTextView;
	private ImageView sportImageView;
	private ImageView coastImageView;
	private TextView dateTextView;
	private Button subscribeButton;
	private TextView usersTextView;
	private TextView descriptionTextView;

	private final DataBase.GatheringDB gatheringDataBase;
	private final DataBase.PersonDB personDataBase;

	public GatheringDetailsFragment(Gathering gathering) {
		this.gathering = gathering;
		gatheringDataBase =  DataBase.GatheringDB.getInstance();
		personDataBase = DataBase.PersonDB.getInstance();
	}

	public static GatheringDetailsFragment newInstance(Gathering gathering) {
		return new GatheringDetailsFragment(gathering);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_gathering_details, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		sportTextView = view.findViewById(R.id.gatheringDetails_sport);
		addressTextView = view.findViewById(R.id.gatheringDetails_address);
		timeTextView = view.findViewById(R.id.gatheringDetails_time);
		coastTextView = view.findViewById(R.id.gatheringDetails_coast);
		coastStaticTextView = view.findViewById(R.id.gatheringDetails_coast_textView);
		dateTextView = view.findViewById(R.id.gatheringDetails_date);
		sportImageView = view.findViewById(R.id.gatheringDetails_sportImageView);
		coastImageView = view.findViewById(R.id.gatheringDetails_coastImageView);
		usersTextView = view.findViewById(R.id.gatheringDetails_usersTextView);
		TextView showMoreTextView = view.findViewById(R.id.gatheringDetails_show_more_TV);
		ImageButton usersImageButton = view.findViewById(R.id.imageButton2);
		descriptionStaticTextView = view.findViewById(R.id.gatheringDetails_descriptionStatic);
		descriptionTextView = view.findViewById(R.id.gatheringDetails_description_textView);
		subscribeButton = view.findViewById(R.id.gatheringDetails_subscribe_button);
		mapView = view.findViewById(R.id.gatheringDetails_miniMap);

		currentUser = CurrentUser.getInstance();

		usersImageButton.setOnClickListener(this);
		showMoreTextView.setOnClickListener(this);

		mapView.onCreate(savedInstanceState);
		mapView.getMapAsync(this);
		setGatheringViews();

	}

	private void setSubscribeButton(Gathering gathering) {

		if (currentUser.getUserId().equals(gathering.getCreatedBy())) {
			isOwner();
			return;
		}

		if (currentUser.getGatherings() == null) {
			isUnsubscribed();
			return;
		}
		if (currentUser.getGatherings().size() == 0) {
			isUnsubscribed();
			return;
		}

		for (String gatherings : currentUser.getGatherings()) {
			if (gatherings.equals(gathering.getId())) {
				isSubscribed();
				return;
			}
		}
		isUnsubscribed();
	}


	private void isOwner() {
		setSubscribeButtonOptions(getResources().getColor(android.R.color.transparent),
				R.string.delete,
				ContextCompat.getColor(requireContext(), R.color.red));

		subscribeButton.setOnClickListener(v -> setAlertDialog());
	}

	private void setAlertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(R.string.delete_gathering)
				.setPositiveButton(R.string.yes, (dialog, id) -> gatheringDataBase.deleteGatheringField(gathering.getId(), () ->
						personDataBase.deleteArrayGatheringPersonField(
								currentUser.getUserId(),
								gathering.getId(),
								() -> {
									dialog.cancel();
									currentUser.deleteGathering(gathering.getId());
									GatheringDetailsFragment.this.replaceFragment(PagerFragment.newInstance());
								}))).setNegativeButton(R.string.no, (dialog, which) ->
				dialog.cancel());
		builder.show();
	}

	private void isSubscribed() {
		setSubscribeButtonOptions(getResources().getColor(android.R.color.transparent),
				R.string.unsubscribe,
				ContextCompat.getColor(requireContext(), R.color.red));


		subscribeButton.setOnClickListener(v -> {
			gatheringDataBase.deleteArrayItemGatheringField(gathering.getId(),
					"users",
					currentUser.getUserId(),
					null);

			personDataBase.deleteArrayGatheringPersonField(
					currentUser.getUserId(),
					gathering.getId(),
					null);

			usersTextView.setText(String.valueOf(Integer.parseInt(usersTextView.getText().toString()) - 1));
			currentUser.deleteGathering(gathering.getId());
			isUnsubscribed();
		});
	}

	private void isUnsubscribed() {
		setSubscribeButtonOptions(ContextCompat.getColor(requireContext(), R.color.blue),
				R.string.subscribe,
				Color.WHITE);

		subscribeButton.setOnClickListener(v -> {
			gatheringDataBase.addArrayItemGatheringField(gathering.getId(),
					"users",
					currentUser.getUserId(),
					null);

			personDataBase.addArrayItemPersonField(
					currentUser.getUserId(),
					"gatherings",
					gathering.getId(),
					null);
			usersTextView.setText(String.valueOf(Integer.parseInt(usersTextView.getText().toString()) + 1));
			currentUser.addGathering(gathering.getId());
			isSubscribed();
		});
	}

	private void setSubscribeButtonOptions(int backgroundColor, int text, int textColor) {
		subscribeButton.setBackgroundColor(backgroundColor);
		subscribeButton.setText(text);
		subscribeButton.setTextColor(textColor);
	}

	private void setGatheringViews() {

		switch (gathering.getSport()) {
			case "Football":
				sportImageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.football_ball));
				break;
			case "Basketball":
				sportImageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.basketball_ball));
				break;
			case "Tennis":
				sportImageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.tennis_ball));
				break;
			case "Volleyball":
				sportImageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.volleyball_ball));
				break;

		}

		if (!TextUtils.isEmpty(gathering.getCoast().trim())) {
			coastStaticTextView.setVisibility(View.VISIBLE);
			coastImageView.setVisibility(View.VISIBLE);
		}
		if (!TextUtils.isEmpty(gathering.getDescription())) {
			descriptionStaticTextView.setVisibility(View.VISIBLE);
		}

		sportTextView.setText(gathering.getSport());
		addressTextView.setText(gathering.getAddress());
		timeTextView.setText(gathering.getTime());
		coastTextView.setText(gathering.getCoast());
		usersTextView.setText(String.valueOf(gathering.getAmountUsers()));
		dateTextView.setText(gathering.getDate());
		descriptionTextView.setText(gathering.getDescription());

		setSubscribeButton(gathering);
	}

	@Override
	public void onClick(View v) {

		replaceFragment(UsersListFragment.newInstance(gathering));
	}

	private void replaceFragment(Fragment fragment) {
		FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.teamSportFragment_container, fragment)
				.addToBackStack("GatheringDetailsFragment")
				.commit();

	}


	@Override
	public void onMapReady(@NonNull GoogleMap googleMap) {
		googleMap.getUiSettings().setMyLocationButtonEnabled(false);

		LatLng location = new LatLng(Double.parseDouble(gathering.getLatitude()),
				Double.parseDouble(gathering.getLongitude()));
		googleMap.addMarker(new MarkerOptions().position(location).title(gathering.getSport()));
		googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 16F));
	}


	@Override
	public void onResume() {
		mapView.onResume();
		super.onResume();
	}


	@Override
	public void onPause() {
		super.onPause();
		mapView.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mapView.onLowMemory();
	}


}