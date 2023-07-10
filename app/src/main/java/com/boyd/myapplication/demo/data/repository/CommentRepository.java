package com.boyd.myapplication.demo.data.repository;

import com.boyd.myapplication.demo.data.model.Comment;
import com.boyd.myapplication.demo.utils.OnTaskCompletedListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommentRepository {
    private static final String COMMENT_COLLECTION = "comments";
    private static final String POSTS_COLLECTION = "posts";
    private final FirebaseFirestore firestore;

    public CommentRepository() {
        firestore = FirebaseFirestore.getInstance();
    }

    public void getAllComments(String postId, OnTaskCompletedListener<List<Comment>> listener) {
        CollectionReference commentCollection = firestore.collection(COMMENT_COLLECTION);
        commentCollection.whereEqualTo("postId", postId).get().addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        List<Comment> comments = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            Comment comment = new Comment();
                            comment.setDocumentId(document.getString("documentId"));
                            comment.setPostId(document.getString("postId"));
                            comment.setUserId(document.getString("userId"));
                            comment.setUserImageUrl(document.getString("userImageUrl"));
                            comment.setName(document.getString("name"));
                            comment.setCreationDate(document.getString("creationDate"));
                            comment.setContent(document.getString("content"));
                            comments.add(comment);
                        }
                        listener.onSuccess(comments);
                    }
                }
        );
    }

    public Task<DocumentReference> addComment(Map<String, String> comment) {
        //Increase the number of comments in posts collection.
        CollectionReference postsCollection = firestore.collection(POSTS_COLLECTION);
        postsCollection.whereEqualTo("documentId", comment.get("postId")).get().addOnSuccessListener(
                queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        String documentIdGeneratedByFirestore = documentSnapshot.getId();
                        DocumentReference documentRef = postsCollection.document(documentIdGeneratedByFirestore);
                        documentRef.update("comments", FieldValue.increment(1));
                    }
                }
        );

        //Update the comment's collection.
        CollectionReference commentCollection = firestore.collection(COMMENT_COLLECTION);
        return commentCollection.add(comment);
    }

    public void deleteComments(String postId, String commentId) {
        //Update the post's collections.
        CollectionReference postsCollection = firestore.collection(POSTS_COLLECTION);
        postsCollection.whereEqualTo("documentId", postId).get().addOnSuccessListener(
                queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        String documentIdGeneratedByFirestore = documentSnapshot.getId();
                        DocumentReference documentRef = postsCollection.document(documentIdGeneratedByFirestore);
                        documentRef.update("comments", FieldValue.increment(-1));
                    }
                }
        );

        //Delete the comment from comment's collection.
        CollectionReference commentCollection = firestore.collection(COMMENT_COLLECTION);
        commentCollection.whereEqualTo("documentId", commentId).get().addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            queryDocumentSnapshot.getReference().delete();
                        }
                    }
                }
        );
    }
}
