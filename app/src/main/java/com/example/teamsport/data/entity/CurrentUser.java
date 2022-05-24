package com.example.teamsport.data.entity;

import android.app.Application;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CurrentUser extends Application {

	private String userName;
	private String userId;
	private String userAge;
	private String userAddress;
	private List<String> gatherings;
	private Uri uriProfileImage;

	private static CurrentUser instance;

	public static CurrentUser getInstance() {
		if (instance == null)
			instance = new CurrentUser();
		return instance;
	}

	public void addGathering(String gatheringId) {
		if (gatherings == null) {
			gatherings = new ArrayList<>();
		}
		gatherings.add(gatheringId);
	}

	public void deleteGathering(String gatheringId) {
		gatherings.remove(gatheringId);
	}

}
