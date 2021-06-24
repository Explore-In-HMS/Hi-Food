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

package com.hms.referenceapp.hifood.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hms.referenceapp.hifood.remote.model.BaseFood
import com.hms.referenceapp.hifood.remote.model.Ingredient
import com.hms.referenceapp.hifood.remote.model.Recipe
import com.hms.referenceapp.hifood.remote.model.RecipeIngredient
import com.hms.referenceapp.hifood.repository.CloudDbZoneWrapper
import com.hms.referenceapp.hifood.repository.RecipeRepository
import com.hms.referenceapp.hifood.repository.UserRecipeLogRepository

class RecipeDetailViewModel : ViewModel() {
    var mCloudDbZoneWrapper: CloudDbZoneWrapper = CloudDbZoneWrapper()

    private var recipeRepository: RecipeRepository
    private var userRecipeLogRepository: UserRecipeLogRepository

    val recipe: MutableLiveData<Recipe>
    val isMealRecorded: MutableLiveData<Boolean>
    var recipeIngredientList: MutableLiveData<MutableList<RecipeIngredient>>
    var ingredientList: MutableLiveData<MutableList<Ingredient>>
    var baseFoodList: MutableLiveData<MutableList<BaseFood>>


    init {
        mCloudDbZoneWrapper.createObjectType()
        mCloudDbZoneWrapper.openCloudDBZone()

        recipeRepository = RecipeRepository(mCloudDbZoneWrapper.mCloudDBZone)
        userRecipeLogRepository = UserRecipeLogRepository(mCloudDbZoneWrapper.mCloudDBZone)

        recipe = recipeRepository.recipe
        isMealRecorded = userRecipeLogRepository.isMealRecorded
        recipeIngredientList = recipeRepository.recipeIngredientList
        ingredientList = recipeRepository.ingredientList
        baseFoodList = recipeRepository.baseFoodList
    }


    fun getRecipeDetails(id: String) {
        recipeRepository.getRecipeDetails(id)
    }

    fun recordMeal(userId: String, proportionValue: Double) {
        userRecipeLogRepository.add(userId, proportionValue, recipe.value!!.id)
    }
}