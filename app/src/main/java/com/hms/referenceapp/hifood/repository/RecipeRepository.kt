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
import com.hms.referenceapp.hifood.model.RecipeSummary
import com.hms.referenceapp.hifood.remote.model.*
import com.huawei.agconnect.cloud.database.*
import java.lang.Exception
import java.util.*


class RecipeRepository(val mCloudDBZone: CloudDBZone) {

    val TAG: String = "RecipeRepository"

    val loadingProgress = MutableLiveData<Boolean>()
    val recipe = MutableLiveData<Recipe>()
    val recipeIngredientList = MutableLiveData<MutableList<RecipeIngredient>>()
    val ingredientList = MutableLiveData<MutableList<Ingredient>>()
    val baseFoodList = MutableLiveData<MutableList<BaseFood>>()
    val recipeSummaryList = MutableLiveData<MutableList<RecipeSummary>>()
    val recipeCaloriesList = MutableLiveData<HashMap<String, Float>>()
    val isRecipeCaloriesHandled = MutableLiveData<Boolean>()

    fun getAllRecipes() {
        loadingProgress.value = true
        val query = CloudDBZoneQuery.where(Recipe::class.java)
        val queryTask: CloudDBZoneTask<CloudDBZoneSnapshot<Recipe>> = mCloudDBZone.executeQuery(
            query,
            CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
        )
        queryTask.addOnSuccessListener {
            Log.w(TAG, "getAllRecipes success")
            allRecipesResult(it)
        }.addOnFailureListener {
            Log.w(TAG, "getAllRecipes error: ${it.message}")
        }
    }

    private fun allRecipesResult(snapshot: CloudDBZoneSnapshot<Recipe>) {

        val recipeCursor: CloudDBZoneObjectList<Recipe> = snapshot.snapshotObjects
        val summaryList = mutableListOf<RecipeSummary>()

        try {

            while (recipeCursor.hasNext()) {
                val recipe = recipeCursor.next()
                summaryList.add(
                    RecipeSummary(
                        id = recipe.id,
                        name = recipe.name,
                        instruction = recipe.instruction,
                        calories = recipe.calories
                    )
                )
            }

            recipeSummaryList.postValue(summaryList)

        } catch (exception: Exception) {
            Log.w(TAG, "getAllRecipes error: ${exception.message}")
        }

        snapshot.release()

        loadingProgress.postValue(false)
        recipeSummaryList.postValue(summaryList)

    }

    fun getAllRecipesFiltered(filterQuery: String) {
        loadingProgress.value = true
        val query = CloudDBZoneQuery.where(Recipe::class.java)
        query.contains("name", filterQuery)
        val queryTask: CloudDBZoneTask<CloudDBZoneSnapshot<Recipe>> = mCloudDBZone.executeQuery(
            query,
            CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
        )
        queryTask.addOnSuccessListener {
            Log.w(TAG, "getAllRecipes success")
            allRecipesResult(it)
        }.addOnFailureListener {
            Log.w(TAG, "getAllRecipes error: ${it.message}")
        }
    }

    fun getRecipeDetails(id: String) {
        loadingProgress.value = true
        val query = CloudDBZoneQuery.where(Recipe::class.java).equalTo("id", id)
        val queryTask: CloudDBZoneTask<CloudDBZoneSnapshot<Recipe>> = mCloudDBZone.executeQuery(
            query,
            CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
        )
        queryTask.addOnSuccessListener {
            if (it.snapshotObjects.hasNext()) {
                getRecipeDetailsResult(it)
            } else {
                loadingProgress.value = false
            }
        }.addOnFailureListener {
            Log.w(TAG, "getRecipeDetails error: ${it.message}")
        }
    }

    private fun getRecipeDetailsResult(snapshot: CloudDBZoneSnapshot<Recipe>) {

        val recipeCursor: CloudDBZoneObjectList<Recipe> = snapshot.snapshotObjects
        var recipeResult: Recipe =
            Recipe()

        try {
            recipeResult = recipeCursor.next()
            recipe.postValue(recipeResult)
            getRecipeIngredientDetails(recipeResult.id)
        } catch (exception: Exception) {
            Log.w(TAG, "getRecipeDetailsResult error: ${exception.message}")
            loadingProgress.value = false
        }

        snapshot.release()

    }

    private fun getRecipeIngredientDetails(recipeId: String) {
        val query =
            CloudDBZoneQuery.where(RecipeIngredient::class.java).equalTo("recipeid", recipeId)
        val queryTask: CloudDBZoneTask<CloudDBZoneSnapshot<RecipeIngredient>> =
            mCloudDBZone.executeQuery(
                query,
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
            )
        queryTask.addOnSuccessListener {
            if (it.snapshotObjects.hasNext()) {
                getRecipeIngredientDetailsResult(it)
            } else {
                loadingProgress.value = false
            }
        }.addOnFailureListener {
            Log.w(TAG, "getRecipeIngredientDetails error: ${it.message}")
        }
    }

    private fun getRecipeIngredientDetailsResult(snapshot: CloudDBZoneSnapshot<RecipeIngredient>) {

        val recipeIngredientCursor: CloudDBZoneObjectList<RecipeIngredient> =
            snapshot.snapshotObjects
        val recipeIngredientLocalList = mutableListOf<RecipeIngredient>()

        try {
            while (recipeIngredientCursor.hasNext()) {
                val recipe = recipeIngredientCursor.next()
                recipeIngredientLocalList.add(recipe)
            }
            recipeIngredientList.postValue(recipeIngredientLocalList)

            getIngredientDetails(recipeIngredientLocalList)

        } catch (exception: Exception) {
            Log.w(TAG, "getRecipeIngredientDetailsResult error: ${exception.message}")
            loadingProgress.value = false
        }

        snapshot.release()

    }

