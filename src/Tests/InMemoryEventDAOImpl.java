package Tests;

import diContainer.Annotations.Inject;

public class InMemoryEventDAOImpl implements EventDAO {

	public void doSomething() {
		System.out.println("I'm InMemoryEventDAOImpl");

	}
	
	
	@Inject
	public InMemoryEventDAOImpl(Object obj, TestInterface ti) {
		
	}
}
