# Microservice Project Development Using Spring Boot

- [Microservice Project Development Using Spring Boot](#microservice-project-development-using-spring-boot)
  - [Java Project Development Concepts](#java-project-development-concepts)
      - [Microservices](#microservices)
      - [Four Layers](#four-layers)
      - [9 + 1 Classes and Interfaces](#9--1-classes-and-interfaces)
  - [Creating Data Access Layer](#creating-data-access-layer)
      - [Creating the Model Class](#creating-the-model-class)
      - [Creating the Repository](#creating-the-repository)
      - [Configure the Data Source](#configure-the-data-source)
      - [Testing](#testing)

## Java Project Development Concepts

#### Microservices

Microservices are **small and focused services**. Microservices are also **autonomous**, which means that we can deploy each microservice into its own machine without impacting other microservices. Communication between services happens through network calls on exposed APIs. Microservices are also **heterogenous**, meaning that each service can be written in a different language.

#### Four Layers

When we develop applications, we divide them into modules which are implemented across four layers: Data Access, Service Layer, Integration, Presentation Layer. The **Data Access Layer** is responsible for performing the CRUD operations against the data store, which uses **ORM** (Hibernate/Spring Data). The services provided by the Data Access Layer is used by the **Service Layer** which contains the business logic. These services are used by the Presentation Layer and Integration Layer. The **Presentation Layer** is responsible for generating the user interface. The **Integration Layer** allows our application to access other applications and is typically made of web services.

#### 9 + 1 Classes and Interfaces

1. Model Class
2. Data Access Layer Interface (IDAO)
3. Data Access Layer Interface Implementation (DAOImpl)
4. Service (IService)
5. Service Implementation (ServiceImpl)
6. Controller
7. Utility Class
8. Validator Class
9. Service Provider / Consumer
10. View

## Creating Data Access Layer

1. Create a Model Class
2. Create a Spring Data JPA Repository Interface

We first create a database table for the Student. We then map the Student model class to the student table in the database. The fields in the Student class will then be mapped to the database columns using JPA annotations. The Student Repository Interface extends CRUDRepository from Spring Data, which will allow us to perform CRUD operations against the student table.

Database Schema:

```sql
create table studenttab(
id int PRIMARY KEY AUTO_INCREMENT,
sname varchar(20),
scourse varchar(30),
sfee int
);
```

The Spring Boot project will be using Spring Data JPA, MySQL Driver as the dependencies.

#### Creating the Model Class

To convert the model class into a JPA entity, we need to annotate it with **@Entity** from JPA. **@Id** marks the field as the primary key. We mark the id field with **@GeneratedValue** to tell the underlying JPA provider that the id field will be automatically incremented.

```java
@Entity
@Table(name = "studenttab")
public class Student {
	@Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "sname")
	private String name;
	@Column(name = "scourse")
	private String course;
	@Column(name = "sfee")
	private Double fee;

}
```

#### Creating the Repository

The StudentRepository is our Data Access Layer. At runtime, Spring will create implementations for our interfaces on the fly to allow us to perform CRUD operations. The CRUDRepository requires two types: the first is the type of the class for which the repository is being created. The second is the type of the primary key, in this case, long.

```java
public interface StudentRepository extends CrudRepository<Student, Long> {
}
```

#### Configure the Data Source

Under application.properties, we can configure the database connection information

```
spring.datasource.url=jdbc:mysql://localhost:3306/projectdb
spring.datasource.username=root
spring.datasource.password=1234

spring.jpa.show-sql=true
```

#### Testing

To perform the CRUD operation, we need to do dependency injection of the StudentRepository we created. We can easily do dependency injection using **@Autowired**

```java
@SpringBootTest
class StudentdalApplicationTests {
	@Autowired
	private StudentRepository repo;

	@Test
	void testCreateStudent() {
		Student student = new Student();
		student.setName("doge");
		student.setCourse("Java");
		student.setFee(30d);
		repo.save(student);
	}

	@Test
	void testFindStudentById() {
		Student student = repo.findById(1l).get();
		System.out.println(student);
	}

	@Test
	void testUpdateStudent() {
		Student student = repo.findById(1l).get();
		student.setFee(100d);
		repo.save(student);
	}

	@Test
	void testDeleteStudent() {
		Student student = repo.findById(1l).get();
		repo.delete(student);
	}
}
```
