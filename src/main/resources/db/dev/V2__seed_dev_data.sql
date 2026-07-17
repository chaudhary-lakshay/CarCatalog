-- ===============================================================
-- RENT-A-CAR SAMPLE DATA
-- ===============================================================
-- This file contains sample data for the Rent-A-Car application.
-- It will be automatically loaded by Spring Boot on application startup.
--
-- The data includes:
-- - Users (individual and corporate)
-- - Cars, brands, and colors
-- - Rental transactions and related records
-- - Payments and invoices
--
-- Note: The data is inserted in the correct order to maintain foreign key relationships.
-- All required fields are included for each entity.
-- ===============================================================

-- 👤 Users Data (with BCrypt-encoded passwords)
-- Original passwords are commented for reference
-- password123 -> BCrypt encoded
INSERT INTO users (user_id, email, password) VALUES (1, 'john.doe@example.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG');
-- securepass456 -> BCrypt encoded
INSERT INTO users (user_id, email, password) VALUES (2, 'jane.smith@example.com', '$2a$10$E2UPv7arXmp3q0LzVzCBNeb4B4AtbTAGjkefVDnSztOwE7Gix6kea');
-- mikepass789 -> BCrypt encoded
INSERT INTO users (user_id, email, password) VALUES (3, 'mike.wilson@example.com', '$2a$10$YEKRDNfjBi8UaZOzf.K1pON5cgNwEJ3ybXKE.31dtR.uSrFTRXe.m');
-- sarahj2023 -> BCrypt encoded
INSERT INTO users (user_id, email, password) VALUES (4, 'sarah.johnson@example.com', '$2a$10$lqWUA3LJbUPMbOuAHJiOgOQI9ZpjJTWHFf1dQQbFUdx8/Q.sCmgYS');
-- robert2023 -> BCrypt encoded
INSERT INTO users (user_id, email, password) VALUES (5, 'robert.brown@example.com', '$2a$10$lPUTXhnVfZ7GyNRpwgS2NOYgxkBE3IOiOuUMWUQiHMJ/KQHX8sbGe');
-- acmepass123 -> BCrypt encoded
INSERT INTO users (user_id, email, password) VALUES (6, 'acme.corp@business.com', '$2a$10$O.hrbBPCioVm237nAHYQ5OZy6k15TOoQSFhloUKVmLnk.t9G0n.RW');
-- globex2023 -> BCrypt encoded
INSERT INTO users (user_id, email, password) VALUES (7, 'globex.inc@business.com', '$2a$10$gs.Vlq1m1jJIzoQsVJQU8uS9Az0yjS2Jl9z6IxjRuu/h2pHQbGEKm');
-- waynetech456 -> BCrypt encoded
INSERT INTO users (user_id, email, password) VALUES (8, 'wayne.enterprises@business.com', '$2a$10$k5NEcti.C/fA1KQ5QB9Ude3VWwU2UtXUcXYOJkeA8803tCN3g5N3a');

-- Add an admin user with email ending with @admin.rentacar.com
-- admin123 -> BCrypt encoded
INSERT INTO users (user_id, email, password) VALUES (9, 'admin@admin.rentacar.com', '$2a$10$OwUoWvA6rXQ1.XzLxk/tg.XD.0z0Wr5ATNtEjO/fDDJ9HK9g0/9KO');

-- 🧑‍💼 Customers Data
INSERT INTO customers (customer_id, registration_date) VALUES (1, '2023-01-15');
INSERT INTO customers (customer_id, registration_date) VALUES (2, '2023-02-20');
INSERT INTO customers (customer_id, registration_date) VALUES (3, '2023-03-10');
INSERT INTO customers (customer_id, registration_date) VALUES (4, '2023-04-05');
INSERT INTO customers (customer_id, registration_date) VALUES (5, '2023-05-12');
INSERT INTO customers (customer_id, registration_date) VALUES (6, '2023-01-10');
INSERT INTO customers (customer_id, registration_date) VALUES (7, '2023-02-15');
INSERT INTO customers (customer_id, registration_date) VALUES (8, '2023-03-20');
INSERT INTO customers (customer_id, registration_date) VALUES (9, '2023-06-01');

