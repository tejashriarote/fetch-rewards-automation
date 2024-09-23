# Spring Boot Web Application

This is a Spring Boot web application built with Maven. This README provides instructions on how to run the project and execute tests from the command line.

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## Getting Started

### Cloning the Repository

First, clone the repository to your local machine:


git clone https://github.com/yourusername/your-repo-name.git<br>
cd your-repo-name

To build the project, navigate to the project directory and run:<br>
mvn spring-boot:run
Once the application is running, you can access it at http://localhost:8080.

To execute all the tests from the command line, use the following command:<br>
mvn test


---

## Importing the Project into IntelliJ IDEA

If you prefer working in an IDE like IntelliJ IDEA, follow these steps to import the project:

1. **Open IntelliJ IDEA**.

2. **Import the project**:
    - Go to `File -> Open` or `Import Project`.
    - Navigate to the root directory of the cloned project (`your-repo-name`) and select it.
    - IntelliJ will automatically detect that it is a Maven project. When prompted, select "Open as a Project."

3. **Wait for Maven to resolve dependencies**:
    - IntelliJ will load the project and start downloading the necessary dependencies as defined in your `pom.xml`. This may take some time if it's the first time you're importing the project.

4. **Verify JDK setup**:
    - Ensure the correct JDK is configured for the project.
    - Go to `File -> Project Structure -> Project` and ensure that the `Project SDK` is set to the correct version (e.g., Java 17 or higher).

5. **Build the project**:
    - Go to `Build -> Build Project` or press `Ctrl + F9` to build the project in IntelliJ.

---

## Running the Application in IntelliJ IDEA

To run the Spring Boot application from IntelliJ IDEA:

1. Go to the `src/main/java` directory in the Project tool window.
2. Right-click the main class file (the class with `@SpringBootApplication` annotation, usually named `Application.java` or similar).
3. Select `Run 'Application.main()'`.

The application will start, and you can access it at `http://localhost:8080`.

---

## Running Tests in IntelliJ IDEA

You can also run the tests directly from IntelliJ:

1. Go to the `src/test/java` directory in the Project tool window.
2. Right-click the test folder or a specific test class you want to run.
3. Select `Run 'Tests in 'test''` or `Run 'YourTestClassName'`.

Alternatively, you can run all tests by right-clicking on the `src/test/java` folder and selecting `Run All Tests`.

---

## Project Structure

- `src/main/java`: Contains the application source code.
- `src/main/resources`: Contains application configuration files.
- `src/test/java`: Contains test classes and test resources.

## Dependencies

This project uses the following key dependencies:

- Spring Boot Starter Web
- Spring Boot Starter Test
- Other relevant dependencies...


## Contact

For any inquiries, please reach out at tejarote@gmail.com .
