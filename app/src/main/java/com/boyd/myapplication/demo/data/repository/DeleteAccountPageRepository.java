package com.boyd.myapplication.demo.data.repository;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Map;
import java.util.Objects;

public class DeleteAccountPageRepository {
    private static final String USERS_COLLECTION = "users";
    private static final String POSTS_COLLECTION = "posts";
    private static final String COMMENT_COLLECTION = "comments";
    private static final String RELATION_COLLECTION = "relation";
    private final FirebaseFirestore firestore;
    private final FirebaseStorage storage;

    public DeleteAccountPageRepository() {
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public void deleteAccount(Map<String, String> userDetails) {
        CollectionReference usersCollection = firestore.collection(USERS_COLLECTION);
        CollectionReference postsCollection = firestore.collection(POSTS_COLLECTION);
        CollectionReference commentCollection = firestore.collection(COMMENT_COLLECTION);
        CollectionReference relationCollection = firestore.collection(RELATION_COLLECTION);

        //Delete the user from the user's collection.
        usersCollection.whereEqualTo("userId", userDetails.get("userId")).get().addOnSuccessListener(
                querySnapshot -> {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        document.getReference().delete();
                    }
                }
        );

        //Remove the user from the post's like list.
        postsCollection.get().addOnSuccessListener(
                querySnapshot -> {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : querySnapshot) {
                        queryDocumentSnapshot.getReference().update("likes", FieldValue.arrayRemove(userDetails.get("name")));
                    }
                }
        );

        //Remove all posts from the user associate with its image content.
        postsCollection.whereEqualTo("userId", userDetails.get("userId")).get().addOnSuccessListener(
                querySnapshot -> {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        document.getReference().delete();
                        storage.getReference("images/content/" + document.getString("documentId")).delete();
                    }
                }
        );

        //Remove all comments of the user and decrease the comments count of the corresponding posts.
        commentCollection.whereEqualTo("userId", userDetails.get("userId")).get().addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            queryDocumentSnapshot.getReference().delete();
                            postsCollection.whereEqualTo("documentId", queryDocumentSnapshot.getString("postId")).get().addOnSuccessListener(
                                    queryDocumentSnapshots -> {
                                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                            String documentIdGeneratedByFirestore = documentSnapshot.getId();
                                            DocumentReference documentRef = postsCollection.document(documentIdGeneratedByFirestore);
                                            documentRef.update("comments", FieldValue.increment(-1));
                                        }
                                    }
                            );
                        }
                    }
                }
        );

        //Remove user from any other user's relation.
        relationCollection.get().addOnSuccessListener(
                querySnapshot -> {
                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                        DocumentReference documentReference = documentSnapshot.getReference();
                        documentReference.update("follower", FieldValue.arrayRemove(userDetails));
                        documentReference.update("following", FieldValue.arrayRemove(userDetails));
                    }
                }
        );

        //Delete the user's profile picture.
        relationCollection.document(Objects.requireNonNull(userDetails.get("userId"))).delete().addOnSuccessListener(
                querySnapshot -> storage.getReference().child("images/user/" + userDetails.get("userId")).delete()
        );
    }
}
