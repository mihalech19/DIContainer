package diContainer;

public interface Injector {
    <T> Provider<T> getProvider(Class<T> type); //получение экземпляпа провайдера по запрошенному классу
    <T> void bind(Class<T> intf, Class<? extends T> impl); //регистрация байндинга по классу интерфейса и его реализации
    <T> void bindSingleton(Class<T> intf, Class<? extends T> impl); //регистрация синглтон класса 
    <T> T get(Class<T> type) throws TooManyConstructorsException, ConstructorNotFoundException, BindingNotFoundException; // Получение экземпляра обьекта класса со всеми инъекциями

}
