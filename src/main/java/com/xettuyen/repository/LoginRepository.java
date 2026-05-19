package com.xettuyen.repository;

public class LoginRepository {

    public static final String SQL_LOGIN =
        "SELECT xu.iduser, xu.username, xu.full_name, xu.email, xu.phone, " +
        "       xu.role_id, xu.is_active, xr.idrole, xr.role_name, xr.description " +
        "FROM xt_users xu " +
        "JOIN xt_roles xr ON xu.role_id = xr.idrole " +
        "WHERE xu.username = :username AND xu.password = :password AND xu.is_active = 1";

    private LoginRepository() {
    }
}
