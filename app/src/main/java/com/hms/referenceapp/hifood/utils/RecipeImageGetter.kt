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

package com.hms.referenceapp.hifood.utils

import com.hms.referenceapp.hifood.R

class RecipeImageGetter {
    companion object RecipeImageGetter {
        fun getImageSource(text: String): Int {
            if (text == "Grilled Cheese Sandwich") {
                return R.drawable.grilled_cheese_sandwich
            }
            if (text == "BLT Guacamole") {
                return R.drawable.guacamole
            }
            if (text == "Cloud Bread") {
                return R.drawable.cloud_bread
            }
            if (text == "Chili-glazed Salmon") {
                return R.drawable.chili_glazed_salmon
            }
            if (text == "Cheesy Garlic Broccoli") {
                return R.drawable.cheese_garlic_broccoli
            }
            if (text == "Scrambled Egg") {
                return R.drawable.scrambled_egg
            }
            return R.drawable.recipe_sample
        }
    }
}