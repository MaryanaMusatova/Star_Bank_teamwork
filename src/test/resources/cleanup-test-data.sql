DELETE FROM transactions WHERE user_id = '123e4567-e89b-12d3-a456-426614174000';
DELETE FROM user_products WHERE user_id = '123e4567-e89b-12d3-a456-426614174000';
DELETE FROM products WHERE id IN (
    '11111111-1111-1111-1111-111111111111',
    '22222222-2222-2222-2222-222222222222'
);
DELETE FROM users WHERE id = '123e4567-e89b-12d3-a456-426614174000';