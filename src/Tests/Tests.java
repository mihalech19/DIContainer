package Tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import diContainer.BindingNotFoundException;
import diContainer.ConstructorNotFoundException;
import diContainer.Injector;
import diContainer.InjectorImpl;
import diContainer.Provider;
import diContainer.TooManyConstructorsException;

class Tests {

	Injector injector;

	@BeforeEach
	void setUp() throws Exception {

		injector = new InjectorImpl();
		injector.bind(EventDAO.class, InMemoryEventDAOImpl.class);
		injector.bindSingleton(TestInterface.class, TestInterfaceImpl.class);
	}

	@Test
	void test() {
		// ��������� ���� �����������, ��������� ��� getInstance() ������� �� ��� ������ ������ ����������
		Provider<EventDAO> daoProvider = injector.getProvider(EventDAO.class);
		Provider<EventDAO> daoProvider2 = injector.getProvider(EventDAO.class);
		EventDAO one = daoProvider.getInstance();
		EventDAO two = daoProvider2.getInstance();
		
		// ��������� �� �������� �� null ��� ����������� ����������
		assertNotNull(daoProvider);
		// ��������� ����� getInstance() ���� �� ��������� null
		assertNotNull(daoProvider.getInstance());
		// ��������� ����� ������������ �� ���������� ������ ��� ����������� ������
		assertSame(InMemoryEventDAOImpl.class, daoProvider.getInstance().getClass());
		// �� ������������� ��������� ��������� ������ ��������� ��� null
		assertNull(injector.getProvider(TestInterface2.class));
		
		// �������� ����� ������ ������ �� �������� ���� �� ���� � ������
		if(one == two)
			fail("Links point to a single instance of a class not registered as a singleton");

	}

	@Test
	void testIOC() throws Exception, ConstructorNotFoundException {
		
		// ���� ������ �������� ���������� � ����������� ��� ������� �������
		EventService eventService = injector.get(EventService.class);

		assertNotNull(eventService);

	}

	@Test
	void testExceptions() {
		Injector injector2 = new InjectorImpl();
	
		// �������� ���������� 
		
		assertThrows(BindingNotFoundException.class, () -> {
			injector2.get(EventService.class);
		});
		
		assertThrows(ConstructorNotFoundException.class, () -> {
			injector2.get(TestEventServiceWithoutConstr.class);
		});
		
		assertThrows(ConstructorNotFoundException.class, () -> {
			injector2.get(TooManyConstructorsException.class);
		});

	}
	
	@Test
	void testSingleton() {
		// �������� ����������� ���������
		Provider<TestInterface> daoProvider = injector.getProvider(TestInterface.class);
		Provider<TestInterface> daoProvider2 = injector.getProvider(TestInterface.class);
		Object one = daoProvider.getInstance();
		Object two = daoProvider2.getInstance();
		// ��������� ��� ������ ������ ��������� ���� �� ���� � ������
		if(one != two)
			fail("A Singltone object is not");
	
		
	}

}
