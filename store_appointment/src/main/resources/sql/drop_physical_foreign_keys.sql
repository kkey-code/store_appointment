-- Drop physical foreign key constraints if they exist.
-- Keep relationships in application logic and indexes only.

delimiter //

drop procedure if exists drop_fk_if_exists//

create procedure drop_fk_if_exists(in table_name_value varchar(64), in constraint_name_value varchar(64))
begin
    if exists (
        select 1
        from information_schema.referential_constraints
        where constraint_schema = database()
          and table_name = table_name_value
          and constraint_name = constraint_name_value
    ) then
        set @drop_fk_sql = concat(
            'alter table `',
            replace(table_name_value, '`', '``'),
            '` drop foreign key `',
            replace(constraint_name_value, '`', '``'),
            '`'
        );
        prepare drop_fk_stmt from @drop_fk_sql;
        execute drop_fk_stmt;
        deallocate prepare drop_fk_stmt;
    end if;
end//

delimiter ;

call drop_fk_if_exists('appointment', 'fk_appointment_customer');
call drop_fk_if_exists('appointment', 'fk_appointment_employee');
call drop_fk_if_exists('appointment', 'fk_appointment_service_item');

call drop_fk_if_exists('order_info', 'fk_order_appointment');
call drop_fk_if_exists('order_info', 'fk_order_customer');
call drop_fk_if_exists('order_info', 'fk_order_service_item');

call drop_fk_if_exists('user', 'fk_user_employee');

drop procedure if exists drop_fk_if_exists;
