package com.example.teamsport.presentation.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.teamsport.R;
import com.example.teamsport.presentation.fragments.fragment.PagerFragment;
import com.example.teamsport.presentation.fragments.fragment.profileFragment.createProfileFragment.CreateProfileFragment;
import com.example.teamsport.presentation.fragments.fragment.profileFragment.createProfileFragment.SignInFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class AuthorizationActivity extends AppCompatActivity {
	FirebaseUser currentUser;
	private FirebaseAuth.AuthStateListener authStateListener;

	private final FirebaseFirestore db = FirebaseFirestore.getInstance();
	private final CollectionReference collectionReference = db.collection("Users");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authorization);

		NetworkCheck networkCheck = new NetworkCheck();
		networkCheck.execute();

		FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
		currentUser = firebaseAuth.getCurrentUser();

		if (currentUser == null) {
			FragmentManager fragmentManager = AuthorizationActivity.this.getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.createProfileFragment_container, SignInFragment.newInstance())
					.commit();
		} else {
			FragmentManager fragmentManager = AuthorizationActivity.this.getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.createProfileFragment_container, CreateProfileFragment.newInstance())
					.commit();
		}
	}

	@Override
	public void onBackPressed() {
		int count = getSupportFragmentManager().getBackStackEntryCount();

		if (count != 0) {
			getSupportFragmentManager().popBackStack();
		}
	}

	private class NetworkCheck extends AsyncTask<Void, Void, Boolean> {


		@Override
		protected Boolean doInBackground(Void... voids) {
			return networkCheck();
		}

		@Override
		protected void onPostExecute(Boolean aBoolean) {
			super.onPostExecute(aBoolean);
			if (!aBoolean) {
				setAlertDialog();
			}
		}

		private boolean networkCheck() {
			if (isInternetAvailable() || isNetworkAvailable()) {
				return true;
			}
			return false;
		}

		private boolean isNetworkAvailable() {
			ConnectivityManager connectivityManager = ((ConnectivityManager) AuthorizationActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE));
			return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
		}

		private boolean isInternetAvailable() {

			try {
				InetAddress address = InetAddress.getByName("www.google.com");
				return !address.equals("");
			} catch (UnknownHostException e) {
				// Log error
			}
			return false;
		}

		private void setAlertDialog() {
			AlertDialog.Builder builder = new AlertDialog.Builder(AuthorizationActivity.this);
			builder.setMessage(R.string.no_network_connetctio)
					.setPositiveButton(R.string.refresh, (dialog, which) -> {
								finish();
								startActivity(getIntent());
							}

					);
			builder.show();
		}
	}
}