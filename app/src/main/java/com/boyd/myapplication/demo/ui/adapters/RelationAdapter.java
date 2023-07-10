package com.boyd.myapplication.demo.ui.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.boyd.myapplication.R;
import com.boyd.myapplication.demo.utils.OnClickUsernameListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class RelationAdapter extends RecyclerView.Adapter<RelationAdapter.RelationViewHolder> {
    private List<HashMap<String, String>> relations;
    private OnClickUsernameListener onClickUsernameListener;

    @SuppressLint("NotifyDataSetChanged")
    public void setRelations(List<HashMap<String, String>> relations) {
        this.relations = relations;
        notifyDataSetChanged();
    }

    public void setOnClickUsernameListener(OnClickUsernameListener onClickUsernameListener) {
        this.onClickUsernameListener = onClickUsernameListener;
    }

    @NonNull
    @Override
    public RelationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item_view, parent, false);
        return new RelationAdapter.RelationViewHolder(itemView).linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull RelationViewHolder holder, int position) {
        //Relation.User user = relations.get(position);
        HashMap<String, String> relation = relations.get(position);
//        holder.bind(user);
        holder.bind(relation);
    }

    @Override
    public int getItemCount() {
        return relations != null ? relations.size() : 0;
    }

    public static class RelationViewHolder extends RecyclerView.ViewHolder {
        private final ImageView contact_userimage;
        private final TextView contact_username;
        private RelationAdapter relationAdapter;

        public RelationViewHolder(@NonNull View itemView) {
            super(itemView);
            contact_userimage = itemView.findViewById(R.id.contact_userimage);
            contact_username = itemView.findViewById(R.id.contact_username);
        }

        public void bind(HashMap<String, String> relation) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/user/" + relation.get("userId"));
            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                if (uri != null) {
                    contact_userimage.setBackgroundResource(0);
                }
                Picasso.get().load(uri).into(contact_userimage);
            });
            contact_username.setText(relation.get("name"));
            relationAdapter.onClickUsernameListener.onClickUsername(relation.get("userId"), relation.get("name"), contact_username);
            contact_username.setText(relation.get("name"));
        }

        public RelationViewHolder linkAdapter(RelationAdapter relationAdapter) {
            this.relationAdapter = relationAdapter;
            return this;
        }
    }
}
