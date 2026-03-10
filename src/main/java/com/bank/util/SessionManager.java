package com.bank.util;

import com.bank.model.Admin;
import com.bank.model.User;

public class SessionManager {

    private static User currentUser;
    private static Admin currentAdmin;

    private static long loginTime;

    // USER SESSION

    public static void setCurrentUser(User user) {
        currentUser = user;
        currentAdmin = null;
        loginTime = System.currentTimeMillis();
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isUserLoggedIn() {
        return currentUser != null;
    }

    // ADMIN SESSION

    public static void setCurrentAdmin(Admin admin) {
        currentAdmin = admin;
        currentUser = null;
        loginTime = System.currentTimeMillis();
    }

    public static Admin getCurrentAdmin() {
        return currentAdmin;
    }

    public static boolean isAdminLoggedIn() {
        return currentAdmin != null;
    }

    // SESSION INFO

    public static long getLoginTime() {
        return loginTime;
    }

    // LOGOUT

    public static void logout() {
        currentUser = null;
        currentAdmin = null;
        loginTime = 0;
    }
}