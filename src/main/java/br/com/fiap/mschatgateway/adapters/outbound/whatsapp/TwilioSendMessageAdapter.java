package br.com.fiap.mschatgateway.adapters.outbound.whatsapp;

import br.com.fiap.mschatgateway.domain.ports.outbound.SendMessagePort;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

import java.util.List;


public class TwilioSendMessageAdapter implements SendMessagePort {

    private final String whatsappNumber;

    public TwilioSendMessageAdapter(
            String accountSid,
            String authToken,
            String whatsappNumber
    ) {
        this.whatsappNumber = whatsappNumber;
        Twilio.init(accountSid, authToken);
    }

    @Override
    public void sendText(String userId, String message) {
        Message.creator(
                new com.twilio.type.PhoneNumber(userId),
                new com.twilio.type.PhoneNumber(whatsappNumber),
                message
        ).create();
    }

    @Override
    public void sendOptions(String userId, String title, List<String> options) {
        StringBuilder sb = new StringBuilder();

        if (title != null && !title.isBlank()) {
            sb.append(title).append("\n");
        }

        for (int i = 0; i < options.size(); i++) {
            sb.append(i + 1).append(" - ").append(options.get(i)).append("\n");
        }

        sendText(userId, sb.toString());
    }
}

