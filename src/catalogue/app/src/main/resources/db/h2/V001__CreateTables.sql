CREATE TABLE PRODUCTS (
    sku VARCHAR(20) NOT NULL,
    brand VARCHAR(20),
    title VARCHAR(40),
    description VARCHAR(500),
    weight VARCHAR(10),
    product_size VARCHAR(25),
    colors VARCHAR(20),
    qty INTEGER,
    price FLOAT,
    image_url_1 VARCHAR(50),
    image_url_2 VARCHAR(50),
    PRIMARY KEY(sku)
);
CREATE TABLE CATEGORIES (
    category_id MEDIUMINT AUTO_INCREMENT,
    name VARCHAR(30),
    PRIMARY KEY(category_id)
);
CREATE TABLE PRODUCT_CATEGORY (
    product_category_id MEDIUMINT AUTO_INCREMENT,
    sku VARCHAR(40),
    category_id MEDIUMINT NOT NULL,
    FOREIGN KEY (sku)
    REFERENCES PRODUCTS(sku),
    FOREIGN KEY(category_id)
    REFERENCES CATEGORIES(category_id),
    PRIMARY KEY(product_category_id)
);