-- 👨‍👩‍👧‍👦 Individual Customers Data
INSERT INTO individua_customers (individual_customer_id, first_name, last_name, national_identity) VALUES (1, 'John', 'Doe', '12345678901');
INSERT INTO individua_customers (individual_customer_id, first_name, last_name, national_identity) VALUES (2, 'Jane', 'Smith', '23456789012');
INSERT INTO individua_customers (individual_customer_id, first_name, last_name, national_identity) VALUES (3, 'Mike', 'Wilson', '34567890123');
INSERT INTO individua_customers (individual_customer_id, first_name, last_name, national_identity) VALUES (4, 'Sarah', 'Johnson', '45678901234');
INSERT INTO individua_customers (individual_customer_id, first_name, last_name, national_identity) VALUES (5, 'Robert', 'Brown', '56789012345');
INSERT INTO individua_customers (individual_customer_id, first_name, last_name, national_identity) VALUES (9, 'Admin', 'User', '99999999999');

-- 🏢 Corporate Customers Data
INSERT INTO corporate_customers (corporate_customer_id, company_name, tax_number) VALUES (6, 'Acme Corporation', '1234567890');
INSERT INTO corporate_customers (corporate_customer_id, company_name, tax_number) VALUES (7, 'Globex Inc.', '2345678901');
INSERT INTO corporate_customers (corporate_customer_id, company_name, tax_number) VALUES (8, 'Wayne Enterprises', '3456789012');

-- 🏙️ Cities Data
INSERT INTO cities (city_id, city_name) VALUES (1, 'New York');
INSERT INTO cities (city_id, city_name) VALUES (2, 'Los Angeles');
INSERT INTO cities (city_id, city_name) VALUES (3, 'Chicago');
INSERT INTO cities (city_id, city_name) VALUES (4, 'Houston');
INSERT INTO cities (city_id, city_name) VALUES (5, 'Miami');

-- 🚗 Brands Data
INSERT INTO brands (brand_id, brand_name) VALUES (1, 'Toyota');
INSERT INTO brands (brand_id, brand_name) VALUES (2, 'Honda');
INSERT INTO brands (brand_id, brand_name) VALUES (3, 'Ford');
INSERT INTO brands (brand_id, brand_name) VALUES (4, 'BMW');
INSERT INTO brands (brand_id, brand_name) VALUES (5, 'Mercedes-Benz');

-- 🎨 Colors Data
INSERT INTO colors (color_id, color_name) VALUES (1, 'Black');
INSERT INTO colors (color_id, color_name) VALUES (2, 'White');
INSERT INTO colors (color_id, color_name) VALUES (3, 'Silver');
INSERT INTO colors (color_id, color_name) VALUES (4, 'Red');
INSERT INTO colors (color_id, color_name) VALUES (5, 'Blue');

-- 🚙 Cars Data
INSERT INTO cars (car_id, daily_price, model_year, description, kilometer, brand_id, color_id)
VALUES (1, 50.00, 2022, 'Compact sedan with great fuel economy', 15000, 1, 2);
INSERT INTO cars (car_id, daily_price, model_year, description, kilometer, brand_id, color_id)
VALUES (2, 65.00, 2021, 'Midsize SUV with plenty of cargo space', 22000, 2, 1);
INSERT INTO cars (car_id, daily_price, model_year, description, kilometer, brand_id, color_id)
VALUES (3, 45.00, 2020, 'Economy car with excellent reliability', 35000, 1, 3);
INSERT INTO cars (car_id, daily_price, model_year, description, kilometer, brand_id, color_id)
VALUES (4, 120.00, 2023, 'Luxury sedan with premium features', 5000, 4, 1);
INSERT INTO cars (car_id, daily_price, model_year, description, kilometer, brand_id, color_id)
VALUES (5, 150.00, 2022, 'Executive class vehicle with leather interior', 12000, 5, 2);
INSERT INTO cars (car_id, daily_price, model_year, description, kilometer, brand_id, color_id)
VALUES (6, 70.00, 2021, 'Compact crossover with all-wheel drive', 18000, 3, 5);
INSERT INTO cars (car_id, daily_price, model_year, description, kilometer, brand_id, color_id)
VALUES (7, 90.00, 2022, 'Midsize sedan with advanced safety features', 9000, 2, 4);

