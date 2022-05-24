package com.example.teamsport.data.db;

import android.content.Context;
import android.net.Uri;

import com.example.teamsport.data.entity.CurrentUser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class UpdateProfileData {
	private final CollectionReference collectionReference;
	private final StorageReference storageReference;


	public UpdateProfileData() {
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		collectionReference = db.collection("Users");
		storageReference = FirebaseStorage.getInstance().getReference();
	}

	public void updateData(CurrentUser currentUser, Context context) {
		final StorageReference filepath = storageReference
				.child("profile_images")
				.child("image" + currentUser.getUserId());

		filepath.putFile(currentUser.getUriProfileImage()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
			@Override
			public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
				filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
					@Override
					public void onSuccess(Uri uri) {
						Map<String, Object> newProfileData = new HashMap<>();

						newProfileData.put("userName", currentUser.getUserName());
						newProfileData.put("userAge", currentUser.getUserName());
						newProfileData.put("userAddress", currentUser.getUserAddress());
						newProfileData.put("imageLink", uri.toString());
						collectionReference.document(currentUser.getUserId()).update(newProfileData);
					}
				});
			}
		});
		}
	}
