package Tests;

import diContainer.Annotations.Inject;

public class TestEventServiceToManyConstr {

    private EventDAO eventDao;	
    
   @Inject
    public TestEventServiceToManyConstr(EventDAO eventDao, Object obj) {
        this.eventDao = eventDao;
     }
    
   @Inject
   public TestEventServiceToManyConstr(EventDAO eventDao) {
       this.eventDao = eventDao;
    }
    
    
    private TestEventServiceToManyConstr() {
 
     }
    
    public void DOWORK() {
    	
    	System.out.println(eventDao.getClass());
    	
    }
    
}
