CREATE TABLE cart (
id INT PRIMARY KEY,
qty INT,
fk_product_id INT
);

CREATE TABLE products (
id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY 
                (START WITH 1, INCREMENT BY 1),
title VARCHAR(30),
price DECIMAL(19,4)
);

ALTER TABLE cart 
ADD CONSTRAINT fk_product_id
FOREIGN KEY (fk_product_id) REFERENCES products(id);

INSERT INTO PRODUCTS(TITLE, PRICE) 
VALUES('water', 1), ('wine', 10), ('beer', 3),
('milk', 2), ('juice', 4) , ('vodka', 8);

--SELECT * FROM PRODUCTS;

INSERT INTO CART VALUES(1, 4, 1), (2, 8, 3), (3, 2, 6);

--SELECT * FROM CART;

/* Show full price for each item type in cart considering qty.*/
SELECT TITLE, QTY, QTY * PRICE AS "Price for all"
FROM CART INNER JOIN PRODUCTS ON CART.FK_PRODUCT_ID = PRODUCTS.ID;

/* Show sum of all items in cart */
SELECT sum(QTY * PRICE) AS "Total price"
FROM CART INNER JOIN PRODUCTS ON CART.FK_PRODUCT_ID = PRODUCTS.ID;
