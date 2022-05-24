package dev.ogabek.kotlin.manager

import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dev.ogabek.kotlin.activity.EditActivity
import dev.ogabek.kotlin.model.Post

class DatabaseManager {

    companion object {

        private val database = FirebaseDatabase.getInstance().reference
        private val reference = database.child("posts")

        fun storePost(post: Post, handler: DatabaseHandler) {
            val key = reference.push().key ?: return
            post.id = key
            reference.child(key).setValue(post).addOnSuccessListener {
                handler.onSuccess()
            }.addOnFailureListener {
                handler.onError()
            }
        }

        fun apiLoadPosts(handler: DatabaseHandler) {
            reference.addValueEventListener(object : ValueEventListener {
                val posts: ArrayList<Post> = ArrayList()
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        posts.clear()
                        for (i in snapshot.children) {
                            val post: Post? = i.getValue(Post::class.java)
                            post.let {
                                posts.add(post!!)
                            }
                        }
                        handler.onSuccess(posts = posts)
                    } else {
                        handler.onSuccess(posts = ArrayList())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    handler.onError()
                }

            })
        }

        fun update(post: Post, handler: DatabaseHandler) {
            reference.child(post.id!!).setValue(post).addOnCompleteListener {
                if (it.isSuccessful) {
                    handler.onSuccess()
                } else {
                    handler.onError()
                }
            }

        }

    }

}