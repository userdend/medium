package com.boyd.myapplication.demo.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.boyd.myapplication.R;
import com.boyd.myapplication.demo.data.model.Posts;
import com.boyd.myapplication.demo.utils.DisplayLikeListener;
import com.boyd.myapplication.demo.utils.DisplayMenuListener;
import com.boyd.myapplication.demo.utils.DisplayOfficialAccount;
import com.boyd.myapplication.demo.utils.DocumentListener;
import com.boyd.myapplication.demo.utils.OnClickUsernameListener;
import com.boyd.myapplication.demo.utils.ProfilePictureListener;
import com.boyd.myapplication.main.component.CommentPage;
import com.boyd.myapplication.util.DateConfiguration;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostsViewHolder> {
    private List<Posts> posts;
    private DocumentListener documentListener;
    private DisplayLikeListener displayLikeListener;
    private DisplayMenuListener displayMenuListener;
    private ProfilePictureListener profilePictureListener;
    private OnClickUsernameListener onClickUsernameListener;
    private DisplayOfficialAccount displayOfficialAccount;

    @SuppressLint("NotifyDataSetChanged")
    public void setPosts(List<Posts> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    public void setDocumentDeleteListener(DocumentListener documentListener) {
        this.documentListener = documentListener;
    }

    public void setDisplayLikeListener(DisplayLikeListener displayLikeListener) {
        this.displayLikeListener = displayLikeListener;
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

    @NonNull
    @Override
    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item_view, parent, false);
        return new PostsAdapter.PostsViewHolder(itemView).linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsViewHolder holder, int position) {
        Posts post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts != null ? posts.size() : 0;
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
        private final ImageView post_userimage;
        private final TextView post_username;
        private final TextView post_creationdate;
        private final TextView post_content;
        private final ImageView post_image;
        private final LinearLayout likes_comments_container;
        private final TextView post_likes;
        private final TextView post_comments;
        private final TextView button_like;
        private final TextView button_comment;
        private final TextView menu;
        private PostsAdapter postsAdapter;

        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);

            post_userimage = itemView.findViewById(R.id.post_userimage);
            post_username = itemView.findViewById(R.id.post_username);
            post_creationdate = itemView.findViewById(R.id.post_creationdate);
            post_content = itemView.findViewById(R.id.post_content);
            post_image = itemView.findViewById(R.id.post_image);
            likes_comments_container = itemView.findViewById(R.id.likes_comments_container);
            post_likes = itemView.findViewById(R.id.post_likes);
            post_comments = itemView.findViewById(R.id.post_comments);
            button_like = itemView.findViewById(R.id.button_like);
            button_comment = itemView.findViewById(R.id.button_comment);
            menu = itemView.findViewById(R.id.item_menu);
            menu.setOnClickListener(this);
        }

        public PostsViewHolder linkAdapter(PostsAdapter postsAdapter) {
            this.postsAdapter = postsAdapter;
            return this;
        }

        public void bind(Posts posts) {
            postsAdapter.displayMenuListener.onDisplayMenu(posts.getName(), menu);
            postsAdapter.profilePictureListener.onDisplayProfilePicture(posts.getUserImageUrl(), posts.getName(), post_userimage);

            postsAdapter.displayOfficialAccount.onDisplayOfficialAccount(posts.getName(), post_username);

            postsAdapter.onClickUsernameListener.onClickUsername(posts.getUserId(), posts.getName(), post_username);
            post_creationdate.setText(new DateConfiguration(posts.getCreationDate()).timeDifference());
            post_content.setText(posts.getContent());

            if (posts.getImageContentUrl() == null) {
                post_image.setVisibility(View.GONE);
            }

            Picasso.get().load(posts.getImageContentUrl()).into(post_image);

            if ((posts.getLikes() != null && posts.getLikes().size() > 0) || (posts.getComments() != null && posts.getComments() > 0)) {
                likes_comments_container.setVisibility(View.VISIBLE);
            }

            if (posts.getComments() != null && posts.getComments() > 0) {
                String grammar = posts.getComments() > 1 ? " comments" : " comment";
                String numberOfComments = posts.getComments() + grammar;
                post_comments.setText(numberOfComments);
            }

            button_comment.setOnClickListener(
                    v -> {
                        Intent intent = new Intent(v.getContext(), CommentPage.class);
                        intent.putExtra("postId", posts.getDocumentId());
                        intent.putExtra("posterName", posts.getName());
                        v.getContext().startActivity(intent);
                    }
            );

            List<String> listLikes = posts.getLikes() != null ? posts.getLikes() : new ArrayList<>();
            Long comments = posts.getComments() != null ? posts.getComments() : 0;
            postsAdapter.displayLikeListener.onDisplayLike(posts.getDocumentId(), listLikes, comments, button_like, post_likes, likes_comments_container);
        }

        @Override
        public void onClick(View v) {
            PopupMenu postsPopupMenu = new PopupMenu(v.getContext(), v);
            postsPopupMenu.inflate(R.menu.item_menu);
            postsPopupMenu.setOnMenuItemClickListener(this);
            postsPopupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == R.id.action_delete) {
                String document = postsAdapter.posts.get(getAdapterPosition()).getDocumentId();

                //Remove from the UI.
                postsAdapter.posts.remove(getAdapterPosition());
                postsAdapter.notifyItemRemoved(getAdapterPosition());

                //Remove from the database.
                postsAdapter.documentListener.onDocumentById(document, null);
                return true;
            }
            return false;
        }
    }
}
