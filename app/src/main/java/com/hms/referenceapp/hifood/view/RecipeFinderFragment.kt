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

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hms.referenceapp.hifood.R
import com.hms.referenceapp.hifood.adapter.OnRecipeClickListener
import com.hms.referenceapp.hifood.adapter.RecipeListAdapter
import com.hms.referenceapp.hifood.model.RecipeSummary
import com.hms.referenceapp.hifood.viewmodel.RecipeFinderViewModel
import com.huawei.hmf.tasks.Task
import com.huawei.hms.mlsdk.MLAnalyzerFactory
import com.huawei.hms.mlsdk.classification.MLImageClassification
import com.huawei.hms.mlsdk.classification.MLLocalClassificationAnalyzerSetting
import com.huawei.hms.mlsdk.classification.MLRemoteClassificationAnalyzerSetting
import com.huawei.hms.mlsdk.common.MLApplication
import com.huawei.hms.mlsdk.common.MLException
import com.huawei.hms.mlsdk.common.MLFrame
import kotlinx.android.synthetic.main.main_fragment.*

private const val REQUEST_CAMERA = 1112

class RecipeFinderFragment : Fragment(), OnRecipeClickListener {

    companion object {
        fun newInstance() = RecipeFinderFragment()
    }

    private lateinit var viewModel: RecipeFinderViewModel

    private val recipeListAdapter = RecipeListAdapter(arrayListOf(), this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.recipe_finder_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RecipeFinderViewModel::class.java)

        recipeRV.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = recipeListAdapter
        }

        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA
            )
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_CAMERA)
        }

        observeViewModel()

    }

    fun observeViewModel() {
        viewModel.recipeList.observe(requireActivity(), Observer { recipes ->
            recipes?.let { recipeListAdapter.updateRecipes(recipes) }
        })

        viewModel.matchedRecipesIdsList.observe(requireActivity(), Observer {
            viewModel.getMatchedRecipesDetails(it)
        })
    }

    private fun performStaticImageClassificationOnCloud(bitmap: Bitmap) {

        MLApplication.getInstance().apiKey =
            "CgB6e3x9o7AUFo2hhiT/FSgY9IyWMXVmfZ3498yNhnH/ZyX/oVpI127YpzxvoJdwrGh6MK0vPgkr6vxJIcv6DbeC"

        val cloudSetting =
            MLRemoteClassificationAnalyzerSetting.Factory()
                .create()
        val analyzer =
            MLAnalyzerFactory.getInstance().getRemoteImageClassificationAnalyzer(cloudSetting)

        val frame = MLFrame.fromBitmap(bitmap)

        val task: Task<List<MLImageClassification>> = analyzer.asyncAnalyseFrame(frame)
        task.addOnSuccessListener { it ->
            val itemList = mutableListOf<String>()
            it.forEach {
                itemList.add(it.name)
            }
            viewModel.checkFoodList(itemList)
        }.addOnFailureListener { e ->
            // Recognition failure.
            try {
                val mlException = e as MLException
                // Obtain the result code. You can process the result code and customize respective messages displayed to users.
                val errorCode = mlException.errCode
                // Obtain the error information. You can quickly locate the fault based on the result code.
                val errorMessage = mlException.message
                Log.i("RecipeFinder", "errorCode: $errorCode and error message: $errorMessage")
            } catch (error: Exception) {
                Log.i("RecipeFinder", error.localizedMessage)
            }
        }
    }

    private fun performStaticImageClassificationOnDevice(bitmap: Bitmap) {
        // Method 1: Use customized parameter settings for on-device recognition.
        val deviceSetting =
            MLLocalClassificationAnalyzerSetting.Factory()
                .setMinAcceptablePossibility(0.8f)
                .create()
        val analyzer1 =
            MLAnalyzerFactory.getInstance().getLocalImageClassificationAnalyzer(deviceSetting)

        val frame = MLFrame.fromBitmap(bitmap)

        val task: Task<List<MLImageClassification>> = analyzer1.asyncAnalyseFrame(frame)
        task.addOnSuccessListener { it ->

            val itemList = mutableListOf<String>()
            it.forEach {
                itemList.add(it.name)
            }
            viewModel.checkFoodList(itemList)

        }.addOnFailureListener { e ->
            // Recognition failure.
            try {
                val mlException = e as MLException
                // Obtain the result code. You can process the result code and customize respective messages displayed to users.
                val errorCode = mlException.errCode
                // Obtain the error information. You can quickly locate the fault based on the result code.
                val errorMessage = mlException.message
            } catch (error: Exception) {
                // Handle the conversion error.
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, REQUEST_CAMERA)
                } else {
                    Log.e("RecipeFinderFragment", "permission denied")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                val imageBitmap = if (data?.data == null) {
                    data?.extras!!["data"] as Bitmap?
                } else {
                    MediaStore.Images.Media.getBitmap(activity?.contentResolver, data.data)
                }
                performStaticImageClassificationOnCloud(imageBitmap!!)
            }
        }
    }

    override fun onItemClick(item: RecipeSummary) {
        val action =
            RecipeFinderFragmentDirections.actionRecipeFinderFragmentToRecipeDetailFragment(item.id)
        findNavController().navigate(action)
    }
}