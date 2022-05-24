package com.example.teamsport.presentation.fragments.fragment.profileFragment.createProfileFragment;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
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
import com.example.teamsport.presentation.activities.TeamSportActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class SignInFragment extends Fragment {

	private FirebaseAuth firebaseAuth;
	private CollectionReference collectionReference;

	private EditText profileEmail;
	private EditText profilePassword;
	private ProgressBar progressBar;
	private TextView errorSignInText;



	public SignInFragment() {
		// Required empty public constructor
	}

	public static SignInFragment newInstance() {
		SignInFragment fragment = new SignInFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		FirebaseFirestore db = FirebaseFirestore.getInstance();
		collectionReference = db.collection("Users");
		firebaseAuth = FirebaseAuth.getInstance();

		profileEmail = view.findViewById(R.id.sign_in_profile_email);
		profilePassword = view.findViewById(R.id.sign_in_profile_password);
		progressBar = view.findViewById(R.id.log_in_progressBar);
		Button signInButton = view.findViewById(R.id.sign_in_button);
		Button createAccountButton = view.findViewById(R.id.to_create_account_button);
		errorSignInText = view.findViewById(R.id.errorSignInTextView);

		signInButton.setOnClickListener(v -> {
			if (!TextUtils.isEmpty(profileEmail.getText().toString())
					&& !TextUtils.isEmpty(profilePassword.getText().toString())
			) {
				String email = profileEmail.getText().toString();
				String password = profilePassword.getText().toString();

				signInUserEmailAccount(email, password);
			} else {
				errorSignInText.setText("Empty fields not allowed");
				errorSignInText.setVisibility(View.VISIBLE);
				EditText[] editTexts = new EditText[]{profileEmail, profilePassword};
				EditTextListener editTextListener = new EditTextListener();
				editTextListener.setListener(editTexts, getContext());
			}

		});
		
		createAccountButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.replace(R.id.createProfileFragment_container, CreateAccountFragment.newInstance())
						.addToBackStack(null)
						.commit();
			}
		});
	}

	private void signInUserEmailAccount(String email, String password) {
		progressBar.setVisibility(View.VISIBLE);

		firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
			@Override
			public void onComplete(@NonNull Task<AuthResult> task) {
				if (task.isSuccessful()) {
					FirebaseUser currentUser = firebaseAuth.getCurrentUser();
					collectionReference.document(currentUser.getUid())
							.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
						@Override
						public void onSuccess(DocumentSnapshot documentSnapshot) {
							Uri uri = Uri.parse(documentSnapshot.getString("imageLink"));


							CurrentUser currentUser = CurrentUser.getInstance();
							currentUser.setUserName(documentSnapshot.getString("userName"));
							currentUser.setGatherings((List<String>) documentSnapshot.getData().get("gatherings"));
							currentUser.setUserId(documentSnapshot.getString("userId"));
							currentUser.setUserAge(documentSnapshot.getString("userAge"));
							currentUser.setUserAddress(documentSnapshot.getString("userAddress"));
							currentUser.setUriProfileImage(uri);

							startActivity(new Intent(getActivity(), TeamSportActivity.class));
							requireActivity().finish();
						}
					});
				}
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {
				Log.d("Error", "onFailure: " + e);
				progressBar.setVisibility(View.GONE);
				errorSignInText.setText("Incorrect data entered ");
				errorSignInText.setVisibility(View.VISIBLE);
				profileEmail.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
				profilePassword.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
			}
		});



	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_sign_in, container, false);
	}
}