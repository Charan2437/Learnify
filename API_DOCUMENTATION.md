# Learnify API Documentation

## Authentication
All protected endpoints require a valid JWT token in the Authorization header.

```
Authorization: Bearer <your_jwt_token>
```

## Course Management APIs

### Create Course
Creates a new course with an AI-generated study plan.

- **URL**: `/api/courses/create`
- **Method**: `POST``
- **Auth Required**: Yes
- **Role Required**: ROLE_USER

**Request Body**:
```json
{
    "name": "Course Name",
    "numberOfDays": 30,
    "preferredLearningStyle": ["visual", "docs"],
    "startDate": "2024-01-01"
}
```

**Success Response**:
- **Code**: 200 OK
- **Content**:
```json
{
    "id": "course_id",
    "name": "Course Name",
    "userId": "user_id",
    "numberOfDays": 30,
    "preferredLearningStyle": ["visual", "docs"],
    "startDate": "2024-01-01",
    "studyPlan": [
        {
            "day": "Day 1",
            "tasks": [
                {
                    "title": "Task Title",
                    "description": "Task Description",
                    "resourceUrls": ["url1", "url2"],
                    "status": "pending"
                }
            ]
        }
    ]
}
```

**Error Response**:
- **Code**: 500 Internal Server Error
- **Content**: `{"message": "Error creating course: <error_message>"}`

### Get All Courses
Retrieve all courses for the authenticated user.

- **URL**: `/api/courses/all`
- **Method**: `GET`
- **Auth Required**: Yes
- **Role Required**: ROLE_USER

**Success Response**:
- **Code**: 200 OK
- **Content**: Array of course objects
```json
[
    {
        "id": "course_id",
        "name": "Course Name",
        "userId": "user_id",
        "studyPlan": [...]
    }
]
```

**Error Response**:
- **Code**: 500 Internal Server Error
- **Content**: `{"message": "Error fetching courses: <error_message>"}`

### Get Course by ID
Retrieve a specific course by its ID.

- **URL**: `/api/courses/{id}`
- **Method**: `GET`
- **Auth Required**: Yes
- **Role Required**: ROLE_USER

**URL Parameters**:
- `id`: Course ID

**Success Response**:
- **Code**: 200 OK
- **Content**: Course object

**Error Responses**:
- **Code**: 404 Not Found
- **Content**: Empty response

- **Code**: 403 Forbidden
- **Content**: `{"message": "Access denied: Please ensure you are authenticated and have proper permissions"}`

### Update Course
Update an existing course.

- **URL**: `/api/courses/{id}`
- **Method**: `PUT`
- **Auth Required**: Yes
- **Role Required**: ROLE_USER

**URL Parameters**:
- `id`: Course ID

**Request Body**:
```json
{
    "name": "Updated Course Name",
    "numberOfDays": 45,
    "preferredLearningStyle": ["visual", "docs"],
    "startDate": "2024-01-01",
    "studyPlan": [...]
}
```

**Success Response**:
- **Code**: 200 OK
- **Content**: Updated course object

**Error Responses**:
- **Code**: 404 Not Found
- **Content**: Empty response

- **Code**: 403 Forbidden
- **Content**: `{"message": "Access denied: You can only update your own courses"}`

### Get Courses by User ID
Retrieve all courses for a specific user.

- **URL**: `/api/courses/mycourses`
- **Method**: `POST`
- **Auth Required**: Yes
- **Role Required**: ROLE_USER

**Request Body**:
```json
{
    "userId": "user_email@example.com"
}
```

**Success Response**:
- **Code**: 200 OK
- **Content**: Array of course objects

**Error Response**:
- **Code**: 500 Internal Server Error
- **Content**: `{"message": "Error fetching courses: <error_message>"}`

## Error Handling

The API uses standard HTTP response codes:
- 200: Success
- 400: Bad Request
- 401: Unauthorized
- 403: Forbidden
- 404: Not Found
- 500: Internal Server Error

Error responses include a message field with details about the error.

## Authentication Notes

1. All protected endpoints require a valid JWT token
2. Token must be included in the Authorization header
3. Users can only access and modify their own courses
4. Invalid or expired tokens will receive a 401 Unauthorized response

## Best Practices

1. Always include the Authorization header for protected endpoints
2. Handle error responses appropriately in your client application
3. Validate input data before sending requests
4. Implement proper error handling for failed requests
5. Use HTTPS for all API communications