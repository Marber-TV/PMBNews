package com.pablo.pmbnews.ui.fragments

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.pablo.pmbnews.R
import com.pablo.pmbnews.adapters.CommentsAdapter
import com.pablo.pmbnews.bbdd.Article
import com.pablo.pmbnews.bbdd.Comment
import com.pablo.pmbnews.bbdd.NewsDatabase
import com.pablo.pmbnews.bbdd.NewsRepository
import com.pablo.pmbnews.bbdd.NewsViewModel
import com.pablo.pmbnews.bbdd.NewsViewModelFactory
import com.pablo.pmbnews.databinding.FragmentNewsDetailsBinding
import com.pablo.pmbnews.ui.Inicio
import java.util.Date


class NewsDetailFragment : Fragment() {

    private var _binding: FragmentNewsDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: NewsViewModel
    companion object {
        private const val ARG_ARTICLE = "article"

        fun newInstance(article: Article): NewsDetailFragment {
            val args = Bundle().apply {
                putSerializable(ARG_ARTICLE, article)
            }
            return NewsDetailFragment().apply {
                arguments = args
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentNewsDetailsBinding.inflate(inflater, container, false)
        val factory = NewsViewModelFactory(requireActivity().application, NewsRepository(NewsDatabase.getDatabase(requireContext()).newsDao()))
        viewModel = ViewModelProvider(this, factory)[NewsViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? Inicio)?.hideBottomNav()
        val article = arguments?.getSerializable(ARG_ARTICLE) as Article?
        article?.let {
            setupUI(it)
            setupObservers(it.title)
        }

        binding.ivArrow.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.fab.setOnClickListener {
            val article = arguments?.getSerializable(ARG_ARTICLE) as Article
            viewModel.alreadyFav(article.title).observe(viewLifecycleOwner, Observer { exists ->
                if (!exists) {
                    viewModel.insert(article)
                    binding.fab.imageTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.red));
                    Toast.makeText(context, "Añadido a favoritos", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Ya esta en tus favoritos", Toast.LENGTH_SHORT).show()
                }
            })
        }
        binding.readMoreButton.setOnClickListener {
            article?.url?.let { url ->
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                startActivity(intent)
            }
        }

    }

    private fun setupUI(article: Article) {
        val article = arguments?.getSerializable(ARG_ARTICLE) as Article
        with(binding) {
            tvTitle.text = article.title
            tvDescription.text = article.description
            tvPublishedAt.text = article.publishedAt
            tvSource.text = article.source?.name ?: ""
            Glide.with(articleImage.context).load(article.urlToImage).error(R.drawable._404).into(articleImage)
            commentsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            commentsRecyclerView.adapter = CommentsAdapter(emptyList())

            submitCommentButton.setOnClickListener {
                val commentContent = commentInput.text.toString()
                if (commentContent.isNotEmpty()) {
                    val user = FirebaseAuth.getInstance().currentUser
                    val username = user?.displayName ?: run {
                        val email = user?.email
                        email?.substringBefore('@') ?: "Anonymous"
                    }
                    val newComment = Comment(articleTitle = article.title, username = username, content = commentContent, timestamp = Date())
                    viewModel.addComment(newComment)
                    commentInput.setText("")
                } else {
                    Toast.makeText(context, "Comentario vacio", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupObservers(articleTitle: String) {
        val article = arguments?.getSerializable(ARG_ARTICLE) as Article
        viewModel.alreadyFav(article.title).observe(viewLifecycleOwner, Observer { exists ->
            if (exists) {
                binding.fab.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.red));
            }

        })



        viewModel.loadCommentsForArticle(articleTitle).observe(viewLifecycleOwner, Observer { comments ->
            (binding.commentsRecyclerView.adapter as CommentsAdapter).updateData(comments)
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? Inicio)?.showBottomNav()
        _binding = null
    }
}
