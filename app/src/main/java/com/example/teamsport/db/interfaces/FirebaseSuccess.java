package com.example.teamsport.db.interfaces;

import com.example.teamsport.entity.Gathering;

public class FirebaseSuccess {

	public interface OnVoidSuccessListener{
		void onVoidSuccess();
	}

	public interface OnGatheringSuccessListener{
		void onGatheringSuccess(Gathering gathering);
	}


}
