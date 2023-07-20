package com.todo.springboot.webapp.todo;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Controller
public class TodoControllerJpa {

    private TodoRepository todoRepository;

    public TodoControllerJpa(TodoRepository todoRepository) {
        super();
        this.todoRepository = todoRepository;
    }

    @RequestMapping("list-todos")
    public String listAllTodos(ModelMap model) {
        String username = getLoggedinUsername();
        List<Todo> todos = todoRepository.findByUsername(username);
        model.addAttribute("todos", todos);
        return "listTodos";
    }

    public String getLoggedinUsername(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    // Two-way binding between controller and form
    @RequestMapping(value="add-todo",method = RequestMethod.GET)
    public String showNewTodoPage(ModelMap model) {
        String username = getLoggedinUsername();
        Todo todo = new Todo(0, username, "", LocalDate.now().plusYears(1), false);
        model.put("todo", todo);
        return "todo";
    }

    @RequestMapping(value="add-todo",method = RequestMethod.POST)
    public String addNewTodoPage(ModelMap model,
                                 @Valid Todo todo, BindingResult result) {
        if(result.hasErrors()) {
            return "todo";
        }
        String username = getLoggedinUsername();
        todo.setUsername(username);
        todoRepository.save(todo);
//        todoService.addTodo(username, todo.getDescription(),
//                todo.getTargetDate(), todo.isDone());
        return "redirect:list-todos";
    }

    @RequestMapping("delete-todo")
    public String deleteTodo(@RequestParam int id) {
        //Delete todo
        todoRepository.deleteById(id);
//        todoService.deleteById(id);
        return "redirect:list-todos";
    }

    @RequestMapping(value="update-todo", method = RequestMethod.GET)
    public String showUpdateTodoPage(@RequestParam int id, ModelMap model) {
        Todo todo = todoRepository.findById(id).get();
        model.addAttribute("todo", todo);
        return "todo";
    }

    @RequestMapping(value="update-todo", method = RequestMethod.POST)
    public String addUpdateTodoPage(ModelMap model,
                                    @Valid Todo todo) {
        String username = getLoggedinUsername();
        todo.setUsername(username);
        todoRepository.save(todo);
        return "redirect:list-todos";
    }

}
