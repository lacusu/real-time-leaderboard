**System Design Documents**:
- **Architecture Diagram**:
![VacabularyQuiz drawio](https://github.com/user-attachments/assets/4dcada1a-d322-4ff1-a2e5-9cd561f399f6)

- **Component Descriptions**:
  - **Client (Players and Admins)**:
    - Interacts with the application through a user interface (web or mobile). Players join quizzes, answer questions, and view the leaderboard. Admins create and manage quizzes.
  - **API Gateway**:
    - The single entry point for all client requests. Handles routing, authentication, authorization, and other cross-cutting concerns (e.g., rate limiting)
    - Receives requests from clients and forwards them to the appropriate backend services. May also handle WebSocket routing for real-time updates
  - **Identity Service**:
    -  Manages user authentication and authorization. Integrates with external identity providers (Google, Facebook, etc.).
    - Verifies user identities and issues authentication tokens. Used by the API Gateway for authentication. Authorize user with predefined role
  - **User Service**:
    - Manages user profile data and provides user information to other services (e.g., Leaderboard Service)
  - **Quiz Service**:
    - Manages the overall quiz logic, including starting quizzes, retrieving questions, processing answers, calculating scores, and managing quiz sessions.
    - Interacts with the Question Service for questions, the Scoring Service (or handles scoring logic itself
  - **Scoring Service**:
    - Calculates scores based on answers and quiz settings (e.g., time bonuses). Receives answers and quiz settings, calculates scores, and sends them to the Message Broker
  - **Question Service**:
    - Manages the question pool, retrieves questions for quizzes, and may handle question creation/editing (by admins). Provides questions to the Quiz Service. Caches frequently accessed questions in Redis.
  - **Leaderboard Service**:
    - Manages the real-time leaderboard. Receives score updates and updates the leaderboard data in Redis. Sends real-time updates to clients via WebSockets
  - **Message Broker**:
    - Enables asynchronous communication between services. Receives messages from services (e.g., score updates from Quiz/Scoring Service, quiz join events) and delivers them to other subscribed services (e.g., Leaderboard Service).
  - **Redis Cache**:
    - Provides in-memory data storage for caching frequently accessed data (user information, questions) and storing real-time leaderboard data using Redis Sorted Sets
  - **Database**:
    - Stores persistent data, including quiz questions, user information, quiz settings, and potentially historical leaderboard data
  - **Prometheus and Grafana (Monitoring)**:
    - Collects and visualizes metrics about the application's performance and health (e.g., API request latency, service uptime, error rates, number of active WebSocket connections). Prometheus collects metrics from the services. Grafana displays the collected metrics in dashboards.


- **Data Flow**:
  Let's describe the data flow for two main scenarios in your real-time quiz application: Quiz Creation/Setup (Admin) and Quiz Play/Leaderboard Update (Player).

  **1. Quiz Creation/Setup (Admin):**
  
  - **Admin Input**: The admin uses the client application (UI) to input quiz details (title, description, time limit, questions, scoring rules, etc.).
  
  - **API Gateway (Setup Quiz)**: The client sends an HTTP request to the API Gateway to create/setup the quiz. The API Gateway authenticates the admin user (likely using the Identity Service) and authorize the user whether is it admin.
  
  - **Quiz Service (Create Quiz)**: The API Gateway routes the request to the Quiz Service. The Quiz Service stores the quiz details in the database.
  
  - **Question Service (Setup Questions)**: The Quiz Service interacts with the Question Service to:
  
    - Retrieve questions from the database if the admin selected existing questions.
    
    - Store new questions in the database if the admin created new questions. The questions might be categorized or tagged for easier retrieval.
    
  - **Response to Admin:** The Quiz Service sends a success/failure response with quiz id back to the admin via the API Gateway.
  
  - **2. Quiz Play/Leaderboard Update (Player):**
  
  - **Player Joins Quiz**: The player uses the client application to join a quiz by an id which send out from admin user
  
  - **API Gateway (Join Quiz)**: The client sends an HTTP request to the API Gateway to join the quiz. The API Gateway authenticates the player (using the Identity Service).
  
  - **Quiz Service (Join Quiz)**: The API Gateway routes the request to the Quiz Service. The Quiz Service:
  
    - Retrieves the quiz questions (from the database or Redis cache if any).
    
    - Creates a quiz session for the player.
    
    - Sends the first question to the player.
  
  - **Message Broker (Quiz Join Event)**: The Quiz Service might publish a "Quiz Join" event to the Message Broker to notify other services like leaderboard to update the player into the leaderboard
  
  - **Player Answers Question**: The player submits an answer.
  
  - **API Gateway (Answer Question)**: The client sends the answer to the API Gateway.
  
  - **Quiz Service (Evaluate Answer)**: The API Gateway routes the request to the Quiz Service. The Quiz Service:
  
    - Evaluates the answer (checking against the correct answer).
    
    - Calculates the score by Scoring Service
  
  - **Scoring Service (Evaluate Answer)**: the Quiz Service sends the answer and quiz settings (e.g., time bonuses) to the Scoring Service for score calculation. The Scoring Service returns the calculated score to the Quiz Service and produce a event to Message Broker about the score changed
  
  - **Message Broker (Score Updated Event)**: The Scoring Service publishes a "Score Updated" event to the message broker, including the player's ID, quiz ID, and score.
  
  - **Leaderboard Service (Consume Score Updated)**: The Leaderboard Service subscribes to the "Score Updated" topic on the Message Broker. It receives the score update and:
     - Updates the leaderboard data in Redis (using a Sorted Set).
    
     - Sends the updated leaderboard to connected clients via WebSockets.
     - The Leaderboard Service might call the User Service to retrieve additional user information (e.g., display names)  to display on the leaderboard.
     - The Leaderboard Service caches the user information retrieved from the User Service in Redis to avoid redundant calls to the User Service.
  
  - **WebSocket (Leaderboard WS)**: The client application (through a WebSocket connection) receives the real-time leaderboard update and updates the display on the player's screen.

**Technology Justification**:
- **Frontend**:
  - **Web App**: It can be built by ReactJS
  - **Mobile App**: Using React Native for a hybrid mobile app that can also be deployed as a web app is a popular approach, often referred to as "code sharing" or "cross-platform development.
- **Backend:**
Java Spring Boot: Provides a robust and mature framework for building microservices. Offers features like dependency injection, REST controller support, easy integration with databases and other technologies (e.g., Redis, Kafka), and built-in WebSocket support. Microservice Architecture: Enables independent development, deployment, and scaling of individual services (Quiz Service, Question Service, User Service, Leaderboard Service). Improves fault isolation and maintainability.

- **API Gateway:** AWS API Gateway: Provides a single entry point for all client requests, handles routing, authentication, authorization, and other cross-cutting concerns. Offers scalability and can be managed independently.

- **Containerization and Orchestration**: 
Docker: Containerizes the microservices for consistent deployment across different environments. Simplifies dependency management and deployment processes. EKS (Elastic Kubernetes Service): Provides a managed Kubernetes environment for orchestrating and scaling the Docker containers. Offers high availability, fault tolerance, and simplifies deployment management in a cloud environment.

- **Messaging:** Kafka: A distributed, fault-tolerant message broker for handling asynchronous communication between services. Suitable for handling high volumes of score updates and other events.

- **Database**: MySQL Relational databases suitable for storing structured data like quiz questions, user information, and quiz results. Offer ACID properties for data consistency and reliability.

- **Caching**:  Redis: An in-memory data store used for caching frequently accessed data (user information, quiz questions) and storing real-time leaderboard data using Sorted Sets. Offers high performance and low latency data access.
- **Real-time Communication**: WebSockets (with STOMP): Enables bidirectional, real-time communication between the Leaderboard Service and client browsers for pushing leaderboard updates. STOMP simplifies message handling and provides a messaging protocol over WebSockets. Spring Boot provides built-in support for WebSockets and STOMP.
- **Monitoring and Logging**: 
  - **Prometheus**: A time-series database for collecting metrics from the services (e.g., API latency, error rates).
  - **Grafana**: A visualization tool for creating dashboards and alerts based on the metrics collected by Prometheus. Provides insights into the application's performance and health.
  - **Logging Libraries (e.g., SLF4j, Logback)**: Essential for tracking application events and debugging issues.
-  **AI Assistant**: Yeah, I am using an AI as my assistant for this challenge. It supported me to research, English assistant, resolve some technical problem which were faced while coding the challenge. one of the bootstrap thing help me to complete the task.
  
...Due to time constraints, I haven't yet included comprehensive unit tests in this implementation. However, thorough testing is a crucial part of the software development process, and I would prioritize adding unit tests, particularly for the core components like the Leaderboard Service and Quiz Service, in a production environment. This would ensure the reliability and maintainability of the code
