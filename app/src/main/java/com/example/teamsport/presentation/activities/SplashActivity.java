package com.example.teamsport.presentation.activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.teamsport.data.entity.CurrentUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


public class SplashActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

	private FirebaseAuth firebaseAuth;
	FirebaseUser currentUser;
	private final FirebaseFirestore db = FirebaseFirestore.getInstance();


	private final CollectionReference collectionReference = db.collection("Users");
	private FirebaseAuth.AuthStateListener authStateListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		firebaseAuth = FirebaseAuth.getInstance();
		currentUser = firebaseAuth.getCurrentUser();

		if (currentUser == null) {
			startActivity(new Intent(SplashActivity.this, AuthorizationActivity.class));
			finish();
		} else {

//			authStateListener = new FirebaseAuth.AuthStateListener() {
//				@Override
//				public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//
//				}
//			};
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
		firebaseAuth.addAuthStateListener(SplashActivity.this);
		FirebaseUser currentUser = firebaseAuth.getCurrentUser();
		if(currentUser == null) {
			startActivity(new Intent(SplashActivity.this, AuthorizationActivity.class));
			finish();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (firebaseAuth != null) {
			firebaseAuth.removeAuthStateListener(authStateListener);
		}
	}

	@Override
	public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
		currentUser = firebaseAuth.getCurrentUser();
		String currentUserId = null;

		if (currentUser != null) {
			currentUserId = currentUser.getUid();

			collectionReference.document(currentUserId)
					.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
				@Override
				public void onSuccess(DocumentSnapshot documentSnapshot) {
					Uri uri = Uri.parse(documentSnapshot.getString("imageLink"));

					CurrentUser currentUser = CurrentUser.getInstance();
					currentUser.setGatherings((List<String>) documentSnapshot.getData().get("gatherings"));
					currentUser.setUserId(documentSnapshot.getString("userId"));
					currentUser.setUserName(documentSnapshot.getString("userName"));
					currentUser.setUserAge(documentSnapshot.getString("userAge"));
					currentUser.setUserAddress(documentSnapshot.getString("userAddress"));
					currentUser.setUriProfileImage(uri);

					startActivity(new Intent(SplashActivity.this, TeamSportActivity.class));
					finish();
				}
			}).addOnFailureListener(new OnFailureListener() {
				@Override
				public void onFailure(@NonNull Exception e) {
					startActivity(new Intent(SplashActivity.this, AuthorizationActivity.class));
					finish();
				}
			});
		} else {
			startActivity(new Intent(SplashActivity.this, AuthorizationActivity.class));
			finish();
		}


	}
}
