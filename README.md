# 🛒 Ecommerce Microservices

![Java](https://img.shields.io/badge/Java-17+-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen) ![Docker](https://img.shields.io/badge/Docker-Compose-blue) ![License](https://img.shields.io/badge/License-MIT-yellow)

**Ecommerce Microservices** là một hệ thống thương mại điện tử được xây dựng theo kiến trúc microservices, sử dụng **Spring Boot**, **Spring Cloud**, và các công nghệ hiện đại như **Kafka**, **RabbitMQ**, **Redis**, **OAuth2**, và **WebSocket**. Dự án hướng đến tính mở rộng, phân tán, bảo mật, và khả năng tích hợp linh hoạt.

## 📋 Tổng quan

Hệ thống bao gồm nhiều microservices độc lập, mỗi dịch vụ xử lý một chức năng cụ thể như quản lý người dùng, sản phẩm, đơn hàng, khuyến mãi, và thông báo. Các dịch vụ giao tiếp qua **API Gateway**, sử dụng **Eureka** cho service discovery và **Kafka/RabbitMQ** cho giao tiếp bất đồng bộ.

### ✨ Các tính năng chính
- Đăng ký/đăng nhập người dùng với **OAuth2** (Google, Facebook).
- Quản lý sản phẩm, tồn kho, đơn hàng, và đánh giá.
- Gửi thông báo real-time qua **WebSocket** và email/tin nhắn qua **Notification Service**.
- Hỗ trợ khuyến mãi và mã giảm giá.
- Tích hợp caching với **Redis** và cơ sở dữ liệu **MySQL/MongoDB**.
- Tài liệu API với **Swagger**.

## 📂 Danh sách Microservices

| Service Name           | Mô tả                                                                 |
|------------------------|----------------------------------------------------------------------|
| `api-gateway`          | Cổng vào hệ thống, định tuyến request, tích hợp JWT và OAuth2       |
| `config-server`        | Cung cấp cấu hình tập trung cho các dịch vụ                         |
| `eureka-server`        | Service Discovery với Spring Cloud Netflix Eureka                   |
| `user-service`         | Quản lý người dùng, đăng ký, đăng nhập, OAuth2                      |
| `product-service`      | Quản lý danh mục sản phẩm                                           |
| `inventory-service`    | Quản lý tồn kho sản phẩm                                            |
| `order-service`        | Quản lý đơn hàng                                                    |
| `review-service`       | Quản lý đánh giá và bình luận sản phẩm                              |
| `promotion-service`    | Quản lý mã giảm giá và chương trình khuyến mãi                      |
| `notification-service` | Gửi thông báo qua email, tin nhắn, hoặc WebSocket                   |
| `design-pattern`       | Demo các mẫu thiết kế áp dụng trong microservices                   |

## 🛠 Công nghệ sử dụng

- **Backend**: Java 17, Spring Boot 3.x, Spring Cloud (Eureka, Config), Spring Security, Spring WebFlux (optional)
- **Messaging**: Kafka, RabbitMQ
- **Caching**: Redis
- **Database**: MySQL, MongoDB (optional)
- **Real-time**: WebSocket
- **API Docs**: Swagger/OpenAPI
- **DevOps**: Docker, Docker Compose
- **Authentication**: OAuth2 (Google, Facebook), JWT

## 🚀 Hướng dẫn cài đặt và chạy (Dev)

### 1. Yêu cầu môi trường
- **Java**: 17+
- **Maven**: 3.8+
- **Docker & Docker Compose**
- **Dịch vụ bên ngoài**: Kafka, Redis, RabbitMQ, MySQL (có thể chạy bằng Docker)

### 2. Cài đặt và chạy

#### Bước 1: Clone repository
```bash
git clone https://github.com/NgSao/ecommerce-microservices.git
cd ecommerce-microservices
```

#### Bước 2: Khởi chạy các dịch vụ phụ thuộc với Docker Compose
```bash
docker-compose up -d
```

#### Bước 3: Build từng microservice
```bash
cd service-name
mvn clean install
```

#### Bước 4: Chạy microservice
```bash
mvn spring-boot:run
```

### 3. Truy cập Swagger UI
Mỗi microservice có giao diện Swagger tại:
```
http://localhost:{port}/swagger-ui.html
```
Ví dụ: `http://localhost:8081/swagger-ui.html` cho `product-service`.

## 📡 Kiến trúc hệ thống

```plaintext
[Client]
   |
[API Gateway] <- [JWT, OAuth2, Routing]
   |
[Discovery Server - Eureka]
   |
------------------------------------------------------------
|      |        |           |         |          |         |
User  Product  Inventory  Order  Promotion  Review  Notification
```

## 📚 Tài liệu bổ sung
- **Kafka Configuration**: Xem file `Kafka.txt` để biết chi tiết về Kafka.
- **Design Patterns**: Xem thư mục `design-pattern` để tìm hiểu các mẫu thiết kế.
- **GitHub Repository**: [NgSao/ecommerce-microservices](https://github.com/NgSao/ecommerce-microservices) (thay bằng link thật nếu có).

## 📫 Thông tin liên hệ
- **Email**: nguyensaovn2019@gmail.com
- **Địa chỉ**: Thủ Đức, TP. Hồ Chí Minh
- **GitHub**: [NgSao](https://github.com/NgSao)

## 🤝 Đóng góp
Chúng tôi hoan nghênh mọi đóng góp! Vui lòng làm theo các bước sau:
1. Fork repository.
2. Tạo branch mới: `git checkout -b feature/your-feature`.
3. Commit thay đổi: `git commit -m "Add your feature"`.
4. Push lên branch: `git push origin feature/your-feature`.
5. Tạo Pull Request.

