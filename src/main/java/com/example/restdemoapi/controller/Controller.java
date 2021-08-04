package com.example.restdemoapi.controller;

import com.example.restdemoapi.model.Student;
import com.example.restdemoapi.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class Controller {
    //autowired the StudentService class
    @Autowired
    StudentService studentService;

    //creating a get mapping that retrieves all the students detail from the database
    @GetMapping("/init")
    private ResponseEntity init() {
        return studentService.init();
    }

    //creating a get mapping that retrieves all the students detail from the database
    @GetMapping("/students")
    private ResponseEntity<List<Student>> getAllStudents(
            @RequestParam(required = false, name = "name") String nameValue,
            @RequestParam(required = false, name = "age") String ageExpression,
            @RequestParam(required = false, name = "sort", defaultValue = "id:asc") String sortExpression,
            @RequestParam(required = false, name = "limit", defaultValue = "100") Integer limit,
            @RequestParam(required = false, name = "offset", defaultValue = "0") Integer offset) {
        return studentService.getAllStudents(nameValue, ageExpression, sortExpression, limit, offset);
    }

    //creating a get mapping that retrieves the detail of a specific student
    @GetMapping("/students/{id}")
    private ResponseEntity<Student> getStudent(@PathVariable("id") int id) {
        return studentService.getStudentById(id);
    }

    //creating post mapping that post the student detail in the database
    @PostMapping("/students")
    private ResponseEntity<Student> createNewStudent(@RequestBody Student student) {
        return studentService.createNewStudent(student);
    }

    //creating post mapping that post the student detail in the database
    @PutMapping("/students/{id}")
    private ResponseEntity<Student> modifyStudent(@PathVariable("id") int id, @RequestBody Student student) {
        return studentService.modifyStudent(id, student);
    }

    //creating post mapping that post the student detail in the database
    @PatchMapping("/students/{id}")
    private ResponseEntity<Student> partiallyUpdateStudent(@PathVariable("id") int id, @RequestBody Map<String, String> updates) {
        return studentService.partiallyUpdateStudent(id, updates);
    }

    //creating a delete mapping that deletes a specific student
    @DeleteMapping("/students/{id}")
    private ResponseEntity deleteStudent(@PathVariable("id") int id) {
        return studentService.deleteStudent(id);
    }

    //creating a delete mapping that deletes a specific student
    @DeleteMapping("/students")
    private ResponseEntity deleteStudents(@RequestBody List<Integer> ids) {
        return studentService.deleteStudents(ids);
    }
}
