# Spring Microservices in Action

Este repositório contém o código fonte desenvolvido do
livro "[Spring Microservices in Action (John Carnell)](https://www.manning.com/books/spring-microservices-in-action)".

![](https://images-na.ssl-images-amazon.com/images/I/616ESSUzesS.jpg)

## Overview

Este código-fonte foi feito do início utilizando a nova versão do Spring e de seus projetos. O repositório original está
disponível em [código fonte original](https://github.com/wuyichen24/spring-microservices-in-action).

## Estrutura

- **Servers**
    - Config: Fornece os parâmetros de configuração para os outros serviços.
    - Eureka: Service discovery.
    - Spring Cloud Gateway: API gateway.
- **Services**
    - Licensing: Gerencia (CRUD) licenças.
    - Organization: Gerencia (CRUD) organizações.
- **Database**: Armazena os registros de licença e organização.
- **Message Queue (Kafka)**: Notifica o serviço de licença sobre qualquer alteração nos registros de organização.
- **Cache (Redis)**: Cache dos registros de organização que foram buscados anteriormente.
- **Security (Keycloak)**: Gerenciamento de identidade e acesso
- **Log Server**
    - ELK Stack: (Elasticsearch, Logstash e Kibana).
    - Zipkin: Trace distribuído.

## Tecnologias

- **Core**
    - Spring
        - Spring Boot
        - Spring Data
            - [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
            - [Spring Data Redis](https://spring.io/projects/spring-data-redis)
        - Spring Cloud
            - [Spring Cloud Circuit Breaker](https://spring.io/projects/spring-cloud-circuitbreaker)
            - [Spring Cloud Config](https://spring.io/projects/spring-cloud-config)
            - [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)
            - [Spring Cloud Open feign](https://spring.io/projects/spring-cloud-openfeign)
            - [Spring Cloud Sleuth](https://spring.io/projects/spring-cloud-sleuth)
            - [Spring Cloud Stream](https://spring.io/projects/spring-cloud-stream)
        - Spring Security
            - [Spring Security](https://spring.io/projects/spring-security)
        - [Spring Hateoas](https://spring.io/projects/spring-hateoas)
    - [Netflix OSS](https://netflix.github.io/)
        - [Netflix Eureka (service discovery)](https://github.com/Netflix/eureka)
- **Message Queue**
    - [Apache Kafka](https://kafka.apache.org/) with [Apache ZooKeeper](https://zookeeper.apache.org/)
- **Cache**
    - [Redis](https://redis.io/)
- **Database**
    - [PostgreSQL](https://www.postgresql.org/)
- **Security**
    - [OAuth2](https://oauth.net/2/)
    - [Keycloak](https://www.keycloak.org/)
- **Log**
    - [Elasticsearch](https://www.elastic.co/pt/elasticsearch/)
    - [Logstash](https://www.elastic.co/pt/logstash/)
    - [Kibana](https://www.elastic.co/pt/kibana/)
- **Trace**
    - [Apache Zipkin](https://zipkin.apache.org/)
- **Cluster Coordination**
    - [Apache ZooKeeper](https://zookeeper.apache.org/)

## Diferenças do código-fonte original

- Utilizada uma versão mais nova do Spring Boot
- Utilizado somente a última versão do Spring Cloud Open Feign ao invés de RestTemplate e Discovery Client
- Utilizada uma versão mais nova do Spring Cloud Stream, mudando radicalmente o código-fonte relativo a mensageria
- Mantido o uso do Keycloak ao invés da implementação de um Authorization Server
- Uso de DTOs
- Uso de HATEOAS em todos os endpoints de ambos os projetos
- Removido código desnecessário referente a traceID após a adoção do Sleuth

## Pré-requisitos

1. Apache Maven (http://maven.apache.org)
2. Git Client (http://git-scm.com)
3. Docker (https://www.docker.com/products/docker-desktop)

## Rodando o projeto

```bash
# Clonar o projeto
$ git clone https://github.com/felipecamatta/spring-microservices-in-action.git

# Entrar no projeto
$ cd spring-microservices-in-action

# Buildar o código e criar o docker image
$ mvn clean package dockerfile:build

# Rodar o docker-compose
$ docker-compose -f docker/docker-compose.yml up -d
```
