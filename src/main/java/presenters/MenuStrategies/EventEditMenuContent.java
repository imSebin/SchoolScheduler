package presenters.MenuStrategies;

import entities.Event;
import usecases.events.EventManager;

import java.util.ArrayList;
import java.util.List;

public class EventEditMenuContent implements MenuContent{
    private Event event;
    public EventEditMenuContent(Event event){
        this.event = event;
    }
    @Override
    public List<String> getContent() {
        ArrayList<String> content = new ArrayList<>();
        EventManager em = new EventManager();
        content.add(em.displayEvent(this.event));
        content.addAll(new ArrayList<>(){{
            add("enter field: [value] and press enter i.e.");
            add("name: test");
            add("or, type:");
            add("prep");
            add("recurse");
            add("save");
            add("delete");
        }});
        return content;
    }
}
