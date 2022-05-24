package com.example.teamsport.presentation.fragments.dialogFragment.gatheringDialogFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.teamsport.R;
import com.example.teamsport.presentation.fragments.fragment.gatheringFragment.GatheringDetailsFragment;
import com.example.teamsport.data.db.DataBase;
import com.example.teamsport.data.db.interfaces.FirebaseSuccess;
import com.example.teamsport.data.entity.Gathering;

import java.util.Objects;

public class GatheringDetailsDialogFragment extends DialogFragment
		implements View.OnClickListener{

	private Gathering currentGathering;

	private TextView addressTextView;
	private TextView countUsersTextView;
	private TextView dateTextView;

	private String gatheringId;
	private final DataBase.GatheringDB dataBase;

	public GatheringDetailsDialogFragment() {
		dataBase =  DataBase.GatheringDB.getInstance();
	}

	public static GatheringDetailsDialogFragment newInstance(String gatheringId) {
		GatheringDetailsDialogFragment fragment = new GatheringDetailsDialogFragment();
		Bundle args = new Bundle();
		args.putString("gatheringId", gatheringId);
		fragment.setArguments(args);

		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.dialog_fragment_gatherings_details, container, false);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			savedInstanceState = getArguments();
			gatheringId = savedInstanceState.getString("gatheringId");
		}
	}

	@SuppressLint("SetTextI18n")
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		Button showDetailsGatheringButton = view.findViewById(R.id.dialogGatheringDetails_showDetailsButton);
		ImageButton closeImageButton = view.findViewById(R.id.dialogGatheringDetails_closeImageButton);
		countUsersTextView = view.findViewById(R.id.dialogGatheringDetails_usersTextView);
		addressTextView = view.findViewById(R.id.dialogGatheringDetails_addressTextView);
		dateTextView = view.findViewById(R.id.dialogGatheringDetails_dateTextView);

		showDetailsGatheringButton.setOnClickListener(this);
		closeImageButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.dialogGatheringDetails_showDetailsButton) {


			final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
			final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			Handler handler = new Handler();
			handler.post(() ->
					fragmentTransaction.replace(R.id.teamSportFragment_container,
					GatheringDetailsFragment.newInstance(currentGathering))
					.addToBackStack("GatheringDetailsDialogFragment")
					.commit());
			dismiss();

		} else if (v.getId() == R.id.dialogGatheringDetails_closeImageButton) {
			dismiss();
		}
	}

	@Override
	public void onStart() {
		dataBase.getGatheringById(gatheringId, new FirebaseSuccess.OnGatheringSuccessListener() {
			@Override
			public void onGatheringSuccess(Gathering gathering) {
				currentGathering = gathering;
				addressTextView.setText(gathering.getAddress());
				countUsersTextView.setText(String.valueOf(gathering.getAmountUsers()));
				dateTextView.setText(gathering.getDate());
			}
		});
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();

		Window window = Objects.requireNonNull(getDialog()).getWindow();

		setWindowAttributes(window);
	}

	private void setWindowAttributes(Window window) {
		window.setBackgroundDrawableResource(R.drawable.dialog_fragment_background);
	}

}
