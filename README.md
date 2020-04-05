# Caching-POC (Using Redis in Docker)

Used spring cache in memory during running tests, And Redis dependencies during runtime.

Use following commands to run Redis in docker (Assuming we are using linux containers).

1. Start Redis:
`docker run -d -p 6379:6379 --name custom-redis redis`

2. Access shell inside Redis container:
`docker exec -it custom-redis sh`

3. Start redis-cli (when inside redis container shell):
`redis-cli`

Basic Redis commands:  
1. `ping` - To check Redis is up  
Output - "PONG"  
2. `set name mark` - Creates key "name" with value "mark"  
Output - OK  
3. `get name` - Gets value for key "name"  
Output - "mark"  
4. `Keys *` - Gets all keys

Use this link to access [Redis-commands-cheatsheet](https://gist.github.com/LeCoupa/1596b8f359ad8812c7271b5322c30946)  
