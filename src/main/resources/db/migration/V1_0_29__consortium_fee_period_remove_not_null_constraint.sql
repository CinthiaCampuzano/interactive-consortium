alter table consortium_fee_period
    modify generation_date date null;

alter table consortium_fee_period
    modify due_date date null;

alter table consortium_fee_period
    modify total_amount decimal(10, 2) null;
