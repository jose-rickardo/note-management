package com.hei.note.endpoint.event;

import java.util.List;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;

@Component
public class EventProducer<T extends PojaEvent> {

  private final org.springframework.context.ApplicationContext applicationContext;

  public EventProducer(org.springframework.context.ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @SuppressWarnings("unchecked")
  public void accept(List<T> events) {
    for (T event : events) {
      var consumer = resolveConsumer(event);
      consumer.accept(event);
    }
  }

  @SuppressWarnings("unchecked")
  private Consumer<T> resolveConsumer(T event) {
    var beanName =
        Character.toLowerCase(event.getClass().getSimpleName().charAt(0))
            + event.getClass().getSimpleName().substring(1)
            + "Service";
    return (Consumer<T>) applicationContext.getBean(beanName, Consumer.class);
  }
}
