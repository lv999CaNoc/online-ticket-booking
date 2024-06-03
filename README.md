# Website quản lý chuỗi rạp chiếu phim
## actvn-cinema
## Hướng dẫn cài đặt
Mở dự án với IntelliJ IDEA (chưa chạy). 
- Thiết lập **JDK version 1.8**
- Cơ sở dữ liệu **MySQL**
  - Vào `src\main\resources\sql`
    - Tạo schema và các bảng: Mở file `schema.sql` với MySQL workbench và chạy.
    - Kết nối với database: Trong IntelliJ IDEA, vào `src/main/resources/application.properties`, thay đổi `spring.datasource.password` thành mật khẩu của root.
    - Vào src/main/java/com/actvn/cinema/CinemaSysApplication.java, run  để spring boot tự động tạo các bảng.
    - Tạo liệu mẫu: Mở file `data.sql` với MySQL workbench và chạy. (Sửa dữ liệu trong bảng schedule phù hợp với hiện tại)

- Truy cập vào website: `localhost:8080/`

## Tài khoản trong hệ thống
### Quyền USER:
* username: user1, user2
* password: user
### Quyền MANAGER:
* username: manager1, manager2
* password: manager
### Quyền ADMIN:
* username: admin
* password: admin 

## Tài khoản paypal thử nhiệm
* username: sb-tbzb626421124@personal.example.com
* password: #d#nyW9l