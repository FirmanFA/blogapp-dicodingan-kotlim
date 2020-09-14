package com.dicodingan.dicodingan

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.dicodingan.dicodingan.model.Post
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target
import java.lang.Exception


class PostAdapter(
    private val context: Context, private val posts: List<Post>,
    private val postClick: (Post) -> Unit
):RecyclerView.Adapter<PostAdapter.Holder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =LayoutInflater.from(context).inflate(R.layout.post_list_layout, parent, false)
        return Holder(view, postClick)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindPost(posts[position], context)
    }

    override fun getItemCount(): Int {
        return posts.count()
    }

    class Holder(itemView: View?, val postClick: (Post) -> Unit): RecyclerView.ViewHolder(itemView!!) {
        val cardViewImage = itemView?.findViewById<CardView>(R.id.cardViewImage)
        private val titleTxt = itemView?.findViewById<TextView>(R.id.titleTxt)
        private val cardViewClick = itemView?.findViewById<CardView>(R.id.cardViewClick)

        fun bindPost(post: Post, context: Context){
            Picasso.get().load(post.thumbnail).into(object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap?, from: LoadedFrom?) {
                    cardViewImage?.background = BitmapDrawable(context.resources,bitmap)

                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {

                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                }

            })
            titleTxt?.text = post.title
            cardViewClick?.setOnClickListener { postClick(post) }

        }
    }



}