//Tổng API hiện tại 50 api

//Dự kiến API: 60 api

//UserDomain (21)
//Token, OTO lưu trong cookies
//Ảnh lưu trên github
//Xác thực qua email
@RequestMapping("/api/v1/users")
        /**
         * 📌 1. API: Đăng ký tài khoản (chưa kích hoạt) +mã xác thực OTP qua email
         */
        @PostMapping("/register")

        /**
         * 📌 2. API: Tài khoản đã có (chưa kích hoạt) + mã xác thực mới OTP qua email.
         */
        @PostMapping("/verify")

        /**
         * 📌 3. API: Xác thực OTP để kích hoạt tài khoản
         */
        @PostMapping("/activate")

        /**
         * 📌 4. API: Xác thực OTP để gửi mật khẩu mới về
         */
        @PostMapping("/forgot-password")
    
        /**
         * 📌 5. API: Đăng nhập tài khoản
         */
        @PostMapping("/login")

        /**
         * 📌 6. API: Cập nhật lại tonken
         */
        @GetMapping("/refresh")

        /**
         * 📌 7. API: Đăng xuất
         */
        @GetMapping("/logout")

        /**
         * 📌 8. API: Lấy người dùng bằng token
         */
        @GetMapping("/account")
        
        /**
         * 📌 9. API: Lấy thông tin người dùng theo email
         */
        @GetMapping("/email")

        /**
         * 📌 10. API: Lấy thông tin người dùng theo idUser
         */
        @GetMapping("/{id}")

        /**
         * 📌 11. API: Cập nhật tài khoản 
         */
        @PostMapping("/account/updated")

        /**
         * 📌 12. API: Cập nhật mật khẩu + gửi mail xác thực
         */
        @PostMapping("/account/reset-password")

        /**
         * 📌 13. API: Tạo địa chỉ giao hàng
         */
        @PostMapping("/address")

        /**
         * 📌 14. API: Thay đổi trạng thái địa chỉ (true, false)
         */
        @GetMapping("/address/active")

        /**
         * 📌 15. API: Cập nhật địa chỉ giao hàng 
         */
        @GetMapping("/address/updated")

        /**
         * 📌 16. API: Xóa địa chỉ giao hàng 
         */
        @DeleteMapping("/address/delete/{addressId}")

        //Admin, Staff
        /**
         * 📌 17. API: Tạo tài khoản người dùng, không cần kích hoạt
         */
        @PostMapping("/admin/account")

        /**
         * 📌 18. API: Thay đổi vai trò người dùng
         */
        @PostMapping("/admin/account/role")

        /**
         * 📌 19. API: Thay đổi trạng thái người dùng
         */
        @PostMapping("/admin/account/active")

        /**
         * 📌 20. API: Lấy ra danh sách người dùng
         */
        @GetMapping("/admin/account")

        /**
         * 📌 21. API: Xóa người dùng
         */
        @DeleteMapping("/admin/account")


//ProductDomain (12)
//Ảnh lưu trên github
@RequestMapping("/api/v1/products")
        /**
         * 📌 1. API: Tạo danh mục
         */
        @PostMapping("/admin/categories/add")

        /**
         * 📌 2. API: Cập nhật danh mục
         */
        @PostMapping("/admin/categories/updated")

        /**
         * 📌 3. API: Lấy ra tất cả danh mục
         */
        @GetMapping("/categories")

        /**
         * 📌 4. API: Lấy ra 1 danh mục
         */
        @GetMapping("/categories/{id}")

        /**
         * 📌 5. API: Thay đổi trạng thái danh mục 
         */
        @GetMapping("/admin/categories/active")
    
        /**
         * 📌 6. API: Xóa danh mục
         */
        @DeleteMapping("/admin/categories/delete/{id}")

        /**
         * 📌 7. API: Tạo sản phẩm
         */
        @PostMapping("/admin/add")

        /**
         * 📌 8. API: Cập nhật sản phẩm + check còn hàng k
         */
        @PostMapping("/admin/updated")

        /**
         * 📌 9. API: Lấy sản phẩm theo id
         */
        @GetMapping("/admin/{id}")

        /**
         * 📌 10. API: Xóa sản phẩm + kiểm tra có đơn hàng ch
         */
        @DeleteMapping("/admin/{id}")

        /**
         * 📌 11. API: Kiểm tra sản phẩm còn hàng không
         */
        @GetMapping("/admin/inventory")

        /**
         * 📌 12. API: Cập nhật trạng thái sản phẩm
         */
        @GetMapping("/admin/active")


//InventoryDomain (2)
//Ảnh lưu trên github
@RequestMapping("/api/v1/inventories")
        /**
         * 📌 1. API: Nhập hàng theo id sp
         */
        @PostMapping("/admin/import")

        /**
         * 📌 2. API: Xuất kho theo id sp
         */
        @PostMapping("/admin/export")



//OrderDomain (6)
//Gửi mail về đơn hàng.
@RequestMapping("/api/v1/orders")
        /**
         * 📌 1. API: Tạo đơn hàng mới 
         */
        @PostMapping("/admin/add")

        /**
         * 📌 2. API: Thay đổi trạng thái đơn hàng: 1-2-3-4-5 
         */
        @GetMapping("/admin/status/{status}")

        /**
         * 📌 3. API: Lấy ra danh sách các đơn hàng
         */
        @GetMapping("/admin")

        /**
         * 📌 4. API: Mua hàng
         */
        @PostMapping

        /**
         * 📌 5. API: Xem chi tiết đơn hàng
         */
        @GetMapping("/{id}")
    
        /**
         * 📌 6. API: Xem danh sách đơn hàng
         */
        @GetMapping("/{id}")


//PaymentDomain (2)
//Gửi mail về đơn hàng.
@RequestMapping("/api/v1/payment")
        /**
         * 📌 1. API: Tạo phương thức thanh toán
         */
        @PostMapping
//Admin + User ( chưa nghĩ tới)

//ReviewDomain (6)
@RequestMapping("/api/v1/reviews")
        /**
         * 📌 1. API: Đánh giá sản phẩm
         */
        @PostMapping

        /**
         * 📌 2. API: Danh sách đánh giá
         */
        @GetMapping

        /**
         * 📌 3. API: Lịch sử đánh giá
         */
        @GetMapping("/{id}")

        /**
         * 📌 4. API: Tạo Đánh giá sp (admin)(buff bẩn)
         */
        @PostMapping("/admin")

        /**
         * 📌 5. API: Ẩn đánh giá sản phẩm
         */
        @GetMapping("/admin/active")
    
        /**
         * 📌 6. API: Xóa đánh giá sản phẩm
         */
        @DeleteMapping("/admin/delete")
