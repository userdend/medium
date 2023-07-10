package com.boyd.myapplication.demo.data.repository;

import com.boyd.myapplication.demo.utils.OnTaskCompletedListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegisterRepository {
    private static final String USER_COLLECTION = "users";
    private static final String RELATIONS_COLLECTION = "relation";
    private final FirebaseFirestore firestore;

    public RegisterRepository() {
        firestore = FirebaseFirestore.getInstance();
    }

    public void checkUsernameIfExist(String username, String password, OnTaskCompletedListener<Boolean> result) {
        CollectionReference usersCollection = firestore.collection(USER_COLLECTION);
        CollectionReference relationCollection = firestore.collection(RELATIONS_COLLECTION);
        usersCollection.whereEqualTo("name", username).get().addOnSuccessListener(
                querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        Map<String, String> newUserDetails = new HashMap<>();
                        String userId = UUID.randomUUID().toString();
                        newUserDetails.put("userId", userId);
                        newUserDetails.put("name", username);
                        newUserDetails.put("password", password);

                        usersCollection.add(newUserDetails).addOnSuccessListener(
                                documentReference -> relationCollection.document(userId).set(new HashMap<>())
                        );

                        result.onSuccess(true);
                    } else {
                        result.onSuccess(false);
                    }
                }
        );
    }
}
