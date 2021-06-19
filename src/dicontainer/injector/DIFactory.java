package dicontainer.injector;

import java.util.HashMap;

public class DIFactory {

private final static Injector _default_injector = new InjectorImpl(new HashMap<>(), new HashMap<>());
private static Injector _injector ;


    public static Injector getContainer(){
        if (_injector != null)
            return  _injector;
        else
            return _default_injector;

    }


    public static Injector getDefaultContainer(){
        return _default_injector;

    }

    public static void setContainer(Injector injector){
        _injector = injector;

    }


}
