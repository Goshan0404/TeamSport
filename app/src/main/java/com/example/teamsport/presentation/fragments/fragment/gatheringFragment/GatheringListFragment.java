package com.example.teamsport.presentation.fragments.fragment.gatheringFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.teamsport.R;
import com.example.teamsport.adapter.recyclerAdapter.gatheringRecyclerAdapter.SportRecyclerViewAdapter;
import com.example.teamsport.data.db.DataBase;

import com.example.teamsport.data.entity.CurrentUser;
import com.example.teamsport.data.entity.Gathering;
import com.example.teamsport.presentation.fragments.fragment.profileFragment.createProfileFragment.MapLocationFragment;

import java.util.ArrayList;
import java.util.List;

public class GatheringListFragment extends Fragment {
	private List<Gathering> gatheringList;
	private SportRecyclerViewAdapter recyclerViewAdapter;
	private TextView noticeTextView;
	private RecyclerView recyclerView;
	private TextView addressTextView;
	private String address;

	private final DataBase.GatheringDB gatheringDataBase;

	public GatheringListFragment() {
		gatheringDataBase =  DataBase.GatheringDB.getInstance();
	}

	public GatheringListFragment(String address) {
		gatheringDataBase =  DataBase.GatheringDB.getInstance();
		this.address = address;
	}

	public static GatheringListFragment newInstance() {
		return new GatheringListFragment();
	}

	public static GatheringListFragment newInstanceFromMap(String address) {
		return new GatheringListFragment(address);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_gathering_list, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		noticeTextView = view.findViewById(R.id.gatheringList_notice_textView);
		recyclerView = view.findViewById(R.id.gatheringList_item_recyclerview);
		addressTextView = view.findViewById(R.id.gatheringList_address_textView);

		if (address != null) {
			addressTextView.setText(address);
		} else {
			addressTextView.setText(CurrentUser.getInstance().getUserAddress());
		}

		addressTextView.setOnClickListener(v -> {
			FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.teamSportFragment_container, MapLocationFragment.newInstance())
					.addToBackStack("GatheringListFragment")
					.commit();
		});

		gatheringList = new ArrayList<>();

		setRecyclerView();
	}

	private void setRecyclerView() {
		recyclerViewAdapter = new SportRecyclerViewAdapter(gatheringList, getContext(), gathering -> {
			FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.teamSportFragment_container,
					GatheringDetailsFragment.newInstance(gathering))
					.addToBackStack(null)
					.commit();
		});

		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		recyclerView.setAdapter(recyclerViewAdapter);
	}

	@Override
	public void onStart() {
		super.onStart();

		gatheringList.clear();
		gatheringDataBase.getSortedGatheringCollection(gatheringList,
				"city",
				addressTextView.getText().toString().trim(), () -> {

					if (gatheringList.size() == 0) {

						noticeTextView.setVisibility(View.VISIBLE);
					} else {
						noticeTextView.setVisibility(View.GONE);
						recyclerViewAdapter.notifyDataSetChanged();
					}
				});

	}


}