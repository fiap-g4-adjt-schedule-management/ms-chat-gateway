package br.com.fiap.mschatgateway.application.usecase;

import br.com.fiap.mschatgateway.application.service.ConversationRouterService;
import br.com.fiap.mschatgateway.domain.ports.inbound.IncomingMessagePort;
import org.springframework.stereotype.Service;

@Service
public class HandleIncomingMessageUseCase implements IncomingMessagePort {

    private final ConversationRouterService routerService;

    public HandleIncomingMessageUseCase(ConversationRouterService routerService) {
        this.routerService = routerService;
    }

    @Override
    public void handleIncomingMessage(String userId, String message) {
        routerService.handle(userId, message);
    }
}
