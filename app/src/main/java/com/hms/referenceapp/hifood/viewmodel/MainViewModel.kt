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

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.hms.referenceapp.hifood.model.RecipeSummary
import com.hms.referenceapp.hifood.repository.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    var mCloudDbZoneWrapper: CloudDbZoneWrapper = CloudDbZoneWrapper()

    private var recipeRepository: RecipeRepository
    private var baseFoodRepository: BaseFoodRepository

    var recipeList: MutableLiveData<MutableList<RecipeSummary>>

    init {

        mCloudDbZoneWrapper.createObjectType()
        mCloudDbZoneWrapper.openCloudDBZone()
        recipeRepository = RecipeRepository(mCloudDbZoneWrapper.mCloudDBZone)
        baseFoodRepository = BaseFoodRepository(mCloudDbZoneWrapper.mCloudDBZone)


        recipeList = recipeRepository.recipeSummaryList

    }

    fun getAllRecipes() {
        recipeRepository.getAllRecipes()
    }

    fun filterResults(query: String) {
        recipeRepository.getAllRecipesFiltered(query)
    }


}