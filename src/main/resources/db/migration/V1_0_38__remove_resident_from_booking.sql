alter table booking
    modify resident_id bigint unsigned null;

alter table booking
    drop foreign key booking_ibfk_2;