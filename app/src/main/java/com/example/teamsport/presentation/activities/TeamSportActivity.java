package com.example.teamsport.presentation.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;

import com.example.teamsport.R;
import com.example.teamsport.presentation.fragments.fragment.gatheringFragment.ChatFragment;
import com.example.teamsport.presentation.fragments.fragment.gatheringFragment.MapFragment;
import com.example.teamsport.presentation.fragments.fragment.PagerFragment;
import com.example.teamsport.presentation.fragments.fragment.profileFragment.CurrentProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TeamSportActivity extends AppCompatActivity {

	private FirebaseAuth firebaseAuth;
	FirebaseUser currentUser;
	private String fragmentTag;
	public static BottomNavigationView bottomNavigationView;

	private Fragment selectedFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_team_sport);

		firebaseAuth = FirebaseAuth.getInstance();
		currentUser = firebaseAuth.getCurrentUser();

		getSupportFragmentManager().beginTransaction()

				.replace(R.id.teamSportFragment_container, PagerFragment.newInstance())
				.commit();

		bottomNavigationView = findViewById(R.id.bottom_navigationView);
		bottomNavigationView.setSelectedItemId(R.id.sport_items_bottom_nav);

		bottomNavigationView.setOnItemSelectedListener(item -> {
			int idItem = item.getItemId();

			String selectedItem = String.valueOf(bottomNavigationView.getMenu()
					.findItem(bottomNavigationView.getSelectedItemId()));

			getSelectedFragment(selectedItem);
			replaceFragments(getNavigationItemSelected(idItem));

			return true;
		});

	}

	private Fragment getNavigationItemSelected(int idItem) {
		switch (idItem) {
			case (R.id.profile_bottom_nav):
				selectedFragment = CurrentProfileFragment.newInstance();
				break;

			case (R.id.map_bottom_nav):
				selectedFragment = MapFragment.newInstance();
				break;

			case (R.id.sport_items_bottom_nav):
				selectedFragment = PagerFragment.newInstance();
				break;
			case (R.id.chat_items_bottom_nav):
				selectedFragment = ChatFragment.newInstance();
		}
		return selectedFragment;
	}

	public void replaceFragments(Fragment fragment) {

		FragmentManager fragmentManager = TeamSportActivity.this.getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.teamSportFragment_container, fragment)
				.addToBackStack(fragmentTag)
				.commit();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.sign_out_button) {
			if (currentUser != null && firebaseAuth != null) {
				firebaseAuth.signOut();
				startActivity(new Intent(TeamSportActivity.this, AuthorizationActivity.class));
				finish();
			}
		}
		return super.onOptionsItemSelected(item);
	}

	private void bottomNavigationListener(String tag) {
		switch (tag) {
			case ("CurrentProfileFragment"):
				bottomNavigationView.getMenu().getItem(3).setChecked(true);
				break;
			case ("MapFragment"):
				bottomNavigationView.getMenu().getItem(0).setChecked(true);
				break;
			case ("PagerFragment"):
				bottomNavigationView.getMenu().getItem(1).setChecked(true);
				break;
			case ("ChatFragment"):
				bottomNavigationView.getMenu().getItem(2).setChecked(true);
		}

	}

	private void getSelectedFragment(String bottomNavItem) {
		if (bottomNavItem.equals(getResources().getString(R.string.profile_bottom_nav))) {
			fragmentTag = "CurrentProfileFragment";
			return;
		} else if (bottomNavItem.equals(getResources().getString(R.string.sport_list_bottom_nav))) {
			fragmentTag = "PagerFragment";
			return;
		} else if (bottomNavItem.equals(getResources().getString(R.string.map_bottom_nav))) {
			fragmentTag = "MapFragment";
			return;
		} else if (bottomNavItem.equals(getResources().getString(R.string.chat))) {
			fragmentTag = "ChatFragment";
		}
	}


	@Override
	public void onBackPressed() {


		int count = getSupportFragmentManager().getBackStackEntryCount();

		if (count != 0) {
			int index = getSupportFragmentManager().getBackStackEntryCount() - 1;
			FragmentManager.BackStackEntry backEntry = getSupportFragmentManager().getBackStackEntryAt(index);
			String tag = backEntry.getName();
			if (tag != null) {
				if ((tag.equals("GatheringDetailsDialogFragment")) || (tag.equals("CreateGatheringDialogFragment"))) {
					FragmentManager fragmentManager = TeamSportActivity.this.getSupportFragmentManager();
					FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
					fragmentTransaction.replace(R.id.teamSportFragment_container, MapFragment.newInstance())
							.addToBackStack(null)
							.commit();
				} else {
					bottomNavigationListener(tag);
					getSupportFragmentManager().popBackStack();
				}
				return;
			}


			getSupportFragmentManager().popBackStack();
		}
	}
}