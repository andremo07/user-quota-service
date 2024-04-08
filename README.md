## User Quota Service

**Introduction**

This project implements a REST API for managing user accounts and their associated quotas. It enforces a maximum request limit for users and blocks those who exceed the threshold.

**Features**

* User Management: Create, retrieve, update, and delete user accounts.
* Quota Management: Track user requests and enforce a maximum limit. Block users who reach the quota limit.
* In-Memory Quota Cache: Stores quota information in memory for efficient retrieval.
* Generic Repository Abstraction: Uses a generic repository factory for flexible data access.

**REST API Endpoints**

**Base URL:** `http://localhost:8146/users`

| Method | Path                       | Description                                       |
|--------|-----------------------------|---------------------------------------------------|
| GET    | /users/{userId}              | Retrieve a user by ID                             |
| POST   | /users                       | Create a new user                                  |
| PUT    | /users/{userId}              | Update an existing user                            |
| DELETE | /users/{userId}              | Delete a user by ID                                |
| GET    | /users/{userId}/quota        | Consume a user's quota and return status          |
| GET    | /quota                       | Retrieve quota information for all users          |

**Running the Application**

**Prerequisites:**

* Java 8 or later
* Maven

**Build the Project:**

```bash
mvn clean install
```

**Run the Application:**

```bash
mvn spring-boot:run
```

**Repository Logic**

The project utilizes a `RepositoryFactory` to provide a layer of abstraction for data access. This factory retrieves repositories based on the entity class and allows for separation of concerns between data access logic and business logic. Here's an overview of the relevant code:

* **GenericRepository.java (interface)**: Defines methods for basic CRUD operations (`save`, `findById`, `deleteById`) and a new method `getEntityClass()` to retrieve the entity class associated with the repository.
* **RepositoryFactory.java**: This class manages retrieval and separation of repositories based on a user-defined logic (implementation omitted). It retrieves all `GenericRepository` beans from the Spring application context and uses reflection or other strategies (depending on implementation) to determine the entity class for each repository. Based on this information, it separates repositories into maps (`databaseRepositories` and `elasticSearchRepositories`) and provides a method `getRepository(Class<T> entityClass)` to retrieve a specific repository by its entity class.
