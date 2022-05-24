package com.example.teamsport.data.entity;

import android.net.Uri;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
	private String userName;
	private String userId;
	private String userAge;
	private String userAddress;
	private List<String> gatherings;
	private Uri uriProfileImage;
}
