package com.pablo.pmbnews.ui.fragments

import com.pablo.pmbnews.adapters.NewsAdapterFav
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pablo.pmbnews.R
import com.pablo.pmbnews.bbdd.NewsDatabase
import com.pablo.pmbnews.bbdd.NewsRepository
import com.pablo.pmbnews.bbdd.NewsViewModel
import com.pablo.pmbnews.bbdd.NewsViewModelFactory
import com.pablo.pmbnews.databinding.FragmentSavedNewsBinding

class FragmentSavedNews : Fragment() {

    private var _binding: FragmentSavedNewsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSavedNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadFavoriteNews()
    }

    private fun setupRecyclerView() {
        binding.favoritesRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.favoritesRecyclerView.adapter = NewsAdapterFav(emptyList()) { article ->
                val fragment = NewsDetailFragment.newInstance(article)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, fragment).addToBackStack(null).commit()

        }
    }

    private fun loadFavoriteNews() {
        val application = requireActivity().application
        val newsDao = NewsDatabase.getDatabase(requireContext()).newsDao()
        val repository = NewsRepository(newsDao)
        val factory = NewsViewModelFactory(application, repository)
        val viewModel = ViewModelProvider(this, factory)[NewsViewModel::class.java]
        viewModel.allFav.observe(viewLifecycleOwner) { articles ->
            if (binding.favoritesRecyclerView.adapter is NewsAdapterFav) {
                (binding.favoritesRecyclerView.adapter as NewsAdapterFav).updateData(articles)
            } else {
                binding.favoritesRecyclerView.adapter = NewsAdapterFav(articles) { article ->
                    val fragment = NewsDetailFragment.newInstance(article)
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_view, fragment).addToBackStack(null)
                        .commit()
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
