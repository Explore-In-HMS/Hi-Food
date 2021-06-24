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

object CaloryCalculator {
    // An algorithm that calculates average calorie intake according to age, height, weight and gender.
    // It is a formula that gives an average result whose accuracy is not exact.
    fun calculateNeededCalories(gender: Int, weight: Int, height: Float, age: Int): Double {

        if (gender == 1) {
            return 655.1 + (9.563 * weight) + (1.85 * height) - (4.67 * age)
        } else {
            return 66.47 + (13.75 * weight) + (5 * height) - (6.75 * age)
        }

    }
}