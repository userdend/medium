package com.boyd.myapplication.demo.data.repository;

import android.net.Uri;

import com.boyd.myapplication.demo.data.model.Posts;
import com.boyd.myapplication.demo.utils.OnTaskCompletedListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostsRepository {
    private static final String POSTS_COLLECTION = "posts";
    private static final String COMMENTS_COLLECTION = "comments";
    private static final String RELATION_COLLECTION = "relation";
    private final FirebaseFirestore firestore;

    public PostsRepository() {
        firestore = FirebaseFirestore.getInstance();
    }

    public void getAllPosts(String appName, String userId, String username, OnTaskCompletedListener<List<Posts>> listener) {
        CollectionReference postsCollection = firestore.collection(POSTS_COLLECTION);
        CollectionReference relationCollection = firestore.collection(RELATION_COLLECTION);
        relationCollection.document(userId).get().addOnSuccessListener(
                documentSnapshot -> {
                    List<String> listFollowingName = new ArrayList<>();
                    List<HashMap<String, String>> following = (List<HashMap<String, String>>) documentSnapshot.get("following");

                    if (following != null) {
                        for (HashMap<String, String> user : following) {
                            listFollowingName.add(user.get("name"));
                        }
                    }

                    listFollowingName.add(username);
                    listFollowingName.add(appName);

                    Task<QuerySnapshot> queryAll = postsCollection.get();
                    Task<QuerySnapshot> queryFromFollowingList = postsCollection.whereIn("name", listFollowingName).get();

                    (username.equals(appName) ? queryAll : queryFromFollowingList).addOnCompleteListener(
                            task -> {
                                if (task.isSuccessful()) {

                                    List<Posts> posts = new ArrayList<>();
                                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                        Posts post = new Posts();
                                        post.setDocumentId(document.getString("documentId"));
                                        post.setUserId(document.getString("userId"));
                                        post.setUserImageUrl(document.getString("userImageUrl"));
                                        post.setName(document.getString("name"));
                                        post.setCreationDate(document.getString("creationDate"));
                                        post.setContent(document.getString("content"));
                                        post.setImageContentUrl(document.getString("imageContentUrl"));
                                        post.setLikes((List<String>) document.get("likes"));
                                        post.setComments((Long) document.get("comments"));
                                        posts.add(post);
                                    }

                                    listFollowingName.clear();
                                    listener.onSuccess(posts);
                                }
                            }
                    );
                }
        ).addOnFailureListener(e -> listener.onError(e.getMessage()));
    }

    public void addNewPostsWithImage(String documentId, Uri imageContentUri, Map<String, Object> newPostsDetails, OnTaskCompletedListener<Boolean> listener) {
        CollectionReference postsCollection = firestore.collection(POSTS_COLLECTION);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/content/" + documentId);
        UploadTask uploadTask = storageReference.putFile(imageContentUri);
        uploadTask.addOnSuccessListener(
                snapshot ->
                        storageReference.getDownloadUrl().addOnSuccessListener(
                                uri -> {
                                    newPostsDetails.put("imageContentUrl", uri);
                                    postsCollection.add(newPostsDetails);
                                    listener.onSuccess(true);
                                }
                        )
        );
    }

    public void addNewPostsWithNoImage(Map<String, Object> newPostsDetails) {
        CollectionReference postsCollection = firestore.collection(POSTS_COLLECTION);
        postsCollection.add(newPostsDetails);
    }

    public void deletePosts(String postsId) {
        CollectionReference postsReference = firestore.collection(POSTS_COLLECTION);
        CollectionReference commentsCollection = firestore.collection(COMMENTS_COLLECTION);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/content/" + postsId);
        storageReference.delete();

        postsReference.whereEqualTo("documentId", postsId).get().addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            queryDocumentSnapshot.getReference().delete();
                        }
                    }
                }
        );

        commentsCollection.whereEqualTo("postId", postsId).get().addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            queryDocumentSnapshot.getReference().delete();
                        }
                    }
                }
        );
    }

    public void updatePostsLike(String action, String documentId, String username) {
        CollectionReference postsReference = firestore.collection(POSTS_COLLECTION);
        postsReference.whereEqualTo("documentId", documentId).get().addOnSuccessListener(
                querySnapshot -> {
                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                        DocumentReference documentReference = postsReference.document(documentSnapshot.getId());
                        switch (action) {
                            case "LIKE":
                                documentReference.update("likes", FieldValue.arrayUnion(username));
                                break;

                            case "UNLIKE":
                                documentReference.update("likes", FieldValue.arrayRemove(username));
                                break;

                            default:
                                break;
                        }
                    }
                }
        );
    }
}
