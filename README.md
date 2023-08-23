##  Run 

1. **Run Kafka**

```
Go to `docker` folder then run `docker compose up -d`
```

2. **Run app(web)**

```
Go to  `simple-cap-kafka-web` folder and run `./mvnw spring-boot:run`
```

3. **Test with curl**

```
curl -s -X POST -H 'Content-Type: application/json' -d 'test' -w "\n"  http://localhost:9080/api/cap
```

OR

```
curl --header "Content-Type: application/json" \
           --request POST \
           --data 'test' \
           -w "\n" \
            http://localhost:9080/api/cap
```

## Sample Problem (Capitalize a given string.)

> Given the input:

>> "farhad"

> The output will be:

>> "Farhad"

## Architecture

       Since scalability is a core goal of any System, it's usually better to design small stages focused 
       on specific operations, especially if we have I/O─intensive tasks. 

       Moreover, having small stages helps us better tune the scale of each stage.

       To solve our Capitalize word problem, we can implement a solution with the following stages:


``` 
         ┌─────────────┐ 
         │  Requestor  │
         │  (Client)   │
         └─────────────┘
            |      ^ 
            |      |
   ╔════════|══════|══════════════════════════════════════════════════════════════════════════════════════════════════════╗ 
   ║        |      |                                                                                                      ║
   ║        |      |                                                                                     Processor        ║
   ║        V      |               ┌───────────────────────┐       ┌───────────────────────┐              Module          ║
   ║ ┌────────────────────┐        │                       │       │                       │        ┌─────────────────┐   ║ 
   ║ │ Capitalize Service │ -----> │ Outbout Kafka Gateway │       │ Inbout Kafka Gateway  │ -----> │ Capitalize word │   ║
   ║ │    ( Gateway )     │ <----- │                       │       │                       │ <----- │   ( Service )   │   ║
   ║ └────────────────────┘        └───────────────────────┘       └───────────────────────┘        └─────────────────┘   ║
   ║     Edge Module                     |          ^                   ^             |                                   ║
   ╚═════════════════════════════════════|══════════|═══════════════════|═════════════|═══════════════════════════════════╝
                                 Request |          |            Request|             |
                                         |          |                   |             |
                                         |          |Reply              |             | Reply
                                       __V__________|___________________|_____________V___                                   
                                      ()_________________________________________________()    
                                                            KAFKA
```
