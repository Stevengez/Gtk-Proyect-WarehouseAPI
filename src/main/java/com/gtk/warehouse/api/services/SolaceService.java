package com.gtk.warehouse.api.services;

import com.solacesystems.jcsmp.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SolaceService {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<SseEmitter>();

    final JCSMPSession solaceSession;

    public SolaceService() throws JCSMPException {
        final JCSMPProperties properties = new JCSMPProperties();
        properties.setProperty(JCSMPProperties.HOST, "tcps://mr-connection-j89s8o3lv2i.messaging.solace.cloud:55443");
        properties.setProperty(JCSMPProperties.USERNAME, "solace-cloud-client");
        properties.setProperty(JCSMPProperties.PASSWORD, "q34vkppud4uehakh2ivmct395h");
        properties.setProperty(JCSMPProperties.VPN_NAME, "gtek-eu-dev-100");
        solaceSession = JCSMPFactory.onlyInstance().createSession(properties);
        solaceSession.connect();

        final Topic topic = JCSMPFactory.onlyInstance().createTopic("warehouse/orders/received");
        final Topic topic2 = JCSMPFactory.onlyInstance().createTopic("warehouse/orders/update");
        solaceSession.addSubscription(topic);
        solaceSession.addSubscription(topic2);

        final XMLMessageConsumer consumer = solaceSession.getMessageConsumer(new XMLMessageListener() {
            @Override
            public void onReceive(BytesXMLMessage msg) {
                if (msg instanceof BytesMessage bytesMessage) {
                    byte[] byteData = bytesMessage.getData();
                    String event = new String(byteData);
                    sendEvent(bytesMessage.getDestination().getName(), event);
                } else {
                    System.out.printf("Other message type: %s%n", msg.dump());
                }
            }

            @Override
            public void onException(JCSMPException e) {
                System.out.println("Error reading message"+e.getMessage());
            }
        });

        consumer.start();
    }

    public SseEmitter addEmitter(){
        SseEmitter emitter = new SseEmitter();
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));
        return emitter;
    }

    public void sendEvent (String name, String payload){
        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();
        for(SseEmitter emitter : emitters){
            try {
                emitter.send(SseEmitter
                        .event()
                        .name(name)
                        .data(payload)
                );
            } catch (IOException e) {
                System.out.println("Error while emiting:"+e.getMessage());
                emitter.completeWithError(e);
                deadEmitters.add(emitter);
            }
        }

        emitters.removeAll(deadEmitters);
    }

    @Scheduled(fixedRate = 2000)
    public void sendKeepAlive() {
        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();
        try {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event().comment("keep-alive"));
                } catch (Exception e) {
                    deadEmitters.add(emitter);
                    emitter.completeWithError(e);
                }
            }
        }catch (Exception e){
            // No need to handle
        }

        if(!deadEmitters.isEmpty()){
            emitters.removeAll(deadEmitters);
        }
    }
}