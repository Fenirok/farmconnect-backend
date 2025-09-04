# AI Microservice Integration for FarmConnect Backend

## Overview

This integration connects your Spring Boot backend with your AI microservice to validate product images before saving them to the database. The system ensures that the uploaded image matches the product name provided by the farmer.

## Flow Description

1. **Frontend (Flutter)** sends product details and image to the backend
2. **Backend** receives the request and forwards it to the AI microservice
3. **AI Microservice** validates the image using YOLO and EfficientNetB0
4. **Backend** receives the validation result and either saves the product or returns an error
5. **Frontend** displays success message or "add a right image" error

## New Endpoints

### 1. Product Creation with AI Validation
```
POST /api/farmers/{farmerId}/products/validate
```

**Request Parameters:**
- `name` (String): Product name
- `description` (String): Product description
- `price` (double): Product price
- `category` (String): Product category
- `farmName` (String): Farm name
- `weight` (double): Product weight
- `unit` (String): Weight unit (kg, lbs, etc.)
- `isOrganic` (boolean): Whether the product is organic
- `location` (String): Product location
- `image` (MultipartFile): Product image

**Response:**
```json
{
  "success": true/false,
  "message": "Success/error message",
  "product": {
    // Product object if successful
  },
  "aiValidation": {
    "accepted": true/false,
    "reason": "Validation reason",
    "yoloResults": [
      {
        "label": "apple",
        "confidence": 0.95
      }
    ]
  }
}
```

### 2. AI Service Health Check
```
GET /api/ai-service/health
```

This endpoint checks the health of your AI microservice at `http://localhost:8000/health`

## Configuration

### AI Microservice URL
Add this to your `application.properties`:
```properties
ai.microservice.url=http://localhost:8000
```

This is configured to match your AI microservice's actual endpoint.

## AI Microservice Expected Interface

Your AI microservice should expose an endpoint at `/verify` that accepts:

**Request:**
```json
{
  "product_name": "apple",
  "image": "base64_encoded_image_string"
}
```

**Response:**
```json
{
  "accepted": true,
  "reason": "accepted",
  "yolo": [
    {
      "label": "apple",
      "confidence": 0.95
    }
  ]
}
```

## Error Handling

The system handles various error scenarios:

1. **AI Service Unavailable**: Returns error message
2. **Image Validation Failed**: Returns detailed reason
3. **Invalid Image Format**: Returns appropriate error
4. **Network Issues**: Graceful degradation

## Frontend Integration

### Flutter Example
```dart
Future<void> addProductWithValidation() async {
  var request = http.MultipartRequest(
    'POST',
    Uri.parse('$baseUrl/api/farmers/$farmerId/products/validate'),
  );
  
  // Add product details
  request.fields['name'] = productName;
  request.fields['description'] = description;
  request.fields['price'] = price.toString();
  // ... other fields
  
  // Add image
  request.files.add(await http.MultipartFile.fromPath(
    'image',
    imagePath,
  ));
  
  var response = await request.send();
  var responseData = await response.stream.bytesToString();
  var jsonResponse = json.decode(responseData);
  
  if (jsonResponse['success']) {
    // Show success message
    showSuccessDialog('Product added successfully!');
  } else {
    // Show error message
    showErrorDialog('Please add a right image: ${jsonResponse['message']}');
  }
}
```

## Testing

1. **Start your AI microservice** on the configured URL
2. **Test the health endpoint**: `GET /api/ai-service/health`
3. **Test product validation**: Use the new endpoint with a valid product and image
4. **Test error scenarios**: Try with mismatched product name and image

## Security Considerations

1. **Image Size Limits**: Configured to handle up to 10MB images
2. **Request Size Limits**: Configured for 50MB total request size
3. **Input Validation**: All inputs are validated before processing
4. **Error Handling**: Sensitive information is not exposed in error messages

## Troubleshooting

### Common Issues

1. **AI Service Not Responding**
   - Check if the AI microservice is running
   - Verify the URL in `application.properties`
   - Check network connectivity

2. **Image Upload Failures**
   - Verify image format (JPEG, PNG, etc.)
   - Check image size (should be < 10MB)
   - Ensure proper multipart form data

3. **Validation Always Failing**
   - Check AI microservice logs
   - Verify the expected request/response format
   - Test the AI microservice independently

### Debug Endpoints

Use the health check endpoint to verify AI service connectivity:
```
GET /api/ai-service/health
```

## Future Enhancements

1. **Caching**: Cache validation results for similar images
2. **Batch Processing**: Handle multiple images at once
3. **Confidence Thresholds**: Configurable confidence levels
4. **Image Preprocessing**: Automatic image optimization
5. **Analytics**: Track validation success rates
