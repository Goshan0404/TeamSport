package com.example.teamsport.data.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Gathering {
	private String sport;
	private String address;
	private String city;
	private String date;
	private String time;
	private String latitude;
	private String longitude;
	private String coast;
	private String description;
	private String createdBy;
	private List<String> usersId = new ArrayList<>();
	private int amountUsers;
	private String id;

	public void addUsers(String userId) {
		usersId.add(userId);
	}
}
