package diContainer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.HashMap;

public class InjectorImpl implements Injector {

	private HashMap<Class<?>, Class<?>> bindings = new HashMap<Class<?>, Class<?>>();
	private HashMap<Class<?>, Object> singletons = new HashMap<Class<?>, Object>();

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public synchronized <T> Provider<T> getProvider(Class<T> type) {
		try {
			if (!Modifier.isAbstract(type.getModifiers()) && !Modifier.isInterface(type.getModifiers()))
				return new ProviderImpl<T>((T) get(type)); // ������� ���������� ������ ����, ���� ����������� ����� �� �������� ����������� / �����������

			else if (this.bindings.containsKey(type)) { // �������� ������� bind
				if (singletons.containsKey(type))        // ��������������� ��� ��������?
				{
					Object singleton = singletons.get(type);
					if (singleton != null)
						return new ProviderImpl <T> ((T) singleton); // ������� ������ ����������, ���� ����� �� ������������������
					else
					{
						singleton = get(this.bindings.get(type));     // ������������� � ���������� ������ �� ���������
						singletons.put(type, singleton);
						return new ProviderImpl <T> ((T) singleton);  
					}
				} else

					return new ProviderImpl<T>((T) get(this.bindings.get(type)));  // ��������� � ������� ���������� ������

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
			for (Constructor constructor : constructors) {   // ���� �������������
				if (constructor.isAnnotationPresent(Inject.class)) // ����� ��������� @Inject
					if (constrWithAnnotation == null)
						constrWithAnnotation = constructor;
					else
						throw new diContainer.TooManyConstructorsException(
								"Too many constructors with @Inject annotation");

				if (constructor.getParameterCount() == 0)  // ����������� �������� ����������� �� ���������, ���� ����
					constrDefault = constructor;

			}

			if (constrWithAnnotation != null) {
				Object[] injParams = getInjectingParams(cl, constrWithAnnotation);  // ��������� ���������� ��� ��������
				if (injParams != null)
					return (T) constrWithAnnotation.newInstance(injParams);
				else
					throw new diContainer.BindingNotFoundException("Binding not found for one or many parameter");
			}

			else if (constrDefault != null) 
				return (T) constrDefault.newInstance(); // �������� ���������� � ������� ������������ �� ���������
			else
				throw new diContainer.ConstructorNotFoundException("No possible constructors found");

		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;

	}

	public Object[] getInjectingParams(Class cl, Constructor constr) throws BindingNotFoundException {
		Parameter[] params = constr.getParameters();
		Object[] injectParams = new Object[params.length]; // ������ ��������� ���������� ��� ������������
		Provider prov = null;
		for (int i = 0; i < params.length; i++) { // ������ �� ���� ���������� ������������ � ������ �� �����������

			prov = getProvider(params[i].getType());

			if (prov == null)
				return null;

			injectParams[i] = prov.getInstance();  // ���������� ������� �������

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
