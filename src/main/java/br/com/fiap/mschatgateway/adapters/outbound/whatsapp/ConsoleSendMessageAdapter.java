package br.com.fiap.mschatgateway.adapters.outbound.whatsapp;

import br.com.fiap.mschatgateway.domain.ports.outbound.SendMessagePort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("local")
public class ConsoleSendMessageAdapter implements SendMessagePort {

    @Override
    public void sendText(String userId, String message) {
        System.out.println("\n==============================");
        System.out.println("[BOT -> " + userId + "]");
        System.out.println(message);
        System.out.println("==============================");
    }

    @Override
    public void sendOptions(String userId, String title, List<String> options) {

        System.out.println("\n[BOT OPTIONS -> " + userId + "]");

        if (title != null && !title.isBlank()) {
            System.out.println(title);
        }

        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + " - " + options.get(i));
        }

        System.out.println("------------------------------");
    }
}

