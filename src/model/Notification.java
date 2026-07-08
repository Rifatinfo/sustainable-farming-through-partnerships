package model;

import java.io.Serializable;
import java.util.Objects;

public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String recipientId;
    private UserRole recipientRole;
    private String message;
    private NotificationType type;
    private boolean isRead;
    private String createdAt;

    public Notification() {}

    public Notification(String id, String recipientId, UserRole recipientRole, String message,
                        NotificationType type, boolean isRead, String createdAt) {
        this.id = id;
        this.recipientId = recipientId;
        this.recipientRole = recipientRole;
        this.message = message;
        this.type = type;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRecipientId() { return recipientId; }
    public void setRecipientId(String recipientId) { this.recipientId = recipientId; }

    public UserRole getRecipientRole() { return recipientRole; }
    public void setRecipientRole(UserRole recipientRole) { this.recipientRole = recipientRole; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean isRead) { this.isRead = isRead; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Notification{id='" + id + "', recipientId='" + recipientId + "', type=" + type + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification)) return false;
        Notification that = (Notification) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
