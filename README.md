# Fiducia.eco interview on java dev position

## Tech screening
General questions, like what is JIT, JCE. How JVM works, etc.

## Tech interview
### Spiral task
[Spiral.java](src/main/java/ru/fiducia/interview/Spiral.java) 

### Ping pong task
[PingPong.java](src/main/java/ru/fiducia/interview/PingPong.java)

### Large file sort question
How to sort large file with limited memory. It's just a question, but here is a simple implementation
[LargeFileRowsSort.java](src/main/java/ru/fiducia/interview/LargeFileRowsSort.java)

### JVM memory types
What kinds of memory in JVM do you know?

Good video that explains most things about JVM memory. 
![watch](https://www.youtube.com/watch?v=kKigibHrV5I)

### Sys design section
You have a system that collects data from many sources. Besides all information, you have 3 important columns: id, timestamp, cost.  
Data collected from 10 vendors, 2 millions events in hour from each. You need to build a system that will allow to query sum
of cost for some period. The query must run not longer than 30 secs, keep data for 2 years.