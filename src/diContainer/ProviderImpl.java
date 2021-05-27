package diContainer;

public class ProviderImpl<T> implements Provider<T> {

	T inst;
	
	public ProviderImpl(T instance)
	{
		this.inst = instance;
	}
	
	
	@Override
	public T getInstance() {
		
		return inst;
	}

}
