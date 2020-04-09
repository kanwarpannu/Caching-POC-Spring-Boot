# Caching-POC (Using Redis in Docker)

## Introduction 

## Requirements
  * Uses **Lombok** - Be sure to enable annotation processing  
  * Uses **Redis** on Docker - Download redis:alpine image from dockerhub  
  * Uses **Spring Cache 'in-memory'** - For running testing only  
  * Uses **Spring Boot** version - v2.2.4.RELEASE  

## Start Redis
1. Start Redis:
`docker run -d -p 6379:6379 --name my-redis redis`

2. Access redis-cli inside Redis container:
`docker exec -it my-redis redis-cli`  

## Basic Redis commands:  
1. `ping` - To check Redis is up  
Output - "PONG"  
2. `set name mark` - Creates key "name" with value "mark"  
Output - OK  
3. `get name` - Gets value for key "name"  
Output - "mark"  
4. `keys *` - Gets all keys  
5. `del name` - Deletes key "name"  

Use this link to access [Redis-commands-cheatsheet](https://gist.github.com/LeCoupa/1596b8f359ad8812c7271b5322c30946)  
