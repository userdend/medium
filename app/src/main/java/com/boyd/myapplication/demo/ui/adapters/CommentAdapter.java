package com.boyd.myapplication.demo.ui.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.boyd.myapplication.R;
import com.boyd.myapplication.demo.data.model.Comment;
import com.boyd.myapplication.demo.utils.DisplayMenuListener;
import com.boyd.myapplication.demo.utils.DisplayOfficialAccount;
import com.boyd.myapplication.demo.utils.DisplayPostsOwner;
import com.boyd.myapplication.demo.utils.DocumentListener;
import com.boyd.myapplication.demo.utils.OnClickUsernameListener;
import com.boyd.myapplication.demo.utils.ProfilePictureListener;
import com.boyd.myapplication.util.DateConfiguration;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> comments;
    private DocumentListener documentListener;
    private DisplayMenuListener displayMenuListener;
    private ProfilePictureListener profilePictureListener;
    private OnClickUsernameListener onClickUsernameListener;
    private DisplayOfficialAccount displayOfficialAccount;
    private DisplayPostsOwner displayPostsOwner;

    @SuppressLint("NotifyDataSetChanged")
    public void setComments(List<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    public void setCommentDeleteListener(DocumentListener documentListener) {
        this.documentListener = documentListener;
    }

    public void setDisplayMenuListener(DisplayMenuListener displayMenuListener) {
        this.displayMenuListener = displayMenuListener;
    }

    public void setProfilePictureListener(ProfilePictureListener profilePictureListener) {
        this.profilePictureListener = profilePictureListener;
    }

    public void setOnClickUsernameListener(OnClickUsernameListener onClickUsernameListener) {
        this.onClickUsernameListener = onClickUsernameListener;
    }

    public void setDisplayOfficialAccount(DisplayOfficialAccount displayOfficialAccount) {
        this.displayOfficialAccount = displayOfficialAccount;
    }

    public void setDisplayPostsOwner(DisplayPostsOwner displayPostsOwner) {
        this.displayPostsOwner = displayPostsOwner;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_view, parent, false);
        return new CommentViewHolder(itemView).linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return comments != null ? comments.size() : 0;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
        private final ImageView comment_userimage;
        private final TextView comment_username;
        private final TextView comment_creationdate;
        private final TextView comment_content;
        private final TextView menu;
        private CommentAdapter commentAdapter;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            comment_userimage = itemView.findViewById(R.id.comment_userimage);
            comment_username = itemView.findViewById(R.id.comment_username);
            comment_creationdate = itemView.findViewById(R.id.comment_creationdate);
            comment_content = itemView.findViewById(R.id.comment_content);
            menu = itemView.findViewById(R.id.item_menu);
            menu.setOnClickListener(this);
        }

        public void bind(Comment comment) {
            commentAdapter.displayMenuListener.onDisplayMenu(comment.getName(), menu);
            commentAdapter.profilePictureListener.onDisplayProfilePicture(comment.getUserImageUrl(), comment.getName(), comment_userimage);
            commentAdapter.displayOfficialAccount.onDisplayOfficialAccount(comment.getName(), comment_username);
            commentAdapter.displayPostsOwner.onDisplayPostsOwner(comment.getName(), comment_username);
            commentAdapter.onClickUsernameListener.onClickUsername(comment.getUserId(), comment.getName(), comment_username);
            comment_creationdate.setText(new DateConfiguration(comment.getCreationDate()).timeDifference());
            comment_content.setText(comment.getContent());
        }

        public CommentViewHolder linkAdapter(CommentAdapter commentAdapter) {
            this.commentAdapter = commentAdapter;
            return this;
        }

        @Override
        public void onClick(View v) {
            PopupMenu commentPopupMenu = new PopupMenu(v.getContext(), v);
            commentPopupMenu.inflate(R.menu.item_menu);
            commentPopupMenu.setOnMenuItemClickListener(this);
            commentPopupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == R.id.action_delete) {
                String postId = commentAdapter.comments.get(getAdapterPosition()).getPostId();
                String commentId = commentAdapter.comments.get(getAdapterPosition()).getDocumentId();

                //Remove from the UI.
                commentAdapter.comments.remove(getAdapterPosition());
                commentAdapter.notifyItemRemoved(getAdapterPosition());

                //Remove from the database.
                commentAdapter.documentListener.onDocumentById(postId, commentId);
                return true;
            }
            return false;
        }
    }
}
