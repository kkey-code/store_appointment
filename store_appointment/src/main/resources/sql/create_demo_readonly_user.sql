-- Optional delivery helper.
-- Run this on the recipient's local MySQL after importing the demo database.
-- The app can read data with this account, but cannot insert/update/delete.

create user if not exists 'store_demo'@'localhost' identified by 'store_demo_123456';
alter user 'store_demo'@'localhost' identified by 'store_demo_123456';
revoke all privileges, grant option from 'store_demo'@'localhost';
grant select on store_appointment.* to 'store_demo'@'localhost';
flush privileges;
