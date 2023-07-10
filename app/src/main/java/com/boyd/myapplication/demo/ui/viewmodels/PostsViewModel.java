package com.boyd.myapplication.demo.ui.viewmodels;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.boyd.myapplication.demo.data.model.Posts;
import com.boyd.myapplication.demo.data.repository.PostsRepository;
import com.boyd.myapplication.demo.utils.OnTaskCompletedListener;

import java.util.List;
import java.util.Map;

public class PostsViewModel extends AndroidViewModel {
    private final PostsRepository postsRepository;
    private final MutableLiveData<List<Posts>> postsLiveData;

    public PostsViewModel(@NonNull Application application) {
        super(application);
        postsRepository = new PostsRepository();
        postsLiveData = new MutableLiveData<>();
    }

    public LiveData<List<Posts>> getAllPosts() {
        return postsLiveData;
    }

    public void fetchPosts(String appName, String userId, String username) {
        postsRepository.getAllPosts(appName, userId, username, new OnTaskCompletedListener<List<Posts>>() {
            @Override
            public void onSuccess(List<Posts> posts) {
                postsLiveData.setValue(posts);
            }

            @Override
            public void onError(String errorMessage) {
                Log.d("", "" + errorMessage);
            }
        });
    }

    public void addNewPostsWithImage(String documentId, Uri imageContentUri, Map<String, Object> newPostsDetails, OnTaskCompletedListener<Boolean> listener) {
        postsRepository.addNewPostsWithImage(documentId, imageContentUri, newPostsDetails, new OnTaskCompletedListener<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                listener.onSuccess(result);
            }

            @Override
            public void onError(String errorMessage) {
                //Later.
            }
        });
    }

    public void addNewPostsWithNoImage(Map<String, Object> newPostsDetails) {
        postsRepository.addNewPostsWithNoImage(newPostsDetails);
    }

    public void updatePostsLike(String action, String documentId, String username) {
        postsRepository.updatePostsLike(action, documentId, username);
    }

    public void deletePostsById(String postsId) {
        postsRepository.deletePosts(postsId);
    }
}
