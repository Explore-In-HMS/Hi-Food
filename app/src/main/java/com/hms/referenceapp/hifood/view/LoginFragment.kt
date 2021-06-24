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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.hms.referenceapp.hifood.R
import com.hms.referenceapp.hifood.viewmodel.LoginViewModel
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.agconnect.auth.AGConnectAuthCredential
import com.huawei.agconnect.auth.HwIdAuthProvider
import com.huawei.agconnect.auth.SignInResult
import com.huawei.agconnect.config.AGConnectServicesConfig
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.api.entity.auth.Scope
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper
import com.huawei.hms.support.hwid.result.AuthHuaweiId
import kotlinx.android.synthetic.main.login_fragment.*
import java.util.*

class LoginFragment : Fragment() {

    private val REQUEST_SIGN_IN_LOGIN = 1002
    private val HUAWEI_ID_SIGNIN = 1002
    private var uId: String? = null
    private val TAG: String = "LoginFragment"

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel
    var huaweiAccount: AuthHuaweiId? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        val appID = AGConnectServicesConfig.fromContext(activity).getString("102771413")
        val user = AGConnectAuth.getInstance().currentUser

        anonymousLogin()

        hwIdLogin.setOnClickListener {
            val huaweiIdAuthParamsHelper =
                HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
            val scopeList: MutableList<Scope> =
                ArrayList()
            huaweiIdAuthParamsHelper.setScopeList(scopeList)
            val authParams =
                huaweiIdAuthParamsHelper.setAccessToken().createParams()
            val service =
                HuaweiIdAuthManager.getService(activity, authParams)
            startActivityForResult(service.signInIntent, REQUEST_SIGN_IN_LOGIN);

        }

        viewModel.isUserExist.observe(viewLifecycleOwner, Observer {
            when (it) {
                true -> {
                    val intent = Intent(context, MainActivity::class.java).apply {
                        putExtra(USER_ID, viewModel.existedUserId.value)
                    }
                    startActivity(intent)
                }
                false -> {
                    val userId = AGConnectAuth.getInstance().currentUser
                    val action: NavDirections =
                        LoginFragmentDirections.actionLoginFragment2ToCreateAccountFragment2(
                            userId.uid
                        )
                    findNavController().navigate(action)
                }
                else -> Log.e(TAG, "user is exist or not exist")
            }
        })
    }

    private fun anonymousLogin() {
        anonymousBtn.setOnClickListener {
            AGConnectAuth.getInstance().signInAnonymously()
                .addOnSuccessListener { signInResult: SignInResult ->
                    val signedInUser = signInResult.user

                    uId = signedInUser.uid
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener { e: Exception? ->
                    Toast.makeText(
                        activity,
                        "Anonymous SignIn Failed",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e(TAG, "error : " + e.toString())
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == HUAWEI_ID_SIGNIN) {
            val authHuaweiIdTask =
                HuaweiIdAuthManager.parseAuthResultFromIntent(data)
            if (authHuaweiIdTask.isSuccessful) {
                huaweiAccount = authHuaweiIdTask.result

                val credential: AGConnectAuthCredential =
                    HwIdAuthProvider.credentialWithToken(huaweiAccount!!.accessToken)

                AGConnectAuth.getInstance().signIn(credential)
                    .addOnSuccessListener { signInResult ->
                        val signedInUser = signInResult.user
                        uId = signedInUser.uid
                        viewModel.checkIsUserExist(uId!!)
                    }.addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "SignIn Error ${it.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }

            } else {
                Log.i(
                    TAG,
                    "sign in failed" + (authHuaweiIdTask.exception as ApiException).statusCode
                )
                Toast.makeText(
                    activity,
                    "HwID signIn failed" + authHuaweiIdTask.exception.message,
                    Toast.LENGTH_LONG
                ).show();
            }
        }
    }
}