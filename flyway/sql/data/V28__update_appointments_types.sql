UPDATE appointments
SET type = 'TEST'
WHERE id IN (SELECT DISTINCT appointment_id
             FROM lab_results);


update appointments
set type = 'ONLINE'
where id = 3;
