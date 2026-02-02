# Інструкція: RabbitMQ та SockJS для чат-модуля

Цей документ описує дві зміни в чат-модулі: **RabbitMQ** як STOMP-брокер для real-time повідомлень між кількома інстансами сервісу та **SockJS** як fallback для браузерів без підтримки WebSocket.

---

## 1. Вимоги

- **RabbitMQ**: повідомлення чату мають передаватися через RabbitMQ (STOMP), щоб два інстанси додатку (app та app2) отримували одні й ті самі повідомлення.
- **SockJS**: клієнти підключаються до `/ws`; якщо браузер не підтримує WebSocket, використовується SockJS (long-polling / streaming).

---

## 2. Що змінено (огляд)

| Що | Де |
|----|-----|
| SockJS fallback | `WebSocketConfig` — endpoint `/ws` реєструється з `.withSockJS()`. |
| RabbitMQ relay | `WebSocketConfig` — замість `enableSimpleBroker` використовується `enableStompBrokerRelay` з хостом/портом/логіном з конфігу. |
| Властивості relay | `WebSocketProperties` + `application.yml` / `application-dev.yml` — relay-host, relay-port, relay-username, relay-password. |
| Docker | `docker-compose.yml` — сервіс RabbitMQ (з STOMP plugin) та другий інстанс додатку `app2`. |
| Змінні середовища | `.env` — RABBITMQ_HOST, RABBITMQ_STOMP_PORT, RABBITMQ_USER, RABBITMQ_PASSWORD. |
| Залежності | Додаткових залежностей у `pom.xml` **не потрібно** — використовуються Spring WebSocket та вбудований STOMP relay client. |

---

## 3. Покрокові зміни по файлах

### 3.1. `src/main/java/com/vention/medfast/chat/websocket/properties/WebSocketProperties.java`

**Мета:** додати властивості для STOMP broker relay (RabbitMQ).

Додати поля та метод:

```java
private String relayHost;

private int relayPort = 61613;

private String relayUsername;

private String relayPassword;

public boolean isRelayEnabled() {
    return relayHost != null && !relayHost.isBlank();
}
```

Повний клас має бути з `@Getter`, `@Setter`, `@Component`, `@ConfigurationProperties(prefix = "app.websocket")`, полем `allowedOrigins` та новими полями вище.

---

### 3.2. `src/main/java/com/vention/medfast/config/WebSocketConfig.java`

**Мета:** увімкнути SockJS на endpoint і використовувати RabbitMQ як брокер, коли relay налаштовано.

**a) Реєстрація endpoint з SockJS**

Було:

```java
registry.addEndpoint("/ws").setAllowedOrigins(webSocketProperties.getAllowedOrigins().toArray(String[]::new));
```

Стало:

```java
registry.addEndpoint("/ws")
    .setAllowedOrigins(webSocketProperties.getAllowedOrigins().toArray(String[]::new))
    .withSockJS();
```

**b) Message broker: relay vs simple**

Було:

```java
registry.enableSimpleBroker("/topic");
registry.setApplicationDestinationPrefixes("/app");
```

Стало:

```java
if (webSocketProperties.isRelayEnabled()) {
    var relay = registry.enableStompBrokerRelay("/topic")
        .setRelayHost(webSocketProperties.getRelayHost())
        .setRelayPort(webSocketProperties.getRelayPort());
    if (webSocketProperties.getRelayUsername() != null
            && !webSocketProperties.getRelayUsername().isBlank()) {
        relay.setClientLogin(webSocketProperties.getRelayUsername())
            .setClientPasscode(webSocketProperties.getRelayPassword() != null
                    ? webSocketProperties.getRelayPassword()
                    : "")
            .setSystemLogin(webSocketProperties.getRelayUsername())
            .setSystemPasscode(webSocketProperties.getRelayPassword() != null
                    ? webSocketProperties.getRelayPassword()
                    : "");
    }
} else {
    registry.enableSimpleBroker("/topic");
}
registry.setApplicationDestinationPrefixes("/app");
```

Якщо `RABBITMQ_HOST` не задано (порожній) — використовується in-memory simple broker. Якщо задано — підключення до RabbitMQ по STOMP.

---

### 3.3. `src/main/resources/application.yml`

**Мета:** підключити конфіг WebSocket relay до змінних середовища.

У блоці `app.websocket` додати:

```yaml
relay-host: ${RABBITMQ_HOST:}
relay-port: ${RABBITMQ_STOMP_PORT:61613}
relay-username: ${RABBITMQ_USER:guest}
relay-password: ${RABBITMQ_PASSWORD:guest}
```

Порожній `relay-host` означає «relay вимкнено» — працює simple broker.

---

### 3.4. `src/main/resources/application-dev.yml`

**Мета:** ті самі ключі для dev-профілю (можна перевизначити порт або креденшали).

У блоці `app.websocket` додати:

```yaml
relay-host: ${RABBITMQ_HOST:}
relay-port: ${RABBITMQ_STOMP_PORT:61613}
relay-username: ${RABBITMQ_USER:guest}
relay-password: ${RABBITMQ_PASSWORD:guest}
```

---

### 3.5. `.env`

**Мета:** змінні для RabbitMQ; якщо не використовуєте RabbitMQ — залишити `RABBITMQ_HOST` порожнім.

Додати (наприклад, в кінець файлу):

```env
# RabbitMQ (STOMP broker для WebSocket чату). Якщо RABBITMQ_HOST порожній — використовується in-memory broker.
RABBITMQ_HOST=
RABBITMQ_STOMP_PORT=61613
RABBITMQ_USER=guest
RABBITMQ_PASSWORD=guest
```

Для запуску з Docker Compose (app + app2) у тому ж `.env` задати:

