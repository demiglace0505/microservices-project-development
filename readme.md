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
      - [Reports Use Case](#reports-use-case)
      - [Creating the Controller](#creating-the-controller)
  - [Integration Layer](#integration-layer)
      - [REST Principles](#rest-principles)
      - [Creating the REST Controller](#creating-the-rest-controller)
  - [Flight Reservation Use Case](#flight-reservation-use-case)
      - [Creating the Model Class](#creating-the-model-class-2)
      - [Creating the Data Access Layer](#creating-the-data-access-layer)
      - [Creating the User Registration Controller](#creating-the-user-registration-controller)
      - [Handling Login](#handling-login)
  - [Find Flights Use Case](#find-flights-use-case)
      - [Creating the Flight Controller](#creating-the-flight-controller)
      - [Creating the Flight Reservation Controller](#creating-the-flight-reservation-controller)
  - [Reservation Use Case](#reservation-use-case)
      - [Creating the Reservation Request Controller Method and Services Layer](#creating-the-reservation-request-controller-method-and-services-layer)
      - [Integration Layer](#integration-layer-1)
      - [CORS](#cors)
  - [Flight Checkin Application](#flight-checkin-application)
      - [Creating the Integration Layer](#creating-the-integration-layer)
      - [Creating the Controller](#creating-the-controller-1)
  - [Itenerary Function](#itenerary-function)
  - [Email Function](#email-function)
  - [Logging](#logging)
      - [Logback Configuration](#logback-configuration)
  - [Externalizing Properties](#externalizing-properties)
  - [Security](#security)
      - [Encoding Password](#encoding-password)
      - [Authorization Data Access Layer](#authorization-data-access-layer)
      - [UserDetails Service](#userdetails-service)
      - [Creating Security Configuration](#creating-security-configuration)
      - [Password Encoder Bean](#password-encoder-bean)
  - [Transaction Management](#transaction-management)
  - [Deployment](#deployment)
  - [Checkin Application with Angular](#checkin-application-with-angular)

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
server.servlet.context-path=/locationweb
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

#### Reports Use Case

For reporting, we can use the open source third party API [JFreeChart](https://www.jfree.org/jfreechart/). In our LocationRepository, we will add a new method for loading a dataset from which the report can be generated. This dataset will then be used by our ReportUtil into the JFree API. To convert this object into an image, we will use the ChartUtilities class from JFree. We need to add the following maven dependency.

```xml
<!-- https://mvnrepository.com/artifact/org.jfree/jfreechart -->
<dependency>
    <groupId>org.jfree</groupId>
    <artifactId>jfreechart</artifactId>
    <version>1.0.19</version>
</dependency>
```

We can now create a method in our LocationRepository for finding the count of location types (urban vs rural).

```java
public interface LocationRepository extends JpaRepository<Location, Integer> {
	@Query(value="SELECT type, COUNT(*) FROM vendor GROUP BY type", nativeQuery=true)
	public List<Object[]> findTypeAndTypeCount();
}
```

We then proceed on creating the ReportUtil interface and its implementation that will use this data and generate a report for us.

```java
@Component
public class ReportUtilImpl implements ReportUtil {

	@Override
	public void generatePieChart(String path, List<Object[]> data) {
		// copy the data into the pie data set
		DefaultPieDataset dataset = new DefaultPieDataset();
		for (Object[] objects: data) {
			// key, value
			dataset.setValue(objects[0].toString(), new Double(objects[1].toString()));
		}
		// create chart object
		JFreeChart chart = ChartFactory.createPieChart3D("Location Report", dataset);

		// convert to image
		try {
			ChartUtilities.saveChartAsJPEG(new File(path+"/pieChart.jpeg"), chart, 300, 300);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
```

#### Creating the Controller

In our LocationController, we add a view for the report endpoint.

```java
  @Autowired
	LocationRepository repository;

	@Autowired
	ReportUtil reportUtil;

	@Autowired
	ServletContext sc;

	@RequestMapping("/generateReport")
	public String generateReport() {
		String path = sc.getRealPath("/");
		List<Object[]> data = repository.findTypeAndTypeCount();
		reportUtil.generatePieChart(null, data);
		return "report";
	}
```

## Integration Layer

The Microservice applications communicate with each other through the Integration Layer. It also allows these applications to be loosely coupled and interoperable. The key technologies used in Integration Layer are REST and messaging.

#### REST Principles

1. Uniform Interface through POST, GET, PUT, DELETE methods
2. Easy Access using Nouns
3. Multiple Formats such as XML, JSON, plain text, etc.

#### Creating the REST Controller

To expose out RESTful web services, we need to create the REST Controller Integration Layer. To specify a RESTful controller, we use the annotation **@RestController** and map to a URI using **@RequestMapping**. Spring also automatically serializes the list into JSON using jackson. The **@GetMapping** annotation binds the GET method to the RESTful endpoint /locations. For the parameters, we need to use **@RequestBody** so that Spring will deserialize the request into a location object. For deleting and retrieving a specific location, we need the uri path variable and map it into the id parameter using **@PathVariable** annotation.

```java
@RestController
@RequestMapping("/locations")
public class LocationRESTController {
	@Autowired
	LocationRepository locationRepository;

	@GetMapping
	public List<Location> getLocations() {
		return locationRepository.findAll();
	}

	@PostMapping
	public Location createLocation(@RequestBody Location location) {
		return locationRepository.save(location);
	}

	@PutMapping
	public Location updateLocation(@RequestBody Location location) {
		return locationRepository.save(location);
	}

	@DeleteMapping("/${id}")
	public void deleteLocation(@PathVariable("id") int id) {
		locationRepository.deleteById(id);
	}

  @GetMapping("/${id}")
	public Location getLocation(@PathVariable("id") int id) {
		return locationRepository.findById(id).get();
	}
}
```

## Flight Reservation Use Case

For the flight reservation use case, we use the following db schema for the reservation database.

```sql
CREATE TABLE USER
(
ID INT NOT NULL AUTO_INCREMENT,
FIRST_NAME VARCHAR(20),
LAST_NAME VARCHAR(20),
EMAIL VARCHAR(20),
PASSWORD VARCHAR(256),
PRIMARY KEY (ID),
UNIQUE KEY (EMAIL)
)

CREATE TABLE FLIGHT
(
  ID INT  NOT NULL AUTO_INCREMENT,
  FLIGHT_NUMBER VARCHAR(20)  NOT NULL,
  OPERATING_AIRLINES VARCHAR(20)  NOT NULL,
  DEPARTURE_CITY VARCHAR(20)  NOT NULL,
  ARRIVAL_CITY VARCHAR(20)  NOT NULL,
  DATE_OF_DEPARTURE DATE  NOT NULL,
  ESTIMATED_DEPARTURE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (ID)
)

CREATE TABLE PASSENGER
(
  ID         INT NOT NULL AUTO_INCREMENT,
  FIRST_NAME       VARCHAR(256),
  LAST_NAME    VARCHAR(256),
  MIDDLE_NAME   VARCHAR(256),
  EMAIL VARCHAR(50),
  PHONE VARCHAR(10),
  PRIMARY KEY (ID)
)

CREATE TABLE RESERVATION
(
  ID INT NOT NULL AUTO_INCREMENT,
  CHECKED_IN TINYINT(1),
  NUMBER_OF_BAGS INT,
  PASSENGER_ID INT,
  FLIGHT_ID INT,
  CREATED TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (ID),
  FOREIGN KEY (PASSENGER_ID) REFERENCES PASSENGER(ID) ON DELETE CASCADE,
  FOREIGN KEY (FLIGHT_ID) REFERENCES FLIGHT(ID)
)
```

The necessary dependencies for this project are Spring Web, JPA and MySQL, Thymeleaf.

#### Creating the Model Class

The first step is to create the Model classes by mapping our database tables into our classes. The id fields are auto-increment in the database, hence we use **@GeneratedValue** annotation. Since the id field is common across all four entities, we can create a parent class AbstractEntity for it, which we mark with **@MappedSuperClass** to tell that this class is not mapped directly to a database table and acts as a parent class for other entities. The relationship between reservation, passenger and flight is one to one. We define the relations in the passenger and flight fields of the Reservation class using JPA annotations so that when we save a reservation, the foreign keys for the passenger and flight will be saved to the reservation table.

```java
@MappedSuperclass
public class AbstractEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
}

@Entity
public class User extends AbstractEntity {
	private String firstName;
	private String lastName;
	private String email;
	private String password;
}

@Entity
public class Passenger extends AbstractEntity {
	private String firstName;
	private String lastName;
	private String middleName;
	private String email;
	private String phone;
}

@Entity
public class Flight extends AbstractEntity{
	private String flightNumber;
	private String operatingAirlines;
	private String departureCity;
	private String arrivalCity;
	@DateTimeFormat(pattern = "MM-dd-yyyy")
	@Temporal(TemporalType.DATE)
	private Date dateOfDeparture;
	private Date dateOfDeparture;
	private Timestamp estimatedDepartureTime;
}

@Entity
public class Reservation extends AbstractEntity{
	private Boolean checkedIn;
	private int numberOfBags;
	@OneToOne
	private Passenger passenger;
	@OneToOne
	private Flight flight;
}
```

#### Creating the Data Access Layer

With Spring Data, we can create the interfaces that will be responsible for performing the CRUD operations on the entity. These interfaces will extend the JpaRepository from Spring Data.

```java
public interface UserRepository extends JpaRepository<User, Long> {

}

public interface FlightRepository extends JpaRepository<Flight, Long> {

}

public interface PassengerRepository extends JpaRepository<Passenger, Long> {

}

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

}
```

#### Creating the User Registration Controller

We create the controller for displaying the user registration page. The `/showReg` URL endpoint is mapped to the thymeleaf template `login/registerUser`. The form is mapped to _registerUser_ so we need to create the register method POST mapping which takes a user using **@ModelAttribute**, saves it to the database, and returns the login view.

```html
<!-- login/registerUser.html -->
<html xmlns:th="http://www.thymeleaf.org">
  <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
  <html>
    <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
      <title>Register User</title>
    </head>
    <body>
      <h2>User Registration:</h2>
      <form action="registerUser" method="post">
        <pre>
First Name: <input type="text" name="firstName"/>
Last Name:  <input type="text" name="lastName"/>
User Name: <input type="text" name="email"/>
Password: <input type="password" name="password"/>
Confirm Password: <input type="password" name="confirmPassword"/>
<input type="submit" value="register"/>
</pre>
      </form>
    </body>
  </html>
</html>
```

```java
@Controller
public class UserController {

	@Autowired
	UserRepository userRepository;

	@RequestMapping("/showReg")
	public String showRegistrationPage() {
		return "login/registerUser";
	}

	@RequestMapping(value="/registerUser", method=RequestMethod.POST)
	public String register(@ModelAttribute("user") User user) {
		userRepository.save(user);
		return "login/login";
	}

  @RequestMapping(value="/login", method=RequestMethod.POST)
	public String login(String email, String password) {
		return "login/login";
	}
}
```

#### Handling Login

For logging in, We need to use **@RequestParam** to map the email and password from the form into the request parameters. To send a message back, we need a **ModelMap** attribute which we send as _msg_ back to the login page.

We add a new method in our UserRepository **findByEmail**, but thanks to Spring Data, we just need to follow the findBy naming convention, and it will automatically use the email field of the User, without us needing to explicitly write the method. Spring Data automatically generates the query using the email from the request.

```java
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public String login(@RequestParam("email") String email, @RequestParam("password") String password, ModelMap modelMap) {
		User user = userRepository.findByEmail(email);
		if (user.getPassword().equals(password)) {
			return "findFlights";
		} else {
			modelMap.addAttribute("msg", "Invalid username or password");
		}
		return "login/login";
	}
```

```java
public interface UserRepository extends JpaRepository<User, Long> {
	User findByEmail(String email);
}
```

## Find Flights Use Case

The find flights module is where the user can enter where he wants to fly from and to ata specified date, which returns a list of flights. We create the template for finding flights.

```html
<html xmlns:th="http://www.thymeleaf.org">
  <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
  <html>
    <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
      <title>Find Flights</title>
    </head>
    <body>
      <h2>Find FLights:</h2>
      <form action="findFlights" method="post">
        From:<input type="text" name="from" /> To:<input
          type="text"
          name="to"
        />
        Departure Date:<input type="text" name="departureDate" />
        <input type="submit" value="search" />
      </form>
    </body>
  </html>
</html>
```

#### Creating the Flight Controller

The findFlights method's parameters needs to be marked with **@RequestParam** to map these form values into the request parameters. The departureDate also requires the **@DateTimeFormat** annotation from Spring to be able to format it appropriately. To return the results back to the view, we need to define a **ModelMap**

```java
@Controller
public class FlightController {
	@Autowired
	FlightRepository flightRepository;

	@RequestMapping("/findFlights")
	public String findFlights(@RequestParam("from") String from, @RequestParam("to") String to,
			@RequestParam("departureDate") @DateTimeFormat(pattern = "MM-dd-yyyy") Date departureDate, ModelMap modelMap) {
		List<Flight> flights = flightRepository.findFlights(from, to, departureDate);
		modelMap.addAttribute("flights", flights);
		return "displayFlights";
	}
}
```

In our FlightRepository, we create a new method findFlights that returns the flights. Using **@Param** annotation, we can bind the method parameters from the query. At runtime when the method is invoked, Spring Data through hibernate will execute the query against the database and generate a native SQL internally, get a list of flights and return back to our controller.

```java
public interface FlightRepository extends JpaRepository<Flight, Long> {
	@Query("from Flight where departureCity=:departureCity and arrivalCity=:arrivalCity and dateOfDeparture=:dateOfDeparture")
	List<Flight> findFlights(@Param("departureCity") String from, @Param("arrivalCity") String to,
			@Param("dateOfDeparture") Date departureDate);
}
```

#### Creating the Flight Reservation Controller

In the Reservation Controller, we create an endpoint at _/showCompleteReservation_. The request parameter for the flightId is mapped into the method parameters using **@RequestParam** annotation. Inside the method, we will retrieve the flight information and forward it to the completeReservation page as an attribute using **ModelMap**.

```java
@Controller
public class ReservationController {
	@Autowired
	FlightRepository flightRepository;

	@RequestMapping("showCompleteReservation")
	public String showCompleteReservation(@RequestParam("flightId") Long flightId, ModelMap modelMap) {
		Flight flight = flightRepository.findById(flightId).get();
		modelMap.addAttribute("flight", flight);
		return "completeReservation";
	}
}
```

## Reservation Use Case

Once the user enters the passenger details and card details, we will create a reservation in the database to which we will assign the passenger and flight information. The flow will be:

1. The ReservationControlle will show the complete reservation to the end user
2. User enters their card information
3. The request will be sent to the completeReservation method on the ReservationController
4. Use the Services Layer ReservationService's bookFlight method
5. Create the reservation using ReservationRepository

#### Creating the Reservation Request Controller Method and Services Layer

The request parameters comes from the completeReservation template such as the passenger details, card details and flight id. We create a new DTO java class for a Reservation Request. This reservation request will be passed on to the Services Layer hence it is a DTO.

```java
public class ReservationRequest {
	private Long flightId;

	// passenger details
	private String passengerFirstName;
	private String passengerLastName;
	private String passengerEmail;
	private String passengerPhone;

	// card details
	private String nameOnTheCard;
	private String cardNumber;
	private String expirationDate;
	private String securityCode;
}
```

We will create the ReservationService, which involves business logic and database calls. The bookFlight method takes a ReservationRequest, retrieves the flight from the database, creates a passenger in the database and uses that information to create a reservation and return it to the controller. This class will be annotated with **@Service**.

```java
@Service
public class ReservationServiceImpl implements ReservationService {
	@Autowired
	FlightRepository flightRepository;

	@Autowired
	PassengerRepository passengerRepository;

	@Autowired
	ReservationRepository reservationRepository;

	@Override
	public Reservation bookFlight(ReservationRequest request) {
		// insert code for invoking payment gateway here

		// get the flight
		Long flightId = request.getFlightId();
		Flight flight = flightRepository.findById(flightId).get();

		// create new passenger and save to database
		Passenger passenger = new Passenger();
		passenger.setFirstName(request.getPassengerFirstName());
		passenger.setLastName(request.getPassengerLastName());
		passenger.setPhone(request.getPassengerPhone());
		passenger.setEmail(request.getPassengerEmail());
		Passenger savedPassenger = passengerRepository.save(passenger);

		// create the reservation and save to database
		Reservation reservation = new Reservation();
		reservation.setFlight(flight);
		reservation.setPassenger(savedPassenger);
		reservation.setCheckedIn(false);
		Reservation savedReservation = reservationRepository.save(reservation);

		return savedReservation;
	}
}
```

We can then proceed on using this service in our ReservationController, which redirects to the _reservationConfirmation_ template to which we pass a reservation message and id by defining a **ModelMap**.

```java
@Controller
public class ReservationController {
	@Autowired
	FlightRepository flightRepository;

	@Autowired
	ReservationService reservationService;

	@RequestMapping(value = "/completeReservation", method = RequestMethod.POST)
	public String completeReservation(ReservationRequest request, ModelMap modelMap) {
		Reservation reservation = reservationService.bookFlight(request);
		modelMap.addAttribute("msg", "Reservation Created Successfully and the id is " + reservation.getId());
		return "reservationConfirmation";
	}
}
```

The table for Reservation uses the primary key of the passenger and flight, which is id, as its foreign key.

#### Integration Layer

In the Flight Check In application, we will need to fetch the reservation data from the Flight Reservation application when a user starts the check in process. Once the user checks in, an update should be sent to the flight reservation application to trigger a database update. We can do this through a RESTful api that we will be exposing in the Flight Reservation application.

To make a class a REST controller, we need to annotate it with **@RestController**. There will be two methods, one for retrieving a reservation and the other for updating the reservation. In the RequestMapping, we will get the particular id using `{id}`. We bind the id method paramater to the URL parameter using the **PathVariable** annotation.

For the updateReservation method, we create a new DTO class ReservationUpdateRequest to serve as a wrapper. This class will wrap the reservation id, numberOfBags, and checkedIn. The **@RequestBody** annotation tells Spring that at runtime, the ReservationUpdateRequest object should be constructed using the deserialized JSON content from the request body.

```java
@RestController
@CrossOrigin
public class ReservationRestController {
	@Autowired
	ReservationRepository reservationRepository;

	@RequestMapping("/reservations/{id}")
	public Reservation findReservation(@PathVariable("id") Long id) {
		Reservation reservation = reservationRepository.findById(id).get();
		return reservation;
	}

	@RequestMapping("/reservations")
	public Reservation updateReservation(ReservationUpdateRequest request) {
		Reservation reservation = reservationRepository.findById(request.getId()).get();
		reservation.setNumberOfBags(request.getNumberOfBags());
		reservation.setCheckedIn(request.getCheckedIn());
		Reservation updatedReservation = reservationRepository.save(reservation);
		return updatedReservation;
	}
}
```

```java
public class ReservationUpdateRequest {
	private Long id;
	private Boolean checkedIn;
	private int numberOfBags;
}
```

#### CORS

Later on, the backend will run in port 8080 and the Angular app in port 4200. In order for the Angular app to communicate with the REST application, we need to turn on cross-origin headers. We can mark our controller with the **@CrossOrigin** annotation.

## Flight Checkin Application

For the Flight Checkin Application, the necessary dependencies are Spring Web, Jasper and JSTL for the JSP pages. We don't need MySQL and JPA anymore since we will be consuming REST services from the Flight Reservation application.

We also need to configure the application.properties to use prefix and suffix for our JSP templates.

```
server.port=9090
server.servlet.context-path=/flightcheckin

spring.mvc.view.prefix=/WEB-INF/jsps/
spring.mvc.view.suffix=.jsp
```

#### Creating the Integration Layer

The Integration Layer for the Checkin application will be the RESTful client layer that will invoke the web services exposed by FlightReservation. We first need our DTO classes for Flight, Passenger, Reservation and ReservationUpdateRequest. These do not need the JPA annotations anymore as these are only plain DTOs.

```java
public class Flight {
	private Long id;
	private String flightNumber;
	private String operatingAirlines;
	private String departureCity;
	private String arrivalCity;
	@DateTimeFormat(pattern = "MM-dd-yyyy")
	private Date dateOfDeparture;
	private Timestamp estimatedDepartureTime;
}

public class Passenger  {
	private Long id;
	private String firstName;
	private String lastName;
	private String middleName;
	private String email;
	private String phone;
}

public class Reservation {
	private Long id;
	private Boolean checkedIn;
	private int numberOfBags;
	private Passenger passenger;
	private Flight flight;
}

public class ReservationUpdateRequest {
	private Long id;
	private Boolean checkedIn;
	private int numberOfBags;
}
```

We can now proceed with the implementation of ReservationRestClient. For the GET method, we can use Spring **RestTemplate** and its **getForObject** method. For POST, we use **postForObject** to which we pass the request object.

```java
@Component
public class ReservationRestClientImpl implements ReservationRestClient {
	private static final String RESERVATION_REST_URL = "http://localhost:8080/flightreservation/reservations/";

	@Override
	public Reservation findReservation(Long id) {
		RestTemplate restTemplate = new RestTemplate();
		Reservation reservation = restTemplate.getForObject(RESERVATION_REST_URL + id,
				Reservation.class);
		return reservation;
	}

	@Override
	public Reservation updateReservation(ReservationUpdateRequest request) {
		RestTemplate restTemplate = new RestTemplate();
		Reservation reservation = restTemplate.postForObject(RESERVATION_REST_URL, request,
				Reservation.class);
		return reservation;
	}
}
```

#### Creating the Controller

We map the showStartCheckin method into the URL _/showStartCheckin_ using the JSP template _startCheckIn_. When the form is submitted, we display the reservation information to the user through the startCheckIn method, where we invoke the ReservationRestClient. We need to map the id from the submitted request (from the form) into the method parameter using **@RequestParam**. The reservation object needs to be sent back to the next page using **ModelMap**. For the checkin, we pass the request parameters into the method parameters, and then instantiate our DTO reservationUpdateRequest.

```java
@Controller
public class CheckInController {

	@Autowired
	ReservationRestClient restClient;

	@RequestMapping("/showStartCheckin")
	public String showStartCheckin() {
		System.out.println("start check in");
		return "startCheckIn";
	}

	@RequestMapping("/startCheckIn")
	public String startCheckIn(@RequestParam("reservationId") Long reservationId, ModelMap modelMap) {
		Reservation reservation = restClient.findReservation(reservationId);
		modelMap.addAttribute("reservation", reservation);
		return "displayReservationDetails";
	}

	@RequestMapping("/completeCheckIn")
	public String completeCheckin(@RequestParam("reservationId") Long reservationid,
			@RequestParam("numberOfBags") int numberOfBags) {
		ReservationUpdateRequest reservationUpdateRequest =	new ReservationUpdateRequest();
		reservationUpdateRequest.setId(reservationid);
		reservationUpdateRequest.setNumberOfBags(numberOfBags);
		reservationUpdateRequest.setCheckedIn(true);
		restClient.updateReservation(reservationUpdateRequest);
		return "checkInConfirmation";
	}
}
```

## Itenerary Function

For generating PDF files, we need to add the **IText** maven dependency. We then created a PDFGenerator utility class.

```java
@Component
public class PDFGenerator {
	public void generateItinerary(Reservation reservation, String filePath) {
		Document document = new Document();
		try {
			PdfWriter.getInstance(document, new FileOutputStream(filePath));
			document.open();
			document.add(generateTable(reservation));
			document.close();
		} catch (FileNotFoundException | DocumentException e) {
			e.printStackTrace();
		}
	}

	private PdfPTable generateTable(Reservation reservation) {
		// 2 columns
		PdfPTable table = new PdfPTable(2);
		PdfPCell cell;

		cell = new PdfPCell(new Phrase("Flight Itinerary"));
		cell.setColspan(2);
		table.addCell(cell);

		cell = new PdfPCell(new Phrase("Flight Details"));
		cell.setColspan(2);
		table.addCell(cell);

		table.addCell("Airlines");
		table.addCell(reservation.getFlight().getOperatingAirlines());

		table.addCell("Departure City");
		table.addCell(reservation.getFlight().getDepartureCity());

		table.addCell("Arrival City");
		table.addCell(reservation.getFlight().getArrivalCity());

		table.addCell("Flight Number");
		table.addCell(reservation.getFlight().getFlightNumber());

		table.addCell("Departure Date");
		table.addCell(reservation.getFlight().getDateOfDeparture().toString());

		table.addCell("Departure Time");
		table.addCell(reservation.getFlight().getEstimatedDepartureTime().toString());

		// passenger details
		cell = new PdfPCell(new Phrase("Passenger Details"));
		cell.setColspan(2);
		table.addCell(cell);

		table.addCell("First Name");
		table.addCell(reservation.getPassenger().getFirstName());

		table.addCell("Last Name");
		table.addCell(reservation.getPassenger().getLastName());

		table.addCell("Email");
		table.addCell(reservation.getPassenger().getEmail());

		table.addCell("Phone");
		table.addCell(reservation.getPassenger().getPhone());

		return table;
	}
}
```

## Email Function

For sending emails, we need to add the spring-boot-starter-mail dependency. We then proceed on creating the Email Utility class. We need to first define the mail sender from Spring using **JavaMailSender** from Spring. To assign fields on our message, we can use the **MimeMessageHelper**.

```java
@Component
public class EmailUtil {

	@Autowired
	private JavaMailSender sender;

	public void sendItinerary(String toAddress, String filePath) {
		MimeMessage message = sender.createMimeMessage();

		try {
			MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
			messageHelper.setTo(toAddress);
			messageHelper.setSubject("Itinerary for Your Flight");
			messageHelper.setText("Please find your Itinerary attached.");
			messageHelper.addAttachment("Itinerary", new File(filePath));

			sender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
}
```

We can now invoke our utility classes for PDF and Email into our Service layer by injecting them.

```java
@Service
public class ReservationServiceImpl implements ReservationService {

	@Autowired
	FlightRepository flightRepository;

	@Autowired
	PassengerRepository passengerRepository;

	@Autowired
	ReservationRepository reservationRepository;

	@Autowired
	PDFGenerator pdfGenerator;

	@Autowired
	EmailUtil emailUtil;

	@Override
	public Reservation bookFlight(ReservationRequest request) {
		// insert code for invoking payment gateway here

		// get the flight
		Long flightId = request.getFlightId();
		Flight flight = flightRepository.findById(flightId).get();

		// create new passenger and save to database
		Passenger passenger = new Passenger();
		passenger.setFirstName(request.getPassengerFirstName());
		passenger.setLastName(request.getPassengerLastName());
		passenger.setPhone(request.getPassengerPhone());
		passenger.setEmail(request.getPassengerEmail());
		Passenger savedPassenger = passengerRepository.save(passenger);

		// create the reservation and save to database
		Reservation reservation = new Reservation();
		reservation.setFlight(flight);
		reservation.setPassenger(savedPassenger);
		reservation.setCheckedIn(false);
		Reservation savedReservation = reservationRepository.save(reservation);

		// generate itinerary from the reservation
		String filePath = "C:\\Users\\ChristianCruz\\Documents\\test\\" + savedReservation.getId() + ".pdf";
		pdfGenerator.generateItinerary(savedReservation,
				filePath);

		// send email
		emailUtil.sendItinerary(passenger.getEmail(), filePath);

		return savedReservation;
	}
}
```

## Logging

Logging is the process of writing messages from within our application to a central location for diagnostic purposes. We can do it by creating a **Logger** object and using it to start writing messages. SL4J is a wrapper that makes logging easy. There are various log levels:

1. Error - when something goes wrong
2. Warning - when something might go wrong
3. Info - information
4. Debug - used for debugging
5. Trace - everything

To use these levels, we use the appropriate method on the logger. We can also set the log level dynamically through configurations.

Springboot already pulls logback-classic and logback-core as dependencies automatically. To use logging, we instantiate a static final of Logger from sl4j.

```java
@Controller
public class UserController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@RequestMapping(value="/login", method=RequestMethod.POST)
	public String login(@RequestParam("email") String email, @RequestParam("password") String password, ModelMap modelMap) {
		LOGGER.error("ERR");
		LOGGER.warn("WARN");
		LOGGER.info("INFO");
		LOGGER.debug("DEBUG");
		LOGGER.trace("TRACE");
		User user = userRepository.findByEmail(email);
		if (user.getPassword().equals(password)) {
			return "findFlights";
		} else {
			modelMap.addAttribute("msg", "Invalid username or password");
		}
		return "login/login";
	}
}

```

By default, the log level of sl4j is info and above. We can configure the root log level in application.properties. We can also configure Spring to write the logs to a file.

```
logging.level.root=ERROR
logging.file.name=C:/Users/ChristianCruz/Documents/Christian/projects/microservices-project-development/flightreservation/logs/flightreservation.log
```

#### Logback Configuration

A lot of real time applications use XML configuration to come up with custom logging patterns and rolling file appenders. Logger internally uses an **Appender**, which is responsible for taking the log message and send it to a file, or console. Appender internally uses an **encoder** which determines how the log message should look like. When we configure the encoder class, we can provide a particular pattern using which it should log the message. The policy configuration enables us to configure rolling file appenders.

Using the logback configuration, we no longer need to define logging parameters in our application.properties. The _class_ property of the appender is where we select a class from the logback API. **RollingFileAppender** has the capability of creating new files once the current log file reaches a limit using rollingPolicy. Within the rollingPolicy, we can determine the naming pattern when it rolls or archives our log files and where it should be archived. We can also determine the timing policy to be used. The following policy will be triggered every 24 hours or when the file size reaches 10KB.

```xml
<configuration>
	<property name="LOG_DIR"
		value="C:/Users/ChristianCruz/Documents/Christian/projects/microservices-project-development/flightreservation/logs"></property>
	<property name="FILE_PREFIX" value="flightreservation"></property>

	<appender name="FILE"
		class="ch.qos.logback.core.rolling.RollingFileAppender">
		<file>${LOG_DIR}/${FILE_PREFIX}.log</file>
		<encoder
			class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
			<Pattern>%d{yyyy-MM-dd HH:mm:ss} - %msg%n</Pattern>
		</encoder>

		<rollingPolicy
			class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
			<fileNamePattern>${LOG_DIR}/archived/${FILE_PREFIX}.%d{yyyy-MM-dd}.%i.log
			</fileNamePattern>
			<timeBasedFileNamingAndTriggeringPolicy
				class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
					<maxFileSize>10KB</maxFileSize>
			</timeBasedFileNamingAndTriggeringPolicy>
		</rollingPolicy>
	</appender>

	<root level="info">
		<appender-ref ref="FILE"></appender-ref>
	</root>
</configuration>
```

## Externalizing Properties

We can set properties in application.properties instead of hardcoding values in our application.

```java
com.demiglace.flightreservation.itinerary.dirpath=C:\\Users\\ChristianCruz\\Documents\\test\\
com.demiglace.flightreservation.itinerary.email.subject=Please find your Itinerary attached
com.demiglace.flightreservation.itinerary.email.body=Itinerary for Your Flight
```

To inject, we need to use the **@Value** annotation.

```java
	@Value("${com.demiglace.flightreservation.itinerary.email.body}")
	private String EMAIL_BODY;

	@Value("${com.demiglace.flightreservation.itinerary.email.subject}")
	private String EMAIL_SUBJECT;

  @Value("${com.demiglace.flightreservation.itinerary.dirpath}")
	private String ITINERARY_DIR;
```

## Security

Using Spring Security, we can encode the password, and load the user details from the database using Spring Security context. We can also implement authorization based on user roles.

#### Encoding Password

To encode a password, we need the **BCryptPasswordEncoder** from Spring Security. Using the encode method, we will no longer be storing the password as plaintext in the database.

```java
@Controller
public class UserController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder encoder;

	@RequestMapping(value="/registerUser", method=RequestMethod.POST)
	public String register(@ModelAttribute("user") User user) {
		LOGGER.info("inside register()" + user);
		user.setPassword(encoder.encode(user.getPassword()));
		userRepository.save(user);
		return "login/login";
	}
```

#### Authorization Data Access Layer

We create an addFlights endpoint that will only be accessible to admins. We also create a new table for role. It has a primary key id and a role name. The user_role table maps a user to a role, with a many to many relationship. It has a user_id and role_id and both are primary keys in the user and role tables respectively.

```sql
CREATE TABLE ROLE
(
ID INT NOT NULL AUTO_INCREMENT,
NAME VARCHAR(20),
PRIMARY KEY (ID)
);

create table user_role(
user_id int,
role_id int,
FOREIGN KEY (user_id)
REFERENCES user(id),
FOREIGN KEY (role_id)
REFERENCES role(id)
);
```

We first create the Role entity and map it to the user entity. A user can have multiple roles, and multiple roles can be assigned to a user hence we can use a Set of roles. To map the relationships between users and roles to the database, we use the **@ManyToMany** annotation. **@JoinTable** refers to the table that joins user and role table.

```java
@Entity
public class User extends AbstractEntity {
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	@ManyToMany
	@JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles;

@Entity
public class Role extends AbstractEntity {
	private String name;
  @ManyToMany(mappedBy="roles")
	private Set<User> users;
}
```

To fetch the roles, we can create a RoleRepository that extends JpaRepository.

```java
public interface RoleRepository extends JpaRepository<Role, Long> {

}
```

#### UserDetails Service

The UserDetailsService interface from Spring Security has a **loadUserByUsername()** method which we need to override. We can instantiate a User object from Spring Security and pass unto it the username, password and role. This method should return a User object which implements a UserDetails from spring (The User is an implementation of UserDetails).

```java
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found for user:" + username);
		}
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
				user.getRoles());
	}
}
```

For the role entity, we implement the **GrantedAuthority** interface from Spring. Spring internally calls the getAuthority() method to check what role a particular user has.

```java
@Entity
public class Role extends AbstractEntity implements GrantedAuthority {
	private String name;
	@ManyToMany(mappedBy="roles")
	private Set<User> users;

	@Override
	public String getAuthority() {
		return name;
	}
}
```

We can then proceed on creating a SecurityService for logging in the user. This service will use the services provided by UserDetailsService. We first retrieve the user details from the database using **UserDetailsService**. We then create a UsernamePasswordAuthenticationToken by passing in the loaded UserDetails and the password that came in from the browser request. We also pass in the roles using **getAuthorities()**. We then use **AuthenticationManager** from Spring which will authenticate the request based on the previous parameters provided. If the authentication is successful, the state will be set to true. We then save the user state into the Spring Security context.

```java
@Service
public class SecurityServiceImpl implements SecurityService {

	@Autowired
	UserDetailsService userDetailsService;

	@Autowired
	AuthenticationManager authenticationManager;

	@Override
	public boolean login(String username, String password) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		// create username and password authentication token
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, password,
				userDetails.getAuthorities());

		// authenticate token then set flag to true if authentication successful
		authenticationManager.authenticate(token);

		// retrieve state of the login
		boolean result = token.isAuthenticated();

		// set the token into the Spring Security context holder
		if (result) {
			SecurityContextHolder.getContext().setAuthentication(token);
		}

		return result;
	}
}
```

We can now update our UserController to use our SecurityService implementation instead of loading the user from the database and comparing the password.

```java
@Controller
public class UserController {

	@Autowired
	private SecurityService securityService;

	@RequestMapping(value="/login", method=RequestMethod.POST)
	public String login(@RequestParam("email") String email, @RequestParam("password") String password, ModelMap modelMap) {
//		User user = userRepository.findByEmail(email);
		boolean loginResponse = securityService.login(email, password);

		if (loginResponse) {
			return "findFlights";
		} else {
			modelMap.addAttribute("msg", "Invalid username or password");
		}

		return "login/login";
	}
}
```

#### Creating Security Configuration

To create the security configuration, we need to create a new class WebSecurityConfig that extends **WebSecurityConfigurerAdapter** from Spring. We need to annotate this class with **@Configuration** and **@EnableWebSecurity** to turn on the Security infrastructure of Spring. In the configure() method, we configure the authentication and authorization for our application. Using **antMatchers()** allows us to pass in any number of patterns and based on the patterns, Spring will do the Security for us. **permitAll()** allows anybody to access the url while **hasAnyAuthority()** signifies that only admins can access the routes. **csrf()** stands for cross site reference support, which we will disable.

```java
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/showReg", "/", "/index.html", "/registerUser", "/login", "/showLogin", "/login/*", "/reservations/*")
				.permitAll()
				.antMatchers("/admin/*").hasAnyAuthority("ADMIN")
				.anyRequest().authenticated()
				.and().csrf().disable();
	}
}
```

#### Password Encoder Bean

The BCryptPasswordEncoder bean is not automatically provided by Spring hence we need to provide it in the configuration as well

```java
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
```

## Transaction Management

The process of executing a bunch of related operations while applying do all or nothing principle is called transaction. Every transaction has four key properties (ACID):

1. Automicity - all or nothing
2. Consistency - database should be left in a consistent state at the end of the transaction
3. Isolation - each transaction should work in isolation
4. Durability - once changes are committed, those changes should stay

Service Layer classes should ideally be wrapped around a transaction. We can use the **@Transactional** annotation from Spring which will automatically create a transaction at runtime and whenever an exception is thrown in any tasks, the entire transaction will be rolled back.

```java
	@Override
	@Transactional
	public Reservation bookFlight(ReservationRequest request) {
		LOGGER.info("inside bookFlight()");
		// insert code for invoking payment gateway here

		// get the flight
		Long flightId = request.getFlightId();
		LOGGER.info("fetching flight for flight id:" + flightId);
		Flight flight = flightRepository.findById(flightId).get();

		// create new passenger and save to database
		Passenger passenger = new Passenger();
		passenger.setFirstName(request.getPassengerFirstName());
		passenger.setLastName(request.getPassengerLastName());
		passenger.setPhone(request.getPassengerPhone());
		passenger.setEmail(request.getPassengerEmail());
		LOGGER.info("saving passenger: " + passenger);
		Passenger savedPassenger = passengerRepository.save(passenger);

		// create the reservation and save to database
		Reservation reservation = new Reservation();
		reservation.setFlight(flight);
		reservation.setPassenger(savedPassenger);
		reservation.setCheckedIn(false);
		LOGGER.info("saving reservation: " + reservation);
		Reservation savedReservation = reservationRepository.save(reservation);

		// generate itinerary from the reservation
		String filePath = ITINERARY_DIR + savedReservation.getId() + ".pdf";
		LOGGER.info("generating itinerary");
		pdfGenerator.generateItinerary(savedReservation,
				filePath);

		// send email
//		emailUtil.sendItinerary(passenger.getEmail(), filePath);

		return savedReservation;
	}
```

## Deployment

There are two ways to deploy a Spring Boot application: JAR and WAR. By default, Spring Boot project's packaging is jar. It will already include all the configuration and jars for maven dependencies. To build our project into jar, we just need to run maven clean then maven install. The jar package contains the **BOOT-INF** directory which contains the class files, project configuration. The **lib** folder contains all the jars that our application depends on. We can run a jar using the command `java -jar flightreservation.jar`. In this case, the application will be launched internally with an embedded tomcat server.

WAR is useful for Weblogic or Websphere servers. To deploy into a war, we need to change the packaging type into war in pom.xml. Afterwards, we need to provide additional configuration in our entry point (FlightreservationApplication). This class should now extend **SpringBootServletInitializer** then override the configure() method. Afterwards, we need to run maven install to bundle the application as a war file.

```java
@SpringBootApplication
public class FlightreservationApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(FlightreservationApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(FlightreservationApplication.class, args);
	}
}
```

## Checkin Application with Angular

We initialize the project using `ng new flightCheckIn`. Afterwards, we create the components and service

```bash
ng g c components/checkin
ng g c components/startcheckin
ng g c components/confirm
ng g s services/checkin
```

The checkin service will be responsible for making the backend calls for fetching the reservation and updating reservation data.

```typescript
export class CheckinService {
  reservationUrl = "http://localhost:8080/flightreservation/reservations/";
  reservationData: any;

  constructor(private _httpClient: HttpClient) {}

  public getReservation(id: number): any {
    return this._httpClient.get(this.reservationUrl + id);
  }

  public checkIn(checkInRequest: any): any {
    return this._httpClient.put(this.reservationUrl, checkInRequest);
  }
}
```

Afterwards, we can configure the routes in our app-routing module.

```typescript
const routes: Routes = [
  { path: "", redirectTo: "", pathMatch: "full" },
  { path: "startCheckIn", component: StartcheckinComponent },
  { path: "checkIn", component: CheckinComponent },
  { path: "confirm", component: ConfirmComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
```

We then start writing the components. In the Startcheckin component, we inject the service we created and the router. In the onClick method, we send the response object into the next component.

```html
<h1>Enter the reservation id</h1>
<input type="text" [(ngModel)]="reservationId" /><br />
<button (click)="onClick()">Next</button>
```

```typescript
export class StartcheckinComponent implements OnInit {
  reservationId!: Number;

  constructor(private service: CheckinService, private router: Router) {}

  ngOnInit(): void {}

  public onClick() {
    this.service.getReservation(this.reservationId).subscribe((res: any) => {
      this.service.reservationData = res;
      this.router.navigate(["checkIn"]);
    });
  }
}
```

Afterwards, we can implement the checkin page and component. Upon init, we need to assign the data by calling the service. In the checkIn method, we construct the request object

```html
<h1>Review Details</h1>
<h2>Flight Details:</h2>
Airlines: {{ data?.flight?.operatingAirlines }}<br />
Flight No: {{ data?.flight?.flightNumber }}<br />
Departure City: {{ data?.flight?.departureCity }}<br />
Arrival City: {{ data?.flight?.arrivalCity }}<br />
Date Of Departure: {{ data?.flight?.dateOfDeparture }}<br />
Estimated Departure Time: {{ data?.flight?.estimatedDepartureTime }}<br />
<h2>Passenger Details:</h2>

First Name: {{ data?.passenger?.firstName }}<br />
Last Name: {{ data?.passenger?.lastName }}<br />
Email : {{ data?.passenger?.email }}<br />
Phone: {{ data?.passenger?.phone }}<br />
Enter the number of bags to check in:<input
  type="text"
  [(ngModel)]="noOfbags"
/>
<button (click)="checkIn()">CheckIn</button>
```

```typescript
export class CheckinComponent implements OnInit {
  noOfbags!: Number;
  data: any;

  constructor(private service: CheckinService, private router: Router) {}

  ngOnInit(): void {
    this.data = this.service.reservationData;
  }

  public checkIn() {
    let request = {
      id: this.data.id,
      checkIn: true,
      noOfBags: this.noOfbags,
    };

    this.service.checkIn(request).subscribe((res: any) => {
      this.router.navigate(["/confirm"]);
    });
  }
}
```

The data is fetched from the backend from the Startcheckin component. It puts the data into the service then navigates to the checkin component, which initializes the data from the service into the _data_ field that is then rendered to the template. Once the number of bags is entered in the template, a backend _put_ call will be made by the service.
