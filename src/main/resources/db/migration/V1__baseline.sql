SET FOREIGN_KEY_CHECKS=0;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `additionals` (
  `additional_daily_price` double NOT NULL,
  `additional_id` int NOT NULL AUTO_INCREMENT,
  `max_units_per_rental` smallint NOT NULL,
  `additional_name` varchar(255) NOT NULL,
  PRIMARY KEY (`additional_id`),
  UNIQUE KEY `UK_bow00utfa5a9qns97m5ovgvne` (`additional_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `brands` (
  `brand_id` int NOT NULL AUTO_INCREMENT,
  `brand_name` varchar(255) NOT NULL,
  PRIMARY KEY (`brand_id`),
  UNIQUE KEY `UK_gds2u6k2vfeo1tkrtgwcyqj36` (`brand_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `car_crashes` (
  `car_crash_id` int NOT NULL AUTO_INCREMENT,
  `car_id` int NOT NULL,
  `crash_date` date NOT NULL,
  `crash_valuation` double NOT NULL,
  PRIMARY KEY (`car_crash_id`),
  KEY `FK1buak3wbwdclg3elueie01xks` (`car_id`),
  CONSTRAINT `FK1buak3wbwdclg3elueie01xks` FOREIGN KEY (`car_id`) REFERENCES `cars` (`car_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `car_maintenances` (
  `car_id` int NOT NULL,
  `maintenance_id` int NOT NULL AUTO_INCREMENT,
  `return_date` date DEFAULT NULL,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY (`maintenance_id`),
  KEY `FKi6bm2ob8ljv96g2023lttf4mx` (`car_id`),
  CONSTRAINT `FKi6bm2ob8ljv96g2023lttf4mx` FOREIGN KEY (`car_id`) REFERENCES `cars` (`car_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cars` (
  `brand_id` int NOT NULL,
  `car_id` int NOT NULL AUTO_INCREMENT,
  `color_id` int NOT NULL,
  `daily_price` double NOT NULL,
  `kilometer` int NOT NULL,
  `model_year` int NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`car_id`),
  KEY `FK864li48nqoslem704ihv4ax3a` (`brand_id`),
  KEY `FKpq7j9o208dp4iox1ydwkw65tk` (`color_id`),
  CONSTRAINT `FK864li48nqoslem704ihv4ax3a` FOREIGN KEY (`brand_id`) REFERENCES `brands` (`brand_id`),
  CONSTRAINT `FKpq7j9o208dp4iox1ydwkw65tk` FOREIGN KEY (`color_id`) REFERENCES `colors` (`color_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cities` (
  `city_id` int NOT NULL AUTO_INCREMENT,
  `city_name` varchar(255) NOT NULL,
  PRIMARY KEY (`city_id`),
  UNIQUE KEY `UK_rlmpoah07xxtfr03pmosd593p` (`city_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `colors` (
  `color_id` int NOT NULL AUTO_INCREMENT,
  `color_name` varchar(255) NOT NULL,
  PRIMARY KEY (`color_id`),
  UNIQUE KEY `UK_ggsx4sf2c5i5d8becr67fps3d` (`color_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `corporate_customers` (
  `corporate_customer_id` int NOT NULL,
  `tax_number` varchar(10) NOT NULL,
  `company_name` varchar(255) NOT NULL,
  PRIMARY KEY (`corporate_customer_id`),
  UNIQUE KEY `UK_1yog2i0kpxjf8fn403s818tuc` (`tax_number`),
  UNIQUE KEY `UK_1xg10yromtio76nlg6ugiqm57` (`company_name`),
  CONSTRAINT `FKkkk9d1mhae241xumpax3g0gue` FOREIGN KEY (`corporate_customer_id`) REFERENCES `customers` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `credit_cards` (
  `card_cvv` varchar(3) NOT NULL,
  `credit_card_id` int NOT NULL AUTO_INCREMENT,
  `customer_id` int NOT NULL,
  `card_number` varchar(16) NOT NULL,
  `card_expiration_date` varchar(255) NOT NULL,
  `card_owner` varchar(255) NOT NULL,
  PRIMARY KEY (`credit_card_id`),
  UNIQUE KEY `UK_rsfixpi1crklj327162ylmq5h` (`card_number`),
  KEY `FKtchnmemki04fmvtx53e83cuo6` (`customer_id`),
  CONSTRAINT `FKtchnmemki04fmvtx53e83cuo6` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customers` (
  `customer_id` int NOT NULL,
  `registration_date` date DEFAULT NULL,
  PRIMARY KEY (`customer_id`),
  CONSTRAINT `FKcl0jfte39nemdhxc8cjkb26ds` FOREIGN KEY (`customer_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `individua_customers` (
  `individual_customer_id` int NOT NULL,
  `national_identity` varchar(11) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  PRIMARY KEY (`individual_customer_id`),
  UNIQUE KEY `UK_ot4h40ky4xw6ji6yl25p1tdeg` (`national_identity`),
  CONSTRAINT `FKigd2w4suw5he8tt6ilh3c0c3o` FOREIGN KEY (`individual_customer_id`) REFERENCES `customers` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invoices` (
  `creation_date` date DEFAULT NULL,
  `customer_id` int NOT NULL,
  `finish_date` date NOT NULL,
  `invoice_id` int NOT NULL AUTO_INCREMENT,
  `payment_id` int NOT NULL,
  `price_of_additional` double DEFAULT NULL,
  `price_of_days` double NOT NULL,
  `price_of_diff_city` double DEFAULT NULL,
  `rental_car_id` int NOT NULL,
  `rental_car_total_price` double NOT NULL,
  `start_date` date NOT NULL,
  `total_rental_day` smallint NOT NULL,
  `invoice_no` varchar(16) NOT NULL,
  PRIMARY KEY (`invoice_id`),
  UNIQUE KEY `UK_8cfbd92rwby0aqvqocuv6aa05` (`payment_id`),
  UNIQUE KEY `UK_465kfg5i8gam6kc8fnb96fqce` (`invoice_no`),
  KEY `FKq2w4hmh6l9othnp6cepp0cfe2` (`customer_id`),
  KEY `FKm273mgcp130rwpm5bih5fuvks` (`rental_car_id`),
  CONSTRAINT `FKm273mgcp130rwpm5bih5fuvks` FOREIGN KEY (`rental_car_id`) REFERENCES `rental_cars` (`rental_car_id`),
  CONSTRAINT `FKq2w4hmh6l9othnp6cepp0cfe2` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
  CONSTRAINT `FKq6fs19k0gqw3rg0mb87h60h6p` FOREIGN KEY (`payment_id`) REFERENCES `payments` (`payment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ordered_additionals` (
  `additional_id` int NOT NULL,
  `ordered_additional_id` int NOT NULL AUTO_INCREMENT,
  `ordered_additional_quantity` smallint NOT NULL,
  `rental_car_id` int NOT NULL,
  PRIMARY KEY (`ordered_additional_id`),
  KEY `FKd3iycqkwe5aft3ijkso8xb9x6` (`additional_id`),
  KEY `FKh2t6ca53ovhna7nm4k87q6es8` (`rental_car_id`),
  CONSTRAINT `FKd3iycqkwe5aft3ijkso8xb9x6` FOREIGN KEY (`additional_id`) REFERENCES `additionals` (`additional_id`),
  CONSTRAINT `FKh2t6ca53ovhna7nm4k87q6es8` FOREIGN KEY (`rental_car_id`) REFERENCES `rental_cars` (`rental_car_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `payment_id` int NOT NULL AUTO_INCREMENT,
  `rental_car_id` int NOT NULL,
  `total_price` double NOT NULL,
  PRIMARY KEY (`payment_id`),
  KEY `FK7o5df8m7o334ljvdek15u6c73` (`rental_car_id`),
  CONSTRAINT `FK7o5df8m7o334ljvdek15u6c73` FOREIGN KEY (`rental_car_id`) REFERENCES `rental_cars` (`rental_car_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rental_cars` (
  `car_id` int NOT NULL,
  `customer_id` int NOT NULL,
  `delivered_city` int NOT NULL,
  `delivered_kilometer` int DEFAULT NULL,
  `finish_date` date NOT NULL,
  `rental_car_id` int NOT NULL AUTO_INCREMENT,
  `rented_city` int NOT NULL,
  `rented_kilometer` int DEFAULT NULL,
  `start_date` date NOT NULL,
  PRIMARY KEY (`rental_car_id`),
  KEY `FKd47mo260mukrunrsi5hhuuyg2` (`car_id`),
  KEY `FKt2rjjbpcbi0kw95fchpatuxja` (`customer_id`),
  KEY `FK6ow6fstjpws71o33pkaon5xlx` (`delivered_city`),
  KEY `FKivtv7utcc4hs7ged4588fusej` (`rented_city`),
  CONSTRAINT `FK6ow6fstjpws71o33pkaon5xlx` FOREIGN KEY (`delivered_city`) REFERENCES `cities` (`city_id`),
  CONSTRAINT `FKd47mo260mukrunrsi5hhuuyg2` FOREIGN KEY (`car_id`) REFERENCES `cars` (`car_id`),
  CONSTRAINT `FKivtv7utcc4hs7ged4588fusej` FOREIGN KEY (`rented_city`) REFERENCES `cities` (`city_id`),
  CONSTRAINT `FKt2rjjbpcbi0kw95fchpatuxja` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UK_6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
SET FOREIGN_KEY_CHECKS=1;
