-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: localhost    Database: stockmanagement
-- ------------------------------------------------------
-- Server version   8.0.45

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
                            `id`          INT NOT NULL AUTO_INCREMENT,
                            `name`        VARCHAR(100) DEFAULT NULL,
                            `price`       DOUBLE DEFAULT NULL,
                            `stock`       INT DEFAULT NULL,
                            `category`    VARCHAR(100) DEFAULT NULL,
                            `minQuantity` INT DEFAULT '10',
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES
                           (1,  'Laptop',           50000,  15, 'Electronics',  10),
                           (2,  'Mouse',              500,   5, 'Accessories',  10),
                           (3,  'Keyboard',          1500,   8, 'Accessories',  10),
                           (4,  'Speaker',            500,   2, 'Electronics',  10),
                           (5,  'Printer',           1500,   5, 'Electronics',  10),
                           (6,  'Headphones',         800,   3, 'Accessories',  10),
                           (7,  'Monitor',          12000,  20, 'Electronics',  10),
                           (8,  'Webcam',            2500,   3, 'Accessories',  10),
                           (9,  'USB Hub',            800,   7, 'Accessories',  10),
                           (10, 'SSD 1TB',           6000,  12, 'Storage',      10),
                           (11, 'RAM 16GB',          4000,   2, 'Components',   10),
                           (12, 'Graphics Card',    35000,   4, 'Components',   10),
                           (13, 'Motherboard',      15000,   6, 'Components',   10),
                           (14, 'Power Supply',      5000,   9, 'Components',   10),
                           (15, 'Cabinet',           3000,  11, 'Accessories',  10),
                           (16, 'Cooling Fan',       1200,   5, 'Components',   10),
                           (17, 'External HDD 2TB',  5500,  14, 'Storage',      10),
                           (18, 'Flash Drive 64GB',   400,  25, 'Storage',      10),
                           (19, 'SD Card 128GB',      800,  18, 'Storage',      10),
                           (20, 'Network Switch',    3500,   8, 'Networking',   10),
                           (21, 'Router',            4500,   6, 'Networking',   10),
                           (22, 'Ethernet Cable',     200,  30, 'Networking',   10),
                           (23, 'HDMI Cable',         300,  22, 'Accessories',  10),
                           (24, 'USB-C Cable',        250,  19, 'Accessories',  10),
                           (25, 'Laptop Stand',      1500,   7, 'Accessories',  10),
                           (26, 'Mouse Pad',          300,  16, 'Accessories',  10),
                           (27, 'Mechanical Keyboard',3500,  9, 'Accessories',  10),
                           (28, 'Gaming Mouse',      2500,  11, 'Accessories',  10),
                           (29, 'Gaming Headset',    3000,   4, 'Accessories',  10),
                           (30, 'Microphone',        4500,   5, 'Audio',        10),
                           (31, 'Webcam HD',         3500,   8, 'Accessories',  10),
                           (32, 'Smart TV 43 inch', 35000,  13, 'Electronics',  10),
                           (33, 'Projector',        25000,   3, 'Electronics',  10),
                           (34, 'Tablet',           20000,   7, 'Electronics',  10),
                           (35, 'Smartwatch',        8000,   9, 'Electronics',  10),
                           (36, 'Bluetooth Speaker', 2000,   6, 'Audio',        10),
                           (37, 'Earbuds',           1500,  12, 'Audio',        10),
                           (38, 'Power Bank 20000mAh',1800,  8, 'Accessories',  10),
                           (39, 'Laptop Bag',        1200,  14, 'Accessories',  10),
                           (40, 'Screen Cleaner',     200,  20, 'Accessories',  10),
                           (41, 'Cable Organizer',    300,  17, 'Accessories',  10),
                           (42, 'UPS 600VA',         5000,   5, 'Electronics',  10),
                           (43, 'Surge Protector',   1000,  10, 'Electronics',  10),
                           (44, 'CPU Processor',    20000,   4, 'Components',   10),
                           (45, 'Thermal Paste',      200,  15, 'Components',   10),
                           (46, 'NVME SSD 512GB',    4500,   8, 'Storage',      10),
                           (47, 'DVD Drive',          800,   6, 'Storage',      10),
                           (48, 'Wireless Keyboard', 1800,  11, 'Accessories',  10),
                           (49, 'Wireless Mouse',    1200,   9, 'Accessories',  10),
                           (50, 'Smart Speaker',     3500,   7, 'Electronics',  10),
                           (51, 'Action Camera',    15000,   5, 'Electronics',  10),
                           (52, 'Tripod Stand',      2000,   8, 'Accessories',  10),
                           (53, 'Green Screen',      1500,   6, 'Accessories',  10),
                           (54, 'Ring Light',        2500,   4, 'Accessories',  10),
                           (55, 'Drawing Tablet',    8000,   3, 'Electronics',  10),
                           (56, 'VR Headset',       45000,   2, 'Electronics',  10);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-05 15:29:51