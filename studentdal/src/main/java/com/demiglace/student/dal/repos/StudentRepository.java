package com.demiglace.student.dal.repos;

import org.springframework.data.repository.CrudRepository;

import com.demiglace.student.dal.entities.Student;

public interface StudentRepository extends CrudRepository<Student, Long> {

}
