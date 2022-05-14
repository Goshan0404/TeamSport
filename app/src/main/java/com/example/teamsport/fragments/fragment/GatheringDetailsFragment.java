package com.example.teamsport.fragments.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.teamsport.R;
import com.example.teamsport.db.DataBase;
import com.example.teamsport.db.interfaces.FirebaseSuccess;
import com.example.teamsport.entity.Gathering;
import com.example.teamsport.entity.Person;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class GatheringDetailsFragment extends Fragment implements OnMapReadyCallback {
    private MapView mapView;
    private GoogleMap map;
    private String gatheringId;

    private TextView sportTextView;
    private TextView addressTextView;
    private TextView timeTextView;
    private TextView coastTextView;
    private TextView coastStaticTextView;
    private TextView dateTextView;
    private Button subscribeButton;
    private TextView usersTextView;
    private TextView descriptionTextView;

    private final DataBase.GatheringDB gatheringDataBase;
    private final DataBase.PersonDB personDataBase;

    public GatheringDetailsFragment() {
        gatheringDataBase = new DataBase.GatheringDB();
        personDataBase = new DataBase.PersonDB();
    }

    public static GatheringDetailsFragment newInstance(String gatheringId) {
        GatheringDetailsFragment fragment  = new GatheringDetailsFragment();

        Bundle args = new Bundle();
        args.putString("gatheringId", gatheringId);
        fragment.setArguments(args);

		return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            savedInstanceState = getArguments();
            gatheringId = savedInstanceState.getString("gatheringId");
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gathering_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sportTextView = view.findViewById(R.id.gatheringDetails_sport);
        addressTextView = view.findViewById(R.id.gatheringDetails_address);
        timeTextView = view.findViewById(R.id.gatheringDetails_time);
        coastTextView = view.findViewById(R.id.gatheringDetails_coast);
        coastStaticTextView = view.findViewById(R.id.gatheringDetails_coast_textView);
        dateTextView = view.findViewById(R.id.gatheringDetails_date);
        usersTextView = view.findViewById(R.id.gatheringDetails_usersTextView);
        descriptionTextView = view.findViewById(R.id.gatheringDetails_description_textView);
        subscribeButton = view.findViewById(R.id.gatheringDetails_subscribe_button);
        mapView = view.findViewById(R.id.gatheringDetails_miniMap);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        getGathering();
    }

    private void setSubscribeButton(Gathering gathering) {
        if (!Person.getInstance().getUserId().equals(gathering.getCreatedBy())) {
            if (Person.getInstance().getGatherings() != null) {
                if (Person.getInstance().getGatherings().size() != 0) {
                    for (String gatherings : Person.getInstance().getGatherings()) {
                        if (gatherings.equals(gatheringId)) {
                            isSubscribed();
                            return;
                        }
                    }
                    isUnsubscribed();
                } else {
                    isUnsubscribed();
                }
            } else {
                isUnsubscribed();
            }
        } else {
            isOwner();
        }
    }

    private void isOwner() {
        subscribeButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        subscribeButton.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        subscribeButton.setText(R.string.delete);

        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAlertDialog();
            }

            private void setAlertDialog() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.delete_gathering)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                gatheringDataBase.deleteGatheringField(gatheringId, new FirebaseSuccess.OnVoidSuccessListener() {
                                    @Override
                                    public void onVoidSuccess() {
                                        personDataBase.deleteArrayGatheringPersonField(
                                                Person.getInstance().getUserId(),
                                                gatheringId,
                                                new FirebaseSuccess.OnVoidSuccessListener() {
                                                    @Override
                                                    public void onVoidSuccess() {
                                                        dialog.cancel();
                                                        Person.getInstance().deleteGathering(gatheringId);
                                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                        fragmentTransaction.replace(R.id.fragment_container, PagerFragment.newInstance())
                                                                .addToBackStack(null)
                                                                .commit();

                                                    }
                                                });
                                    }
                                });
                            }
                        }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
    }

    private void isSubscribed() {
        subscribeButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        subscribeButton.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        subscribeButton.setText(R.string.unsubscribe);

        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gatheringDataBase.deleteArrayItemGatheringField(gatheringId,
                        "users",
                        Person.getInstance().getUserId(),
                        null);

                personDataBase.deleteArrayGatheringPersonField(
                        Person.getInstance().getUserId(),
                        gatheringId,
                        null);

                usersTextView.setText(String.valueOf(Integer.parseInt(usersTextView.getText().toString()) - 1));
                Person.getInstance().deleteGathering(gatheringId);
                isUnsubscribed();
            }
        });
    }

    private void isUnsubscribed() {
        subscribeButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blue));
        subscribeButton.setTextColor(Color.WHITE);
        subscribeButton.setText(R.string.subscribe);

        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gatheringDataBase.addArrayItemGatheringField(gatheringId,
                        "users",
                        Person.getInstance().getUserId(),
                        null);

                personDataBase.addArrayItemPersonField(
                        Person.getInstance().getUserId(),
                        "gatherings",
                        gatheringId,
                        null);
                usersTextView.setText(String.valueOf(Integer.parseInt(usersTextView.getText().toString()) + 1));
                Person.getInstance().addGathering(gatheringId);
                isSubscribed();
            }
        });
    }

    private void getGathering() {
        gatheringDataBase.getGatheringById(gatheringId, new FirebaseSuccess.OnGatheringSuccessListener() {
            @Override
            public void onGatheringSuccess(Gathering gathering) {


                if (gathering.getCoast() != null)
                    coastStaticTextView.setVisibility(View.VISIBLE);

                LatLng location = new LatLng(Double.parseDouble(gathering.getLatitude()),
                        Double.parseDouble(gathering.getLongitude()));

                sportTextView.setText(gathering.getSport());
                addressTextView.setText(gathering.getAddress());
                timeTextView.setText(gathering.getTime());
                coastTextView.setText(gathering.getCoast());
                usersTextView.setText(String.valueOf(gathering.getAmountUsers()));
                dateTextView.setText(gathering.getDate());
                descriptionTextView.setText(gathering.getDescription());

                setSubscribeButton(gathering);

                map.addMarker(new MarkerOptions().position(location).title(gathering.getSport()));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 16F));
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);

    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}