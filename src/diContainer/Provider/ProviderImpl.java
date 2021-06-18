package diContainer.Provider;

public class ProviderImpl<T> implements Provider<T> {

	private final T inst;
	
	public ProviderImpl(T instance)
	{
		this.inst = instance;
	}
	
	
	@Override
	public T getInstance() {
		
		return inst;
	}

}
