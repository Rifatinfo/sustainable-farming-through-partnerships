package model;

import java.util.Objects;

public class Investor extends User {

    private static final long serialVersionUID = 1L;

    private double walletBalance;

    public Investor() {
        super();
        setRole(UserRole.INVESTOR);
    }

    public Investor(String id, String name, String email, String passwordHash, String createdAt,
                    double walletBalance) {
        super(id, name, email, passwordHash, UserRole.INVESTOR, createdAt);
        this.walletBalance = walletBalance;
    }

    public double getWalletBalance() { return walletBalance; }
    public void setWalletBalance(double walletBalance) { this.walletBalance = walletBalance; }

    @Override
    public String toString() {
        return "Investor{id='" + getId() + "', name='" + getName() + "', email='" + getEmail()
                + "', walletBalance=" + walletBalance + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Investor)) return false;
        return Objects.equals(getId(), ((Investor) o).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
