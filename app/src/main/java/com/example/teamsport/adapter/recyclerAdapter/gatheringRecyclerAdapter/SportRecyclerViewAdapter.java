package com.example.teamsport.adapter.recyclerAdapter.gatheringRecyclerAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamsport.R;
import com.example.teamsport.data.entity.Gathering;

import java.util.List;

public class SportRecyclerViewAdapter extends RecyclerView.Adapter<SportRecyclerViewAdapter.ViewHolder> {
	private final List<Gathering> gatheringList;
	private final OnGatheringClickListener gatheringClickListener;
	private Context context;

	public SportRecyclerViewAdapter(List<Gathering> gatheringList,
									Context context,
									OnGatheringClickListener onGatheringClickListener) {
		this.context = context;
		this.gatheringList = gatheringList;
		this.gatheringClickListener = onGatheringClickListener;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.gathering_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		Gathering gathering = gatheringList.get(position);

		switch (gathering.getSport()) {
			case "Football":
				holder.sportImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.football_ball));
				break;
			case "Basketball":
				holder.sportImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.basketball_ball));
				break;
			case "Tennis":
				holder.sportImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.tennis_ball));
				break;
			case "Volleyball":
				holder.sportImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.volleyball_ball));
				break;

		}

		holder.sportTextView.setText(gathering.getSport());
		holder.addressTextView.setText(gathering.getAddress());
		holder.timeTextView.setText(gathering.getTime());
		holder.dateTextView.setText(gathering.getDate());
		holder.usersTextView.setText(String.valueOf(gathering.getAmountUsers()));

	}

	@Override
	public int getItemCount() {
		return gatheringList.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		private final TextView sportTextView;
		private final TextView addressTextView;
		private final TextView dateTextView;
		private final TextView timeTextView;
		private final TextView usersTextView;
		private final ImageView sportImageView;
		OnGatheringClickListener onGatheringClickListener;

		public ViewHolder(@NonNull View itemView) {
			super(itemView);

			sportTextView = itemView.findViewById(R.id.gatheringItem_sport_textView);
			addressTextView = itemView.findViewById(R.id.gatheringItem_address_textView);
			timeTextView = itemView.findViewById(R.id.gatheringItem_time_textView);
			dateTextView = itemView.findViewById(R.id.gatheringItem_date_textView);
			usersTextView = itemView.findViewById(R.id.gatheringItem_usersTextView);
			sportImageView = itemView.findViewById(R.id.gatheringItem_sport_imageView);
			this.onGatheringClickListener = gatheringClickListener;
			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			Gathering gathering = gatheringList.get(getAdapterPosition());
			onGatheringClickListener.onGatheringClick(gathering);
		}
	}
}