-- 🛠️ Car Maintenances Data
INSERT INTO car_maintenances (maintenance_id, description, return_Date, car_id)
VALUES (1, 'Regular oil change and filter replacement', '2023-06-15', 3);
INSERT INTO car_maintenances (maintenance_id, description, return_Date, car_id)
VALUES (2, 'Brake pad replacement', '2023-06-20', 2);
INSERT INTO car_maintenances (maintenance_id, description, return_Date, car_id)
VALUES (3, 'Annual inspection and tune-up', '2023-07-05', 5);

-- 💥 Car Crashes Data
INSERT INTO car_crashes (car_crash_id, crash_date, crash_valuation, car_id)
VALUES (1, '2023-04-10', 1200.00, 2);
INSERT INTO car_crashes (car_crash_id, crash_date, crash_valuation, car_id)
VALUES (2, '2023-05-22', 3500.00, 6);
INSERT INTO car_crashes (car_crash_id, crash_date, crash_valuation, car_id)
VALUES (3, '2023-03-15', 800.00, 3);

-- 🧩 Additionals Data (Extra services/features)
INSERT INTO additionals (additional_id, additional_name, additional_daily_price, max_units_per_rental)
VALUES (1, 'GPS Navigation', 5.00, 1);
INSERT INTO additionals (additional_id, additional_name, additional_daily_price, max_units_per_rental)
VALUES (2, 'Child Seat', 7.50, 3);
INSERT INTO additionals (additional_id, additional_name, additional_daily_price, max_units_per_rental)
VALUES (3, 'Additional Driver', 10.00, 2);
INSERT INTO additionals (additional_id, additional_name, additional_daily_price, max_units_per_rental)
VALUES (4, 'Wi-Fi Hotspot', 8.00, 1);
INSERT INTO additionals (additional_id, additional_name, additional_daily_price, max_units_per_rental)
VALUES (5, 'Roof Rack', 6.50, 1);

-- 🚗 Rental Cars Data
INSERT INTO rental_cars (rental_car_id, start_date, finish_date, car_id, customer_id, rented_city, delivered_city, rented_kilometer, delivered_kilometer)
VALUES (1, '2023-06-10', '2023-06-15', 1, 1, 1, 1, 15000, 15300);
INSERT INTO rental_cars (rental_car_id, start_date, finish_date, car_id, customer_id, rented_city, delivered_city, rented_kilometer, delivered_kilometer)
VALUES (2, '2023-06-12', '2023-06-18', 4, 2, 2, 3, 5000, 5500);
INSERT INTO rental_cars (rental_car_id, start_date, finish_date, car_id, customer_id, rented_city, delivered_city, rented_kilometer, delivered_kilometer)
VALUES (3, '2023-06-15', '2023-06-20', 5, 6, 3, 3, 12000, 12400);
INSERT INTO rental_cars (rental_car_id, start_date, finish_date, car_id, customer_id, rented_city, delivered_city, rented_kilometer, delivered_kilometer)
VALUES (4, '2023-06-18', '2023-06-25', 7, 7, 1, 5, 9000, 9800);
INSERT INTO rental_cars (rental_car_id, start_date, finish_date, car_id, customer_id, rented_city, delivered_city, rented_kilometer, delivered_kilometer)
VALUES (5, '2023-06-20', '2023-06-22', 6, 3, 4, 4, 18000, 18200);

-- 🧩 Ordered Additionals Data
INSERT INTO ordered_additionals (ordered_additional_id, ordered_additional_quantity, additional_id, rental_car_id)
VALUES (1, 1, 1, 1);
INSERT INTO ordered_additionals (ordered_additional_id, ordered_additional_quantity, additional_id, rental_car_id)
VALUES (2, 2, 2, 2);
INSERT INTO ordered_additionals (ordered_additional_id, ordered_additional_quantity, additional_id, rental_car_id)
VALUES (3, 1, 3, 3);
INSERT INTO ordered_additionals (ordered_additional_id, ordered_additional_quantity, additional_id, rental_car_id)
VALUES (4, 1, 4, 4);
INSERT INTO ordered_additionals (ordered_additional_id, ordered_additional_quantity, additional_id, rental_car_id)
VALUES (5, 1, 5, 2);
INSERT INTO ordered_additionals (ordered_additional_id, ordered_additional_quantity, additional_id, rental_car_id)
VALUES (6, 1, 1, 5);

