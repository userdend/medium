package com.boyd.myapplication.demo.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.boyd.myapplication.demo.data.model.Relation;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfilePageViewModel extends AndroidViewModel {
    private final String RELATION_COLLECTION = "relation";
    private final FirebaseFirestore firestore;
    private final MutableLiveData<Relation> relations;

    public ProfilePageViewModel(@NonNull Application application) {
        super(application);
        firestore = FirebaseFirestore.getInstance();
        relations = new MutableLiveData<>();
    }

    public LiveData<Relation> getRelations() {
        return relations;
    }

    public void fetchRelations(String userId) {
        CollectionReference relationCollection = firestore.collection(RELATION_COLLECTION);
        relationCollection.document(userId).get().addOnSuccessListener(
                documentSnapshot -> {
                    Relation relation = new Relation();

                    relation.setFollower((List<HashMap<String, String>>) documentSnapshot.get("follower"));
                    relation.setFollowing((List<HashMap<String, String>>) documentSnapshot.get("following"));

                    relations.setValue(relation);
                }
        );
    }

    public void followUser(String activeUserId, String userId, Map<String, String> intoOrFromActiveUser, Map<String, String> intoOrFromUser) {
        firestore.collection(RELATION_COLLECTION).document(activeUserId).update("following", FieldValue.arrayUnion(intoOrFromActiveUser));
        firestore.collection(RELATION_COLLECTION).document(userId).update("follower", FieldValue.arrayUnion(intoOrFromUser));
        fetchRelations(userId);
    }

    public void unFollowUser(String activeUserId, String userId, Map<String, String> intoOrFromActiveUser, Map<String, String> intoOrFromUser) {
        firestore.collection(RELATION_COLLECTION).document(activeUserId).update("following", FieldValue.arrayRemove(intoOrFromActiveUser));
        firestore.collection(RELATION_COLLECTION).document(userId).update("follower", FieldValue.arrayRemove(intoOrFromUser));
        fetchRelations(userId);
    }
}
