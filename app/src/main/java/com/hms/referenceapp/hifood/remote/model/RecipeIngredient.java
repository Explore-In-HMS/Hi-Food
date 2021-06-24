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
package com.hms.referenceapp.hifood.remote.model;

import com.huawei.agconnect.cloud.database.CloudDBZoneObject;
import com.huawei.agconnect.cloud.database.annotations.PrimaryKey;

/**
 * Definition of ObjectType RecipeIngredient.
 *
 * @since 2020-09-29
 */
public class RecipeIngredient extends CloudDBZoneObject {
    @PrimaryKey
    private String id;

    private String recipeid;

    private String ingredientid;

    public RecipeIngredient() {
        super();

    }
    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setRecipeid(String recipeid) {
        this.recipeid = recipeid;
    }

    public String getRecipeid() {
        return recipeid;
    }

    public void setIngredientid(String ingredientid) {
        this.ingredientid = ingredientid;
    }

    public String getIngredientid() {
        return ingredientid;
    }

}
