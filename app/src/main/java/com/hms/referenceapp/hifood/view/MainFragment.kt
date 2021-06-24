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
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hms.referenceapp.hifood.R
import com.hms.referenceapp.hifood.adapter.OnRecipeClickListener
import com.hms.referenceapp.hifood.adapter.RecipeListAdapter
import com.hms.referenceapp.hifood.model.RecipeSummary
import com.hms.referenceapp.hifood.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.main_fragment.*


private const val ARG_RECIPE_ID = "recipeId"

class MainFragment : Fragment(), OnRecipeClickListener {

    private var PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )
    private var PERMISSION_ALL = 1


    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private val recipeListAdapter = RecipeListAdapter(arrayListOf(), this)

    var filterQueryText: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestPermissions(PERMISSIONS, PERMISSION_ALL)

        recipeRV.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = recipeListAdapter
        }

        getDataBTN.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToRecipeFinderFragment()
            findNavController().navigate(action)
        }

        filterIV.setOnClickListener {
            val filterDialog = AlertDialog.Builder(requireContext())
            filterDialog.setTitle("Enter the food")
            filterDialog.setPositiveButton(R.string.filter_it) { _, _ ->
                viewModel.filterResults(filterQueryText)
            }
            val filterQueryET: EditText = EditText(requireContext())

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            params.setMargins(0, 10, 0, 10)

            filterQueryET.layoutParams = params

            filterQueryET.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    filterQueryText = s.toString()
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })

            filterDialog.setView(filterQueryET)

            filterDialog.create().show()
        }

        observeViewModel()

        viewModel.getAllRecipes()
    }

    private fun observeViewModel() {
        viewModel.recipeList.observe(requireActivity(), Observer { recipes ->
            recipes?.let { recipeListAdapter.updateRecipes(recipes) }
        })
    }

    override fun onItemClick(item: RecipeSummary) {
        val action = MainFragmentDirections.actionMainFragmentToRecipeDetailFragment(item.id)
        findNavController().navigate(action)
    }
}