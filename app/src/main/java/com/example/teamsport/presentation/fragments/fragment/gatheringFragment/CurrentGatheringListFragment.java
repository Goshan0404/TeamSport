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
import com.example.teamsport.data.entity.Gathering;
import com.example.teamsport.data.entity.CurrentUser;

import java.util.ArrayList;
import java.util.List;

public class CurrentGatheringListFragment extends Fragment {
	private List<Gathering> gatheringList;
	private SportRecyclerViewAdapter recyclerViewAdapter;
	private TextView noticeTextView;

	private final DataBase.GatheringDB gatheringDataBase;


	public CurrentGatheringListFragment() {
		gatheringDataBase = DataBase.GatheringDB.getInstance();
	}

	public static CurrentGatheringListFragment newInstance() {
		return new CurrentGatheringListFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_current_gatherings_list, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		gatheringList = new ArrayList<>();

		noticeTextView = view.findViewById(R.id.gatheringList_notice_textView);
		RecyclerView recyclerView = view.findViewById(R.id.gatheringList_item_recyclerview);

		setRecyclerView(recyclerView);
	}

	private void setRecyclerView(RecyclerView recyclerView) {
		recyclerViewAdapter = new SportRecyclerViewAdapter(gatheringList, getContext(), gathering -> {
			FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.teamSportFragment_container,
					GatheringDetailsFragment.newInstance(gathering))
					.addToBackStack("CurrentGatheringListFragment")
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
		gatheringDataBase.getUserSortedGatheringCollection(gatheringList,
				"usersId",
				CurrentUser.getInstance().getUserId(), () -> {

					if (gatheringList.size() == 0) {

						noticeTextView.setVisibility(View.VISIBLE);
					} else {
						noticeTextView.setVisibility(View.GONE);
						recyclerViewAdapter.notifyDataSetChanged();
					}
				});

	}
}