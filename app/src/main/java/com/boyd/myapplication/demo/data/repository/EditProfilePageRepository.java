package com.boyd.myapplication.demo.data.repository;

import android.net.Uri;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfilePageRepository {
    private static final String USERS_COLLECTION = "users";
    private static final String POSTS_COLLECTION = "posts";
    private static final String COMMENT_COLLECTION = "comments";
    private final FirebaseFirestore firestore;

    public EditProfilePageRepository() {
        firestore = FirebaseFirestore.getInstance();
    }

    public void updateUsersCollection(Uri imageUri, String currentActiveUserId) {
        CollectionReference collectionReferenceUsers = firestore.collection(USERS_COLLECTION);
        collectionReferenceUsers.whereEqualTo("userId", currentActiveUserId).get().addOnSuccessListener(
                querySnapshot -> {
                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                        DocumentReference documentReference = collectionReferenceUsers.document(documentSnapshot.getId());
                        documentReference.update("userImageUrl", imageUri);
                    }
                }
        );
    }

    public void updatePostsCollection(Uri imageUri, String currentActiveUserId) {
        CollectionReference collectionReferencePosts = firestore.collection(POSTS_COLLECTION);
        collectionReferencePosts.whereEqualTo("userId", currentActiveUserId).get().addOnSuccessListener(
                querySnapshot -> {
                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                        DocumentReference documentReference = collectionReferencePosts.document(documentSnapshot.getId());
                        documentReference.update("userImageUrl", imageUri);
                    }
                }
        );
    }

    public void updateCommentCollection(Uri imageUri, String currentActiveUserId) {
        CollectionReference collectionReferenceComments = firestore.collection(COMMENT_COLLECTION);
        collectionReferenceComments.whereEqualTo("userId", currentActiveUserId).get().addOnSuccessListener(
                querySnapshot -> {
                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                        DocumentReference documentReference = collectionReferenceComments.document(documentSnapshot.getId());
                        documentReference.update("userImageUrl", imageUri);
                    }
                }
        );
    }
}
