package com.example.teamsport.data.db.interfaces;

import com.example.teamsport.data.entity.Gathering;
import com.example.teamsport.data.entity.User;

import java.util.List;

public class FirebaseSuccess {

	public interface OnVoidSuccessListener{
		void onVoidSuccess();
	}

	public interface OnGatheringSuccessListener{
		void onGatheringSuccess(Gathering gathering);
	}

	public interface OnUsersSuccessListener {
		void onUsersSuccess(List<User> users);
	}

}
