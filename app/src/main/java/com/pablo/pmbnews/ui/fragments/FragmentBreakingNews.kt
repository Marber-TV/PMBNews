package com.pablo.pmbnews.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pablo.pmbnews.R
import com.pablo.pmbnews.adapters.NewsAdapter
import com.pablo.pmbnews.adapters.PaginationScrollListener
import com.pablo.pmbnews.bbdd.Article
import com.pablo.pmbnews.bbdd.NewsDatabase
import com.pablo.pmbnews.bbdd.NewsRepository
import com.pablo.pmbnews.bbdd.NewsViewModel
import com.pablo.pmbnews.bbdd.NewsViewModelFactory
import com.pablo.pmbnews.bbdd.Resource
import com.pablo.pmbnews.databinding.FragmentBreakingNewsBinding

class FragmentBreakingNews : Fragment() {

    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    private var _binding: FragmentBreakingNewsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBreakingNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupViewModel()
        viewModel.fetchNews()
        observeViewModel()
        newsAdapter.notifyDataSetChanged()
    }

    private var isLoading = false
    private var isLastPage = false
    private val pageSize = 20
    private var currentPage = 1

    private fun setupRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.recycler.layoutManager = linearLayoutManager
        newsAdapter = NewsAdapter(emptyList(),
            { article -> openArticleDetails(article) }
        ) { article ->
            viewModel.alreadyFav(article.title).observe(viewLifecycleOwner) { exists ->
                if (!exists) {
                    viewModel.insert(article)
                    Toast.makeText(
                        requireContext(),
                        "Artículo agregado a favoritos",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Este artículo ya está en favoritos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.recycler.adapter = newsAdapter
        binding.recycler.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                currentPage += 1
                viewModel.fetchNews(currentPage.toString())
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }
        })
    }


    private fun openArticleDetails(article: Article) {
        val fragment = NewsDetailFragment.newInstance(article)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, fragment).addToBackStack(null).commit()
    }

    private fun setupViewModel() {
        val application = requireActivity().application
        val newsDao = NewsDatabase.getDatabase(requireContext()).newsDao()
        val repository = NewsRepository(newsDao)
        val factory = NewsViewModelFactory(application, repository)
        viewModel = ViewModelProvider(this, factory).get(NewsViewModel::class.java)
    }


    private fun observeViewModel() {
        viewModel.newsState.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    isLoading = false
                    binding.paginationProgressBar.visibility = View.GONE
                    resource.data?.let { articles ->
                        val totalArticles = articles.size
                        isLastPage = totalArticles < pageSize
                        newsAdapter.updateData(articles)
                    }
                }

                Resource.Status.ERROR -> {
                    isLoading = false
                    binding.paginationProgressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }

                Resource.Status.LOADING -> {
                    binding.paginationProgressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}