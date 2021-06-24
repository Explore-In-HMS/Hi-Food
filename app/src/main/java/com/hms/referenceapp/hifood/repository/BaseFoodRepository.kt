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
import com.hms.referenceapp.hifood.remote.model.BaseFood
import com.hms.referenceapp.hifood.remote.model.Ingredient
import com.hms.referenceapp.hifood.remote.model.RecipeIngredient
import com.huawei.agconnect.cloud.database.*
import java.lang.Exception

class BaseFoodRepository(val mCloudDBZone: CloudDBZone) {

    val loadingProgress = MutableLiveData<Boolean>()
    val baseFoodListInfo = MutableLiveData<MutableList<BaseFood>>()
    val TAG: String = "BaseFoodRepository"

    val baseFoodInfo = MutableLiveData<BaseFood>()

    val matchedRecipeId = MutableLiveData<String>()

    val recognizedBaseFood = MutableLiveData<MutableList<BaseFood>>(mutableListOf())

    val matchedRecipesIdsList = MutableLiveData<MutableList<String>>()

    fun getAllBaseFoods() {
        loadingProgress.value = true
        val query = CloudDBZoneQuery.where(BaseFood::class.java)
        val queryTask: CloudDBZoneTask<CloudDBZoneSnapshot<BaseFood>> = mCloudDBZone.executeQuery(
            query,
            CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
        )
        queryTask.addOnSuccessListener {
            Log.w(TAG, "getAllBaseFoods success")
            allBaseFoodsResult(it)
        }.addOnFailureListener {
            Log.w(TAG, "getAllBaseFoods error: ${it.message}")
        }
    }

    private fun allBaseFoodsResult(snapshot: CloudDBZoneSnapshot<BaseFood>) {

        val baseFoodCursor: CloudDBZoneObjectList<BaseFood> = snapshot.snapshotObjects
        val baseFoodList = mutableListOf<BaseFood>()

        try {
            while (baseFoodCursor.hasNext()) {
                val baseFood = baseFoodCursor.next()
                baseFoodList.add(baseFood)
            }
        } catch (exception: Exception) {
            Log.w(TAG, "getAllbaseFoods error: ${exception.message}")
        }

        snapshot.release()

        loadingProgress.postValue(false)
        baseFoodListInfo.postValue(baseFoodList)
    }

    fun getById(id: String) {
        loadingProgress.value = true
        val query = CloudDBZoneQuery.where(BaseFood::class.java).equalTo("id", id)
        val queryTask: CloudDBZoneTask<CloudDBZoneSnapshot<BaseFood>> =
            mCloudDBZone.executeQuery(
                query,
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
            )
        queryTask.addOnSuccessListener {
            if (it.snapshotObjects.hasNext()) {
                baseFoodResult(it)
            } else {
                loadingProgress.value = false
            }
        }.addOnFailureListener {
            Log.w(TAG, "getById error: ${it.message}")
        }
    }

    private fun baseFoodResult(snapshot: CloudDBZoneSnapshot<BaseFood>) {

        val baseFoodCursor: CloudDBZoneObjectList<BaseFood> = snapshot.snapshotObjects
        var baseFoodResult: BaseFood =
            BaseFood()

        try {
            baseFoodResult = baseFoodCursor.next()
        } catch (exception: Exception) {
            Log.w(TAG, "getById error: ${exception.message}")
        }

        snapshot.release()

        loadingProgress.postValue(false)
        baseFoodInfo.postValue(baseFoodResult)
    }

    fun getIfBaseFood(items: MutableList<String>) {
        loadingProgress.value = true
        val itemList = items.toTypedArray()
        val query = CloudDBZoneQuery.where(BaseFood::class.java).`in`("name", itemList)
        val queryTask: CloudDBZoneTask<CloudDBZoneSnapshot<BaseFood>> =
            mCloudDBZone.executeQuery(
                query,
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
            )
        queryTask.addOnSuccessListener {
            if (it.snapshotObjects.hasNext()) {
                recognizedFoodResult(it)
            }
        }.addOnFailureListener {
            Log.w(TAG, "getById error: ${it.message}")
        }
    }

    private fun recognizedFoodResult(snapshot: CloudDBZoneSnapshot<BaseFood>) {

        val baseFoodCursor: CloudDBZoneObjectList<BaseFood> = snapshot.snapshotObjects
        val baseFoodList = mutableListOf<BaseFood>()

        try {
            while (baseFoodCursor.hasNext()) {
                val baseFood = baseFoodCursor.next()
                baseFoodList.add(baseFood)
            }

        } catch (exception: Exception) {
            Log.w(TAG, "getById error: ${exception.message}")
        }

        getIngredientsByRecognizedFoods(baseFoodList)

        snapshot.release()

    }

