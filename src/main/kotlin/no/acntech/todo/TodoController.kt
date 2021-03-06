package no.acntech.todo

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping("todo")
class TodoController(val todoRepository: TodoRepository) {

    @GetMapping
    fun findAll(): List<Todo> {
        return todoRepository.findAll().toList()
    }

    @GetMapping("{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<Todo> {
        val todo = todoRepository.findById(id)
        if (todo.isPresent) {
            return ResponseEntity.ok(todo.get())
        } else {
            return ResponseEntity.notFound().build()
        }
    }

    @GetMapping("search")
    fun findByQuery(@RequestParam(required = false) done: Boolean?): List<Todo> {
        if (done != null) {
            return todoRepository.findByDone(done).toList()
        } else {
            return todoRepository.findAll().toList()
        }
    }

    @PostMapping
    fun create(@RequestBody description: String, uri: UriComponentsBuilder): ResponseEntity<String> {
        val todo = todoRepository.save(Todo(description = description))
        val path = uri.path("todo/${todo.id}").build().toUri()
        return ResponseEntity.created(path).build()
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun delete(@PathVariable id: Long) {
        val todo = todoRepository.findById(id)
        if (todo.isPresent) {
            todoRepository.delete(todo.get())
        }
    }

    @PutMapping("{id}")
    fun update(@PathVariable id: Long, @RequestBody todoRequest: Todo): ResponseEntity<Todo> {
        val existingTodoOptional = todoRepository.findById(id)
        if (existingTodoOptional.isPresent) {
            val existingTodo = existingTodoOptional.get()
            existingTodo.description = todoRequest.description
            existingTodo.done = todoRequest.done
            val updatedTodo = todoRepository.save(existingTodo)
            return ResponseEntity.ok(updatedTodo)
        }
        return ResponseEntity.notFound().build()
    }
}