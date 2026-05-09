import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

class SubscriberMethod {
    public Object target;
    public Method method;
    public Class<?> eventType;
    public ThreadMode threadMode;

    public SubscriberMethod(Object target, Method method, Class<?> eventType, ThreadMode threadMode) {
        this.target = target;
        this.method = method;
        this.eventType = eventType;
        this.threadMode = threadMode;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SubscriberMethod other)) {
            return false;
        }

        return target == other.target
                && method.equals(other.method)
                && eventType.equals(other.eventType)
                && threadMode == other.threadMode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                System.identityHashCode(target),
                method,
                eventType,
                threadMode
        );
    }
}
enum ThreadMode {
    POSTING,
    ASYNC
}
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Subscribe {
    ThreadMode threading() default ThreadMode.POSTING;
}

public class EventBus {
    private final Map<Class<?>, CopyOnWriteArrayList<SubscriberMethod>> subscribersByEventType;
    private final Map<Object, List<SubscriberMethod>> subscriptionsByObject;
    private final ExecutorService executorService;

    public EventBus() {
        this.subscribersByEventType = new ConcurrentHashMap<>();
        this.subscriptionsByObject = new ConcurrentHashMap<>();
        this.executorService = Executors.newCachedThreadPool();
    }

    public void register(Object subscriber) {
        List<SubscriberMethod> subscriberMethods = findSubscriberMethods(subscriber);

        if (subscriberMethods.isEmpty()) {
            throw new IllegalArgumentException("No valid @Subscrib methods found in " + subscriber.getClass().getName());
        }

        subscriptionsByObject.putIfAbsent(subscriber, new ArrayList<>());

        for (SubscriberMethod subscriberMethod : subscriberMethods) {
            Class<?> clazz = subscriberMethod.eventType;
            do{
            subscribersByEventType
                    .computeIfAbsent(clazz, k -> new CopyOnWriteArrayList<>())
                    .add(subscriberMethod);

            subscriptionsByObject.get(subscriber).add(subscriberMethod);
                clazz = clazz.getSuperclass();
            }
            while (clazz != null && clazz != Object.class) ;
        }
    }

    public void unregister(Object subscriber) {
        List<SubscriberMethod> subscriberMethods = subscriptionsByObject.remove(subscriber);

        if (subscriberMethods == null) {
            return;
        }

        for (SubscriberMethod subscriberMethod : subscriberMethods) {
            List<SubscriberMethod> methods = subscribersByEventType.get(subscriberMethod.eventType);

            if (methods != null) {
                methods.remove(subscriberMethod);

                if (methods.isEmpty()) {
                    subscribersByEventType.remove(subscriberMethod.eventType);
                }
            }
        }
    }

    public void post(Object event) {
        Class<?> eventClass = event.getClass();

        for (Map.Entry<Class<?>, CopyOnWriteArrayList<SubscriberMethod>> entry : subscribersByEventType.entrySet()) {
            if (entry.getKey().isAssignableFrom(eventClass)) {
                for (SubscriberMethod subscriberMethod : entry.getValue()) {
                    dispatch(event, subscriberMethod);
                }
            }
        }
    }

    private void dispatch(Object event, SubscriberMethod subscriberMethod) {
        if (subscriberMethod.threadMode == ThreadMode.POSTING) {
            invokeSubscriber(event, subscriberMethod);
        } else {
            executorService.submit(() -> invokeSubscriber(event, subscriberMethod));
        }
    }

    private void invokeSubscriber(Object event, SubscriberMethod subscriberMethod) {
        try {
            subscriberMethod.method.invoke(subscriberMethod.target, event);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to invoke subscriber method: " + subscriberMethod.method.getName(), e);
        }
    }

    private List<SubscriberMethod> findSubscriberMethods(Object subscriber) {
        List<SubscriberMethod> subscriberMethods = new ArrayList<>();
        Class<?> clazz = subscriber.getClass();

        while (clazz != null && clazz != Object.class) {
            Method[] methods = clazz.getDeclaredMethods();

            for (Method method : methods) {
                if (!method.isAnnotationPresent(Subscribe.class)) {
                    continue;
                }

                Class<?>[] parameterTypes = method.getParameterTypes();

                if (parameterTypes.length != 1) {
                    throw new IllegalArgumentException("Method " + method.getName() + " must have exactly one parameter");
                }

                method.setAccessible(true);

                Subscribe annotation = method.getAnnotation(Subscribe.class);

                subscriberMethods.add(
                        new SubscriberMethod(
                                subscriber,
                                method,
                                parameterTypes[0],
                                annotation.threading()
                        )
                );
            }

            clazz = clazz.getSuperclass();
        }

        return subscriberMethods;
    }

    public void shutdown() {
        executorService.shutdown();
    }
}