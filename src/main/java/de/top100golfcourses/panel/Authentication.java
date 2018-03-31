package de.top100golfcourses.panel;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.vaadin.server.VaadinSession;

import org.apache.catalina.User;
import org.apache.catalina.UserDatabase;

import de.top100golfcourses.panel.entity.Role;

public class Authentication {

    public Authentication() {
    }

    public boolean authenticate(String userName, String password) {
        try {
            UserDatabase db = (UserDatabase) new InitialContext().lookup("java:comp/env/UserDatabase");
            User user = db.findUser(userName);
            if (user == null) {
                Logger.getAnonymousLogger().info(userName + " not found");
                return false;
            }
            else if (user.getPassword() == null) {
                Logger.getAnonymousLogger().info("No Password found for " + user);
                return false;
            }
            else if (! user.getRoles().hasNext()) {
                Logger.getAnonymousLogger().info("No Role found for " + user);
                return false;
            }
            else if (user.getPassword().equalsIgnoreCase(getHash(password))) {
                VaadinSession.getCurrent().setAttribute("user", userName);
                VaadinSession.getCurrent().setAttribute(Role.class, getRole(user.getRoles()));
                Logger.getAnonymousLogger().info("Authenticated: " + user);
                return true;
            }
            else {
                Logger.getAnonymousLogger().info("Authentication failed: " + user);
                return false;
            }
        }
        catch (NamingException ex) {
            Logger.getAnonymousLogger().severe("Cannot find UserDatabase: " + ex.toString());
            VaadinSession.getCurrent().setAttribute("user", "Fazil Ongudar");
            VaadinSession.getCurrent().setAttribute(Role.class, Role.Correspondent);
            return true; // set to true for local testing, false in production
        }
    }

    private String getHash(String clearText) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(clearText.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedHash);
        }
        catch (NoSuchAlgorithmException ex) {
            Logger.getAnonymousLogger().severe("Hashing broken - should never happen: " + ex.toString());
            return "";
        }
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < hash.length; i++) {
        String hex = Integer.toHexString(0xff & hash[i]);
        if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private Role getRole(Iterator<org.apache.catalina.Role> roles) {
        while (roles.hasNext()) {
            String roleName = roles.next().getRolename();
            try {
                return Role.valueOf(roleName);
            }
            catch (IllegalArgumentException ex) {
                Logger.getAnonymousLogger().info("Unknown role: " + roleName);
            }
        }
        return Role.Kibitzer; // nothing found, so let's assign the role with the least privileges
    }

}
