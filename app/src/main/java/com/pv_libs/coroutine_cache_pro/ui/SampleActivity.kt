package com.pv_libs.coroutine_cache_pro.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.pv_libs.coroutine_cache_pro.R
import com.pv_libs.coroutine_cache_pro.databinding.ActivitySampleBinding
import com.pv_libs.coroutine_cache_pro.ui.adapters.UsersAdapter
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

class SampleActivity : AppCompatActivity() {

    private val viewModel: SampleActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataBinding =
            DataBindingUtil.setContentView<ActivitySampleBinding>(this, R.layout.activity_sample)

        val adapter = UsersAdapter()
        dataBinding.recyclerView.adapter = adapter

        Timber.d("onCreate")
        lifecycleScope.launchWhenCreated {
            viewModel.usersListFlow.collectLatest {
                Timber.d("usersListFlow - collectLatest - $it - ${it?.users?.size}")
                adapter.listItems = it?.users ?: ArrayList()
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.inApiRunningStateFlow.collectLatest {
                Timber.d("swipeRefreshLayout.isRefreshing - $it")
                dataBinding.swipeRefreshLayout.isRefreshing = it
            }
        }

        dataBinding.swipeRefreshLayout.setOnRefreshListener {
            Timber.d("swipeRefreshLayout.setOnRefreshListener")
            viewModel.fetchUsers()
        }

    }

}