package usecases.events.worksessions;

import entities.Event;
import interfaces.EventListObserver;
import usecases.events.worksessions.strategies.DayOrderer.FewestSessions;
import usecases.events.worksessions.strategies.TimeGetters.DefaultTimeGetter;
import usecases.events.worksessions.strategies.TimeGetters.TimeGetter;
import usecases.events.worksessions.strategies.DayOrderer.DayOrderer;
import usecases.events.worksessions.strategies.TimeOrderer.BreaksBetween;
import usecases.events.worksessions.strategies.TimeOrderer.EveningPerson;
import usecases.events.worksessions.strategies.TimeOrderer.MorningPerson;
import usecases.events.worksessions.strategies.TimeOrderer.TimeOrderer;

import usecases.events.EventManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class WorkSessionScheduler {
    private final TimeGetter timeGetter;
    private final List<DayOrderer> dayOrderers;
    private final List<TimeOrderer> timeOrderers;

    public WorkSessionScheduler(Map<LocalTime, LocalTime> freeTime) {
        this.timeGetter = new DefaultTimeGetter(freeTime);
        this.dayOrderers = new ArrayList<>();
        this.timeOrderers = new ArrayList<>();
    }

    /**
     * Method which marks incomplete a session for the Event
     *
     * @param event        An Event
     * @param session      A String with details of session
     * @param eventManager An EventManager
     */
    public void markInComplete(UUID event, String session, EventManager eventManager) {
        WorkSessionManager workSessionManager = new WorkSessionManager(eventManager);
        eventManager.timeOrder(workSessionManager.getWorkSessions(event));
        this.markInComplete(event, eventManager.getID(workSessionManager.getWorkSessions(event).get(Integer.parseInt(session))), eventManager);
    }

    public void markInComplete(UUID event, UUID session, EventManager eventManager) {
        WorkSessionManager workSessionManager = new WorkSessionManager(eventManager);
        workSessionManager.getWorkSessions(event).remove(eventManager.get(session));
        this.autoSchedule(event, eventManager);
    }

    /**
     * Method which marks complete a session for the Event
     *
     * @param event        An Event
     * @param session      A String which is the session
     * @param eventManager An EventManager
     */
    public void markComplete(UUID event, String session, EventManager eventManager) {
        WorkSessionManager workSessionManager = new WorkSessionManager(eventManager);
        this.markComplete(event, eventManager.getID(workSessionManager.getWorkSessions(event).get(Integer.parseInt(session))), eventManager);
    }

    public void markComplete(UUID event, UUID session, EventManager eventManager) {
        WorkSessionManager workSessionManager = new WorkSessionManager(eventManager);
        workSessionManager.setHoursNeeded(event, (long) (workSessionManager.getHoursNeeded(event) -
                eventManager.getLength(eventManager.get(session))));
        workSessionManager.getWorkSessions(event).remove(eventManager.get(session));

        this.autoSchedule(event, eventManager);
    }

    /**
     * @param deadline     the deadline event
     * @param hoursNeeded  whole long number of hours of this event to be scheduled
     * @param eventManager the eventManager to autoSchedule around
     */
    public void setHoursNeeded(UUID deadline, Long hoursNeeded, EventManager eventManager) {
        WorkSessionManager workSessionManager = new WorkSessionManager(eventManager);
        workSessionManager.setHoursNeeded(deadline, hoursNeeded);
        System.out.println("set session length");
        this.autoSchedule(deadline, eventManager);
    }

    /**
     * A method for setting the length of the session
     *
     * @param deadline      An Event
     * @param sessionLength A Long which is the session length
     * @param eventManager  An EventManager
     */
    public void setSessionLength(UUID deadline, Long sessionLength, EventManager eventManager) {
        WorkSessionManager workSessionManager = new WorkSessionManager(eventManager);
        workSessionManager.setSessionLength(deadline, sessionLength);
        this.autoSchedule(deadline, eventManager);
    }

    public void addDayOrderer(DayOrderer dayOrderer) {
        this.dayOrderers.add(dayOrderer);
    }

    public void addTimeOrderer(TimeOrderer timeOrderer) {
        this.timeOrderers.add(timeOrderer);
    }

    public void autoSchedule(UUID deadline, EventManager eventManager) {
        WorkSessionManager workSessionManager = new WorkSessionManager(eventManager);
        long totalHours = workSessionManager.getTotalHoursNeeded(deadline)- (long)
                (eventManager.totalHours(workSessionManager.getPastSessions(deadline)));

        workSessionManager.setWorkSessions(deadline, workSessionManager.getPastSessions(deadline));

        while (totalHours > 0) {
            System.out.println("scheduling");
            //Step one: determine the length the work session should be by default
            Long length = this.getLength(deadline, totalHours, eventManager);
            //step two: get a list of eligible times the event could take place according to
            // timeGetters analysis of scheduleGetters list of interfering events
            List<LocalDateTime> times = this.timeGetter.getStartTimes(deadline, eventManager, length);
            //step three: determines the ideal start time
            LocalDateTime idealStartTime = this.bestTime(deadline, length, eventManager, times);

            if(!(idealStartTime == null)){
                //step four: adds the work sessions and merges adjacent work sessions
                workSessionManager.addWorkSession(deadline, idealStartTime, idealStartTime.plusHours(length));
                this.mergeSessions(deadline, eventManager, workSessionManager.getWorkSessions(deadline)
                        .get(workSessionManager.getWorkSessions(deadline).size()-1));
            }
            totalHours -= length;
        }
    }

    //private methods and helpers


    //checks if session intersects with or flows into other work session for this event - if it does, merge them into
    //one event
    private void mergeSessions(UUID deadline, EventManager eventManager, Event newSession) {
        System.out.println("attempt merge");
        WorkSessionManager workSessionManager = new WorkSessionManager(eventManager);
        Event toMerge = timeGetter.sessionAdjacent(deadline, eventManager, newSession);
        if (toMerge != null) {
            workSessionManager.removeWorkSession(deadline, newSession);
            workSessionManager.removeWorkSession(deadline, toMerge);
            if (eventManager.getStart(newSession).isBefore(eventManager.getStart(toMerge))) {
                workSessionManager.addWorkSession(deadline, eventManager.getStart(newSession), eventManager.getEnd(toMerge));
            } else {
                workSessionManager.addWorkSession(deadline, eventManager.getStart(toMerge), eventManager.getEnd(newSession));
            }
            mergeSessions(deadline, eventManager, workSessionManager.getWorkSessions(deadline).get(workSessionManager.getWorkSessions(deadline).size()-1));
        }
    }

    private Long getLength(UUID deadline, Long totalHours, EventManager eventManager) {
        WorkSessionManager workSessionManager = new WorkSessionManager(eventManager);
        Long length = workSessionManager.getEventSessionLength(deadline);
        if (workSessionManager.getEventSessionLength(deadline) > totalHours) {
            length = totalHours;
        }
        return length;
    }

    private List<LocalDate> filterDays(UUID deadline, List<LocalDateTime> times, EventManager eventManager) {
        List<LocalDate> eligibleDates = this.getDates(times);
        if (!this.dayOrderers.isEmpty()) {
            for (DayOrderer dayOrderer : this.dayOrderers) {
                dayOrderer.order(deadline, eventManager, eligibleDates, timeGetter.getDaySchedule(eventManager, deadline));
            }
        }
        return eligibleDates;
    }

    private LocalDateTime bestTime(UUID deadline, Long length, EventManager eventManager, List<LocalDateTime> times) {
        if (!this.timeOrderers.isEmpty()) {
            for (TimeOrderer timeOrderer : this.timeOrderers) {
                timeOrderer.order(deadline, eventManager, length, this.filterDays(deadline, times, eventManager), times,
                        timeGetter);
            }
        }
        if (times.isEmpty()){
            return null;
        }
        return times.get(0);
    }

    private List<LocalDate> getDates(List<LocalDateTime> times) {
        List<LocalDate> dates = new ArrayList<>();
        for (LocalDateTime time : times) {
            if (!dates.contains(time.toLocalDate())) {
                dates.add(time.toLocalDate());
            }
        }
        return dates;
    }

    public void changeStartWorking(UUID eventID, LocalDate date, EventManager eventManager) {
        eventManager.changeStartWorking(eventID, date);
        autoSchedule(eventID, eventManager);
    }

    public void changeStartWorking(UUID eventID, Long date, EventManager eventManager) {
        eventManager.changeStartWorking(eventID, date);
        autoSchedule(eventID, eventManager);
    }
}
