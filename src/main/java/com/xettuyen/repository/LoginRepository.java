package com.xettuyen.repository;

import com.xettuyen.config.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;

public class LoginRepository {
    
    // Allowed roles for login
    private static final String[] ALLOWED_ROLES = {"admin", "teacher"};
    
    // SQL Query with JOIN
    private static final String SQL_LOGIN =
            "SELECT xu.iduser, xu.username, xu.full_name, xu.email, xu.phone, " +
                    "       xu.role_id, xu.is_active, xr.idrole, xr.role_name, xr.description " +
                    "FROM xt_users xu " +
                    "JOIN xt_roles xr ON xu.role_id = xr.idrole " +
                    "WHERE xu.username = :username AND xu.password = :password AND xu.is_active = 1";

    public LoginResponse login(String username, String password) {
        // Validate input
        if (username == null || username.trim().isEmpty()) {
            return new LoginResponse(false, "Vui lòng nhập tên đăng nhập");
        }
        if (password == null || password.trim().isEmpty()) {
            return new LoginResponse(false, "Vui lòng nhập mật khẩu");
        }
        
        try {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                NativeQuery<?> query = session.createNativeQuery(SQL_LOGIN);
                query.setParameter("username", username.trim());
                query.setParameter("password", password.trim());

                Object row = query.uniqueResult();
                if (row == null) {
                    return new LoginResponse(false, "Tên đăng nhập hoặc mật khẩu không chính xác");
                }

                Object[] columns = (Object[]) row;
                int userId = toInt(columns[0]);
                String fullName = toStringSafe(columns[2]);
                String email = toStringSafe(columns[3]);
                String phone = toStringSafe(columns[4]);
                String roleName = toStringSafe(columns[8]);
                int isActive = toInt(columns[6]);
                
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
            }
        } catch (Exception e) {
            return new LoginResponse(false, "Lỗi: " + e.getMessage());
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

    private int toInt(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof Boolean) {
            return (Boolean) value ? 1 : 0;
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String toStringSafe(Object value) {
        return value == null ? "" : value.toString();
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