```env
RABBITMQ_HOST=rabbitmq
```

У контейнерах значення `RABBITMQ_HOST=rabbitmq` буде передаватися через `environment` у `docker-compose.yml`, тому в `.env` для локального запуску можна залишити `RABBITMQ_HOST=`.

---

### 3.6. `docker-compose.yml`

**Мета:** додати RabbitMQ з увімкненим STOMP plugin та другий інстанс додатку (app2) для перевірки обміну повідомленнями між двома інстансами.

**a) Сервіс RabbitMQ** (додати на початок `services`, перед `app`):

```yaml
rabbitmq:
  image: rabbitmq:3-management
  container_name: medfast-rabbitmq
  hostname: rabbitmq
  ports:
    - "5672:5672"
    - "15672:15672"
    - "61613:61613"
  environment:
    RABBITMQ_DEFAULT_USER: ${RABBITMQ_USER:-guest}
    RABBITMQ_DEFAULT_PASS: ${RABBITMQ_PASSWORD:-guest}
  command: bash -c "rabbitmq-plugins enable rabbitmq_stomp && rabbitmq-server"
  healthcheck:
    test: ["CMD", "rabbitmq-diagnostics", "-q", "ping"]
    interval: 10s
    timeout: 5s
    retries: 5
```

**b) Сервіс `app`** — додати залежність від RabbitMQ та змінні:

- У `depends_on` додати: `rabbitmq` з `condition: service_healthy`.
- У `environment` додати:
  - `RABBITMQ_HOST=rabbitmq`
  - `RABBITMQ_STOMP_PORT=61613`
  - `RABBITMQ_USER=${RABBITMQ_USER:-guest}`
  - `RABBITMQ_PASSWORD=${RABBITMQ_PASSWORD:-guest}`

**c) Сервіс `app2`** (новий) — копія `app` з іншими ім’ям контейнера та портом:

- `container_name: medfast_api2`
- Порти: `"8081:8080"` (доступ з хоста на порт 8081).
- Ті самі `environment`, що й у `app` (включно з `RABBITMQ_HOST=rabbitmq`).
- `depends_on`: postgres, rabbitmq (healthy), loki.

Приклад фрагмента для `app2`:

```yaml
app2:
  container_name: medfast_api2
  build: .
  environment:
    - SPRING_PROFILES_ACTIVE=dev
    - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/${POSTGRES_DB}
    - SPRING_DATASOURCE_USERNAME=${POSTGRES_USER}
    - SPRING_DATASOURCE_PASSWORD=${POSTGRES_PASSWORD}
    - RABBITMQ_HOST=rabbitmq
    - RABBITMQ_STOMP_PORT=61613
    - RABBITMQ_USER=${RABBITMQ_USER:-guest}
    - RABBITMQ_PASSWORD=${RABBITMQ_PASSWORD:-guest}
    # … решта змінних як у app (MAIL_*, JWT_SECRET, STRIPE_*, тощо)
  ports:
    - "8081:8080"
  depends_on:
    postgres:
      condition: service_healthy
    rabbitmq:
      condition: service_healthy
    loki:
      condition: service_started
```

---

### 3.7. `pom.xml`

**Нічого додавати не потрібно.** Spring Boot WebSocket уже включає клієнт для STOMP broker relay; окремий AMQP/RabbitMQ клієнт для чату не використовується.

---

## 4. Як перевірити

### 4.1. RabbitMQ та два інстанси (app + app2)

1. У `docker-compose.yml` мають бути сервіси `rabbitmq`, `app`, `app2` та змінні `RABBITMQ_HOST=rabbitmq` для обох додатків.
2. Запустити: `docker compose up -d` (або `docker-compose up -d`).
3. Дочекатися healthy для postgres та rabbitmq, потім старту app та app2.
4. **Перевірка повідомлень між інстансами:**
   - Клієнт A підключається до WebSocket на **app** (порт 8080): `http://localhost:8080/api/ws`.
   - Клієнт B підключається до **app2** (порт 8081): `http://localhost:8081/api/ws`.
   - Обидва підписуються на один і той самий чат, наприклад `/topic/chats/1`.
   - Повідомлення, надіслане через один інстанс (наприклад app), має з’явитися в підписці на другому (app2), оскільки брокер — спільний RabbitMQ.

### 4.2. SockJS fallback

- Endpoint один: `/api/ws` (з контекстом додатку).
- Якщо клієнт підтримує WebSocket — браузер підключається по WebSocket до того ж URL.
- Якщо ні (або політика/проксі блокують WS) — клієнт може використовувати SockJS: той самий URL, але з префіксом SockJS (наприклад, бібліотека `sockjs-client` + `@stomp/stompjs` сама обере транспорт).
- На сервері достатньо мати `.withSockJS()` на endpoint — додатковий код для SockJS окремо писати не потрібно.

Приклад URL для SockJS-клієнта: `http://localhost:8080/api/ws` (з `withSockJS()` сервер віддає потрібні SockJS-ендпоінти автоматично).

---

## 5. Підсумок

- **SockJS:** у `WebSocketConfig` на endpoint `/ws` викликано `.withSockJS()` — клієнти з браузерів без WebSocket використовують SockJS.
- **RabbitMQ:** у `WebSocketConfig` при заданому `app.websocket.relay-host` (через `RABBITMQ_HOST`) використовується `enableStompBrokerRelay` замість `enableSimpleBroker`; у Docker додано сервіс RabbitMQ з STOMP і другий інстанс `app2` для тестування доставки повідомлень між двома інстансами.
- Конфіг relay виноситься в `application.yml` / `application-dev.yml` та `.env`; у `pom.xml` нових залежностей не потрібно.
