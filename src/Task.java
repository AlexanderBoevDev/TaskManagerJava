import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    private String description;
    private LocalDateTime dueDate;
    private LocalDateTime creationDate;
    private boolean completed;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public Task(String description, LocalDateTime dueDate) {
        this.description = description;
        this.dueDate = dueDate;
        this.creationDate = LocalDateTime.now();
        this.completed = false;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void complete() {
        this.completed = true;
    }

    public String toFileFormat() {
        return String.format("%s;%s;%s;%s",
                completed,
                description,
                creationDate.format(formatter),
                dueDate.format(formatter));
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (Создана: %s, Завершить до: %s)",
                completed ? "x" : " ", description,
                creationDate.format(formatter),
                dueDate.format(formatter));
    }
}