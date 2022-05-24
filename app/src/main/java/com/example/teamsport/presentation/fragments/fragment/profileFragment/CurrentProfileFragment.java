package com.example.teamsport.presentation.fragments.fragment.profileFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.teamsport.R;
import com.example.teamsport.data.entity.CurrentUser;
import com.squareup.picasso.Picasso;

public class CurrentProfileFragment extends Fragment implements View.OnClickListener {
	private final CurrentUser currentUser;


	public CurrentProfileFragment() {
		currentUser = CurrentUser.getInstance();
	}


	public static CurrentProfileFragment newInstance() {
		CurrentProfileFragment fragment = new CurrentProfileFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_current_profile, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		TextView profileName = view.findViewById(R.id.currentProfile_name_text);
		TextView profileAge = view.findViewById(R.id.currentProfile_age_text);
		ImageView profileImage = view.findViewById(R.id.currentProfile_image);
		TextView profileAddress = view.findViewById(R.id.currentProfile_addressTextView);
		Button editButton = view.findViewById(R.id.currentProfile_editData_button);

		editButton.setOnClickListener(this);

		profileAddress.setText(currentUser.getUserAddress());
		profileName.setText(currentUser.getUserName());
		Picasso.get().load(currentUser.getUriProfileImage()).into(profileImage);
		profileAge.setText(currentUser.getUserAge());
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		if (id == R.id.currentProfile_editData_button) {
			FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.teamSportFragment_container, EditProfileFragment.newInstance())
					.addToBackStack(null)
					.commit();
		}
	}
}