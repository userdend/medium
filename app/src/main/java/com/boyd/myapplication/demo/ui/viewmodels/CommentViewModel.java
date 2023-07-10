package com.boyd.myapplication.demo.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.boyd.myapplication.demo.data.model.Comment;
import com.boyd.myapplication.demo.data.repository.CommentRepository;
import com.boyd.myapplication.demo.utils.OnTaskCompletedListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommentViewModel extends AndroidViewModel {
    private final CommentRepository commentRepository;
    private final MutableLiveData<List<Comment>> commentsLiveData;

    public CommentViewModel(@NonNull Application application) {
        super(application);
        commentRepository = new CommentRepository();
        commentsLiveData = new MutableLiveData<>();
    }

    public LiveData<List<Comment>> getAllComments() {
        return commentsLiveData;
    }

    public void fetchComments(String postId) {
        commentRepository.getAllComments(postId, new OnTaskCompletedListener<List<Comment>>() {
            @Override
            public void onSuccess(List<Comment> comments) {
                commentsLiveData.setValue(comments);
            }

            @Override
            public void onError(String errorMessage) {
                //Later.
            }
        });
    }

    public void updateCommentLiveData(Comment comment) {
        List<Comment> commentList = commentsLiveData.getValue() == null ? new ArrayList<>() : commentsLiveData.getValue();
        commentList.add(comment);
        commentsLiveData.setValue(commentList);
    }

    public Task<DocumentReference> addComment(Map<String, String> comment) {
        return commentRepository.addComment(comment);
    }

    public void deleteCommentById(String postId, String commentId) {
        commentRepository.deleteComments(postId, commentId);
    }
}
