package com.example.teamsport.presentation.fragments.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.teamsport.adapter.viewAdapter.PagerAdapter;
import com.example.teamsport.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class PagerFragment extends Fragment {
	private String address;

	public PagerFragment() {

	}

	public PagerFragment(String address) {
		this.address = address;
	}

	public static PagerFragment newInstance() {
		PagerFragment fragment = new PagerFragment();
		return fragment;
	}

	public static PagerFragment newInstanceFromMap(String addresss) {
		PagerFragment fragment = new PagerFragment(addresss);
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getLocationPermission();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		return inflater.inflate(R.layout.fragment_pager, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ViewPager2 viewpager = view.findViewById(R.id.sport_items_vewPager);
		TabLayout tableLayout = view.findViewById(R.id.tab_layout);

		NetworkCheck networkCheck = new NetworkCheck();
		networkCheck.execute();

		PagerAdapter pagerAdapter = new PagerAdapter(this, address);
		viewpager.setAdapter(pagerAdapter);

		new TabLayoutMediator(tableLayout, viewpager, (tab, position) -> {
			if (position == 0)
				tab.setText(getResources().getString(R.string.gatherings));
			if (position == 1)
				tab.setText(getResources().getString(R.string.current_gatherings));
		}).attach();

	}

	private void getLocationPermission() {

		if (ContextCompat.checkSelfPermission(getContext(),
				android.Manifest.permission.ACCESS_FINE_LOCATION)
				== PackageManager.PERMISSION_GRANTED) {
			boolean locationPermissionGranted = true;
		} else {
			int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
			ActivityCompat.requestPermissions(getActivity(),
					new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
					PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
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
			ConnectivityManager connectivityManager = ((ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE));
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
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(R.string.no_network_connetctio)
					.setPositiveButton(R.string.refresh, (dialog, which) -> {

								FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
								FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
								fragmentTransaction.replace(R.id.teamSportFragment_container, PagerFragment.newInstance())
										.addToBackStack("PagerFragment")
										.commit();
							}

					);
			builder.show();
		}
	}

}