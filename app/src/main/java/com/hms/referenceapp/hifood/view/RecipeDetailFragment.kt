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

@file:Suppress("DEPRECATED_IDENTITY_EQUALS")

package com.hms.referenceapp.hifood.view

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.hms.referenceapp.hifood.R
import com.hms.referenceapp.hifood.utils.RecipeImageGetter
import com.hms.referenceapp.hifood.viewmodel.RecipeDetailViewModel
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.hmf.tasks.Task
import com.huawei.hms.iap.Iap
import com.huawei.hms.iap.IapApiException
import com.huawei.hms.iap.IapClient
import com.huawei.hms.iap.entity.*
import com.huawei.hms.mlsdk.common.MLApplication
import com.huawei.hms.mlsdk.tts.*
import com.huawei.hms.support.api.client.Status
import kotlinx.android.synthetic.main.recipe_detail_fragment.*
import org.json.JSONException


class RecipeDetailFragment : Fragment() {

    val args: RecipeDetailFragmentArgs by navArgs()
    private lateinit var mlTtsEngine: MLTtsEngine
    private lateinit var mlTtsConfig: MLTtsConfig
    var proportionValue: Double = 1.0
    private lateinit var viewModel: RecipeDetailViewModel

    // Text to speech
    private val callback: MLTtsCallback = object : MLTtsCallback {
        override fun onAudioAvailable(
            p0: String?,
            p1: MLTtsAudioFragment?,
            p2: Int,
            p3: Pair<Int, Int>?,
            p4: Bundle?
        ) {
        }

        override fun onEvent(taskId: String?, eventName: Int, bundle: Bundle?) {
            if (eventName == MLTtsConstants.EVENT_PLAY_STOP) {
                Toast.makeText(activity, "Service stopped", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onWarn(p0: String?, p1: MLTtsWarn?) {
            Log.i(TAG, "onWarn")
        }

        override fun onError(p0: String?, p1: MLTtsError?) {
            Log.i(TAG, "onError")
        }

        override fun onRangeStart(p0: String?, p1: Int, p2: Int) {
            Log.i(TAG, "onRangeStart")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.recipe_detail_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MLApplication.getInstance().apiKey =
            "CgB6e3x9o7AUFo2hhiT/FSgY9IyWMXVmfZ3498yNhnH/ZyX/oVpI127YpzxvoJdwrGh6MK0vPgkr6vxJIcv6DbeC"

        //text to speech
        mlTtsConfig = MLTtsConfig()
            .setLanguage(MLTtsConstants.TTS_EN_US)
            .setPerson(MLTtsConstants.TTS_SPEAKER_FEMALE_EN)
            .setSpeed(1.0f)
            .setVolume(1.0f)

        mlTtsEngine = MLTtsEngine(mlTtsConfig)
        mlTtsEngine.updateConfig(mlTtsConfig)

        speakBtn.setOnClickListener {
            mlTtsEngine = MLTtsEngine(mlTtsConfig)
            mlTtsEngine.setTtsCallback(callback)
            val text: String = speakTxt.text.toString()
            val id = mlTtsEngine.speak(text, MLTtsEngine.QUEUE_APPEND)
        }

        findMap.setOnClickListener {
            val action =
                RecipeDetailFragmentDirections.actionRecipeDetailFragmentToNearToBuyFragment()
            findNavController().navigate(action)
        }

        // nav-component args
        val recId = args.recipeId

        val activity: Activity? = activity
        val task = Iap.getIapClient(activity).isEnvReady
        task.addOnSuccessListener {
        }.addOnFailureListener { e ->
            if (e is IapApiException) {
                val status: Status = e.status
                if (status.statusCode === OrderStatusCode.ORDER_HWID_NOT_LOGIN) {
                    if (status.hasResolution()) {
                        try {
                            status.startResolutionForResult(getActivity(), 6666)
                        } catch (exp: SendIntentException) {
                        }
                    }
                } else if (status.statusCode === OrderStatusCode.ORDER_ACCOUNT_AREA_NOT_SUPPORTED) {
                    // The current region does not support HUAWEI IAP.
                }
            }
        }

        getProductList()

        iapClick.setOnClickListener {
            createPurchase()
        }

        addListBtn.setOnClickListener {
            getProportionOfMeal()
        }
    }

    private fun getProportionOfMeal() {

        val proportionOptions = mutableListOf<Double>(0.5, 1.0, 1.5, 2.0)

        val alertDialogBuilder = AlertDialog.Builder(requireContext())

        alertDialogBuilder.setTitle(getString(R.string.proportion_title))

        val user = AGConnectAuth.getInstance().currentUser

        alertDialogBuilder.setPositiveButton(R.string.add) { _, _ ->
            viewModel.recordMeal(user.uid, proportionValue)
        }

        alertDialogBuilder.setNegativeButton(R.string.cancel) { dialog, _ -> dialog?.dismiss() }

        val sp: Spinner = Spinner(requireContext())

        sp.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        sp.adapter = ArrayAdapter<Double>(
            requireContext(),
            android.R.layout.simple_list_item_1,
            mutableListOf<Double>(0.5, 1.0, 1.5, 2.0)
        )

        sp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                proportionValue = proportionOptions[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.i(TAG, "Nothing selected in proportion spinner")
            }

        }

        alertDialogBuilder.setView(sp)

        alertDialogBuilder.create().show()
    }

    private fun getProductList() {
        val productIdList: MutableList<String> = ArrayList()
        productIdList.add("002")
        val req = ProductInfoReq()
        // priceType: 0: consumable; 1: non-consumable; 2: auto-renewable subscription
        req.priceType = IapClient.PriceType.IN_APP_CONSUMABLE
        req.productIds = productIdList
        val taskProduct =
            Iap.getIapClient(activity).obtainProductInfo(req)
        taskProduct.addOnSuccessListener { // Obtain the result
            val productList = req.productIds
        }.addOnFailureListener { e ->
            if (e is IapApiException) {
                val returnCode = e.statusCode
            } else {
                // Other external errors
            }
        }
    }

    private fun createPurchase() {
        val req = PurchaseIntentReq()
        req.productId = "002";
        req.priceType = 0;
        req.developerPayload = "test";
        val task =
            Iap.getIapClient(activity).createPurchaseIntent(req)
        task.addOnSuccessListener {
            val status = it.status
            if (status.hasResolution()) {
                try {
                    // 6666 is an int constant defined by the developer.
                    status.startResolutionForResult(activity, 6666)
                } catch (exp: SendIntentException) {
                }
            }
        }.addOnFailureListener { e ->
            if (e is IapApiException) {
                val status = e.status
                val returnCode = e.statusCode
            } else {
            }
        }
    }

    private fun consumeOwnedPurchase(
        context: Context,
        inAppPurchaseData: String
    ) {
        Log.i(TAG, "call consumeOwnedPurchase")
        val mClient = Iap.getIapClient(context)
        val task =
            mClient.consumeOwnedPurchase(createConsumeOwnedPurchaseReq(inAppPurchaseData))
        task.addOnSuccessListener { // Consume success
            Log.i(TAG, "consumeOwnedPurchase success")
            Toast.makeText(
                context,
                "Pay success, and the product has been delivered",
                Toast.LENGTH_SHORT
            ).show()
        }.addOnFailureListener { e ->
            Log.e(TAG, e.message!!)
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            if (e is IapApiException) {
                val returnCode = e.statusCode
                Log.e(TAG, "consumeOwnedPurchase fail,returnCode: $returnCode")
            } else {
                // Other external errors
            }
        }
    }

    private fun createConsumeOwnedPurchaseReq(purchaseData: String): ConsumeOwnedPurchaseReq? {
        val req = ConsumeOwnedPurchaseReq()
        // Parse purchaseToken from InAppPurchaseData in JSON format.
        try {
            val inAppPurchaseData = InAppPurchaseData(purchaseData)
            req.purchaseToken = inAppPurchaseData.purchaseToken
        } catch (e: JSONException) {
            Log.e(TAG, "createConsumeOwnedPurchaseReq JSONExeption")
        }
        return req
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 6666) {
            if (data == null) {
                Log.e("onActivityResult", "data is null");
                return;
            }
            val purchaseResultInfo: PurchaseResultInfo =
                Iap.getIapClient(activity).parsePurchaseResultInfoFromIntent(data)
            when (purchaseResultInfo.returnCode) {
                OrderStatusCode.ORDER_STATE_CANCEL -> {
                    Log.i(TAG, "order cancelled")
                    val action =
                        RecipeDetailFragmentDirections.actionRecipeDetailFragmentToVideoFragment2()
                    findNavController().navigate(action)
                }
                OrderStatusCode.ORDER_STATE_FAILED, OrderStatusCode.ORDER_PRODUCT_OWNED -> {
                }
                OrderStatusCode.ORDER_STATE_SUCCESS -> {
                    // pay success.
                    var purchaseToken = ""
                    val inAppPurchaseData: String = purchaseResultInfo.inAppPurchaseData
                    val inAppPurchaseDataSignature: String =
                        purchaseResultInfo.inAppDataSignature
                    try {
                        val inAppPurchaseDataBean =
                            InAppPurchaseData(inAppPurchaseData)
                        purchaseToken = inAppPurchaseDataBean.purchaseToken
                    } catch (e: JSONException) {
                    }
                    val req = ConsumeOwnedPurchaseReq()
                    req.purchaseToken = purchaseToken;
                    val activity: Activity? = activity
                    val task: Task<ConsumeOwnedPurchaseResult> =
                        Iap.getIapClient(activity).consumeOwnedPurchase(req)
                    task.addOnSuccessListener {
                        val action =
                            RecipeDetailFragmentDirections.actionRecipeDetailFragmentToVideoFragment2()
                        findNavController().navigate(action)
                    }.addOnFailureListener { e ->
                        if (e is IapApiException) {
                            val status =
                                e.status
                            val returnCode = e.statusCode
                            Toast.makeText(activity, "payment failed", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(activity, "payment failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else -> {
                }
            }
            return
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RecipeDetailViewModel::class.java)

        observeViewModel()

        val recId = args.recipeId

        viewModel.getRecipeDetails(recId!!)
    }

    private fun observeViewModel() {

        viewModel.recipe.observe(viewLifecycleOwner, Observer {
            it?.let {
                recipeName.text = it.name
                instructionTV.text = it.instruction
            }
        })

        viewModel.recipeIngredientList.observe(viewLifecycleOwner, Observer {
            it?.let {

            }
        })

        viewModel.ingredientList.observe(viewLifecycleOwner, Observer {
            it?.let {

            }
        })

        viewModel.isMealRecorded.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    Toast.makeText(
                        requireContext(),
                        "Meal is added to your diary.",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
        })

        viewModel.baseFoodList.observe(viewLifecycleOwner, Observer { baseFoodList ->
            baseFoodList?.let {
                ingredientsLV.adapter =
                    ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        baseFoodList.map { it.name }.toList()
                    )
                recipeImg.setImageResource(RecipeImageGetter.getImageSource(viewModel.recipe.value!!.name))
                for (food in baseFoodList) {
                    mlTtsEngine.speak(food.name, MLTtsEngine.QUEUE_APPEND)
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mlTtsEngine.shutdown()
    }
}