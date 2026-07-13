package repository;

import model.Notification;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class NotificationRepository extends JsonRepository<Notification> {

    private static final String FILE_PATH = "src/data/notifications.json";

    public NotificationRepository() {
        super(FILE_PATH, Notification.class);
    }

    @Override
    protected String getId(Notification notification) {
        return notification.getId();
    }

    public List<Notification> findByRecipientId(String recipientId) {
        return findAll().stream()
                .filter(n -> Objects.equals(n.getRecipientId(), recipientId))
                .collect(Collectors.toList());
    }

    public void markAsRead(String notificationId) {
        findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            update(notification);
        });
    }

    public void deleteOld() {
        List<Notification> unread = findAll().stream()
                .filter(n -> !n.isRead())
                .collect(Collectors.toList());
        saveAll(unread);
    }

    public void save(Notification notification) {
        findById(notification.getId()).ifPresentOrElse(
                existing -> update(notification),
                () -> add(notification)
        );
    }
}
