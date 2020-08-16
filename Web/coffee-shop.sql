-- phpMyAdmin SQL Dump
-- version 4.9.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: May 09, 2020 at 04:48 PM
-- Server version: 10.4.10-MariaDB
-- PHP Version: 7.3.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `coffee-shop`
--

-- --------------------------------------------------------

--
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
CREATE TABLE IF NOT EXISTS `address` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8mb4_bin NOT NULL,
  `lat` float NOT NULL,
  `lng` float NOT NULL,
  `address` varchar(150) COLLATE utf8mb4_bin NOT NULL,
  `uid` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

--
-- Dumping data for table `address`
--

INSERT INTO `address` (`id`, `name`, `lat`, `lng`, `address`, `uid`) VALUES
(11, 'خونه 😀', 36.3711, 54.971, 'شهرک نوین، بلوار گلها، غربی سوم، خانه طرح درخت', 1),
(13, 'Home', 37.422, -122.084, 'Google Plex', 2),
(14, 'my Home Place', 37.422, -122.084, 'my home Address', 2);

-- --------------------------------------------------------

--
-- Table structure for table `card`
--

DROP TABLE IF EXISTS `card`;
CREATE TABLE IF NOT EXISTS `card` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `image` varchar(50) COLLATE utf8mb4_bin NOT NULL,
  `alt` varchar(50) COLLATE utf8mb4_bin NOT NULL,
  `time` datetime NOT NULL DEFAULT current_timestamp(),
  `link` varchar(50) COLLATE utf8mb4_bin NOT NULL,
  `color` varchar(9) COLLATE utf8mb4_bin NOT NULL DEFAULT '#00000000',
  `position` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

--
-- Dumping data for table `card`
--

INSERT INTO `card` (`id`, `image`, `alt`, `time`, `link`, `color`, `position`) VALUES
(1, 'coffee_banner.png', 'چالش کافی شاپ', '2020-04-30 00:20:45', 'post:2', '#4C2F1F', 1),
(2, 'banner_1.png', 'تخفیف برای کافی های اسپرسو', '2020-04-30 09:13:15', 'post:1', '#4C2F1F', 0);

-- --------------------------------------------------------

--
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
CREATE TABLE IF NOT EXISTS `cart` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `itemId` int(11) NOT NULL,
  `userId` varchar(50) COLLATE utf8mb4_bin NOT NULL,
  `count` int(11) NOT NULL,
  `size` varchar(10) COLLATE utf8mb4_bin NOT NULL,
  `orderId` int(11) NOT NULL DEFAULT -1,
  `status` int(11) NOT NULL DEFAULT 0,
  `cartPrice` float NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=318 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- --------------------------------------------------------

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
CREATE TABLE IF NOT EXISTS `category` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(25) COLLATE utf8mb4_bin NOT NULL,
  `information` varchar(50) COLLATE utf8mb4_bin NOT NULL,
  `image` varchar(25) COLLATE utf8mb4_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

--
-- Dumping data for table `category`
--

INSERT INTO `category` (`id`, `name`, `information`, `image`) VALUES
(1, 'قهوه', 'انواع مدل قهوه', 'img/Category-Coffee.png'),
(2, 'چای', 'چای و دمنوش', 'img/Category-Tea.png'),
(3, 'آب میوه', 'انواع مدل آب میوه های تازه', 'img/Category-Juice.png'),
(8, 'بستنی', 'بستنی های سنتی و میوه ای', 'img/category/بستنی.png');

-- --------------------------------------------------------

--
-- Table structure for table `discount`
--

DROP TABLE IF EXISTS `discount`;
CREATE TABLE IF NOT EXISTS `discount` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(50) COLLATE utf8mb4_bin NOT NULL,
  `code` varchar(20) COLLATE utf8mb4_bin NOT NULL,
  `value` int(11) NOT NULL,
  `status` int(11) NOT NULL,
  `time` datetime NOT NULL DEFAULT current_timestamp(),
  `expiration` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

--
-- Dumping data for table `discount`
--

