CREATE TABLE asset (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       customer_id VARCHAR(50) NOT NULL,
                       asset_name VARCHAR(50) NOT NULL,
                       size BIGINT NOT NULL,
                       usable_size BIGINT NOT NULL
);

CREATE TABLE orders (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        customer_id VARCHAR(50) NOT NULL,
                        asset_name VARCHAR(50) NOT NULL,
                        order_side VARCHAR(10) NOT NULL,
                        size BIGINT NOT NULL,
                        price DOUBLE NOT NULL,
                        status VARCHAR(10) NOT NULL,
                        create_date TIMESTAMP NOT NULL
);
