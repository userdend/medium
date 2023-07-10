package com.boyd.myapplication.demo.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.boyd.myapplication.demo.data.model.Relation;
import com.boyd.myapplication.demo.data.repository.RelationRepository;
import com.boyd.myapplication.demo.utils.OnTaskCompletedListener;

public class RelationViewModel extends AndroidViewModel {
    private final RelationRepository relationRepository;
    private final MutableLiveData<com.boyd.myapplication.demo.data.model.Relation> relationMutableLiveData;

    public RelationViewModel(@NonNull Application application) {
        super(application);
        relationRepository = new RelationRepository();
        relationMutableLiveData = new MutableLiveData<>();
    }

    public LiveData<com.boyd.myapplication.demo.data.model.Relation> getRelationMutableLiveData() {
        return relationMutableLiveData;
    }

    public void fetchRelation(String userid) {
        relationRepository.getUserRelation(userid, new OnTaskCompletedListener<Relation>() {
            @Override
            public void onSuccess(com.boyd.myapplication.demo.data.model.Relation result) {
                relationMutableLiveData.setValue(result);
            }

            @Override
            public void onError(String errorMessage) {
                //Later.
            }
        });
    }
}
