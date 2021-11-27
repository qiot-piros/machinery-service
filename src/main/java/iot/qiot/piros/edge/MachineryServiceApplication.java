package iot.qiot.piros.edge;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduler;
import iot.qiot.piros.edge.core.model.event.SubscriptionCompletedEvent;
import iot.qiot.piros.edge.service.FacilityService;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@RequiredArgsConstructor
public class MachineryServiceApplication {

  private static final Logger LOG = LoggerFactory.getLogger(MachineryServiceApplication.class);

  private final FacilityService facilityService;
  private final Scheduler scheduler;
  private final Event<SubscriptionCompletedEvent> subscriptionCompletedEventEvent;

  void onApplicationStart(@Observes StartupEvent event) {
    LOG.info("qiot.machinery - Starting machinery-service setup");
    scheduler.pause();
    facilityService.subscribeMachinery();
    subscriptionCompletedEventEvent.fire(new SubscriptionCompletedEvent());
    scheduler.resume();
    LOG.info("qiot.machinery - Finished machinery-service setup");
  }
}