INSERT INTO `discount` (`id`, `title`, `code`, `value`, `status`, `time`, `expiration`) VALUES
(1, 'تخفیف ویژه برای اسپرسو', 'espresso99', 45, 0, '2020-05-08 00:00:00', '2020-05-15 00:00:00');

-- --------------------------------------------------------

--
-- Table structure for table `discount_items`
--

DROP TABLE IF EXISTS `discount_items`;
CREATE TABLE IF NOT EXISTS `discount_items` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `itemId` int(11) NOT NULL,
  `discountId` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `itemId` (`itemId`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

--
-- Dumping data for table `discount_items`
--

INSERT INTO `discount_items` (`id`, `itemId`, `discountId`) VALUES
(1, 1, 1),
(2, 2, 1);

-- --------------------------------------------------------

--
-- Table structure for table `feed`
--

DROP TABLE IF EXISTS `feed`;
CREATE TABLE IF NOT EXISTS `feed` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(50) COLLATE utf8mb4_bin NOT NULL,
  `description` mediumtext COLLATE utf8mb4_bin NOT NULL,
  `time` datetime NOT NULL DEFAULT current_timestamp(),
  `action` int(11) NOT NULL,
  `position` int(11) NOT NULL DEFAULT 1,
  `status` int(11) NOT NULL DEFAULT 1,
  `color` varchar(10) COLLATE utf8mb4_bin NOT NULL DEFAULT '0x00000000',
  `image` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

--
-- Dumping data for table `feed`
--

INSERT INTO `feed` (`id`, `title`, `description`, `time`, `action`, `position`, `status`, `color`, `image`) VALUES
(1, 'برترین ها', 'پرفروش ترین محصولات', '2020-04-28 00:00:00', 2, 1, 1, '#00000000', NULL),
(2, 'جدید ترین ها', 'جدید ترین محصولاتی که به کافی شاپ افزوده شده اند', '2020-04-29 00:37:39', 4, 1, 1, '#00000000', NULL),
(3, 'آخرین سفارش', 'آخرین سفارش شما از نرم افزار کافی شاپ', '2020-04-29 00:41:45', 3, 2, 1, '#00000000', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `items`
--

DROP TABLE IF EXISTS `items`;
CREATE TABLE IF NOT EXISTS `items` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(20) COLLATE utf8mb4_bin NOT NULL,
  `information` varchar(250) COLLATE utf8mb4_bin NOT NULL,
  `image` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(20) COLLATE utf8mb4_bin NOT NULL,
  `category` int(11) NOT NULL,
  `sizes` varchar(150) COLLATE utf8mb4_bin NOT NULL,
  `price` float NOT NULL,
  `dateCreate` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

--
-- Dumping data for table `items`
--

INSERT INTO `items` (`id`, `title`, `information`, `image`, `type`, `category`, `sizes`, `price`, `dateCreate`) VALUES
(1, 'سینگل', 'اسپرسو نوعی قهوهٔ غلیظ است که در اوایل قرن بیستم در تورین ایتالیا ابداع شده‌است. اسپرسو با عبور دادن آب نزدیک به نقطه جوش با فشار زیاد از میان دانه‌های آسیاب شده قهوه تهیه می‌شود. اسپرسو دارای غلظت بالاتری از مواد محلول و معلق جامد است.', 'img/Coffee-Espresso.png', 'اسپرسو', 1, 'S-L', 3500, '2020-04-07 10:31:48'),
(2, 'دابل', 'دابل اسپرسو یک شات دوتایی از اسپرسو است', 'img/Coffee-Doppio.png', 'اسپرسو', 1, 'S-M-L', 5000, '2020-04-08 10:31:48'),
(3, 'آمریکانو', 'قهوه امریکانو با اضافه کردن اب داغ به اسپرسو تهیه می شود که طعم متفاوتی با اسپرسو دارد', 'img/Coffee-Americano.png', 'قهوه با پخت آمریکایی', 1, 'S-M', 4000, '2020-04-14 10:31:48'),
(8, 'لاته', 'قهوه لاته قهوه ای است که با اضافه کردن اسپرسو و شیر تهیه می شود', 'img/Latte.png', 'لاته', 1, 'Large', 6000, '2020-04-26 10:31:48'),
(9, 'چای سیاه', 'چای سیاه از انواع چایی های نوشیدنی است', 'img/black_tea.png', 'چایی', 2, 'S-M', 1000, '2020-04-26 17:26:01'),
(14, 'چای سبز', 'چای سبز', 'img/چای_سبز.png', 'چای', 2, 'کوچک-متوسط', 2000, '2020-04-30 22:41:13'),
(19, 'آب پرتقال', 'آب میوه طبیعی با جدیدترین میوه ها', 'img/آب_پرتقال.png', 'آب میوه ساده', 3, 'بزرگ-کوچک-متوسط', 4000, '2020-05-05 01:15:51');

-- --------------------------------------------------------

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
CREATE TABLE IF NOT EXISTS `notification` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(50) COLLATE utf8mb4_bin NOT NULL,
  `text` longtext COLLATE utf8mb4_bin NOT NULL,
  `type` int(11) NOT NULL,
  `fromId` int(11) NOT NULL,
  `toId` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=269 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- --------------------------------------------------------

--
-- Table structure for table `order_table`
--

DROP TABLE IF EXISTS `order_table`;
CREATE TABLE IF NOT EXISTS `order_table` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` int(11) NOT NULL COMMENT 'User id',
  `message` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT 'User message to admin',
  `addressId` int(11) NOT NULL,
  `status` int(11) DEFAULT 0,
  `time` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- --------------------------------------------------------

--
-- Table structure for table `order_view`
--

DROP TABLE IF EXISTS `order_view`;
CREATE TABLE IF NOT EXISTS `order_view` (
  `id_view` int(11) NOT NULL AUTO_INCREMENT,
  `uid_view` int(11) NOT NULL,
  `orderId` int(11) NOT NULL DEFAULT -1,
  `dateTime` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id_view`)
) ENGINE=MyISAM AUTO_INCREMENT=76 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

