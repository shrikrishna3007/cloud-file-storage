# cloud-file-storage

A Spring Boot REST API that allows users to **search, upload (optional), and download files** stored in an AWS S3 bucket.  
Each user has a logical "folder" (prefix) within the bucket.  
This project was built as part of a backend coding assessment to demonstrate clean architecture, exception handling, and AWS integration.

---

## Features

- **Search files** by name within a user's folder in S3
- **Download files** from a user's folder
- (Optional) **Upload files** to a user's folder
- AWS S3 integration using `S3Client`
- Unit testing with JUnit and Mockito

---

## Tech Stack

- Java 17
- Spring Boot
- AWS SDK v2 (S3Client)
- JUnit 5
- Mockito
- Maven

---

## Running the Project

1. **Set AWS credentials** as environment variables:
    - `AWS_ACCESS_KEY_ID`
    - `AWS_SECRET_ACCESS_KEY`

2. **Set the S3 bucket name** in `application.properties` or via env variable:
   ```properties
   s3.bucket.name=your-bucket-name
   ```
3. Build the project:
    ```bash
    mvn clean package
    ```
4. Run the application:
    ```bash
    mvn spring-boot:run
    ```
5. Use the API endpoints to search, upload, and download files.