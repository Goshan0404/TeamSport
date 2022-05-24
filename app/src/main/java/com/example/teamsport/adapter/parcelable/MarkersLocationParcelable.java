package com.example.teamsport.adapter.parcelable;


import android.location.Address;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class MarkersLocationParcelable implements Parcelable {
	private List<Address> addresses;
	private double latitude;
	private double longitude;

	public MarkersLocationParcelable(List<Address> addresses, double latitude, double longitude) {
		this.addresses = addresses;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public List<Address> getAddresses() {
		return addresses;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	protected MarkersLocationParcelable(Parcel in) {
		addresses = in.createTypedArrayList(Address.CREATOR);
		latitude = in.readDouble();
		longitude = in.readDouble();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeTypedList(addresses);
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<MarkersLocationParcelable> CREATOR = new Creator<MarkersLocationParcelable>() {
		@Override
		public MarkersLocationParcelable createFromParcel(Parcel in) {
			return new MarkersLocationParcelable(in);
		}

		@Override
		public MarkersLocationParcelable[] newArray(int size) {
			return new MarkersLocationParcelable[size];
		}
	};
}
