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
import com.hms.referenceapp.hifood.remote.model.Ingredient
import com.huawei.agconnect.cloud.database.*
import java.lang.Exception

class IngredientRepository(val mCloudDBZone: CloudDBZone) {

    val loadingProgress = MutableLiveData<Boolean>()
    val ingredientListInfo = MutableLiveData<MutableList<Ingredient>>()
    val TAG: String = "IngredientRepository"
    val ingredientInfo = MutableLiveData<Ingredient>()

    fun getAllIngredient() {
        loadingProgress.value = true
        val query = CloudDBZoneQuery.where(Ingredient::class.java)
        val queryTask: CloudDBZoneTask<CloudDBZoneSnapshot<Ingredient>> = mCloudDBZone.executeQuery(
            query,
            CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
        )
        queryTask.addOnSuccessListener {
            Log.w(TAG, "getIngredients success")
            allIngredientsResult(it)
        }.addOnFailureListener {
            Log.w(TAG, "getAllIngredients error: ${it.message}")
        }
    }

    private fun allIngredientsResult(snapshot: CloudDBZoneSnapshot<Ingredient>) {

        val ingredientCursor: CloudDBZoneObjectList<Ingredient> = snapshot.snapshotObjects
        val ingredientList = mutableListOf<Ingredient>()

        try {
            while (ingredientCursor.hasNext()) {
                val ingredient = ingredientCursor.next()
                ingredientList.add(ingredient)
            }
        } catch (exception: Exception) {
            Log.w(TAG, "getAllIngredients error: ${exception.message}")
        }

        snapshot.release()

        loadingProgress.postValue(false)
        ingredientListInfo.postValue(ingredientList)
    }

    fun getById(id: String) {
        loadingProgress.value = true
        val query = CloudDBZoneQuery.where(Ingredient::class.java).equalTo("id", id)
        val queryTask: CloudDBZoneTask<CloudDBZoneSnapshot<Ingredient>> =
            mCloudDBZone.executeQuery(
                query,
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
            )
        queryTask.addOnSuccessListener {
            if (it.snapshotObjects.hasNext()) {
                ingredientResult(it)
            } else {
                loadingProgress.value = false
            }
        }.addOnFailureListener {
            Log.w(TAG, "getUserById error: ${it.message}")
        }
    }

    private fun ingredientResult(snapshot: CloudDBZoneSnapshot<Ingredient>) {

        val ingredientCursor: CloudDBZoneObjectList<Ingredient> = snapshot.snapshotObjects
        var ingredientResult: Ingredient =
            Ingredient()

        try {
            ingredientResult = ingredientCursor.next()
        } catch (exception: Exception) {
            Log.w(TAG, "getUserById error: ${exception.message}")
        }

        snapshot.release()

        loadingProgress.postValue(false)
        ingredientInfo.postValue(ingredientResult)
    }
}