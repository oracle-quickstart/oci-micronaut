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

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "Catalogue item")
@Serdeable
public class CatalogueItem {

    private final String id;
    private final String brand;
    private final String title;
    private final String description;
    private final String weight;
    private final String productSize;
    private final String colors;
    private final int qty;
    private final double price;
    private final String[] imageUrl;
    private final String[] category;

    public CatalogueItem(
            String id,
            String brand,
            String title,
            String description,
            String weight,
            String productSize,
            String colors,
            int qty,
            double price,
            String[] imageUrl,
            String[] category) {
        this.id = id;
        this.brand = brand;
        this.title = title;
        this.description = description;
        this.weight = weight;
        this.productSize = productSize;
        this.colors = colors;
        this.qty = qty;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    /**
     * The id of the product
     */
    public String getId() {
        return id;
    }

    /**
     * The brand of the product
     */
    public String getBrand() {
        return brand;
    }

    /**
     * The title of the product
     */
    public String getTitle() {
        return title;
    }

    /**
     * The description of the product
     */
    public String getDescription() {
        return description;
    }

    /**
     * The weight of the product
     */
    public String getWeight() {
        return weight;
    }

    /**
     * The size of the product
     */
    public String getProductSize() {
        return productSize;
    }

    /**
     * The available colors of the product
     */
    public String getColors() {
        return colors;
    }

    /**
     * The quantity of the product
     */
    public int getQty() {
        return qty;
    }

    /**
     * The price of the product
     */
    public double getPrice() {
        return price;
    }

    /**
     * An array of size 2 with the first image being the thumbnail and the second the full size image
     */
    public String[] getImageUrl() {
        return imageUrl;
    }

    /**
     * The categories of the image
     */
    public String[] getCategory() {
        return category;
    }
}
