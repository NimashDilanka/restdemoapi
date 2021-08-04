package com.example.restdemoapi.service;

import com.example.restdemoapi.model.Student;
import com.example.restdemoapi.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentService {
    final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    //getting all students
    public ResponseEntity<List<Student>> getAllStudents(String nameValue, String ageExpression, String sortExpression, Integer limit, Integer offset) {
        //Get all students. If there is no students, return an empty json array.
        //return with 200 HTTP code
        List<Student> allStudents = studentRepository.findAll();
        //filter by name
        allStudents = filterListByName(nameValue, allStudents);
        //filter by age
        //ex: if ageExpression is $ne:208 then keyOperator is $ne and value is 208
        allStudents = filterListByAge(ageExpression, allStudents);
        //sort by given parameter
        allStudents = sortByParameter(sortExpression, allStudents);
        //apply limit and offset
        allStudents = allStudents.stream().skip(offset).limit(limit).collect(Collectors.toList());
        return ResponseEntity.ok(allStudents);
    }

    private List<Student> sortByParameter(String sortExpression, List<Student> allStudents) {
        if (sortExpression != null && !sortExpression.isEmpty()) {
            String keyField = sortExpression.split(":")[0];
            String category = sortExpression.split(":")[1];
            switch (keyField) {
                case "id":
                    if ("asc".equals(category)) {
                        allStudents = allStudents.stream().sorted((s1, s2) -> Integer.compare(s1.getId(), s2.getId())).collect(Collectors.toList());
                    } else if ("desc".equals(category)) {
                        allStudents = allStudents.stream().sorted((s1, s2) -> Integer.compare(s2.getId(), s1.getId())).collect(Collectors.toList());
                    }
                    break;
                case "name":
                    if ("asc".equals(category)) {
                        allStudents = allStudents.stream().sorted((s1, s2) -> s1.getName().compareTo(s2.getName())).collect(Collectors.toList());
                    } else if ("desc".equals(category)) {
                        allStudents = allStudents.stream().sorted((s1, s2) -> s2.getName().compareTo(s1.getName())).collect(Collectors.toList());
                    }
                    break;
                case "age":
                    if ("asc".equals(category)) {
                        allStudents = allStudents.stream().sorted((s1, s2) -> Integer.compare(s1.getAge(), s2.getAge())).collect(Collectors.toList());
                    } else if ("desc".equals(category)) {
                        allStudents = allStudents.stream().sorted((s1, s2) -> Integer.compare(s2.getAge(), s1.getAge())).collect(Collectors.toList());
                    }
                    break;
                case "email":
                    if ("asc".equals(category)) {
                        allStudents = allStudents.stream().sorted((s1, s2) -> s1.getEmail().compareTo(s2.getEmail())).collect(Collectors.toList());
                    } else if ("desc".equals(category)) {
                        allStudents = allStudents.stream().sorted((s1, s2) -> s2.getEmail().compareTo(s1.getEmail())).collect(Collectors.toList());
                    }
                    break;
            }
        }
        return allStudents;
    }

    private List<Student> filterListByName(String nameValue, List<Student> allStudents) {
        if (nameValue != null && !nameValue.isEmpty()) {
            allStudents = allStudents.stream()
                    .filter(s -> s.getName().equals(nameValue))
                    .collect(Collectors.toList());
        }
        return allStudents;
    }

    private List<Student> filterListByAge(String ageExpression, List<Student> allStudents) {
        if (ageExpression != null && !ageExpression.isEmpty()) {

            String keyOperator = ageExpression.split(":")[0];
            Integer value = Integer.parseInt(ageExpression.split(":")[1]);
            switch (keyOperator) {
                case "$eq": //equals operator
                    allStudents = allStudents.stream().filter(s -> s.getAge() == value).collect(Collectors.toList());
                    break;
                case "$ne": //not equals operator
                    allStudents = allStudents.stream().filter(s -> s.getAge() != value).collect(Collectors.toList());
                    break;
                case "$lte": //less than equals
                    allStudents = allStudents.stream().filter(s -> s.getAge() <= value).collect(Collectors.toList());
                    break;
                case "$gte": //greater than equals
                    allStudents = allStudents.stream().filter(s -> s.getAge() >= value).collect(Collectors.toList());
                    break;
            }

        }
        return allStudents;
    }

    //getting a specific student
    public ResponseEntity<Student> getStudentById(int id) {
        //get student if present and return with 200 HTTP code
        //otherwise send 404 HTTP for not found
        Optional<Student> studentById = studentRepository.findById(id);
        if (studentById.isPresent()) {
            return ResponseEntity.ok(studentById.get());
        }
        return ResponseEntity.notFound().build();
    }

    //create a student if doesn't exist already. return 201 HTTP code
    //or else return 400 HTTP code to indicate student already exists
    public ResponseEntity<Student> createNewStudent(Student student) {
        if (studentRepository.existsById(student.getId())) {
            return ResponseEntity.badRequest().build();
        }
        Student savedStudentEntity = studentRepository.save(student);
        return new ResponseEntity<>(savedStudentEntity, HttpStatus.CREATED);
    }

    //update the student if exist. return 200 HTTP code
    //or else return 404 HTTP code to indicate student doesn't exist
    public ResponseEntity<Student> modifyStudent(int id, Student student) {
        if (!studentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        student.setId(id);
        Student updateStudentEntity = studentRepository.save(student);
        return ResponseEntity.ok(updateStudentEntity);
    }

    //update only specific attributes of the student. return 200 HTTP code
    //or else return 404 HTTP code to indicate student doesn't exist
    public ResponseEntity<Student> partiallyUpdateStudent(int id, Map<String, String> updates) {
        if (!studentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        Student student = studentRepository.getById(id);
        for (Map.Entry<String, String> entry : updates.entrySet()) {
            String code = entry.getKey();
            String value = entry.getValue();
            switch (code) {
                case "name":
                    student.setName(value);
                    break;
                case "age":
                    student.setAge(Integer.parseInt(value));
                    break;
                case "email":
                    student.setEmail(value);
                    break;
            }
        }
        Student savedStudent = studentRepository.save(student);
        return ResponseEntity.ok(savedStudent);
    }


    public ResponseEntity deleteStudent(int id) {
        if (!studentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        studentRepository.deleteById(id);
        if (studentRepository.existsById(id)) {
            //student was not deleted. something went wrong in server
            //raise internal server error
            return ResponseEntity.internalServerError().build();
        }
        //successfully deleted student
        return ResponseEntity.noContent().build();
    }

    //deleting a specific records if exist. return 204 HTTP code
    //if objects doesn't exist for deletion return 404 HTTP code
    public ResponseEntity deleteStudents(List<Integer> ids) {
        for (Integer studentId : ids) {
            ResponseEntity res = deleteStudent(studentId);
            if (!res.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
                //not deleted successfully. raise the error
                return res;
            }
        }
        //deleted everythign successfully
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity init() {
        for (int i = 1; i < 1000; i++) {
            Student s = new Student();
            s.setId(i);
            s.setName("Kamal_" + i);
            s.setEmail("kamal" + i + "@gmail.com");
            s.setAge(1 + 56);
            studentRepository.save(s);
        }
        return ResponseEntity.ok().build();
    }
}
