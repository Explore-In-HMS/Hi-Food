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

package com.hms.referenceapp.hifood.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hms.referenceapp.hifood.R
import com.hms.referenceapp.hifood.model.RecipeSummary
import com.hms.referenceapp.hifood.utils.RecipeImageGetter
import kotlinx.android.synthetic.main.item_recipes.view.*

class RecipeListAdapter(
    var recipes: ArrayList<RecipeSummary>,
    var clickListener: OnRecipeClickListener
) : RecyclerView.Adapter<RecipeListAdapter.RecipeViewHolder>() {

    fun updateRecipes(newRecipes: List<RecipeSummary>) {
        recipes.clear()
        recipes.addAll(newRecipes)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecipeViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_recipes, parent, false)
    )

    override fun getItemCount() = recipes.size

    override fun onBindViewHolder(holder: RecipeListAdapter.RecipeViewHolder, position: Int) {
        holder.bind(recipes[position], clickListener)

    }

    class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val recipeName = view.recipe_name
        private val recipeImage = view.recipe_image

        fun bind(recipe: RecipeSummary, action: OnRecipeClickListener) {
            recipeName.text = recipe.name

            recipeImage.setImageResource(RecipeImageGetter.getImageSource(recipe.name))

            itemView.setOnClickListener {
                action.onItemClick(recipe)
            }

        }
    }
}

interface OnRecipeClickListener {
    fun onItemClick(item: RecipeSummary)
}