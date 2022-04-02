package com.example.instaparse.fragments

import android.util.Log
import com.example.instaparse.MainActivity
import com.example.instaparse.Post
import com.parse.ParseQuery
import com.parse.ParseUser

class ProfileFragment: HomeFragment() {
    override fun queryPosts() {

        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        allPosts.clear()
        // Find all Post Objects
        query.include(Post.KEY_USER)
        // Find only posts from curr user
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser())
        // Sort with most recent post at top
        query.addDescendingOrder("createdAt")
        // Limit to top 20 Posts
        query.limit = 20
        query.findInBackground { posts, e ->
            if (e != null) {
                //something went wrong
                Log.e(MainActivity.TAG, "Error fetching posts")
            } else {
                if (posts != null) {
                    for (post in posts) {
                        Log.i(
                            MainActivity.TAG,
                            "Post: " + post.getDescription() + ", username: " + post.getUser()
                        )
                    }
                    allPosts.addAll(posts)
                }

                adapter.notifyDataSetChanged()
            }
        }
    }
}