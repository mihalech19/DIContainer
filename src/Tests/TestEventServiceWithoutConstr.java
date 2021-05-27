package Tests;

import diContainer.Inject;

public class TestEventServiceWithoutConstr {

    private EventDAO eventDao;	
    
   
    public TestEventServiceWithoutConstr(EventDAO eventDao, Object obj) {
        this.eventDao = eventDao;
     }
    
    
    
    private TestEventServiceWithoutConstr() {
 
     }
    
    public void DOWORK() {
    	
    	System.out.println(eventDao.getClass());
    	
    }
    
}
