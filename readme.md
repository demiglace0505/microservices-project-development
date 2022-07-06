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
  - [Creating Presentation Layer](#creating-presentation-layer)
      - [Creating the Model Class](#creating-the-model-class-1)
      - [Creating the Repository](#creating-the-repository-1)
      - [Configure the Data Source](#configure-the-data-source-1)
      - [Create the Services Layer](#create-the-services-layer)
      - [Create the Controller](#create-the-controller)
      - [Flow](#flow)
  - [Utility Layer](#utility-layer)
      - [Email Use Case](#email-use-case)

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

## Creating Presentation Layer

In this section, we created a frontend using Thymeleaf that allows the user to save locations to a database. The createLocation page will allow the end user to enter a unique id for the location, a location code, name of the location, and the city type into a form. This form will be converted into a Java object and persisted into the database using Hibernate. The response will then be returned to the new page displayLocations.

The schema of the locations table is:

```sql
use projectdb;
create table location (
  id int PRIMARY KEY,
  code varchar(20),
  name varchar(20),
  type varchar(10));
```

The Spring Boot project will be using Spring Data JPA, MySQL Driver, Spring Web, and Thymeleaf as the dependencies.

#### Creating the Model Class

We create the fields for the model class according to the database table columns. Since the field names are the same as the column names, we don't need to use the @Column annotation anymore

```java
@Entity
public class Location {
	@Id
	private int id;
	private String code;
	private String name;
	private String type;
}
```

#### Creating the Repository

We then proceed with the data access layer of our module. With Spring Data, we don't need to create a DAO interface or its implementation anymore. We just need to create an interface that extends JpaRepository. We use JpaRepository instead of CRUDRepository since CRUDRepository returns an Iterable with its **findAll()** method. With JpaRepository, it returns a list instead.

```java
public interface LocationRepository extends JpaRepository<Location, Integer> {

}
```

#### Configure the Data Source

Using the application.properties, we can configure the data source.

```
spring.datasource.url=jdbc:mysql://localhost:3306/projectdb
spring.datasource.username=root
spring.datasource.password=1234

spring.jpa.show-sql=true
spring.servlet.context-path=/locationweb
```

#### Create the Services Layer

In the Service Layer, we created a Location Service interface and its implementation. This will use the services provided by the data access Layer.

```java
public interface LocationService {
	Location saveLocation(Location location);
	Location updateLocation(Location location);
	void deleteLocation(Location location);
	Location getLocationById(int id);
	List<Location> getAllLocations();
}
```

The implementation class is marked with the annotation **@Service** which allows Spring to create an instance of this service at runtime and do the dependency injection in other classes as required. To access the location repository, we need to autowire it using **@Autowired**.

```java
@Service
public class LocationServiceImpl implements LocationService {

	@Autowired
	private LocationRepository repository;

	@Override
	public Location saveLocation(Location location) {
		return repository.save(location);
	}

	@Override
	public Location updateLocation(Location location) {
		return repository.save(location);
	}

	@Override
	public void deleteLocation(Location location) {
		repository.delete(location);
	}

	@Override
	public Location getLocationById(int id) {
		return repository.findById(id).get();
	}

	@Override
	public List<Location> getAllLocations() {
		return repository.findAll();
	}
}
```

#### Create the Controller

In a Spring MVC application, the users cannot access the views directly. Everything has to go through a controller first. We create the controller class that we annotate with **@Controller**, and create methods that returns strings which is a page to which a user should be directed to. These methods are annotated with **@RequestMapping**.

To make the Spring Container convert the form fields into an object and retrieve it to our controller, we use **@ModelAttribute**. We tell Spring to create a model object, set the fields from the request and expose it out as a bean with the class Location (location). To send back the response message, we use **ModelMap** which we can access in the template using _msg_

```java
@Controller
public class LocationController {

	@Autowired
	public LocationService service;

	@RequestMapping("/showCreate")
	public String showCreate() {
		return "createLocation";
	}

	@RequestMapping("/saveLoc")
	public String saveLocation(@ModelAttribute("location") Location location, ModelMap modelMap) {
		Location locationSaved = service.saveLocation(location);
		String msg = "Location saved with id: " + locationSaved.getId();
		modelMap.addAttribute("msg", msg);
		return "createLocation";
	}
}
```

#### Flow

The flow of creating a location is as follows:

1. Form is submitted
2. Spring Container converts form parameters into a model object and hands it to the controller method (saveLocation)
3. The controller method invokes the service
4. The service implementation uses the LocationRepository
5. Spring uses Hibernate internally to convert the model object into a database record
6. We get the location object back as a response through the controller

## Utility Layer

The Utility Layer performs a specialized operation, for example, an email use case or generating a report. The code in the Utility Layer is typically common across the application.

#### Email Use Case

For the email use case, we will follow three steps:

1. Add Spring Mail Dependency
2. Code the Utility Layer
3. Configure Spring Boot Properties

The EmailSender interface and its implementation from Spring Email will be used. We will be creating the EmailUtil interface and its implementation and will use the EmailSender interface.

```java
@Component
public class EmailUtilImpl implements EmailUtil {

	@Autowired
	private JavaMailSender sender;

	@Override
	public void sendEmail(String toAddress, String subject, String body) {
		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		try {
			helper.setTo(toAddress);
			helper.setText(body);
			helper.setSubject(subject);
		} catch (MessagingException e) {
			e.printStackTrace();
		}

		sender.send(message);
	}
}
```

In the application.properties, we need to configure the email properties. Gmail allows us to use port 587.

```
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=
spring.mail.password=
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.auth=true
```

To test, we can add the following code to our LocationController to send an email after creating a location.

```java
@Controller
public class LocationController {

	@Autowired
	LocationService service;

	@Autowired
	EmailUtil emailUtil;

	@RequestMapping("/saveLoc")
	public String saveLocation(@ModelAttribute("location") Location location, ModelMap modelMap) {
		Location locationSaved = service.saveLocation(location);
		String msg = "Location saved with id: " + locationSaved.getId();
		modelMap.addAttribute("msg", msg);

		// send email
		emailUtil.sendEmail("springxyzabc@gmail.com", "Location Saved", "Location Saved successfully");
		return "createLocation";
	}
```
