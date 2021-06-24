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

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.hms.referenceapp.hifood.R
import com.hms.referenceapp.hifood.viewmodel.CreateAccountViewModel
import com.huawei.agconnect.auth.AGConnectAuth
import kotlinx.android.synthetic.main.create_account_fragment.*

const val TAG: String = "CreateAccountFragment"
const val USER_ID = "UserId"

class CreateAccountFragment : Fragment() {


    companion object {
        fun newInstance() = CreateAccountFragment()
    }

    private lateinit var viewModel: CreateAccountViewModel
    private var userId: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.create_account_fragment, container, false)
    }

    val args: CreateAccountFragmentArgs by navArgs()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CreateAccountViewModel::class.java)


        userId = if (args.id != null) {
            args.id
        } else {
            ""
        }

        save_btn.setOnClickListener {
            viewModel.registerUser(
                userId!!,
                gender_spinner.selectedItem as String,
                weight_et.text.toString().toInt(),
                height_et.text.toString().toInt()
            )
        }

        viewModel.isUserRegistered.observe(viewLifecycleOwner, Observer {
            when (it) {
                true -> {
                    val user = AGConnectAuth.getInstance().currentUser

                    val intent = Intent(context, MainActivity::class.java).apply {
                        putExtra(USER_ID, user.uid)
                    }
                    startActivity(intent)
                }
                else -> Log.e(TAG, "User is not registered yet")
            }
        })
    }
}