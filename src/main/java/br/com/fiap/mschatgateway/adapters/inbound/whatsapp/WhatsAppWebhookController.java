package br.com.fiap.mschatgateway.adapters.inbound.whatsapp;

import br.com.fiap.mschatgateway.domain.ports.inbound.IncomingMessagePort;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/webhook/whatsapp")
public class WhatsAppWebhookController {

    private final IncomingMessagePort incomingMessagePort;

    public WhatsAppWebhookController(IncomingMessagePort incomingMessagePort) {
        this.incomingMessagePort = incomingMessagePort;
    }

    @PostMapping
    public void receive(@RequestParam Map<String, String> payload) {

        String from = payload.get("From");
        String body = payload.get("Body");

        incomingMessagePort.handleIncomingMessage(from, body);
    }
}


