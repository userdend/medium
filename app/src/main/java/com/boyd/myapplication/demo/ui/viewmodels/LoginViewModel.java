package com.boyd.myapplication.demo.ui.viewmodels;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.boyd.myapplication.demo.data.model.User;
import com.boyd.myapplication.util.SessionManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

public class LoginViewModel extends ViewModel {
    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();

    public LiveData<Boolean> getLoginSuccess() {
        return loginSuccess;
    }

    public void login(Context context, String username, String password) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = firestore.collection("users");
        usersCollection.whereEqualTo("name", username).get().addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        boolean loginSuccessful = false;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (Objects.equals(document.getString("password"), password)) {
                                loginSuccessful = true;
                                User user = new User(0, document.getId(), document.getString("name"), document.getString("userId"), document.getString("userImageUrl"));
                                new SessionManager(context).saveSession(user);
                                break;
                            }
                        }

                        if (loginSuccessful) {
                            loginSuccess.setValue(true);
                        } else {
                            loginSuccess.setValue(false);
                            Toast.makeText(context, "Incorrect Username or Password.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Incorrect Username or Password.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}
