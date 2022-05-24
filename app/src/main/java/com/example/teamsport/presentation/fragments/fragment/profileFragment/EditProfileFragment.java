package com.example.teamsport.presentation.fragments.fragment.profileFragment;

import android.content.Intent;
import android.net.Uri;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.teamsport.R;
import com.example.teamsport.adapter.viewAdapter.EditTextListener;
import com.example.teamsport.data.db.UpdateProfileData;
import com.example.teamsport.data.entity.CurrentUser;
import com.example.teamsport.presentation.fragments.fragment.profileFragment.createProfileFragment.MapLocationFragment;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;

public class EditProfileFragment extends Fragment implements View.OnClickListener {
	private ImageView profileImage;
	private EditText profileName;
	private EditText profileAge;
	private String address;
	private Button toMapButton;
	private TextView addressTextView;
	private ProgressBar progressBar;
	private CurrentUser currentUser;
	private Uri imageUri;

	public EditProfileFragment() {
	}

	public EditProfileFragment(String address) {
		this.address = address;
		currentUser = CurrentUser.getInstance();
	}

	public static EditProfileFragment newInstance() {
		return new EditProfileFragment();
	}

	public static EditProfileFragment newInstanceFromMap(String address) {
		Bundle bundle = new Bundle();
		return new EditProfileFragment(address);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		return inflater.inflate(R.layout.fragment_edit_pfofile, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		currentUser = CurrentUser.getInstance();

		profileImage = view.findViewById(R.id.edit_profile_image);
		profileName = view.findViewById(R.id.edit_profile_name);
		profileAge = view.findViewById(R.id.edit_profile_age);
		toMapButton = view.findViewById(R.id.editProfile_toMapButton);
		progressBar = view.findViewById(R.id.editProfile_progressBar);
		addressTextView = view.findViewById(R.id.editProfile_addressTextView);
		Button saveDataButton = view.findViewById(R.id.editProfile_saveNewProfileData_button);


		if (address != null) {
			addressTextView.setText(address);
		} else {
			addressTextView.setText(currentUser.getUserAddress());
		}

		imageUri = currentUser.getUriProfileImage();

		profileName.setText(currentUser.getUserName());
		profileAge.setText(currentUser.getUserAge());
		Picasso.get().load(imageUri).into(profileImage);

		profileImage.setOnClickListener(this);
		saveDataButton.setOnClickListener(this);
		toMapButton.setOnClickListener(this);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 & resultCode == RESULT_OK & data != null) {
			imageUri = data.getData();
			Picasso.get().load(imageUri).into(profileImage);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		switch (id) {
			case (R.id.edit_profile_image):
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intent, 1);
				break;
			case (R.id.editProfile_saveNewProfileData_button):
				saveNewData();
				break;
			case (R.id.editProfile_toMapButton):
				FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.replace(R.id.teamSportFragment_container,
						MapLocationFragment.newInstance())
						.addToBackStack("EditProfileFragment")
						.commit();
		}

	}

	private void saveNewData() {
		if (profileName.getText() != null
				&& profileAge != null) {

			toMapButton.setVisibility(View.GONE);
			progressBar.setVisibility(View.VISIBLE);

			currentUser.setUserName(profileName.getText().toString());
			currentUser.setUriProfileImage(imageUri);
			currentUser.setUserAge(profileAge.getText().toString());
			currentUser.setUserAddress(addressTextView.getText().toString());


			UpdateProfileData updateProfileData = new UpdateProfileData();
			updateProfileData.updateData(currentUser, getContext());

			FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.teamSportFragment_container, CurrentProfileFragment.newInstance())
					.commit();
		} else {
			EditText[] editTexts = new EditText[]{profileName, profileAge};
			EditTextListener editTextListener = new EditTextListener();
			editTextListener.setListener(editTexts, getContext());
		}
	}
}