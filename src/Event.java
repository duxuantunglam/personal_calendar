import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class Event {
    private LocalDateTime dateTime;
    private String title;
    private String description;

    public Event(LocalDateTime dateTime, String title, String description) {
        this.dateTime = dateTime;
        this.title = title;
        this.description = description;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean containsKeyword(String keyword) {
        return title.toLowerCase().contains(keyword.toLowerCase()) || description.toLowerCase().contains(keyword.toLowerCase());
    }

    public void updateEvent(LocalDateTime dateTime, String title, String description) {
        this.dateTime = dateTime;
        this.title = title;
        this.description = description;
    }

    public boolean isValidDate() {
        LocalDate date = dateTime.toLocalDate();
        try {
            YearMonth.from(date);
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }

    public boolean isValidTime() {
        int hour = dateTime.toLocalTime().getHour();
        int minute = dateTime.toLocalTime().getMinute();
        return hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59;
    }

    @Override
    public String toString() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        String dateString = dateTime.format(dateFormatter);
        String timeString = dateTime.format(timeFormatter);

        return "Title: " + title + "\nDate: " + dateString + "\nTime: " + timeString + "\nDescription: " + description;
    }
}