    private fun getIngredientDetails(recipeIngredientList: MutableList<RecipeIngredient>) {
        val itemList = recipeIngredientList.map { it.ingredientid }.toTypedArray()
        val query = CloudDBZoneQuery.where(Ingredient::class.java).`in`("id", itemList)

        val queryTask: CloudDBZoneTask<CloudDBZoneSnapshot<Ingredient>> =
            mCloudDBZone.executeQuery(
                query,
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
            )
        queryTask.addOnSuccessListener {
            if (it.snapshotObjects.hasNext()) {
                ingredientDetailsResult(it)
            }
        }.addOnFailureListener {
            Log.w(TAG, "getIngredientDetails error: ${it.message}")
            loadingProgress.value = false
        }
    }

    private fun ingredientDetailsResult(snapshot: CloudDBZoneSnapshot<Ingredient>) {

        val ingredientCursor: CloudDBZoneObjectList<Ingredient> = snapshot.snapshotObjects
        val ingredientLocalList = mutableListOf<Ingredient>()

        try {
            while (ingredientCursor.hasNext()) {
                val ingredient = ingredientCursor.next()
                ingredientLocalList.add(ingredient)
            }
            ingredientList.postValue(ingredientLocalList)
            getBaseFoodDetails(ingredientLocalList)

        } catch (exception: Exception) {
            Log.w(TAG, "ingredientDetailsResult error: ${exception.message}")
            loadingProgress.value = false
        }
        snapshot.release()
    }

    private fun getBaseFoodDetails(ingredientList: MutableList<Ingredient>) {
        val itemList = ingredientList.map { it.basefoodid }.toTypedArray()
        val query = CloudDBZoneQuery.where(BaseFood::class.java).`in`("id", itemList)

        val queryTask: CloudDBZoneTask<CloudDBZoneSnapshot<BaseFood>> =
            mCloudDBZone.executeQuery(
                query,
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
            )
        queryTask.addOnSuccessListener {
            if (it.snapshotObjects.hasNext()) {
                baseFoodDetailsResult(it)
            }
        }.addOnFailureListener {
            Log.w(TAG, "getBaseFoodDetails error: ${it.message}")
            loadingProgress.value = false
        }
    }

    private fun baseFoodDetailsResult(snapshot: CloudDBZoneSnapshot<BaseFood>) {
        val baseFoodCursor: CloudDBZoneObjectList<BaseFood> = snapshot.snapshotObjects
        val baseFoodLocalList = mutableListOf<BaseFood>()

        try {
            while (baseFoodCursor.hasNext()) {
                val baseFood = baseFoodCursor.next()
                baseFoodLocalList.add(baseFood)
            }
            baseFoodList.postValue(baseFoodLocalList)

        } catch (exception: Exception) {
            Log.w(TAG, "ingredientDetailsResult error: ${exception.message}")
            loadingProgress.value = false
        }
        snapshot.release()

        loadingProgress.value = false
    }

    fun getCaloriesForSummary(userRecipeLogList: MutableList<UserRecipeLog>) {

        if(userRecipeLogList.isNullOrEmpty()){
            isRecipeCaloriesHandled.postValue(true)
        }

        val recipeList = userRecipeLogList.map { it.recipeid }.distinct().toTypedArray()

        val query = CloudDBZoneQuery.where(Recipe::class.java).`in`("id", recipeList)

        val queryTask: CloudDBZoneTask<CloudDBZoneSnapshot<Recipe>> =
            mCloudDBZone.executeQuery(
                query,
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
            )
        queryTask.addOnSuccessListener {
            if (it.snapshotObjects.hasNext()) {
                caloriesForSummaryResult(it)
            }
        }.addOnFailureListener {
            Log.w(TAG, "getCaloriesForSummary error: ${it.message}")
            loadingProgress.value = false
        }
    }

    private fun caloriesForSummaryResult(snapshot: CloudDBZoneSnapshot<Recipe>) {
        val recipeCursor: CloudDBZoneObjectList<Recipe> = snapshot.snapshotObjects
        val recipeCaloriesListLocal = hashMapOf<String, Float>()

        try {

            while (recipeCursor.hasNext()) {
                val recipe = recipeCursor.next()
                recipeCaloriesListLocal[recipe.id] = recipe.calories
            }

            recipeCaloriesList.postValue(recipeCaloriesListLocal)
            isRecipeCaloriesHandled.postValue(true)


        } catch (exception: Exception) {
            Log.w(TAG, "caloriesForSummaryResult error: ${exception.message}")
            loadingProgress.value = false
        }
        snapshot.release()

        loadingProgress.value = false
    }

    fun getMatchedRecipesDetails(matchedRecipesIds: MutableList<String>){
        loadingProgress.value = true
        val matchedRecipesIdsList = matchedRecipesIds.toTypedArray()

        val query = CloudDBZoneQuery.where(Recipe::class.java).`in`("id", matchedRecipesIdsList)

        val queryTask: CloudDBZoneTask<CloudDBZoneSnapshot<Recipe>> = mCloudDBZone.executeQuery(
            query,
            CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
        )
        queryTask.addOnSuccessListener {
            Log.w(TAG, "getAllRecipes success")
            allRecipesResult(it)
        }.addOnFailureListener {
            Log.w(TAG, "getAllRecipes error: ${it.message}")
        }
    }
}