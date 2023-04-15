package com.example.firebaseapp.ui.bookmark


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebaseapp.data.ArticleModel
import com.example.firebaseapp.databinding.ItemArticleBinding
import com.example.firebaseapp.ui.home.ArticleItem

class BookmarkArticleAdapter(val onItemClicked : (ArticleModel) -> Unit) : ListAdapter<ArticleModel, BookmarkArticleAdapter.ViewHolder>(diffUtil) {
    inner class ViewHolder(private val binding : ItemArticleBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(articleModel: ArticleModel){

            binding.descriptionTextView.text = articleModel.description

            Glide.with(binding.thumbnailImageView)
                .load(articleModel.imageUrl)
                .into(binding.thumbnailImageView)

            binding.root.setOnClickListener {
                onItemClicked(articleModel)
            }

            binding.bookmarkImageButton.isVisible = false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemArticleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }


    companion object{
        val diffUtil = object : DiffUtil.ItemCallback<ArticleModel>(){
            override fun areContentsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
                return oldItem.articleId == newItem.articleId
            }
        }
    }

}