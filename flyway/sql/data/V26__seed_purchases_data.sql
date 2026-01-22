INSERT INTO purchases (patient_id, service_id, stripe_payment_intent_id, amount, currency, status, created_at, updated_at)
VALUES
(1, 1, 'pi_3SriAkFgMD80M87Q1KckTest1', 150.00, 'usd', 'SUCCESS', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '5 days'),

(2, 2, 'pi_3SriAkFgMD80M87Q1KckTest2', 500.00, 'usd', 'SUCCESS', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days'),

(1, 3, 'pi_3SriAkFgMD80M87Q1KckTest3', 75.00, 'usd', 'FAILED', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day'),

(2, 1, 'pi_3SriAkFgMD80M87Q1KckTest4', 150.00, 'usd', 'FAILED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
