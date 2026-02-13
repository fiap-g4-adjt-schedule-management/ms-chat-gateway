# 📲 ms-chat-gateway

Microserviço BFF responsável por orquestrar conversas via WhatsApp utilizando arquitetura Hexagonal (Ports & Adapters).


---

# 🧱 Arquitetura

O projeto segue **Arquitetura Hexagonal explícita**:

```
application  → regras de orquestração (ConversationRouterService)
domain       → modelos + ports (contratos)
adapters     → integrações externas (Twilio, API de farmácias, JSON local)
infrastructure → loaders e configs
```

---

# ⚙️ Tecnologias

* Java 21
* Spring Boot 3.4.4
* Maven
* Docker
* Twilio SDK
* ngrok (para webhook público)

---

# 📦 Pré-requisitos

Antes de executar com Twilio, você precisa:

* Java 21 instalado
* Maven ou usar `./mvnw`
* Docker instalado
* Conta no Twilio
* ngrok instalado

---

# 🔐 Variáveis de Ambiente (.env)

Crie um arquivo `.env` na raiz do projeto:

```env
SPRING_PROFILES_ACTIVE=backend,twilio

BACKEND_BASE_URL=http://localhost:8088

TWILIO_ACCOUNT_SID=SEU_ACCOUNT_SID
TWILIO_AUTH_TOKEN=SEU_AUTH_TOKEN
TWILIO_WHATSAPP_NUMBER=whatsapp:+14155238886
```

⚠️ Nunca versionar esse arquivo.

---

# 🐳 Subindo a aplicação com Docker

## 🔨 Build da imagem

```bash
docker compose build
```

## ▶️ Subir container

```bash
docker compose up
```

Se quiser rodar em background:

```bash
docker compose up -d
```

---

# 🎯 Profiles disponíveis

Você pode forçar profiles manualmente:

Usar mock local (sem api de farmácias e sem Twilio)
```bash
SPRING_PROFILES_ACTIVE=local docker compose up
```

Usar api de farmácias
```bash
SPRING_PROFILES_ACTIVE=backend docker compose up
```

Usar apenas Twilio
```bash
SPRING_PROFILES_ACTIVE=twilio docker compose up
```

Usar API Farmácias + Twilio (produção / integração completa)
```bash
SPRING_PROFILES_ACTIVE=backend,twilio docker compose up
```

---

# 🌐 Configuração do ngrok (OBRIGATÓRIO para Twilio)

⚠️ IMPORTANTE: O Twilio só consegue enviar mensagens para uma URL pública HTTPS.
Por isso precisamos do ngrok.

---

## 1️⃣ Instalar ngrok

Baixe em:

https://ngrok.com/

Depois configure o seu token:

```bash
ngrok config add-authtoken SEU_TOKEN_AQUI
```

---

## 2️⃣ Subir túnel HTTPS

Com a aplicação rodando na porta 8080:

```bash
ngrok http 8080
```

Ele irá gerar algo como:

```
https://abc123.ngrok-free.app
```

---

## 🚨 ATENÇÃO MUITO IMPORTANTE

O ngrok **PRECISA ficar a rodar com o terminal aberto**.

Se você:

* Fechar o terminal
* Encerrar o processo
* Reiniciar o computador

👉 O link HTTPS deixa de funcionar
👉 O Twilio não consegue mais enviar mensagens
👉 O webhook quebra

Sempre que reiniciar o ngrok, um **novo link será gerado**, e você precisará atualizar no Twilio.

---

# 📲 Configuração no Twilio

No Console do Twilio:

1. Vá em:

   ```
   Messaging → Try it out → Send a WhatsApp message
   ```

2. Em "WHEN A MESSAGE COMES IN", configure:

```
https://SEU_LINK_NGROK/webhook/whatsapp
```

Exemplo:

```
https://abc123.ngrok-free.app/webhook/whatsapp
```

3. Método: POST

Salvar.

---

# 📡 Endpoint do Webhook

```
POST /webhook/whatsapp
```

Controller:

```java
@PostMapping
public void receive(@RequestParam Map<String, String> payload) {
    String from = payload.get("From");
    String body = payload.get("Body");
    incomingMessagePort.handleIncomingMessage(from, body);
}
```

---

# 🧪 Testando Local sem Twilio

Para testar apenas fluxo interno:

```bash
SPRING_PROFILES_ACTIVE=local docker compose up
```

Simular mensagem inicial

curl -X POST http://localhost:8080/webhook/whatsapp \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "From=whatsapp:+5511999999999" \
  -d "Body=Olá"


Simular escolha do menu

curl -X POST http://localhost:8080/webhook/whatsapp \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "From=whatsapp:+5511999999999" \
  -d "Body=1"

Nesse modo:

* Usa MockPharmacyServiceAdapter
* Não chama Twilio
* Permite testar regras de conversação

---

# 🧠 Fluxo do Sistema

1. Usuário envia mensagem no WhatsApp
2. Twilio chama webhook público
3. Chat Gateway processa conversa
4. Consulta API de farmácias
5. Retorna resposta formatada
6. Ao final do fluxo:

   * Usuário envia feedback (Sim/Não)
   * Sistema envia PUT para API com UUID da pesquisa

---

# 🔄 Feedback Flow

Quando a API retorna o UUID da pesquisa:

```
GET /medication/{id}/pharmacys
```

O UUID é armazenado na sessão.

Ao final do fluxo:

```
PUT /feedback/{UUID}
{
  "flagFeedback": true | false
}
```

---

# 🧼 Encerramento da Conversa

Ao final:

```
Deseja encerrar a conversa?
1 - Encerrar conversa
2 - Voltar ao menu principal
```

---

# 👩‍💻 Desenvolvido por

Projeto FIAP — Pós-Tech Arquitetura e Desenvolvimento Java
