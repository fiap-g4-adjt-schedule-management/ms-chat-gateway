package br.com.fiap.mschatgateway.domain.ports.outbound;

import java.util.List;

public interface SendMessagePort {

    void sendText(String userId, String message);

    void sendOptions(String userId, String title, List<String> options);
}

