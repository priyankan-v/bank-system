package com.bank.util;

import com.bank.model.Admin;
import com.bank.model.User;

public class SessionManager {

    private static User currentUser;
    private static Admin currentAdmin;
    private static Admin currentTargetAdmin; // For super admin to manage other admins

    private static long loginTime;
    private static final long SESSION_TIMEOUT = 10 * 60 * 1000;

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

    // ADMIN SESSION FOR SUPER ADMIN TO MANAGE OTHER ADMINS
    public static void setCurrentTargetAdmin(Admin admin) {
        currentTargetAdmin = admin;
        currentUser = null;
        loginTime = System.currentTimeMillis();
    }

    public static Admin getCurrentTargetAdmin() {
        return currentTargetAdmin;
    }

    public static boolean isTargetAdminLoggedIn() {
        return currentTargetAdmin != null;
    }

    // SESSION INFO
    public static long getLoginTime() {
        return loginTime;
    }

    /**
     * Check if admin session is valid (not expired)
     * @return true if admin is logged in and session hasn't expired
     */
    public static boolean isAdminSessionValid() {
        if (currentAdmin == null) {
            return false;
        }
        
        // Check if session has expired
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - loginTime;
        
        return elapsedTime < SESSION_TIMEOUT;
    }
    
    /**
     * Check if user session is valid (not expired)
     * @return true if user is logged in and session hasn't expired
     */
    public static boolean isUserSessionValid() {
        if (currentUser == null) {
            return false;
        }
        
        // Check if session has expired
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - loginTime;
        
        return elapsedTime < SESSION_TIMEOUT;
    }
    
    /**
     * Validate and handle session timeout
     * Automatically logs out if session has expired
     * @return true if session is valid, false if expired (and logged out)
     */
    public static boolean validateAndHandleTimeout() {
        if (currentAdmin != null && !isAdminSessionValid()) {
            logout();
            return false;
        }
        
        if (currentUser != null && !isUserSessionValid()) {
            logout();
            return false;
        }
        
        return true;
    }
    
    /**
     * Get remaining session time in milliseconds
     * @return remaining time, or -1 if no session
     */
    public static long getRemainingSessionTime() {
        if (loginTime == 0) {
            return -1;
        }
        
        long elapsed = System.currentTimeMillis() - loginTime;
        long remaining = SESSION_TIMEOUT - elapsed;
        
        return remaining > 0 ? remaining : 0;
    }
    // LOGOUT

    public static void logout() {
        currentUser = null;
        currentAdmin = null;
        currentTargetAdmin = null;
        loginTime = 0;
    }
}