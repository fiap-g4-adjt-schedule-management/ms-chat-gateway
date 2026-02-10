package br.com.fiap.mschatgateway.domain.ports.inbound;

public interface IncomingMessagePort {

    void handleIncomingMessage(String userId, String message);
}

