import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventManager {
    private List<Event> events;

    public EventManager() {
        events = new ArrayList<>();
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    public void removeEvent(Event event) {
        events.remove(event);
    }

    public List<Event> getAllEvents() {
        return events;
    }

    public List<Event> searchEvents(String keyword) {
        List<Event> searchResults = new ArrayList<>();

        for (Event event : events) {
            if (event.containsKeyword(keyword)) {
                searchResults.add(event);
            }
        }

        return searchResults;
    }

    public Event findEventByDateAndTitle(LocalDate date, String title) {
        for (Event event : events) {
            if (event.getDateTime().toLocalDate().equals(date) && event.getTitle().equals(title)) {
                return event;
            }
        }
        return null;
    }

    public Event findEventByTitle(String title) {
        for (Event event : events) {
            if (event.getTitle().equals(title)) {
                return event;
            }
        }
        return null;
    }

    public void updateEvent(Event event, LocalDateTime dateTime, String title, String description) {
        event.updateEvent(dateTime, title, description);
    }

    public List<Event> getEventsInDay(LocalDate date) {
        List<Event> eventsInDay = new ArrayList<>();

        for (Event event : events) {
            if (event.getDateTime().toLocalDate().equals(date)) {
                eventsInDay.add(event);
            }
        }

        return eventsInDay;
    }
}