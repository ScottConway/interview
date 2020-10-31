create table if not exists supplier_invoices
(
    supplier_id     varchar(255)       not null,
    invoice_id      varchar(255)       not null,
    invoice_date    date               not null,
    invoice_amount  decimal(20,2)             not null,
    terms           int                not null,
    payment_date    date,
    payment_amount  decimal(20,2),
    primary key (supplier_id, invoice_id)
);

create table if not exists temp_supplier_invoices
(
    supplier_id     varchar(255)       not null,
    invoice_id      varchar(255)       not null,
    invoice_date    date               not null,
    invoice_amount  decimal(20,2)             not null,
    terms           int                not null,
    payment_date    date,
    payment_amount  decimal(20,2),
    primary key (supplier_id, invoice_id)
);