-- INSERT TEST DATA FOR LOGIN
-- Run this script after importing schema.sql

USE `xettuyen2026`;

-- Insert Roles
INSERT INTO `xt_roles` (`role_name`, `description`) VALUES
('admin', 'Quản trị viên hệ thống'),
('teacher', 'Giáo viên'),
('student', 'Học sinh');

-- Insert Test Users
-- Username: admin, Password: admin123
-- Username: teacher1, Password: teacher123
INSERT INTO `xt_users` (`username`, `password`, `full_name`, `email`, `phone`, `role_id`, `is_active`) VALUES
('admin', 'admin123', 'Nguyễn Văn An', 'admin@xettuyen.edu.vn', '0901234567', 1, 1),
('teacher1', 'teacher123', 'Trần Thị Bình', 'teacher@xettuyen.edu.vn', '0912345678', 2, 1),
('teacher2', 'teacher456', 'Lê Văn Cường', 'teacher2@xettuyen.edu.vn', '0923456789', 2, 1);

SELECT 'Test data inserted successfully' as status;
