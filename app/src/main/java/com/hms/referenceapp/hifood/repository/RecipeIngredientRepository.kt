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

package com.hms.referenceapp.hifood.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.hms.referenceapp.hifood.remote.model.RecipeIngredient
import com.huawei.agconnect.cloud.database.*
import java.lang.Exception

class RecipeIngredientRepository(val mCloudDBZone: CloudDBZone) {

    val loadingProgress = MutableLiveData<Boolean>()
    val recipeIngredientListInfo = MutableLiveData<MutableList<RecipeIngredient>>()
    val TAG: String = "RecipeIngredientRepo"

    val recipeInfo = MutableLiveData<RecipeIngredient>()

    fun getAllRecipeIngredient() {
        loadingProgress.value = true
        val query = CloudDBZoneQuery.where(RecipeIngredient::class.java)
        val queryTask: CloudDBZoneTask<CloudDBZoneSnapshot<RecipeIngredient>> =
            mCloudDBZone.executeQuery(
                query,
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
            )
        queryTask.addOnSuccessListener {
            Log.w(TAG, "getRecipeIngredients success")
            allRecipeIngredientsResult(it)
        }.addOnFailureListener {
            Log.w(TAG, "getAllRecipeIngredients error: ${it.message}")
        }
    }

    private fun allRecipeIngredientsResult(snapshot: CloudDBZoneSnapshot<RecipeIngredient>) {

        val recipeIngredientCursor: CloudDBZoneObjectList<RecipeIngredient> =
            snapshot.snapshotObjects
        val recipeIngredientList = mutableListOf<RecipeIngredient>()

        try {
            while (recipeIngredientCursor.hasNext()) {
                val recipeIngredient = recipeIngredientCursor.next()
                recipeIngredientList.add(recipeIngredient)
            }
        } catch (exception: Exception) {
            Log.w(TAG, "getAllRecipeIngredients error: ${exception.message}")
        }

        snapshot.release()

        loadingProgress.postValue(false)
        recipeIngredientListInfo.postValue(recipeIngredientList)
    }

    fun getById(id: String) {
        loadingProgress.value = true
        val query = CloudDBZoneQuery.where(RecipeIngredient::class.java).equalTo("id", id)
        val queryTask: CloudDBZoneTask<CloudDBZoneSnapshot<RecipeIngredient>> =
            mCloudDBZone.executeQuery(
                query,
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
            )
        queryTask.addOnSuccessListener {
            if (it.snapshotObjects.hasNext()) {
                recipeResult(it)
            } else {
                loadingProgress.value = false
            }
        }.addOnFailureListener {
            Log.w(TAG, "getUserById error: ${it.message}")
        }
    }

    private fun recipeResult(snapshot: CloudDBZoneSnapshot<RecipeIngredient>) {

        val recipeCursor: CloudDBZoneObjectList<RecipeIngredient> = snapshot.snapshotObjects
        var recipeResult: RecipeIngredient =
            RecipeIngredient()

        try {
            recipeResult = recipeCursor.next()
        } catch (exception: Exception) {
            Log.w(TAG, "getUserById error: ${exception.message}")
        }

        snapshot.release()

        loadingProgress.postValue(false)
        recipeInfo.postValue(recipeResult)
    }
}