package com.example.instaparse

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PostAdapter (val context: Context, val posts: MutableList<Post>)
    : RecyclerView.Adapter<PostAdapter.ViewHolder> () {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostAdapter.ViewHolder {
        // Dictates which layout file to use for the item
        val view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostAdapter.ViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUsernamePost : TextView
        val ivPost : ImageView
        val tvDescriptionPost : TextView
        init {
            tvUsernamePost = itemView.findViewById(R.id.tvUsernamePost)
            ivPost = itemView.findViewById(R.id.ivPost)
            tvDescriptionPost = itemView.findViewById(R.id.tvDiscriptionPost)
        }

        fun bind(post: Post) {
            tvUsernamePost.text = post.getUser()?.username
            tvDescriptionPost.text = post.getDescription()

            Glide.with(itemView.context).load(post.getImage()?.url).into(ivPost)
        }
    }

    fun clear() {
        posts.clear()
        notifyDataSetChanged()
    }
    fun addAll (postList: MutableList<Post>) {
        posts.addAll(postList)
        notifyDataSetChanged()
    }
}