/* Copyright 2020. Explore in HMS. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0

 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hms.referenceapp.hifood.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hms.referenceapp.hifood.R
import com.hms.referenceapp.hifood.adapter.OnSummaryClickListener
import com.hms.referenceapp.hifood.adapter.SummaryListAdapter
import com.hms.referenceapp.hifood.model.LogSummary
import com.hms.referenceapp.hifood.utils.CaloryCalculator
import com.hms.referenceapp.hifood.viewmodel.ProfileViewModel
import com.huawei.agconnect.auth.AGConnectAuth
import kotlinx.android.synthetic.main.profile_fragment.*

class ProfileFragment : Fragment(), OnSummaryClickListener {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private lateinit var viewModel: ProfileViewModel
    private val summaryLogListAdapter = SummaryListAdapter(arrayListOf(), this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        summaryRV.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = summaryLogListAdapter
        }

        observeViewModel()

        val user = AGConnectAuth.getInstance().currentUser

        viewModel.getRecipeSummariesById(user.uid)

        viewModel.getUserInfo(user.uid)
    }

    private fun observeViewModel() {
        viewModel.logSummaryList.observe(requireActivity(), Observer { logSummaries ->
            logSummaries?.let { summaryLogListAdapter.updateLogs(logSummaries) }
        })

        viewModel.userRecipeLogList.observe(requireActivity(), Observer {
            viewModel.getRecipeCalories()
        })

        viewModel.isRecipeCaloriesHandled.observe(requireActivity(), Observer {
            if (it) {
                val user = AGConnectAuth.getInstance().currentUser
                viewModel.getConvenienceSummariesById(user.uid)
            }
        })

        viewModel.isConvenienceCaloriesHandled.observe(requireActivity(), Observer {
            if (it) {
                viewModel.calculateSummary()
            }
        })

        viewModel.userConvenienceLogList.observe(requireActivity(), Observer {
            viewModel.getConvenienceCalories()
        })

        viewModel.userInfo.observe(requireActivity(), Observer {
            if (it != null) {
                neededCaloriesValueTV.text =
                    CaloryCalculator.calculateNeededCalories(it.gender, it.weight, it.height, 18)
                        .toString()
            }
        })
    }

    override fun onItemClick(item: LogSummary) {
        //val action = MainFragmentDirections.actionMainFragmentToRecipeDetailFragment(item.id)
        //findNavController().navigate(action)
    }
}