package Tests;

import diContainer.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
		// Получение двух провайдеров, ожидается что getInstance() каждого из них вернет разные экземпляры
		Provider<EventDAO> daoProvider = injector.getProvider(EventDAO.class);
		Provider<EventDAO> daoProvider2 = injector.getProvider(EventDAO.class);
		EventDAO one = daoProvider.getInstance();
		EventDAO two = daoProvider2.getInstance();
		
		// Проверяем не вернулся ли null для забинженого интерфейса
		assertNotNull(daoProvider);
		// Проверяем чтобы getInstance() тоже не возвращал null
		assertNotNull(daoProvider.getInstance());
		// Проверяем чтобы возвращенный из контейнера обьект был забинженого класса
		assertSame(InMemoryEventDAOImpl.class, daoProvider.getInstance().getClass());
		// На незабинженный интерфейс провайдер должно вернуться как null
		assertNull(injector.getProvider(TestInterface2.class));
		
		// Проверка чтобы первый обьект не указывал туда же куда и второй
		if(one == two)
			fail("Links point to a single instance of a class not registered as a singleton");

	}

	@Test
	void testIOC() throws Exception {
		
		// Тест работы иньекции параметров в конструктор при запросе обьекта
		EventService eventService = injector.get(EventService.class);

		assertNotNull(eventService);

	}

	@Test
	void testExceptions() {
		Injector injector2 = new InjectorImpl();
	
		// Проверка исключений 
		
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
		// Проверка регистрации синглтона
		Provider<TestInterface> daoProvider = injector.getProvider(TestInterface.class);
		Provider<TestInterface> daoProvider2 = injector.getProvider(TestInterface.class);
		Object one = daoProvider.getInstance();
		Object two = daoProvider2.getInstance();
		// Ожидается что первый обьект указывает туда же куда и второй
		if(one != two)
			fail("A Singltone object is not");
	
		
	}

}
