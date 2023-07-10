package com.boyd.myapplication.demo.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.boyd.myapplication.demo.data.repository.RegisterRepository;
import com.boyd.myapplication.demo.utils.OnTaskCompletedListener;

public class RegisterViewModel extends AndroidViewModel {
    private final RegisterRepository registerRepository;
    private final MutableLiveData<Boolean> registerSuccess;

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        registerRepository = new RegisterRepository();
        registerSuccess = new MutableLiveData<>();
    }

    public MutableLiveData<Boolean> getRegisterSuccess() {
        return registerSuccess;
    }

    public void registerUser(String username, String password) {
        registerRepository.checkUsernameIfExist(username, password, new OnTaskCompletedListener<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    registerSuccess.setValue(true);
                } else {
                    registerSuccess.setValue(false);
                }
            }

            @Override
            public void onError(String errorMessage) {
                //Later.
            }
        });
    }
}
