
INSERT INTO asset (customer_id, asset_name, size, usable_size) VALUES ('CUST-1', 'TRY', 100000, 100000);
INSERT INTO asset (customer_id, asset_name, size, usable_size) VALUES ('CUST-1', 'ASELS', 500, 500);
INSERT INTO asset (customer_id, asset_name, size, usable_size) VALUES ('CUST-1', 'GARAN', 300, 300);
INSERT INTO asset (customer_id, asset_name, size, usable_size) VALUES ('CUST-2', 'TRY', 50000, 50000);
INSERT INTO asset (customer_id, asset_name, size, usable_size) VALUES ('CUST-2', 'KCHOL', 200, 200);

INSERT INTO orders (customer_id, asset_name, order_side, size, price, status, create_date) VALUES ('CUST-1', 'ASELS', 'BUY', 10, 56.5000, 'PENDING', CURRENT_TIMESTAMP);

INSERT INTO orders (customer_id, asset_name, order_side, size, price, status, create_date) VALUES ('CUST-1', 'THYAO', 'SELL', 5, 23.0000, 'MATCHED', CURRENT_TIMESTAMP);

INSERT INTO orders (customer_id, asset_name, order_side, size, price, status, create_date) VALUES ('CUST-2', 'KRDMD', 'BUY', 20, 12.0000, 'CANCELED', CURRENT_TIMESTAMP);

INSERT INTO orders (customer_id, asset_name, order_side, size, price, status, create_date) VALUES ('CUST-2', 'ASELS', 'SELL', 15, 55.0000, 'PENDING', CURRENT_TIMESTAMP);
