package com.boyd.myapplication.demo.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.boyd.myapplication.demo.data.repository.DeleteAccountPageRepository;

import java.util.Map;

public class DeleteAccountPageViewModel extends AndroidViewModel {
    private final DeleteAccountPageRepository deleteAccountPageRepository;
    private final MutableLiveData<Boolean> isDeleted;

    public DeleteAccountPageViewModel(@NonNull Application application) {
        super(application);
        deleteAccountPageRepository = new DeleteAccountPageRepository();
        isDeleted = new MutableLiveData<>();
    }

    public MutableLiveData<Boolean> getIsDeleted() {
        return isDeleted;
    }

    public void onDelete(Map<String, String> userDetails) {
        deleteAccountPageRepository.deleteAccount(userDetails);
        isDeleted.setValue(true);
    }
}
