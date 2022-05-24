package com.example.teamsport.presentation.fragments.fragment.gatheringFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
import com.example.teamsport.data.db.DataBase;

import com.example.teamsport.data.entity.CurrentUser;
import com.example.teamsport.presentation.fragments.dialogFragment.gatheringDialogFragment.CreateGatheringDialogFragment;
import com.example.teamsport.presentation.fragments.dialogFragment.gatheringDialogFragment.GatheringDetailsDialogFragment;
import com.example.teamsport.data.entity.Gathering;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MapFragment extends Fragment {

	private GoogleMap map;
	private androidx.appcompat.widget.SearchView searchView;

	private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
	private boolean locationPermissionGranted;
	private FusedLocationProviderClient fusedLocationProviderClient;
	private Location lastKnownLocation;
	private static final String KEY_CAMERA_POSITION = "camera_position";
	private static final String KEY_LOCATION = "location";
	private final LatLng defaultLocation;

	private final CurrentUser currentUser;

	private SupportMapFragment mapFragment;
	private Marker currentMarker;
	private List<Address> addressList;

	private List<Gathering> gatherings;
	private LatLng markersLatLng;

	private final DataBase.GatheringDB dataBase;

	private FragmentManager manager;

	public MapFragment() {
		dataBase =  DataBase.GatheringDB.getInstance();
		defaultLocation = new LatLng(-33.8523341, 151.2106085);
		currentUser = CurrentUser.getInstance();
	}

	public static MapFragment newInstance() {
		return new MapFragment();
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
							 @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_maps, container, false);
		searchView = view.findViewById(R.id.searchView);
		return view;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		if (savedInstanceState != null) {
			lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
		}
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

		fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

		gatherings = new ArrayList<>();

		setSearchView();

		if (mapFragment != null) {
			mapFragment.getMapAsync(callback);
		}
	}

	private final OnMapReadyCallback callback = googleMap -> {
		googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		map = googleMap;
		map.getUiSettings().setMyLocationButtonEnabled(true);

		getLocationPermission();
		geolocationCheck();
		updateLocationUI();
		setMarkers();
		onMapClick();
		onMarkClick();
	};

	private void setMarkers() {
		dataBase.getSortedGatheringCollection(gatherings, "city",
				currentUser.getUserAddress(), () -> {
			for (Gathering gathering : gatherings) {
				switch (gathering.getSport()) {
					case "Football":
						Objects.requireNonNull(map
								.addMarker(getPreparedMarkerOptions(gathering,
										BitmapDescriptorFactory.fromResource(R.drawable.football_ball))))
								.setTag(gathering.getId());
						break;
					case "Basketball":
						Objects.requireNonNull(map
								.addMarker(getPreparedMarkerOptions(gathering,
										BitmapDescriptorFactory.fromResource(R.drawable.basketball_ball))))
								.setTag(gathering.getId());
						break;
					case "Tennis":
						Objects.requireNonNull(map
								.addMarker(getPreparedMarkerOptions(gathering,
										BitmapDescriptorFactory.fromResource(R.drawable.tennis_ball))))
								.setTag(gathering.getId());
						break;
					case "Volleyball":
						Objects.requireNonNull(map
								.addMarker(getPreparedMarkerOptions(gathering,
										BitmapDescriptorFactory.fromResource(R.drawable.volleyball_ball))))
								.setTag(gathering.getId());
						break;
				}
			}
		});
	}

	private void onMarkClick() {
		map.setOnMarkerClickListener(marker -> {
			manager = requireActivity().getSupportFragmentManager();

			boolean isCurrentMarkerPersonMarker = marker.equals(currentMarker);

			if (isCurrentMarkerPersonMarker) {
				if (addressList != null && addressList.size() != 0) {

					CreateGatheringDialogFragment createGatheringDialogFragment = CreateGatheringDialogFragment
							.newInstance(addressList, markersLatLng.latitude, markersLatLng.longitude);
					createGatheringDialogFragment.show(manager, "createGatheringDialogFragment");

				} else
					Toast.makeText(getContext(), "Not correct place", Toast.LENGTH_SHORT).show();

			} else {
				GatheringDetailsDialogFragment gatheringDetailsDialogFragment =
						GatheringDetailsDialogFragment.newInstance(String.valueOf(marker.getTag()));
				gatheringDetailsDialogFragment.show(manager, "gatheringDetailsDialogFragment");
			}
			return false;
		});

	}

	private void onMapClick() {
		map.setOnMapClickListener(latLng -> {
			markersLatLng = latLng;

			if (currentMarker != null) {
				currentMarker.remove();
			}
			currentMarker = map.addMarker(getPreparedMarkerOptions(markersLatLng));

			Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
			try {
				addressList = geocoder.getFromLocation(markersLatLng.latitude, markersLatLng.longitude, 1);
			} catch (IOException e) {
				e.printStackTrace();
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
					markersLatLng = new LatLng(address.getLatitude(), address.getLongitude());
					currentMarker = map.addMarker(getPreparedMarkerOptions(markersLatLng));
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

	private void getLocationPermission() {

		if (ContextCompat.checkSelfPermission(requireContext(),
				android.Manifest.permission.ACCESS_FINE_LOCATION)
				== PackageManager.PERMISSION_GRANTED) {
			locationPermissionGranted = true;
			updateLocationUI();
		} else {
			ActivityCompat.requestPermissions(requireActivity(),
					new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
					PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
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
							map.moveCamera(CameraUpdateFactory.newLatLngZoom(
									new LatLng(lastKnownLocation.getLatitude(),
											lastKnownLocation.getLongitude()), 10));
						}
					} else {
						map.moveCamera(CameraUpdateFactory
								.newLatLngZoom(defaultLocation, 10));
						map.getUiSettings().setMyLocationButtonEnabled(false);
					}
				});
			}
		} catch (SecurityException ignored) {
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

	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (map != null) {
			outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
			outState.putParcelable(KEY_LOCATION, lastKnownLocation);
		}
		super.onSaveInstanceState(outState);
	}


	private MarkerOptions getPreparedMarkerOptions(Gathering gathering, BitmapDescriptor descriptor) {
		LatLng location = new LatLng(Double.parseDouble(gathering.getLatitude()),
				Double.parseDouble(gathering.getLongitude()));

		return getPreparedMarkerOptions(location, gathering.getSport(), descriptor);
	}

	private MarkerOptions getPreparedMarkerOptions(LatLng latLng) {
		return new MarkerOptions()
				.position(latLng)
				.title("Your marker");
	}

	private MarkerOptions getPreparedMarkerOptions(LatLng latLng, String title, BitmapDescriptor descriptor) {
		return new MarkerOptions()
				.position(latLng)
				.title(title)
				.icon(descriptor);
	}


	@Override
	public void onResume() {
		mapFragment.onResume();
		super.onResume();
	}


	@Override
	public void onPause() {
		super.onPause();
		mapFragment.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mapFragment.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mapFragment.onLowMemory();
	}
}