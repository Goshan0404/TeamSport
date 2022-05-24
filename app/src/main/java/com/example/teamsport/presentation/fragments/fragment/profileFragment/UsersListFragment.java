package com.example.teamsport.presentation.fragments.fragment.profileFragment;

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

import com.example.teamsport.R;
import com.example.teamsport.adapter.recyclerAdapter.usersRecyclerAdapter.OnUserClickListener;
import com.example.teamsport.adapter.recyclerAdapter.usersRecyclerAdapter.UsersRecyclerViewAdapter;
import com.example.teamsport.data.db.DataBase;
import com.example.teamsport.data.entity.Gathering;
import com.example.teamsport.data.entity.User;

import java.util.ArrayList;
import java.util.List;


public class UsersListFragment extends Fragment {

	private final Gathering gathering;

	private RecyclerView recyclerView;
	private List<User> users;

	private final DataBase.PersonDB personDB;

	public UsersListFragment(Gathering gathering) {
		this.gathering = gathering;
		personDB = DataBase.PersonDB.getInstance();
	}

	public static UsersListFragment newInstance(Gathering gathering) {
		UsersListFragment fragment = new UsersListFragment(gathering);

		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_users_list, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		users = new ArrayList<>();

		recyclerView = view.findViewById(R.id.usersList_recyclerView);

		personDB.getListUser(gathering.getUsersId(), userList -> {
			users = userList;
			setRecyclerView();
		});



	}

	private void setRecyclerView() {
		UsersRecyclerViewAdapter usersRecyclerViewAdapter = new UsersRecyclerViewAdapter(users, new OnUserClickListener() {
			@Override
			public void onGatheringClick(User user) {
				FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.replace(R.id.teamSportFragment_container,
						UserProfileFragment.newInstance(user))
						.addToBackStack(null)
						.commit();
			}
		});

		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		recyclerView.setAdapter(usersRecyclerViewAdapter);

	}

}