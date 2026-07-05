/*
 * ===== ROLE MODEL (enforced across all code) =====
 *
 * Admin   — Can log in: Yes  — Self-register: No  — Created by: pre-seeded in users.json
 * Monitor — Can log in: Yes  — Self-register: No  — Created by: Admin only
 * Investor— Can log in: Yes  — Self-register: Yes — Created by: self-registration
 * Farmer  — Can log in: No   — Self-register: No  — Created by: Monitor only, as a profile
 *
 * RULES:
 *  1. Farmer is NOT a User subclass. Farmer has no password, no login, no dashboard,
 *     and is NEVER stored in users.json.
 *  2. Farmer data lives in farmer_profiles.json referenced by farmerId.
 *  3. Every FarmerProfile stores the monitorId of the Monitor who created it.
 *  4. Only ADMIN, MONITOR, INVESTOR are valid UserRole values.
 * ======================
 */
package model;

import java.io.Serializable;
import java.util.Objects;

public abstract class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String email;
    private String passwordHash;
    private UserRole role;
    private String createdAt;

    protected User() {}

    protected User(String id, String name, String email, String passwordHash, UserRole role, String createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id='" + id + "', name='" + name + "', email='" + email + "', role=" + role + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