--
-- Dumping data for table `order_view`
--

INSERT INTO `order_view` (`id_view`, `uid_view`, `orderId`, `dateTime`) VALUES
(75, 1, 45, '2020-05-09 17:06:22');

-- --------------------------------------------------------

--
-- Table structure for table `posts`
--

DROP TABLE IF EXISTS `posts`;
CREATE TABLE IF NOT EXISTS `posts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(50) COLLATE utf8mb4_bin NOT NULL,
  `text` text COLLATE utf8mb4_bin NOT NULL,
  `imageUrl` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL,
  `createdTime` datetime NOT NULL DEFAULT current_timestamp(),
  `writerId` int(11) NOT NULL,
  `color` varchar(9) COLLATE utf8mb4_bin NOT NULL DEFAULT '#00000000',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

--
-- Dumping data for table `posts`
--

INSERT INTO `posts` (`id`, `title`, `text`, `imageUrl`, `createdTime`, `writerId`, `color`) VALUES
(1, 'تخفیف ویژه برای اسپرسو', 'برای قهوه های دسته اسپرسو برای شما تخفیف ویژه ای را در نظر گرفته ایم که نمیشه به راحتی ازش گذشت.\r\nشما می توانید با استفاده کد تخفیف زیر از تخفیف های کافی های اسپرسو بهره مند شوید.', 'card/coffee_banner.png', '2020-05-07 01:40:43', 1, '#4C2F1F');

-- --------------------------------------------------------

--
-- Table structure for table `post_link`
--

DROP TABLE IF EXISTS `post_link`;
CREATE TABLE IF NOT EXISTS `post_link` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(25) COLLATE utf8mb4_bin NOT NULL,
  `type` int(11) NOT NULL COMMENT '0=discount, 1=category',
  `address` int(11) NOT NULL,
  `postId` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

--
-- Dumping data for table `post_link`
--

INSERT INTO `post_link` (`id`, `title`, `type`, `address`, `postId`) VALUES
(1, 'کد تخفیف', 0, 1, 1);

-- --------------------------------------------------------

--
-- Table structure for table `register`
--

DROP TABLE IF EXISTS `register`;
CREATE TABLE IF NOT EXISTS `register` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `firstname` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL,
  `lastname` varchar(30) COLLATE utf8mb4_bin DEFAULT NULL,
  `phoneNumber` varchar(13) COLLATE utf8mb4_bin NOT NULL,
  `email` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL,
  `password` varchar(50) COLLATE utf8mb4_bin NOT NULL,
  `loginDate` datetime NOT NULL DEFAULT current_timestamp(),
  `language` varchar(5) COLLATE utf8mb4_bin NOT NULL DEFAULT 'en',
  `admin` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `phoneNumber` (`phoneNumber`),
  UNIQUE KEY `email` (`email`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

--
-- Dumping data for table `register`
--

INSERT INTO `register` (`id`, `firstname`, `lastname`, `phoneNumber`, `email`, `password`, `loginDate`, `language`, `admin`) VALUES
(1, 'ابوالفضل', 'علیزاده', '09123456789', NULL, 'ParisaAnd729', '2020-04-13 23:56:05', 'en', 1),
(2, 'عرفان', 'جعفری', '09124356789', NULL, 'ParisaAnd729', '2020-04-14 00:52:02', 'en', 0);

-- --------------------------------------------------------

--
-- Table structure for table `review`
--

DROP TABLE IF EXISTS `review`;
CREATE TABLE IF NOT EXISTS `review` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` int(11) NOT NULL COMMENT 'User Id',
  `iid` int(11) NOT NULL COMMENT 'Item Id',
  `text` longtext COLLATE utf8mb4_bin NOT NULL,
  `rate` int(11) NOT NULL,
  `time` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

