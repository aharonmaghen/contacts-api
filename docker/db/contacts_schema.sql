CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE public.contacts (
    uuid UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    first_name character varying(100) NOT NULL,
    last_name character varying(100) NOT NULL,
    phone_number character varying(20) NOT NULL,
    address text,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITHOUT TIME ZONE
);
INSERT INTO public.contacts (first_name, last_name, phone_number, address, created_at, updated_at) VALUES 
('Alice', 'Johnson', '123-456-7890', '123 Maple St', '2025-06-10 14:22:05', '2025-06-14 09:15:32'),
('Bob', 'Smith', '234-567-8901', '456 Oak Ave', '2025-06-07 11:05:44', '2025-06-12 17:39:08'),
('Charlie', 'Brown', '345-678-9012', '789 Pine Rd', '2025-06-08 08:30:12', '2025-06-15 21:12:55'),
('Diana', 'Prince', '456-789-0123', '101 Elm St', '2025-06-06 19:45:33', '2025-06-17 10:47:21'),
('Ethan', 'Hunt', '567-890-1234', '202 Cedar Blvd', '2025-06-12 13:04:28', '2025-06-16 18:29:49'),
('Fiona', 'Gallagher', '678-901-2345', '303 Birch Ln', '2025-06-04 16:17:09', '2025-06-08 20:02:37'),
('George', 'Washington', '789-012-3456', '404 Redwood Dr', '2025-06-03 09:22:41', '2025-06-10 14:35:16'),
('Hannah', 'Montana', '890-123-4567', '505 Spruce Way', '2025-06-05 20:41:57', '2025-06-13 11:26:58'),
('Ivan', 'Petrov', '901-234-5678', '606 Chestnut Cir', '2025-06-09 07:13:00', '2025-06-15 08:44:39'),
('Julia', 'Roberts', '012-345-6789', '707 Walnut Ct', '2025-06-11 15:59:19', '2025-06-18 13:03:05'),
('Kevin', 'Hart', '111-222-3333', '808 Sycamore Pl', '2025-06-13 18:40:51', '2025-06-17 22:18:34'),
('Laura', 'Palmer', '222-333-4444', '909 Willow Pkwy', '2025-06-02 12:36:22', '2025-06-07 05:48:16'),
('Mike', 'Tyson', '333-444-5555', '1010 Hickory St', '2025-06-01 10:27:03', '2025-06-09 16:12:20'),
('Nina', 'Williams', '444-555-6666', '1111 Poplar Ave', '2025-06-15 06:51:18', '2025-06-18 19:33:02'),
('Oscar', 'Isaac', '555-666-7777', '1212 Magnolia Blvd', '2025-06-14 21:17:30', '2025-06-19 07:44:57');