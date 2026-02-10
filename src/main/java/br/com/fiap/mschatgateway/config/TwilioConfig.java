package br.com.fiap.mschatgateway.config;

import br.com.fiap.mschatgateway.adapters.outbound.whatsapp.TwilioSendMessageAdapter;
import br.com.fiap.mschatgateway.domain.ports.outbound.SendMessagePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("twilio")
public class TwilioConfig {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.whatsapp-number}")
    private String whatsappNumber;

    @Bean
    public SendMessagePort sendMessagePort() {
        return new TwilioSendMessageAdapter(
                accountSid,
                authToken,
                whatsappNumber
        );
    }
}

