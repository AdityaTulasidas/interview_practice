# Java Stream API Examples

Comprehensive examples of Java Stream API operations in a Spring Boot application.

## Stream Operations

### Intermediate Operations (Middle Methods)
These return a new stream and are **lazy** - they don't execute until a terminal operation is called.

**Common intermediate operations:**
- `filter()` - filters elements
- `map()` - transforms elements
- `flatMap()` - flattens nested structures
- `distinct()` - removes duplicates
- `sorted()` - sorts elements
- `peek()` - performs action without consuming stream
- `limit()` - limits number of elements
- `skip()` - skips elements
- `takeWhile()` - takes while condition is true
- `dropWhile()` - drops while condition is true

### Terminal Operations (Final Methods)
These consume the stream and produce a final result. They trigger the execution of the entire pipeline.

**Common terminal operations:**
- `forEach()` - performs action on each element
- `collect()` - collects to collection/map
- `reduce()` - reduces to single value
- `count()` - counts elements
- `findFirst()` - finds first element
- `findAny()` - finds any element
- `anyMatch()` - checks if any match
- `allMatch()` - checks if all match
- `noneMatch()` - checks if none match
- `min()` - finds minimum
- `max()` - finds maximum
- `toArray()` - converts to array

## Running the Application

```bash
mvn spring-boot:run
```

## Practice Questions

### Basic Stream Operations
1. Create a list of integers 1-10 and filter even numbers
2. Convert a list of strings to uppercase using map()
3. Find the sum of all numbers in a list using reduce()
4. Count how many strings start with "A" in a list
5. Get the first 5 elements from a stream of 1-100

### Intermediate Level
6. Remove duplicates from a list of integers
7. Sort a list of strings by length (shortest first)
8. Skip the first 3 elements and take the next 5
9. Find the maximum value in a list of integers
10. Check if all numbers in a list are positive

### Advanced Operations
11. Flatten a list of lists into a single list using flatMap()
12. Group employees by department using collect(groupingBy())
13. Find the average salary of employees
14. Get the top 3 highest paid employees
15. Convert a list to a Map where key=name, value=age

### Complex Scenarios
16. Find employees who earn more than average salary
17. Get distinct cities from a list of employees, sorted alphabetically
18. Calculate total sales by product category
19. Find the second highest salary
20. Get names of employees in "IT" department with salary > 50000

### String Processing
21. Count words in a sentence using streams
22. Find the longest word in a list
23. Remove empty strings and convert to lowercase
24. Check if any string contains a specific substring
25. Join all strings with a comma separator

### Date/Time Streams
26. Filter dates from the last 30 days
27. Group transactions by month
28. Find the earliest and latest dates
29. Count working days between two dates
30. Get all Mondays in a given year