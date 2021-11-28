package usecases.events.worksessions.strategies.DayOrderer;

import entities.Event;
import usecases.events.EventManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FewestSessions implements DayOrderer{
    @Override
    public void order(UUID deadline, EventManager eventManager, List<LocalDate> eligibleDates,
                           Map<LocalDate, List<Event>> schedule) {
        List<LocalDate> ordered = new ArrayList<>();

        while (!eligibleDates.isEmpty()) {
            LocalDate leastWorkSessions = this.leastSessions(eligibleDates, eventManager.getWorkSessions(deadline),eventManager);
            eligibleDates.remove(leastWorkSessions);
            ordered.add(leastWorkSessions);
        }
        eligibleDates.addAll(ordered);
    }

    private LocalDate leastSessions(List<LocalDate> eligibleDates, List<Event> workSessions, EventManager eventManager){
        LocalDate leastWorkSessions = eligibleDates.get(0);
        int record = workSessions.size();
        for (LocalDate day : eligibleDates) {
            int num = 0;
            for (Event session : workSessions) {
                if (eventManager.getEnd(session).toLocalDate().isEqual(day)) {
                    num = num + 1;
                }
            }
            if (num <= record) {
                leastWorkSessions = day;
                record = num;
            }
        }

        return leastWorkSessions;
    }
}
