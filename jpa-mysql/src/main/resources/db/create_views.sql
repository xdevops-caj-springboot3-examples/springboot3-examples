-- create view for order
CREATE OR REPLACE VIEW v_order_summary AS
SELECT oi.item_id, o.order_id, o.order_date, c.name as customer_name, p.product_name, oi.quantity
FROM t_order o
JOIN t_customer c ON o.customer_id = c.customer_id
JOIN t_order_item oi ON o.order_id = oi.order_id
JOIN t_product p ON oi.product_id = p.product_id;