package diContainer.Injector;

import diContainer.Annotations.Inject;
import diContainer.DIExceptions.BindingNotFoundException;
import diContainer.DIExceptions.ConstructorNotFoundException;
import diContainer.DIExceptions.DIException;
import diContainer.DIExceptions.TooManyConstructorsException;
import diContainer.Provider.Provider;
import diContainer.Provider.ProviderImpl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Optional;

public class InjectorImpl implements Injector {

	private final Map<Class<?>, Class<?>> bindings ;
	private final Map<Class<?>, Optional<Object>> singletons ;

	public InjectorImpl(Map<Class<?>, Class<?>> bindings, Map<Class<?>, Optional<Object>> singletons) {
		this.bindings = bindings;
		this.singletons = singletons;
	}


	@SuppressWarnings({ "unchecked"})
	@Override
	public <T> Provider<T> getProvider(Class<T> type) throws DIException{

			if (!Modifier.isAbstract(type.getModifiers()) && !Modifier.isInterface(type.getModifiers()))
				return new ProviderImpl<>(get(type)); // Возврат экземпляра самого себя, если запрошенный класс не является интерфейсом / абстрактным

			else if (this.bindings.containsKey(type)) { // Проверка наличия bind
				if (singletons.containsKey(type))        // Зарегистрирован как синглтон?
				{
					Optional<Object> singleton = singletons.get(type);
					if (!singleton.isPresent()) {
						singleton = Optional.of(get(bindings.get(type)));     // Инициализация и сохранение ссылки на синглотон
						singletons.put(type, singleton);
					}
					return new ProviderImpl<>((T) singleton.get());
				} else
					return new ProviderImpl<>((T) get(bindings.get(type)));  // получение и возврат экземпляра класса

			}
			else
				return null;

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> T get(Class<T> type) throws DIException {
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
						throw new TooManyConstructorsException(
								"Too many constructors with @Inject annotation");

				if (constructor.getParameterCount() == 0)  // Параллельно сохраним конструктор по умолчанию, если есть
					constrDefault = constructor;

			}

			if (constrWithAnnotation != null) {
				Object[] injParams = getInjectingParams(constrWithAnnotation);  // Получение параметров для иньекции
				if (injParams != null)
					return (T) constrWithAnnotation.newInstance(injParams);
				else
					throw new BindingNotFoundException("Binding not found for one or many parameter");
			}

			else if (constrDefault != null) 
				return (T) constrDefault.newInstance(); // Создание экземпляра с помощью конструктора по умолчанию
			else
				throw new ConstructorNotFoundException("No possible constructors found");

		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		return null;

	}

	public Object[] getInjectingParams(Constructor constr) throws DIException {
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
	public synchronized  <T> void bind(Class<T> intf, Class<? extends T> impl) {

		this.bindings.put(intf, impl);

	}

	@Override
	public synchronized <T> void bindSingleton(Class<T> intf, Class<? extends T> impl) {
		bindings.put(intf, impl);
		singletons.put(intf, Optional.empty());

	}

}
