package model;

import java.util.Objects;

public class Admin extends User {

    private static final long serialVersionUID = 1L;

    @Override
    public String getRoleDisplayName() {
        return "Admin Panel";
    }

    public Admin() {
        super();
        setRole(UserRole.ADMIN);
    }

    public Admin(String id, String name, String email, String passwordHash, String createdAt) {
        super(id, name, email, passwordHash, UserRole.ADMIN, createdAt);
    }

    @Override
    public String toString() {
        return "Admin{id='" + getId() + "', name='" + getName() + "', email='" + getEmail() + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Admin)) return false;
        return Objects.equals(getId(), ((Admin) o).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
