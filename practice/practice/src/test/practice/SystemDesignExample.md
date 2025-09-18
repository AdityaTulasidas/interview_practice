# System Design Example

## Design a URL Shortener Service
- Use a hash or base62 encoding for short URLs
- Store mapping in a database (SQL/NoSQL)
- Use cache (Redis) for fast lookup
- Handle collisions and analytics
- Deploy with load balancer and auto-scaling

## Design a Scalable Notification System
- Use message queues (Kafka, RabbitMQ)
- Microservices for producer/consumer
- Store notifications in DB
- Push via WebSocket, email, SMS
- Monitor and retry failed notifications

