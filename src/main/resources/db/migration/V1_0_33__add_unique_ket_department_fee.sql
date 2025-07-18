alter table department_fee
    add constraint department_fee_pk
        unique (department_id, consortium_fee_period_id);