    private fun getIngredientsByRecognizedFoods(recognizedBaseFoods: MutableList<BaseFood>) {
        val itemList = recognizedBaseFoods.map { it.id }.toTypedArray()
        val query = CloudDBZoneQuery.where(Ingredient::class.java).`in`("basefoodid", itemList)
        val queryTask: CloudDBZoneTask<CloudDBZoneSnapshot<Ingredient>> =
            mCloudDBZone.executeQuery(
                query,
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
            )
        queryTask.addOnSuccessListener {
            if (it.snapshotObjects.hasNext()) {
                ingredientResult(it)
            }
        }.addOnFailureListener {
            Log.w(TAG, "getById error: ${it.message}")
        }
    }

    private fun ingredientResult(snapshot: CloudDBZoneSnapshot<Ingredient>) {

        val ingredientCursor: CloudDBZoneObjectList<Ingredient> = snapshot.snapshotObjects
        val ingredientList = mutableListOf<Ingredient>()

        try {
            while (ingredientCursor.hasNext()) {
                val ingredient = ingredientCursor.next()
                ingredientList.add(ingredient)
            }

        } catch (exception: Exception) {
            Log.w(TAG, "getById error: ${exception.message}")
        }

        handleRecipeIngredients(ingredientList)

        snapshot.release()

    }

    private fun handleRecipeIngredients(ingredients: MutableList<Ingredient>) {
        val itemList = ingredients.map { it.id }.toTypedArray()
        val query =
            CloudDBZoneQuery.where(RecipeIngredient::class.java).`in`("ingredientid", itemList)
        val queryTask: CloudDBZoneTask<CloudDBZoneSnapshot<RecipeIngredient>> =
            mCloudDBZone.executeQuery(
                query,
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
            )
        queryTask.addOnSuccessListener {
            if (it.snapshotObjects.hasNext()) {
                recipeIngredientResult(it)
            }
        }.addOnFailureListener {
            Log.w(TAG, "getById error: ${it.message}")
        }
    }

    private fun recipeIngredientResult(snapshot: CloudDBZoneSnapshot<RecipeIngredient>) {

        val recipeIngredientCursor: CloudDBZoneObjectList<RecipeIngredient> =
            snapshot.snapshotObjects
        val recipeIngredientList = mutableListOf<RecipeIngredient>()

        try {
            while (recipeIngredientCursor.hasNext()) {
                val recipeIngredient = recipeIngredientCursor.next()
                recipeIngredientList.add(recipeIngredient)
            }

        } catch (exception: Exception) {
            Log.w(TAG, "getById error: ${exception.message}")
        }

        getMatchedRecipeId(recipeIngredientList)

        snapshot.release()

    }

    private fun getMatchedRecipeId(recipeIngredients: MutableList<RecipeIngredient>) {
        val itemList = recipeIngredients.map { it.recipeid }.distinct().toTypedArray()
        val query = CloudDBZoneQuery.where(RecipeIngredient::class.java).`in`("recipeid", itemList)
        val queryTask: CloudDBZoneTask<CloudDBZoneSnapshot<RecipeIngredient>> =
            mCloudDBZone.executeQuery(
                query,
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
            )
        queryTask.addOnSuccessListener {
            if (it.snapshotObjects.hasNext()) {
                matchedRecipeResult(it, recipeIngredients)
            }
        }.addOnFailureListener {
            Log.w(TAG, "getById error: ${it.message}")
        }
    }

    private fun matchedRecipeResult(
        snapshot: CloudDBZoneSnapshot<RecipeIngredient>,
        recognizedRecipeIngredient: MutableList<RecipeIngredient>
    ) {

        val recipeIngredientCursor: CloudDBZoneObjectList<RecipeIngredient> =
            snapshot.snapshotObjects
        val matchedRecipeIngredientList = mutableListOf<RecipeIngredient>()

        try {
            while (recipeIngredientCursor.hasNext()) {
                val recipeIngredient = recipeIngredientCursor.next()
                matchedRecipeIngredientList.add(recipeIngredient)
            }

        } catch (exception: Exception) {
            Log.w(TAG, "getById error: ${exception.message}")
        }

        checkList(matchedRecipeIngredientList, recognizedRecipeIngredient)

        snapshot.release()
    }

    private fun checkList(
        recipeIngredientList: MutableList<RecipeIngredient>,
        recognizedList: MutableList<RecipeIngredient>
    ) {
        val possibleRecipeList = recipeIngredientList.map { it.recipeid }.distinct().toTypedArray()
        val matchedRecipesIdsListLocal = mutableListOf<String>()

        for (recipeId in possibleRecipeList) {
            val matchStatus =
                recipeIngredientList.filter { it.recipeid == recipeId }.size == recognizedList.filter { it.recipeid == recipeId }.size
            if (matchStatus) {
                matchedRecipesIdsListLocal.add(recipeId)
            }
        }
        matchedRecipesIdsList.postValue(matchedRecipesIdsListLocal)
    }
}
