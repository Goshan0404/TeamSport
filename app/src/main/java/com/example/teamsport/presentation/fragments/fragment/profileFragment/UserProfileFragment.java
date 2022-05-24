package com.example.teamsport.presentation.fragments.fragment.profileFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.teamsport.R;
import com.example.teamsport.data.entity.User;
import com.squareup.picasso.Picasso;

public class UserProfileFragment extends Fragment {
	private final User user;


	public UserProfileFragment(User user) {
		this.user = user;
	}

	public static UserProfileFragment newInstance(User user) {
		UserProfileFragment fragment = new UserProfileFragment(user);
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_user_profile_, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		TextView userName = view.findViewById(R.id.userProfile_name_text);
		TextView userAge = view.findViewById(R.id.userProfile_age_text);
		TextView userAddress = view.findViewById(R.id.userProfile_addressTextView);
		ImageView userImage = view.findViewById(R.id.userProfile_image);

		userAddress.setText(user.getUserAddress());
		userName.setText(user.getUserName());
		userAge.setText(user.getUserAge());
		Picasso.get().load(user.getUriProfileImage()).into(userImage);


	}


}