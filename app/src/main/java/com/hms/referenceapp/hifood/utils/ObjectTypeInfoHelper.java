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
package com.hms.referenceapp.hifood.utils;

import com.hms.referenceapp.hifood.remote.model.Action;
import com.hms.referenceapp.hifood.remote.model.BaseFood;
import com.hms.referenceapp.hifood.remote.model.Convenience;
import com.hms.referenceapp.hifood.remote.model.Ingredient;
import com.hms.referenceapp.hifood.remote.model.Recipe;
import com.hms.referenceapp.hifood.remote.model.RecipeIngredient;
import com.hms.referenceapp.hifood.remote.model.User;
import com.hms.referenceapp.hifood.remote.model.UserActionLog;
import com.hms.referenceapp.hifood.remote.model.UserConvenienceLog;
import com.hms.referenceapp.hifood.remote.model.UserRecipeLog;
import com.huawei.agconnect.cloud.database.ObjectTypeInfo;

import java.util.Arrays;

/**
 * Definition of ObjectType Helper.
 *
 * @since 2020-09-29
 */
public class ObjectTypeInfoHelper {
    private final static int FORMAT_VERSION = 1;
    private final static int OBJECT_TYPE_VERSION = 41;

    public static ObjectTypeInfo getObjectTypeInfo() {
        ObjectTypeInfo objectTypeInfo = new ObjectTypeInfo();
        objectTypeInfo.setFormatVersion(FORMAT_VERSION);
        objectTypeInfo.setObjectTypeVersion(OBJECT_TYPE_VERSION);
        objectTypeInfo.setObjectTypes(Arrays.asList(Convenience.class, Action.class, UserActionLog.class, User.class, UserRecipeLog.class, Ingredient.class, Recipe.class, BaseFood.class, RecipeIngredient.class, UserConvenienceLog.class));
        return objectTypeInfo;
    }
}
