package com.xettuyen.service;

import java.sql.*;

/**
 * LoginService - Direct MySQL Database Authentication
 * 
 * Authenticates user directly from MySQL database (xettuyen2026)
 * Requirements: role_name must be 'admin' or 'teacher', is_active = 1
 * 
 * SQL Query:
 *   SELECT * FROM xt_users 
 *   JOIN xt_roles ON xt_users.role_id = xt_roles.idrole 
 *   WHERE username = ? AND password = ? AND is_active = 1
 * 
 * Usage:
 *   LoginService service = new LoginService();
 *   LoginResponse response = service.login("admin", "admin123");
 *   if (response.isSuccess()) {
 *       String username = response.getUsername();
 *       String role = response.getRoleName();
 *   }
 */
public class LoginService {
    
    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/xettuyen2026";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    // Allowed roles for login
    private static final String[] ALLOWED_ROLES = {"admin", "teacher"};
    
    // SQL Query with JOIN
    private static final String SQL_LOGIN = 
        "SELECT xu.iduser, xu.username, xu.full_name, xu.email, xu.phone, " +
        "       xu.role_id, xu.is_active, xr.idrole, xr.role_name, xr.description " +
        "FROM xt_users xu " +
        "JOIN xt_roles xr ON xu.role_id = xr.idrole " +
        "WHERE xu.username = ? AND xu.password = ? AND xu.is_active = 1";
    
    static {
        // Load MySQL driver
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver không tìm thấy: " + e.getMessage());
        }
    }
    
    /**
     * Authenticate user with username and password
     * Connects directly to MySQL database
     * 
     * @param username User's username
     * @param password User's password
     * @return LoginResponse with authentication details or error message
     */
    public LoginResponse login(String username, String password) {
        // Validate input
        if (username == null || username.trim().isEmpty()) {
            return new LoginResponse(false, "Vui lòng nhập tên đăng nhập");
        }
        if (password == null || password.trim().isEmpty()) {
            return new LoginResponse(false, "Vui lòng nhập mật khẩu");
        }
        
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            // Establish database connection
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            // Prepare statement with parameters
            statement = connection.prepareStatement(SQL_LOGIN);
            statement.setString(1, username.trim());
            statement.setString(2, password.trim());
            
            // Execute query
            resultSet = statement.executeQuery();
            
            // Check if user exists
            if (resultSet.next()) {
                // Extract user data from result set
                int userId = resultSet.getInt("iduser");
                String fullName = resultSet.getString("full_name");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                String roleName = resultSet.getString("role_name");
                int isActive = resultSet.getInt("is_active");
                
                // Check if user is active
                if (isActive == 0) {
                    return new LoginResponse(false, "Tài khoản này đã bị vô hiệu hóa");
                }
                
                // Check if role is allowed (admin or teacher)
                boolean isValidRole = isAllowedRole(roleName);
                if (!isValidRole) {
                    return new LoginResponse(false, 
                        "Tài khoản này không có quyền truy cập. Chỉ admin và teacher được phép.");
                }
                
                // Create successful response
                LoginResponse response = new LoginResponse(true, "Đăng nhập thành công");
                response.setUserId(userId);
                response.setUsername(username.trim());
                response.setFullName(fullName != null ? fullName : "");
                response.setEmail(email != null ? email : "");
                response.setPhone(phone != null ? phone : "");
                response.setRoleName(roleName);
                response.setToken(generateToken(userId));
                
                return response;
            } else {
                return new LoginResponse(false, "Tên đăng nhập hoặc mật khẩu không chính xác");
            }
            
        } catch (SQLException e) {
            // Handle database connection errors
            if (e.getMessage().contains("Connection refused") || 
                e.getMessage().contains("Unknown host")) {
                return new LoginResponse(false, 
                    "Không thể kết nối tới cơ sở dữ liệu MySQL.\n" +
                    "Vui lòng kiểm tra:\n" +
                    "- XAMPP/MySQL đang chạy\n" +
                    "- Database 'xettuyen2026' tồn tại\n" +
                    "- URL: jdbc:mysql://localhost:3306/xettuyen2026");
            } else if (e.getMessage().contains("Access denied")) {
                return new LoginResponse(false, 
                    "Lỗi xác thực cơ sở dữ liệu.\n" +
                    "Kiểm tra username/password MySQL (root/empty)");
            } else {
                return new LoginResponse(false, "Lỗi cơ sở dữ liệu: " + e.getMessage());
            }
            
        } catch (Exception e) {
            return new LoginResponse(false, "Lỗi: " + e.getMessage());
            
        } finally {
            // Close resources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.err.println("Lỗi đóng kết nối: " + e.getMessage());
            }
        }
    }
    
    /**
     * Check if role is allowed for login
     * 
     * @param roleName Role name from database
     * @return true if role is allowed, false otherwise
     */
    private boolean isAllowedRole(String roleName) {
        if (roleName == null) return false;
        for (String allowedRole : ALLOWED_ROLES) {
            if (allowedRole.equalsIgnoreCase(roleName)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Generate a simple session token
     * In production, use JWT or similar
     * 
     * @param userId User ID
     * @return Token string
     */
    private String generateToken(int userId) {
        return "TOKEN_" + userId + "_" + System.currentTimeMillis();
    }
    
    /**
     * Inner class to hold login response data
     */
    public static class LoginResponse {
        private boolean success;
        private String message;
        private String token;
        private int userId;
        private String username;
        private String fullName;
        private String email;
        private String phone;
        private String roleName;
        
        public LoginResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        // Getters & Setters
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getToken() {
            return token;
        }
        
        public void setToken(String token) {
            this.token = token;
        }
        
        public int getUserId() {
            return userId;
        }
        
        public void setUserId(int userId) {
            this.userId = userId;
        }
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getFullName() {
            return fullName;
        }
        
        public void setFullName(String fullName) {
            this.fullName = fullName;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getPhone() {
            return phone;
        }
        
        public void setPhone(String phone) {
            this.phone = phone;
        }
        
        public String getRoleName() {
            return roleName;
        }
        
        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }
    }
}
