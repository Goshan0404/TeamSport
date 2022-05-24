package com.example.teamsport.adapter.recyclerAdapter.usersRecyclerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamsport.R;
import com.example.teamsport.data.entity.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UsersRecyclerViewAdapter extends RecyclerView.Adapter<UsersRecyclerViewAdapter.ViewHolder> {
	private final List<User> users;
	OnUserClickListener onUserClickListener;

	public UsersRecyclerViewAdapter(List<User> users, OnUserClickListener onUserClickListener) {
		this.users = users;
		this.onUserClickListener = onUserClickListener;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.user_tem, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		User user = users.get(position);

		holder.userNameTextView.setText(user.getUserName());
		Picasso.get().load(user.getUriProfileImage()).into(holder.userImage);
	}

	@Override
	public int getItemCount() {
		return users.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		TextView userNameTextView;
		ImageView userImage;
		OnUserClickListener userClickListener;

		public ViewHolder(@NonNull View itemView) {
			super(itemView);

			userNameTextView = itemView.findViewById(R.id.user_item_userName);
			userImage = itemView.findViewById(R.id.user_tem_userImage);
			this.userClickListener = onUserClickListener;
			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			User user = users.get(getAdapterPosition());
			userClickListener.onGatheringClick(user);
		}
	}
}
