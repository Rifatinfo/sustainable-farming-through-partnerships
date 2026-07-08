package util;

import model.UserRole;

public class UserSession {

    private static String userId;
    private static String userName;
    private static String userEmail;
    private static UserRole userRole;

    public static void login(String userId, String userName, String userEmail, UserRole userRole) {
        UserSession.userId = userId;
        UserSession.userName = userName;
        UserSession.userEmail = userEmail;
        UserSession.userRole = userRole;
    }

    public static void logout() {
        userId = null;
        userName = null;
        userEmail = null;
        userRole = null;
    }

    public static String getUserId() { return userId; }
    public static String getUserName() { return userName; }
    public static String getUserEmail() { return userEmail; }
    public static UserRole getUserRole() { return userRole; }

    public static boolean isLoggedIn() {
        return userId != null;
    }
}
