/*
 * Copyright 2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package api.model;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(title = "Categories list")
@Introspected
public class Categories {

    private final String[] categories;

    public Categories(List<String> categories) {
        this.categories = categories.toArray(new String[0]);
    }

    /**
     * An array of category names
     *
     * @return An array of category names
     */
    public String[] getCategories() {
        return categories;
    }
}
