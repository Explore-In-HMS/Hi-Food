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
 * Definition of ObjectType BaseFood.
 *
 * @since 2020-09-29
 */
public class BaseFood extends CloudDBZoneObject {
    @PrimaryKey
    private String id;

    private String name;

    private Float calories;

    private Float carbohydrate;

    private Float protein;

    private Float fat;

    private Integer measurementtype;

    public BaseFood() {
        super();
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCalories(Float calories) {
        this.calories = calories;
    }

    public Float getCalories() {
        return calories;
    }

    public void setCarbohydrate(Float carbohydrate) {
        this.carbohydrate = carbohydrate;
    }

    public Float getCarbohydrate() {
        return carbohydrate;
    }

    public void setProtein(Float protein) {
        this.protein = protein;
    }

    public Float getProtein() {
        return protein;
    }

    public void setFat(Float fat) {
        this.fat = fat;
    }

    public Float getFat() {
        return fat;
    }

    public void setMeasurementtype(Integer measurementtype) {
        this.measurementtype = measurementtype;
    }

    public Integer getMeasurementtype() {
        return measurementtype;
    }

}
