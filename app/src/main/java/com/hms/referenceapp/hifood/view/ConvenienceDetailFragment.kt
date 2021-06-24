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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.hms.referenceapp.hifood.R
import com.hms.referenceapp.hifood.viewmodel.ConvenienceDetailViewModel
import com.huawei.agconnect.auth.AGConnectAuth
import kotlinx.android.synthetic.main.convenience_detail_fragment.*

class ConvenienceDetailFragment : Fragment() {

    val args: ConvenienceDetailFragmentArgs by navArgs()

    companion object {
        fun newInstance() = ConvenienceDetailFragment()
    }

    private lateinit var viewModel: ConvenienceDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.convenience_detail_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ConvenienceDetailViewModel::class.java)

        val convenienceId = args.id

        convenienceId?.let { viewModel.getConvenience(it) }

        observeViewModel()

        val user = AGConnectAuth.getInstance().currentUser

        addListBtn.setOnClickListener {
            viewModel.recordConvenience(user.uid)
        }
    }

    private fun observeViewModel() {
        viewModel.scannedConvenience.observe(viewLifecycleOwner, Observer {
            it?.let {
                caloriesValueTV.text = it.calories.toString()
                carbohydratesValueTV.text = it.carbohydrate.toString()
                proteinValueTV.text = it.protein.toString()
                fatValueTV.text = it.fat.toString()
            }
        })

        viewModel.isConvenienceRecorded.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    Toast.makeText(
                        requireContext(),
                        "Food is added to your diary.",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
        })
    }

}