package Tests;

import diContainer.Inject;

public class EventService {

    private EventDAO eventDao;	
    
    @Inject
    public EventService(EventDAO eventDao, Object obj) {
        this.eventDao = eventDao;
     }
    
    
    
    public EventService() {
 
     }
    
    public void DOWORK() {
    	
    	System.out.println(eventDao.getClass());
    	
    }
    
}
