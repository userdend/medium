package com.boyd.myapplication.demo.ui.viewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.boyd.myapplication.demo.data.repository.EditProfilePageRepository;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EditProfilePageViewModel extends AndroidViewModel {
    private final EditProfilePageRepository editProfilePageRepository;
    MutableLiveData<Uri> imageUri;

    public EditProfilePageViewModel(@NonNull Application application) {
        super(application);
        editProfilePageRepository = new EditProfilePageRepository();
        imageUri = new MutableLiveData<>();
    }

    public MutableLiveData<Uri> getImageUri() {
        return imageUri;
    }

    public void addImageUri(Uri imageUri, String currentActiveUserId) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/user/" + currentActiveUserId);
        UploadTask uploadTask = storageReference.putFile(imageUri);
        uploadTask.addOnSuccessListener(
                snapshot -> storageReference.getDownloadUrl().addOnSuccessListener(
                        uri -> {
                            setImageUri(currentActiveUserId);
                            editProfilePageRepository.updateUsersCollection(uri, currentActiveUserId);
                            editProfilePageRepository.updatePostsCollection(uri, currentActiveUserId);
                            editProfilePageRepository.updateCommentCollection(uri, currentActiveUserId);
                        }
                )
        );
    }

    public void setImageUri(String currentActiveUserId) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/user/" + currentActiveUserId);
        storageReference.getDownloadUrl().addOnSuccessListener(
                uri -> imageUri.setValue(uri)
        );
    }
}
