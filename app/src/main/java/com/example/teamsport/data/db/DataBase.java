package com.example.teamsport.data.db;

import android.net.Uri;
import android.text.TextUtils;
import android.text.format.DateUtils;

import com.example.teamsport.data.db.interfaces.FirebaseSuccess;
import com.example.teamsport.data.entity.CurrentUser;
import com.example.teamsport.data.entity.Gathering;
import com.example.teamsport.data.entity.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DataBase {
	private static FirebaseFirestore db;

	public DataBase() {
		db = FirebaseFirestore.getInstance();
	}


	public static class GatheringDB {
		private final CollectionReference gatheringCollection;

		private static GatheringDB instance;

		public static GatheringDB getInstance() {
			if (instance == null)
				instance = new GatheringDB();
			return instance;
		}

		public GatheringDB() {
			new DataBase();
			gatheringCollection = db.collection("Gathering");
		}


		public void getUserSortedGatheringCollection(List<Gathering> gatherings,
													 String fieldString,
													 String field,
													 final FirebaseSuccess.OnVoidSuccessListener firebaseSuccessListener) {
			gatheringCollection.whereArrayContains(fieldString, field).get().addOnSuccessListener(queryDocumentSnapshots -> {
				for (QueryDocumentSnapshot gatheringsInfo : queryDocumentSnapshots) {

					Gathering gathering = gatheringsInfo.toObject(Gathering.class);

					if (isObsolete(gathering)) {
						deleteGatheringField(gathering.getId(), new FirebaseSuccess.OnVoidSuccessListener() {
							@Override
							public void onVoidSuccess() {
								deleteGatheringField(gathering.getId(), new FirebaseSuccess.OnVoidSuccessListener() {
									@Override
									public void onVoidSuccess() {
										PersonDB.getInstance().deleteArrayGatheringPersonField(
												CurrentUser.getInstance().getUserId(),
												String.valueOf(gathering), null);
									}
								});
							}
						});
					} else {
						gatherings.add(gathering);
					}
				}
				if (firebaseSuccessListener != null) {
					firebaseSuccessListener.onVoidSuccess();
				}

			});
		}

		public void getSortedGatheringCollection(List<Gathering> gatherings,
												 String fieldString,
												 String field,
												 final FirebaseSuccess.OnVoidSuccessListener firebaseSuccessListener) {
			gatheringCollection.whereEqualTo(fieldString, field).get().addOnSuccessListener(queryDocumentSnapshots -> {
				for (QueryDocumentSnapshot gatheringsInfo : queryDocumentSnapshots) {

					Gathering gathering = gatheringsInfo.toObject(Gathering.class);

					if (isObsolete(gathering)) {
						deleteGatheringField(gathering.getId(), new FirebaseSuccess.OnVoidSuccessListener() {
							@Override
							public void onVoidSuccess() {
								PersonDB.getInstance().deleteArrayGatheringPersonField(
										CurrentUser.getInstance().getUserId(),
										gathering.getId(), null);
							}
						});
					} else {
						gatherings.add(gathering);
					}
				}
				if (firebaseSuccessListener != null) {
					firebaseSuccessListener.onVoidSuccess();
				}

			});
		}

		private boolean isObsolete(Gathering gathering) {
			Date now = new Date();
			String gatheringDateString = gathering.getDate() +  "_" +  gathering.getTime();

			try {
				Date gatheringDate = new SimpleDateFormat("dd.MM.yyyy_HH:mm").parse(gatheringDateString);


				if (Objects.requireNonNull(gatheringDate).after(now)) {
					return false;
				} else
					return true;
				
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return true;
		}

		public void getGatheringById(String gatheringId,
									 final FirebaseSuccess.OnGatheringSuccessListener firebaseSuccessListener) {
			gatheringCollection.document(gatheringId).get().addOnSuccessListener(documentSnapshot -> {
				Gathering gathering = documentSnapshot.toObject(Gathering.class);
				if (firebaseSuccessListener != null) {
					firebaseSuccessListener.onGatheringSuccess(gathering);
				}
			});
		}

		public void createGatheringField(String document,
										 Map<String, Object> gatheringObj,
										 final FirebaseSuccess.OnVoidSuccessListener firebaseSuccessListener) {
			gatheringCollection.document(document).set(gatheringObj, SetOptions.merge())
					.addOnSuccessListener(aVoid -> {
						if (firebaseSuccessListener != null) {
							firebaseSuccessListener.onVoidSuccess();
						}
					});
		}

		public void deleteGatheringField(String document,
										 final FirebaseSuccess.OnVoidSuccessListener firebaseSuccessListener) {
			gatheringCollection.document(document).delete().addOnSuccessListener(aVoid -> {
				if (firebaseSuccessListener != null) {
					firebaseSuccessListener.onVoidSuccess();
				}
			});
		}

		public void addArrayItemGatheringField(String document,
											   String arrayName,
											   String arrayItem,
											   final FirebaseSuccess.OnVoidSuccessListener firebaseSuccessListener) {
			gatheringCollection.document(document).update(arrayName, FieldValue.arrayUnion(arrayItem))
					.addOnSuccessListener(aVoid -> {
						addAmountPerson(document);
						if (firebaseSuccessListener != null) {
							firebaseSuccessListener.onVoidSuccess();
						}
					});
		}

		public void deleteArrayItemGatheringField(String document,
												  String arrayName,
												  String arrayItem,
												  final FirebaseSuccess.OnVoidSuccessListener firebaseSuccessListener) {
			gatheringCollection.document(document).update(arrayName, FieldValue.arrayRemove(arrayItem))
					.addOnSuccessListener(aVoid -> {
						subtractAmountPerson(document);
						if (firebaseSuccessListener != null) {
							firebaseSuccessListener.onVoidSuccess();
						}
					});
		}

		private void subtractAmountPerson(String document) {
			gatheringCollection.document(document).update("amountUsers", FieldValue.increment(-1));
		}

		private void addAmountPerson(String document) {
			gatheringCollection.document(document).update("amountUsers", FieldValue.increment(1));
		}

	}

	public static class PersonDB {

		private final CollectionReference personCollection;

		private static PersonDB instance;

		public static PersonDB getInstance() {
			if (instance == null)
				instance = new PersonDB();
			return instance;
		}

		public PersonDB() {
			new DataBase();
			personCollection = db.collection("Users");
		}

		public void getListUser(List<String> usersId, FirebaseSuccess.OnUsersSuccessListener onUsersSuccessListener) {
			List<User> users = new ArrayList<>();
			for (int i = 0; i < usersId.size(); i++) {
				personCollection.document(usersId.get(i)).get().addOnSuccessListener(documentSnapshot -> {
					Uri uri = Uri.parse(documentSnapshot.getString("imageLink"));

					User user = documentSnapshot.toObject(User.class);
					assert user != null;
					user.setUriProfileImage(uri);
					users.add(user);
					if (onUsersSuccessListener != null) {
						onUsersSuccessListener.onUsersSuccess(users);
					}
				});
			}
		}

		public void createPersonField(String document,
									  Map<String, Object> personObj,
									  final FirebaseSuccess.OnVoidSuccessListener firebaseSuccessListener) {
			personCollection.document(document).set(personObj, SetOptions.merge())
					.addOnSuccessListener(aVoid -> {
						if (firebaseSuccessListener != null) {
							firebaseSuccessListener.onVoidSuccess();
						}
					});
		}

		public void addArrayItemPersonField(String document,
											String arrayName,
											String arrayItem,
											final FirebaseSuccess.OnVoidSuccessListener firebaseSuccessListener) {
			personCollection.document(document).update(arrayName, FieldValue.arrayUnion(arrayItem))
					.addOnSuccessListener(aVoid -> {
						if (firebaseSuccessListener != null) {
							firebaseSuccessListener.onVoidSuccess();
						}
					});
		}

		public void deleteArrayGatheringPersonField(String document,
													String arrayItem,
													final FirebaseSuccess.OnVoidSuccessListener firebaseSuccessListener) {
			personCollection.document(document).update("gatherings", FieldValue.arrayRemove(arrayItem))
					.addOnSuccessListener(aVoid -> {
						if (firebaseSuccessListener != null) {
							firebaseSuccessListener.onVoidSuccess();
						}
					});

		}
	}
}