-- 💳 Payments Data
INSERT INTO payments (payment_id, total_price, rental_car_id)
VALUES (1, 250.00, 1);
INSERT INTO payments (payment_id, total_price, rental_car_id)
VALUES (2, 840.00, 2);
INSERT INTO payments (payment_id, total_price, rental_car_id)
VALUES (3, 750.00, 3);
INSERT INTO payments (payment_id, total_price, rental_car_id)
VALUES (4, 720.00, 4);
INSERT INTO payments (payment_id, total_price, rental_car_id)
VALUES (5, 146.50, 5);

-- 📄 Invoices Data
INSERT INTO invoices (invoice_id, invoice_no, creation_date, start_date, finish_date, total_rental_day, price_of_days, price_of_diff_city, price_of_additional, rental_car_total_price, rental_car_id, customer_id, payment_id)
VALUES (1, '2023061000001', '2023-06-10', '2023-06-10', '2023-06-15', 5, 250.00, 0.00, 25.00, 275.00, 1, 1, 1);
INSERT INTO invoices (invoice_id, invoice_no, creation_date, start_date, finish_date, total_rental_day, price_of_days, price_of_diff_city, price_of_additional, rental_car_total_price, rental_car_id, customer_id, payment_id)
VALUES (2, '2023061200001', '2023-06-12', '2023-06-12', '2023-06-18', 6, 720.00, 50.00, 70.00, 840.00, 2, 2, 2);
INSERT INTO invoices (invoice_id, invoice_no, creation_date, start_date, finish_date, total_rental_day, price_of_days, price_of_diff_city, price_of_additional, rental_car_total_price, rental_car_id, customer_id, payment_id)
VALUES (3, '2023061500001', '2023-06-15', '2023-06-15', '2023-06-20', 5, 750.00, 0.00, 50.00, 800.00, 3, 6, 3);
INSERT INTO invoices (invoice_id, invoice_no, creation_date, start_date, finish_date, total_rental_day, price_of_days, price_of_diff_city, price_of_additional, rental_car_total_price, rental_car_id, customer_id, payment_id)
VALUES (4, '2023061800001', '2023-06-18', '2023-06-18', '2023-06-25', 7, 630.00, 50.00, 40.00, 720.00, 4, 7, 4);
INSERT INTO invoices (invoice_id, invoice_no, creation_date, start_date, finish_date, total_rental_day, price_of_days, price_of_diff_city, price_of_additional, rental_car_total_price, rental_car_id, customer_id, payment_id)
VALUES (5, '2023062000001', '2023-06-20', '2023-06-20', '2023-06-22', 2, 140.00, 0.00, 6.50, 146.50, 5, 3, 5);

-- 💳 Credit Cards Data
INSERT INTO credit_cards (credit_card_id, card_number, card_owner, card_cvv, card_expiration_date, customer_id)
VALUES (1, '4111111111111111', 'John Doe', '123', '12/25', 1);
INSERT INTO credit_cards (credit_card_id, card_number, card_owner, card_cvv, card_expiration_date, customer_id)
VALUES (2, '5555555555554444', 'Jane Smith', '456', '10/24', 2);
INSERT INTO credit_cards (credit_card_id, card_number, card_owner, card_cvv, card_expiration_date, customer_id)
VALUES (3, '3700000000000002', 'Mike Wilson', '789', '03/26', 3);
INSERT INTO credit_cards (credit_card_id, card_number, card_owner, card_cvv, card_expiration_date, customer_id)
VALUES (4, '6011111111111117', 'Acme Corporation', '321', '05/25', 6);
INSERT INTO credit_cards (credit_card_id, card_number, card_owner, card_cvv, card_expiration_date, customer_id)
VALUES (5, '3566002020360505', 'Globex Inc.', '654', '08/24', 7);
