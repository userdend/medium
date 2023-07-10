package com.boyd.myapplication.demo.data.model;

import androidx.annotation.Keep;

import java.util.HashMap;
import java.util.List;

public class Relation {
    private List<HashMap<String, String>> follower;
    private List<HashMap<String, String>> following;

    public Relation() {
    }

    public List<HashMap<String, String>> getFollower() {
        return follower;
    }

    @Keep
    public void setFollower(List<HashMap<String, String>> follower) {
        this.follower = follower;
    }

    public List<HashMap<String, String>> getFollowing() {
        return following;
    }

    @Keep
    public void setFollowing(List<HashMap<String, String>> following) {
        this.following = following;
    }
}
