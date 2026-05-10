package custom_rmi;

import java.util.HashMap;
import java.util.Map;

public class RemoteObjectRegistry {

    private final Map<String, Object> objects = new HashMap<>();

    public void bind(String name, Object object) {
        objects.put(name, object);
        System.out.println("Bound remote object: " + name);
    }

    public Object lookup(String name) {
        return objects.get(name);
    }
}