--
-- Dumping data for table `review`
--

INSERT INTO `review` (`id`, `uid`, `iid`, `text`, `rate`, `time`) VALUES
(1, 1, 1, 'it\'s very, very, very good !\r\nThanks', 5, '2020-04-29 00:16:48'),
(2, 2, 1, 'it\'s good', 4, '2020-04-29 00:16:48'),
(3, 1, 2, 'I want it again ðŸ˜‹ðŸ˜', 5, '2020-04-29 00:16:48'),
(4, 1, 3, 'actually I don\'t like it ðŸ˜', 3, '2020-04-29 00:16:48'),
(5, 1, 3, 'I like it', 4, '2020-04-29 00:16:48'),
(6, 1, 3, 'it\'s very good ðŸ˜Š', 5, '2020-04-29 00:16:48'),
(7, 1, 3, 'I like Americano coffee', 5, '2020-04-29 00:16:48'),
(8, 1, 1, 'when I drink it, I got a very good feel', 3, '2020-04-29 00:16:48'),
(9, 1, 1, 'Test', 3, '2020-04-29 00:16:48'),
(11, 1, 2, 'Ù…Ù† Ø±Ø§Ø¶ÛŒ Ø¨ÙˆØ¯Ù…', 5, '2020-04-29 00:16:48'),
(12, 1, 2, 'Ø¹Ø§Ø´Ù‚Ø´Ù… ðŸ˜', 5, '2020-04-29 00:16:48'),
(13, 1, 1, 'man dost dshtm', 4, '2020-04-29 00:20:35'),
(14, 1, 3, 'I don\'t like Americano coffee', 2, '2020-04-29 15:28:47'),
(15, 1, 3, 'i rate by emoji 😍😍', 5, '2020-04-29 15:30:51'),
(16, 1, 3, 'من این مورد را دوست دارم 😀', 0, '2020-04-29 15:32:23');

-- --------------------------------------------------------

--
-- Table structure for table `token`
--

DROP TABLE IF EXISTS `token`;
CREATE TABLE IF NOT EXISTS `token` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `key` varchar(50) COLLATE utf8mb4_bin NOT NULL,
  `permission` int(11) NOT NULL DEFAULT 0,
  `userid` varchar(25) COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `key` (`key`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

--
-- Dumping data for table `token`
--

INSERT INTO `token` (`id`, `key`, `permission`, `userid`) VALUES
(1, 'ad31e18b6b5d43645ec50fb02cb5a07c', 1, NULL);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
