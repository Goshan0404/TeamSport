package com.example.teamsport.presentation.fragments.fragment.profileFragment.createProfileFragment;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.teamsport.R;
import com.example.teamsport.adapter.viewAdapter.EditTextListener;
import com.example.teamsport.data.entity.CurrentUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class CreateAccountFragment extends Fragment implements View.OnClickListener {
	private FirebaseAuth firebaseAuth;
	FirebaseUser currentUser;
	private FirebaseAuth.AuthStateListener authStateListener;


	private Button createAccountButton;
	private EditText profileEmail;
	private EditText profilePassword;

	private ProgressBar progressBar;
	private TextView errorCreateAccountText;



	public CreateAccountFragment() {
		// Required empty public constructor
	}



	public static CreateAccountFragment newInstance() {
		CreateAccountFragment fragment = new CreateAccountFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		firebaseAuth = FirebaseAuth.getInstance();


		createAccountButton = view.findViewById(R.id.createAccount_createAccountButton);
		profileEmail = view.findViewById(R.id.createAccount_emailET);

		profilePassword = view.findViewById(R.id.createAccount_passwordET);

		progressBar = view.findViewById(R.id.createProfileProgressBar);

		errorCreateAccountText = view.findViewById(R.id.createProfile_errorTV);



		authStateListener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
				currentUser = firebaseAuth.getCurrentUser();
			}
		};



		createAccountButton.setOnClickListener(this);
	}

	private void createUserAccount(String email, String password) {
		createAccountButton.setVisibility(View.INVISIBLE);
		createAccountButton.setVisibility(View.INVISIBLE);
		progressBar.setVisibility(View.VISIBLE);

		firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
			@Override
			public void onComplete(@NonNull Task<AuthResult> task) {
				task.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
					@Override
					public void onSuccess(AuthResult authResult) {
						CurrentUser currentUser = CurrentUser.getInstance();
						currentUser.setUserId(authResult.getUser().getUid());
						FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
						FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
						fragmentTransaction.replace(R.id.createProfileFragment_container, CreateProfileFragment.newInstance())
								.commit();
					}
				});
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {
				progressBar.setVisibility(View.GONE);
				createAccountButton.setVisibility(View.VISIBLE);
				profileEmail.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
				profilePassword.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
				errorCreateAccountText.setText(R.string.incorrect_data_entered);
				errorCreateAccountText.setVisibility(View.VISIBLE);
			}
		});
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_create_account, container, false);
	}


	@Override
	public void onStart() {
		super.onStart();
		currentUser = firebaseAuth.getCurrentUser();
		firebaseAuth.addAuthStateListener(authStateListener);
	}

	@Override
	public void onClick(View v) {
		if ((v.getId() == R.id.createProfile_addImageIV) || (v.getId() == R.id.createProfile_UserImage_IV)) {
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent, 1);
		} else {
			setOnCreateButtonClick();
		}
	}

	private void setOnCreateButtonClick() {
		if (!TextUtils.isEmpty(profileEmail.getText().toString())
				&& !TextUtils.isEmpty(profilePassword.getText().toString())) {

			String email = profileEmail.getText().toString();
			String password = profilePassword.getText().toString();

			createUserAccount(email, password);

		} else {
			errorCreateAccountText.setText(R.string.empty_fields);
			errorCreateAccountText.setVisibility(View.VISIBLE);

			EditText[] editTexts = new EditText[]{profileEmail, profilePassword};
			EditTextListener editTextListener = new EditTextListener();
			editTextListener.setListener(editTexts, getContext());

		}
	}
}