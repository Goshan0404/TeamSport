package com.example.teamsport.presentation.fragments.fragment.profileFragment.createProfileFragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.teamsport.R;
import com.example.teamsport.adapter.viewAdapter.EditTextListener;
import com.example.teamsport.data.entity.CurrentUser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class CreateProfileFragment extends Fragment implements View.OnClickListener {
	private EditText profileName;
	private EditText profileAge;
	private ImageView profileImage;
	private ImageView addImage;
	private Uri imageUri;
	private ProgressBar progressBar;
	private Button nextButton;

	private boolean requestCode;

	private CollectionReference collectionReference;
	private StorageReference storageReference;
	private StorageReference filepath;
	private FirebaseAuth firebaseAuth;
	FirebaseUser currentUser;
	private FirebaseAuth.AuthStateListener authStateListener;


	public CreateProfileFragment() {
		// Required empty public constructor
	}


	public static CreateProfileFragment newInstance() {
		CreateProfileFragment fragment = new CreateProfileFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		firebaseAuth = FirebaseAuth.getInstance();
		storageReference = FirebaseStorage.getInstance().getReference();
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		collectionReference = db.collection("Users");

		Resources resources = this.getResources();
		imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
				resources.getResourcePackageName(R.drawable.person_profile_image) + '/' +
				resources.getResourceTypeName(R.drawable.person_profile_image) + '/' +
				resources.getResourceEntryName(R.drawable.person_profile_image));

		profileName = view.findViewById(R.id.createProfile_nameET);
		profileAge = view.findViewById(R.id.createProfileAge);
		profileImage = view.findViewById(R.id.createProfile_UserImage_IV);
		addImage = view.findViewById(R.id.createProfile_addImageIV);
		progressBar = view.findViewById(R.id.createProfileProgressBar);
		nextButton = view.findViewById(R.id.createProfile_nextButton);

		profileImage.setImageURI(imageUri);

		profileImage.setOnClickListener(this);
		nextButton.setOnClickListener(this);
		addImage.setOnClickListener(this);
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
		if (!TextUtils.isEmpty(profileName.getText().toString()) &&
				!TextUtils.isEmpty(profileAge.getText().toString())) {

			String name = profileName.getText().toString();
			String age = profileAge.getText().toString();

			createUserProfile(name, age);

		} else {
			EditText[] editTexts = new EditText[]{profileName, profileAge};
			EditTextListener editTextListener = new EditTextListener();
			editTextListener.setListener(editTexts, getContext());

		}
	}

	private void createUserProfile(String name, String age) {
		progressBar.setVisibility(View.VISIBLE);
		nextButton.setVisibility(View.INVISIBLE);

		currentUser = firebaseAuth.getCurrentUser();
		assert currentUser != null;
		String currentUserId = currentUser.getUid();

		if (requestCode) {
			filepath = storageReference
					.child("profile_images")
					.child("image" + currentUser.getUid());
		} else {
			filepath = storageReference
					.child("profile_images")
					.child("standard_image");
		}

		filepath.putFile(imageUri)
				.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
					@Override
					public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

						filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
							@Override
							public void onSuccess(Uri uri) {

								Map<String, String> userObj = new HashMap<>();
								userObj.put("userId", currentUserId);
								userObj.put("userName", name);
								userObj.put("userAge", age);
								userObj.put("imageLink", uri.toString());

								collectionReference.document(currentUserId).set(userObj)
										.addOnSuccessListener(new OnSuccessListener<Void>() {
											@Override
											public void onSuccess(Void aVoid) {
												CurrentUser currentUser = CurrentUser.getInstance();
												currentUser.setUserName(name);
												currentUser.setUserAge(age);
												currentUser.setUserId(currentUser.getUserId());
												currentUser.setUriProfileImage(uri);
												currentUser.setGatherings(null);
												progressBar.setVisibility(View.GONE);

												FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
												FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
												fragmentTransaction.replace(R.id.createProfileFragment_container, SetUsersLocationFragment.newInstance())
														.commit();
											}
										});
							}
						});
					}
				});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 1 & resultCode == RESULT_OK & data != null) {

			this.requestCode = true;

			profileImage.setBackgroundResource(0);
			imageUri = data.getData();
			profileImage.setImageURI(imageUri);
			addImage.setVisibility(View.GONE);
		} else {
			this.requestCode = false;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_create_profile, container, false);
	}
}