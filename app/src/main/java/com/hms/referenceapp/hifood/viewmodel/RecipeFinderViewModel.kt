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
import com.hms.referenceapp.hifood.model.RecipeSummary
import com.hms.referenceapp.hifood.remote.model.BaseFood
import com.hms.referenceapp.hifood.repository.BaseFoodRepository
import com.hms.referenceapp.hifood.repository.CloudDbZoneWrapper
import com.hms.referenceapp.hifood.repository.RecipeRepository

class RecipeFinderViewModel : ViewModel() {

    var mCloudDbZoneWrapper: CloudDbZoneWrapper = CloudDbZoneWrapper()

    private var recipeRepository: RecipeRepository
    private var baseFoodRepository: BaseFoodRepository

    var recognizedFoods = MutableLiveData<MutableList<BaseFood>>()
    var recipeList: MutableLiveData<MutableList<RecipeSummary>>
    var matchedRecipeId: MutableLiveData<String>
    var matchedRecipesIdsList: MutableLiveData<MutableList<String>>

    init {
        mCloudDbZoneWrapper.createObjectType()
        mCloudDbZoneWrapper.openCloudDBZone()
        baseFoodRepository = BaseFoodRepository(mCloudDbZoneWrapper.mCloudDBZone)
        recipeRepository = RecipeRepository(mCloudDbZoneWrapper.mCloudDBZone)

        matchedRecipesIdsList = baseFoodRepository.matchedRecipesIdsList
        recognizedFoods = baseFoodRepository.recognizedBaseFood
        recipeList = recipeRepository.recipeSummaryList
        matchedRecipeId = baseFoodRepository.matchedRecipeId
    }

    fun checkFoodList(items: MutableList<String>) {
        baseFoodRepository.getIfBaseFood(items)
    }

    fun getMatchedRecipesDetails(matchedRecipesIds: MutableList<String>) {
        recipeRepository.getMatchedRecipesDetails(matchedRecipesIds)
    }
}