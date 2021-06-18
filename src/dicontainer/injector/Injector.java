package dicontainer.injector;

import dicontainer.diexceptions.DIException;
import dicontainer.provider.Provider;

public interface Injector {
    <T> Provider<T> getProvider(Class<T> type) throws DIException; //получение экземпляпа провайдера по запрошенному классу
    <T> void bind(Class<T> intf, Class<? extends T> impl); //регистрация байндинга по классу интерфейса и его реализации
    <T> void bindSingleton(Class<T> intf, Class<? extends T> impl); //регистрация синглтон класса 
    <T> T get(Class<T> type) throws DIException; // Получение экземпляра обьекта класса со всеми инъекциями

}
