package com.example.teamsport.presentation.fragments.fragment.profileFragment.createProfileFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.teamsport.R;
import com.example.teamsport.presentation.fragments.fragment.PagerFragment;
import com.example.teamsport.presentation.fragments.fragment.profileFragment.EditProfileFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapLocationFragment extends Fragment {

	private boolean locationPermissionGranted;
	private FusedLocationProviderClient fusedLocationProviderClient;
	private Location lastKnownLocation;
	private Marker currentMarker;
	private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
	private LatLng defaultLocation;
	private List<Address> addressList;
	private GoogleMap map;
	private androidx.appcompat.widget.SearchView searchView;

	public MapLocationFragment() {

	}


	public static MapLocationFragment newInstance() {
		return new MapLocationFragment();
	}

	private final OnMapReadyCallback callback = new OnMapReadyCallback() {

		@Override
		public void onMapReady(GoogleMap googleMap) {
			map = googleMap;
			
			getLocationPermission();
			geolocationCheck();
			updateLocationUI();
			onMapClick();
			onMarkClick();
		}
	};

	private void getLocationPermission() {

		if (ContextCompat.checkSelfPermission(requireContext(),
				android.Manifest.permission.ACCESS_FINE_LOCATION)
				== PackageManager.PERMISSION_GRANTED) {
			locationPermissionGranted = true;
		} else {
			ActivityCompat.requestPermissions(requireActivity(),
					new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
					PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
			updateLocationUI();
		}
	}

	private void updateLocationUI() {

		try {
			if (locationPermissionGranted) {
				map.setMyLocationEnabled(true);
				map.getUiSettings().setMyLocationButtonEnabled(true);
				getDeviceLocation();
			} else {
				map.setMyLocationEnabled(false);
				map.getUiSettings().setMyLocationButtonEnabled(false);
				lastKnownLocation = null;
				getLocationPermission();
			}
		} catch (SecurityException ignored) {
		}
	}

	private void getDeviceLocation() {
		try {
			if (locationPermissionGranted) {
				Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
				locationResult.addOnCompleteListener(requireActivity(), task -> {
					if (task.isSuccessful()) {
						lastKnownLocation = task.getResult();
						if (lastKnownLocation != null) {

							if (currentMarker != null)
								currentMarker.remove();

							LatLng latLng = new LatLng(lastKnownLocation.getLatitude(),
									lastKnownLocation.getLongitude());

							map.moveCamera(CameraUpdateFactory.newLatLngZoom(
									latLng, 50));

							currentMarker = map.addMarker(new MarkerOptions().position(latLng));

							Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
							try {
								addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					} else {
						map.moveCamera(CameraUpdateFactory
								.newLatLngZoom(defaultLocation, 50));
						map.getUiSettings().setMyLocationButtonEnabled(false);
					}
				});
			}
		} catch (SecurityException ignored) {
		}

	}

	private void geolocationCheck() {
		final LocationManager manager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);

		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			buildAlertMessageNoGps();

		}
	}

	private void buildAlertMessageNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setMessage(getResources().getString(R.string.geolocationCheck))
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int id) {
						startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int id) {
						dialog.cancel();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		locationPermissionGranted = false;
		if (requestCode
				== PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
			if (grantResults.length > 0
					&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				locationPermissionGranted = true;
			}
		} else {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
		updateLocationUI();
	}


	private void onMapClick() {
		map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
			@Override
			public void onMapClick(@NonNull LatLng latLng) {
				if (currentMarker != null) {
					currentMarker.remove();
				}

				currentMarker = map.addMarker(new MarkerOptions().position(latLng).title("Your location"));
				map.moveCamera(CameraUpdateFactory.newLatLng(latLng));

				Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
				try {
					addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void onMarkClick() {

		map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(@NonNull Marker marker) {
				if (addressList != null && addressList.size() != 0) {

					int count = requireActivity().getSupportFragmentManager().getBackStackEntryCount();
					String tag = "";

					if (count != 0) {
						int index = requireActivity().getSupportFragmentManager().getBackStackEntryCount() - 1;
						FragmentManager.BackStackEntry backEntry = requireActivity().getSupportFragmentManager().getBackStackEntryAt(index);
						tag = backEntry.getName();
					}


					assert tag != null;
					if (tag.equals("EditProfileFragment")) {
						replaceFragment(R.id.teamSportFragment_container, EditProfileFragment
								.newInstanceFromMap(String.valueOf(addressList.get(0).getLocality())));
					} else if (tag.equals("SetUsersLocationFragment")){
						replaceFragment(R.id.createProfileFragment_container, SetUsersLocationFragment
								.newInstanceFromMap(String.valueOf(addressList.get(0).getLocality())));
					} else if (tag.equals("GatheringListFragment")) {
						replaceFragment(R.id.teamSportFragment_container, PagerFragment
								.newInstanceFromMap(String.valueOf(addressList.get(0).getLocality())));
					}
				} else {
					Toast.makeText(getContext(), "Not correct place", Toast.LENGTH_SHORT).show();
				}
				return false;
			}
		});
	}

	private void setSearchView() {

		searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				String location = searchView.getQuery().toString();
				Geocoder geocoder = new Geocoder(getContext());
				try {
					addressList = geocoder.getFromLocationName(location, 1);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (addressList.size() != 0) {

					if (currentMarker != null) {
						currentMarker.remove();
					}

					Address address = addressList.get(0);
					LatLng markersLatLng = new LatLng(address.getLatitude(), address.getLongitude());
					currentMarker = map.addMarker(new MarkerOptions().position(markersLatLng));
					map.animateCamera(CameraUpdateFactory.newLatLngZoom(markersLatLng, 10));
				}
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
	}

	private void replaceFragment(int container, Fragment fragment) {
		FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(container, fragment)
				.commit();
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
							 @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_map_users_location, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
		defaultLocation = new LatLng(-33.8523341, 151.2106085);

		addressList = new ArrayList<>();

		searchView = view.findViewById(R.id.userLocation_searchView);
		setSearchView();

		SupportMapFragment mapFragment =
				(SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.userLocationMap);
		if (mapFragment != null) {
			mapFragment.getMapAsync(callback);
		}
	}


}