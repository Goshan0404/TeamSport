package com.example.teamsport.adapter.viewAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.teamsport.presentation.fragments.fragment.gatheringFragment.CurrentGatheringListFragment;
import com.example.teamsport.presentation.fragments.fragment.gatheringFragment.GatheringListFragment;

public class PagerAdapter extends FragmentStateAdapter {
	private final String address;


	public PagerAdapter(@NonNull Fragment fragment, String address) {
		super(fragment);
		this.address = address;
	}

	@NonNull
	@Override
	public Fragment createFragment(int position) {

		if (position == 0) {
			if (address != null)
				return GatheringListFragment.newInstanceFromMap(address);
			else
				return GatheringListFragment.newInstance();
		} else {
			return CurrentGatheringListFragment.newInstance();
		}
	}

	@Override
	public int getItemCount() {
		return 2;
	}


}
