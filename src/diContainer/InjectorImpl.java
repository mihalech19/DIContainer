package diContainer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.HashMap;

public class InjectorImpl implements Injector {

	private final HashMap<Class<?>, Class<?>> bindings = new HashMap<>();
	private final HashMap<Class<?>, Object> singletons = new HashMap<>();

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public synchronized <T> Provider<T> getProvider(Class<T> type) {
		try {
			if (!Modifier.isAbstract(type.getModifiers()) && !Modifier.isInterface(type.getModifiers()))
				return new ProviderImpl<>(get(type)); // Возврат экземпляра самого себя, если запрошенный класс не является интерфейсом / абстрактным

			else if (this.bindings.containsKey(type)) { // Проверка наличия bind
				if (singletons.containsKey(type))        // Зарегистрирован как синглтон?
				{
					Object singleton = singletons.get(type);
					if (singleton != null)
						return new ProviderImpl<>((T) singleton); // Возврат нового экземпляра, если ранее не инициализировалось
					else
					{
						singleton = get(this.bindings.get(type));     // Инициализация и сохранение ссылки на синглотон
						singletons.put(type, singleton);
						return new ProviderImpl<>((T) singleton);
					}
				} else

					return new ProviderImpl<>((T) get(this.bindings.get(type)));  // получение и возврат экземпляра класса

			}

			else
				return null;
		} catch (TooManyConstructorsException | ConstructorNotFoundException | BindingNotFoundException e) {
			e.printStackTrace();
		}

		return null;

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> T get(Class<T> type)
			throws TooManyConstructorsException, ConstructorNotFoundException, BindingNotFoundException {
		Class cl;
		try {
			cl = Class.forName(type.getName());

			Constructor[] constructors = cl.getConstructors();
			Constructor constrWithAnnotation = null;
			Constructor constrDefault = null;
			for (Constructor constructor : constructors) {   // Скан конструкторов
				if (constructor.isAnnotationPresent(Inject.class)) // Поиск аннотации @Inject
					if (constrWithAnnotation == null)
						constrWithAnnotation = constructor;
					else
						throw new diContainer.TooManyConstructorsException(
								"Too many constructors with @Inject annotation");

				if (constructor.getParameterCount() == 0)  // Параллельно сохраним конструктор по умолчанию, если есть
					constrDefault = constructor;

			}

			if (constrWithAnnotation != null) {
				Object[] injParams = getInjectingParams(constrWithAnnotation);  // Получение параметров для иньекции
				if (injParams != null)
					return (T) constrWithAnnotation.newInstance(injParams);
				else
					throw new diContainer.BindingNotFoundException("Binding not found for one or many parameter");
			}

			else if (constrDefault != null) 
				return (T) constrDefault.newInstance(); // Создание экземпляра с помощью конструктора по умолчанию
			else
				throw new diContainer.ConstructorNotFoundException("No possible constructors found");

		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;

	}

	public Object[] getInjectingParams(Constructor constr) {
		Parameter[] params = constr.getParameters();
		Object[] injectParams = new Object[params.length]; // массив требуемых параметров для конструктора
		Provider prov;
		for (int i = 0; i < params.length; i++) { // Проход по всем параметрам конструктора и запрос их провайдеров

			prov = getProvider(params[i].getType());

			if (prov == null)
				return null;

			injectParams[i] = prov.getInstance();  // сохранение нужного объекта

		}

		return injectParams;
	}

	@Override
	public <T> void bind(Class<T> intf, Class<? extends T> impl) {

		this.bindings.put(intf, impl);

	}

	@Override
	public <T> void bindSingleton(Class<T> intf, Class<? extends T> impl) {
		this.bindings.put(intf, impl);
		singletons.put(intf, null);

	}

}
