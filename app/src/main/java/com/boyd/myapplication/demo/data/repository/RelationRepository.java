package com.boyd.myapplication.demo.data.repository;

import com.boyd.myapplication.demo.data.model.Relation;
import com.boyd.myapplication.demo.utils.OnTaskCompletedListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;

public class RelationRepository {
    private static final String RELATION_COLLECTION = "relation";
    private final FirebaseFirestore firestore;

    public RelationRepository() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    public void getUserRelation(String userid, OnTaskCompletedListener<com.boyd.myapplication.demo.data.model.Relation> listener) {
        CollectionReference relationCollection = firestore.collection(RELATION_COLLECTION);
        relationCollection.document(userid).get().addOnSuccessListener(
                documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Relation relation = new Relation();

                        relation.setFollower((List<HashMap<String, String>>) documentSnapshot.get("follower"));
                        relation.setFollowing((List<HashMap<String, String>>) documentSnapshot.get("following"));

                        listener.onSuccess(relation);
                    }
                }
        );
    }
}
