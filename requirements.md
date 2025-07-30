# Flag Quiz Game - Comprehensive Requirements Document

## 1. Project Overview

### 1.1 Project Description
The Flag Quiz Game is an interactive web-based application built with Java Spring Boot that challenges users to identify countries based on their flags. The application leverages Google's Gemini AI API to provide intelligent question answering, hint generation, and answer validation, creating an engaging educational experience.

### 1.2 Project Goals
- Create an educational and entertaining flag identification game
- Demonstrate integration with Google Gemini AI API
- Provide a responsive web interface using modern web technologies
- Implement session-based game state management
- Support both Japanese and English language inputs/outputs

### 1.3 Target Audience
- Students learning world geography
- Educational institutions
- General users interested in world knowledge
- Web developers learning AI API integration

## 2. Game Rules and Mechanics

### 2.1 Core Game Rules
- **Objective**: Identify the country name by looking at its flag
- **Answer Attempts**: Maximum 2 incorrect answers allowed
- **Questions**: Up to 10 Yes/No questions can be asked
- **Hints**: 3 different types of hints available (one per type)
- **Language Support**: Accept answers in both Japanese and English
- **Session Duration**: 30 minutes timeout for inactive sessions

### 2.2 Game Flow
1. **Game Initialization**: System randomly selects a country and displays its flag
2. **Information Gathering Phase**: 
   - Player can ask Yes/No questions (up to 10)
   - Player can request hints (up to 3 different types)
3. **Answer Submission**: Player submits country name guess
4. **Result Evaluation**: System validates answer and provides feedback
5. **Game Conclusion**: Game ends on correct answer or exhausted attempts

### 2.3 Hint System
- **主食 (Staple Food)**: Information about the country's main dietary staples
- **面積 (Area)**: Comparison of country's area relative to Japan
- **言語 (Language)**: Information about official languages spoken
- Each hint type can only be used once per game session
- Country name must not be revealed in hint responses

## 3. Technical Architecture

### 3.1 Technology Stack
- **Backend Framework**: Spring Boot 3.2.0
- **Java Version**: Java 17
- **Template Engine**: Thymeleaf
- **HTTP Client**: OkHttp 4.12.0
- **Build Tool**: Maven
- **AI Integration**: Google Gemini 2.0 Flash Experimental API
- **Configuration Management**: dotenv-java 3.0.0
- **Session Management**: Spring Session (in-memory)

### 3.2 Application Architecture
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Presentation  │    │    Business     │    │   External      │
│     Layer       │    │     Layer       │    │   Services      │
├─────────────────┤    ├─────────────────┤    ├─────────────────┤
│ GameController  │ -> │ GameService     │ -> │ GeminiService   │
│ ErrorController │    │                 │    │                 │
├─────────────────┤    ├─────────────────┤    ├─────────────────┤
│ Thymeleaf       │    │ GameState       │    │ Gemini API      │
│ Templates       │    │ Model           │    │ flagcdn.com     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### 3.3 Package Structure
```
com.example.flagquiz/
├── FlagQuizApplication.java        # Main application entry point
├── controller/
│   ├── GameController.java         # Game logic endpoints
│   └── CustomErrorController.java  # Error handling
├── service/
│   ├── GameService.java           # Game business logic
│   └── GeminiService.java         # AI API integration
├── model/
│   └── GameState.java             # Game state data model
└── config/
    └── DotEnvConfig.java          # Environment configuration
```

## 4. API Specifications

### 4.1 Web Endpoints

#### 4.1.1 Game Interface
- **GET /** - Main game interface
  - Returns: Game page with current state
  - Template: `index.html`
  - Session: Retrieves `gameState` if exists

#### 4.1.2 Game Actions
- **POST /new_game** - Initialize new game session
  - Action: Creates new GameState, selects random country
  - Redirect: Back to main page with success message
  - Error Handling: Shows error message on failure

- **POST /ask_question** - Submit Yes/No question
  - Parameters: `question` (String)
  - Validation: Question format and remaining attempts
  - Response: AI-generated Yes/No answer
  - Side Effects: Decrements question count, logs interaction

- **POST /get_hint** - Request hint
  - Parameters: `hintType` (String: "主食", "面積", "言語")
  - Validation: Hint availability and type validity
  - Response: AI-generated hint without country name
  - Side Effects: Marks hint as used, decrements hint count

- **POST /submit_answer** - Submit country guess
  - Parameters: `answer` (String)
  - Validation: Answer format and remaining attempts
  - Response: Correct/incorrect feedback with remaining attempts
  - Side Effects: Decrements answer count on incorrect guess

### 4.2 Gemini API Integration

#### 4.2.1 API Configuration
- **Base URL**: `https://generativelanguage.googleapis.com/v1beta/models/`
- **Model**: `gemini-2.0-flash-exp`
- **Authentication**: API Key via query parameter
- **Content Type**: `application/json`

#### 4.2.2 API Methods

##### Country Generation
```json
{
  "contents": [{
    "parts": [{
      "text": "世界の国連加盟国から1つの国をランダムに選んで、以下の形式で回答してください：\n国名（英語）: [英語の正式名称]\n国名（日本語）: [日本語の国名]\n国旗URL: https://flagcdn.com/w640/[2文字の国コード].png"
    }]
  }]
}
```

##### Question Validation
```json
{
  "contents": [{
    "parts": [{
      "text": "質問: \"[USER_QUESTION]\"\n対象国: [COUNTRY]\n\nこの質問が以下の条件を満たしているかYes/Noで回答してください：\n1. Yes/No形式で回答できる質問である\n2. 答えに直結しない質問である"
    }]
  }]
}
```

##### Question Answering
```json
{
  "contents": [{
    "parts": [{
      "text": "質問: \"[USER_QUESTION]\"\n対象国: [COUNTRY]\n\nこの国について上記の質問にYesまたはNoで回答してください。"
    }]
  }]
}
```

##### Hint Generation
```json
{
  "contents": [{
    "parts": [{
      "text": "[COUNTRY]の[HINT_TYPE]について教えてください。国名は絶対に言及しないでください。"
    }]
  }]
}
```

##### Answer Validation
```json
{
  "contents": [{
    "parts": [{
      "text": "ユーザーの回答: \"[USER_ANSWER]\"\n正解の国（英語）: [ENGLISH_NAME]\n正解の国（日本語）: [JAPANESE_NAME]\n\nユーザーの回答が正解かどうかを判定してください。正解の場合は「正解」、不正解の場合は「不正解」のみ回答してください。"
    }]
  }]
}
```

## 5. Data Models

### 5.1 GameState Model
```java
public class GameState {
    private String currentCountryEnglish;     // Country name in English
    private String currentCountryJapanese;    // Country name in Japanese
    private String countryFlag;               // Flag image URL
    private int answersLeft = 2;              // Remaining answer attempts
    private int questionsLeft = 10;           // Remaining questions
    private int hintsLeft = 3;                // Remaining hints
    private List<String> hintsUsed;           // Used hint types
    private List<String> gameLog;             // Question-answer history
    // ... getters and setters
}
```

### 5.2 Session Management
- **Storage**: HTTP Session (in-memory)
- **Key**: `gameState`
- **Timeout**: 30 minutes
- **Scope**: Single browser session

## 6. External Service Dependencies

### 6.1 Google Gemini API
- **Purpose**: AI-powered game logic
- **Authentication**: API Key
- **Rate Limits**: As per Google's terms
- **Fallback**: Basic hardcoded responses when API unavailable
- **Error Handling**: Graceful degradation with fallback responses

### 6.2 Flag Image Service
- **Provider**: flagcdn.com
- **Format**: PNG images
- **Resolution**: 640px width (w640)
- **URL Pattern**: `https://flagcdn.com/w640/[country-code].png`
- **Availability**: Public CDN, no authentication required

## 7. Configuration Requirements

### 7.1 Environment Variables
```bash
# Required
GEMINI_API_KEY=your-actual-gemini-api-key-here

# Optional (with defaults)
GEMINI_MODEL=gemini-2.0-flash-exp
SERVER_PORT=8080
```

### 7.2 Application Properties
```properties
# Application
spring.application.name=flag-quiz-jv
server.port=8080

# Session Management
server.servlet.session.timeout=30m
server.servlet.session.cookie.max-age=1800

# Thymeleaf Template Engine
spring.thymeleaf.cache=false
spring.thymeleaf.mode=HTML
spring.thymeleaf.encoding=UTF-8

# Character Encoding
server.servlet.encoding.charset=UTF-8
server.servlet.encoding.enabled=true
server.servlet.encoding.force=true

# Logging
logging.level.com.example.flagquiz=DEBUG

# Error Handling
server.error.include-message=always
server.error.include-binding-errors=always
server.error.include-stacktrace=always

# Gemini API
gemini.api.key=${GEMINI_API_KEY}
gemini.model=gemini-2.0-flash-exp
```

## 8. User Interface Requirements

### 8.1 Layout Structure
- **Responsive Design**: Mobile and desktop compatible
- **Two-Column Layout**: 
  - Left: Flag display, game status, hints
  - Right: Questions, answers, interaction forms
- **Color Scheme**: Modern, accessible color palette
- **Typography**: Clear, readable fonts with proper contrast

### 8.2 UI Components

#### 8.2.1 Game Status Panel
- Display remaining attempts (answers, questions, hints)
- Visual progress indicators
- Clear status messages and feedback

#### 8.2.2 Flag Display
- Large, clear flag image
- Responsive sizing
- Alt text for accessibility

#### 8.2.3 Interaction Forms
- Question input with placeholder text
- Hint request buttons with disable states
- Answer submission form
- Form validation and error display

#### 8.2.4 Message System
- Success messages (green)
- Error messages (red)
- Info messages (blue)
- Temporary flash messages

### 8.3 User Experience Flow
1. **Landing**: Clear "Start New Game" button
2. **Game Active**: All controls accessible, status visible
3. **Feedback**: Immediate response to all actions
4. **Game End**: Clear success/failure indication
5. **Reset**: Easy restart functionality

## 9. Security Requirements

### 9.1 Input Validation
- **Question Text**: Non-empty, reasonable length limits
- **Answer Text**: Non-empty, trimmed whitespace
- **Hint Type**: Validated against allowed values
- **XSS Prevention**: Thymeleaf auto-escaping enabled

### 9.2 API Security
- **API Keys**: Stored in environment variables
- **Request Validation**: All Gemini API requests validated
- **Error Handling**: No sensitive information in error messages
- **Rate Limiting**: Respect API provider limits

### 9.3 Session Security
- **Session Timeout**: 30-minute inactivity timeout
- **Session Isolation**: Game state per session
- **No Persistent Storage**: Game data not stored permanently

## 10. Performance Requirements

### 10.1 Response Times
- **Page Load**: < 2 seconds initial load
- **Game Actions**: < 5 seconds for API-dependent actions
- **Static Content**: < 500ms for cached resources

### 10.2 Scalability
- **Concurrent Users**: Support for moderate concurrent usage
- **Memory Usage**: Efficient session management
- **API Efficiency**: Minimize unnecessary API calls

### 10.3 Reliability
- **Uptime**: 99% availability during operation hours
- **Error Recovery**: Graceful handling of API failures
- **Fallback Mechanisms**: Basic functionality without API

## 11. Testing Requirements

### 11.1 Unit Testing
- **Service Layer**: GameService and GeminiService methods
- **Model Validation**: GameState data integrity
- **Utility Functions**: Parsing and validation logic

### 11.2 Integration Testing
- **Controller Endpoints**: All HTTP endpoints
- **Session Management**: Game state persistence
- **Template Rendering**: Thymeleaf template processing

### 11.3 System Testing
- **Complete Game Flow**: Full game scenarios
- **Error Scenarios**: API failures, invalid inputs
- **Browser Compatibility**: Major browsers support

## 12. Deployment Requirements

### 12.1 Runtime Environment
- **Java Runtime**: Java 17 or higher
- **Application Server**: Embedded Tomcat (Spring Boot)
- **Operating System**: Cross-platform (Windows, Linux, macOS)
- **Memory**: Minimum 512MB RAM

### 12.2 Build Requirements
- **Build Tool**: Apache Maven 3.6+
- **Dependencies**: All specified in pom.xml
- **Build Command**: `mvn clean package`
- **Run Command**: `mvn spring-boot:run` or `java -jar target/flag-quiz-jv-0.0.1-SNAPSHOT.jar`

### 12.3 Environment Setup
```bash
# 1. Clone/extract project
# 2. Set environment variables
echo "GEMINI_API_KEY=your-key-here" > .env

# 3. Build and run
mvn clean package
mvn spring-boot:run

# 4. Access application
# http://localhost:8080
```

### 12.4 Production Considerations
- **Logging**: Configure appropriate log levels
- **Monitoring**: Health check endpoints
- **Configuration**: Externalized configuration files
- **Security**: HTTPS in production environment

## 13. Maintenance and Support

### 13.1 Code Maintenance
- **Code Style**: Follow Java naming conventions
- **Documentation**: Javadoc for all public methods
- **Version Control**: Git with meaningful commit messages
- **Dependencies**: Regular security updates

### 13.2 Operational Support
- **Log Monitoring**: Error and access logs
- **API Monitoring**: Gemini API usage and limits
- **Performance Monitoring**: Response times and errors
- **User Feedback**: Collection and analysis

### 13.3 Future Enhancements
- **Database Integration**: Persistent game history
- **User Authentication**: Personal game statistics
- **Multiplayer Mode**: Competitive gameplay
- **Additional Languages**: Multi-language support
- **Mobile App**: Native mobile applications

## 14. Quality Assurance

### 14.1 Code Quality
- **Static Analysis**: Use tools like SonarQube
- **Code Coverage**: Minimum 80% test coverage
- **Code Review**: Peer review for all changes
- **Style Guide**: Consistent coding standards

### 14.2 User Experience Quality
- **Accessibility**: WCAG 2.1 AA compliance
- **Usability Testing**: Regular user feedback
- **Performance Testing**: Load and stress testing
- **Cross-browser Testing**: Major browsers compatibility

### 14.3 Documentation Quality
- **API Documentation**: Complete endpoint documentation
- **User Guide**: Clear usage instructions
- **Developer Guide**: Setup and development instructions
- **Troubleshooting**: Common issues and solutions

---

## Appendix A: API Response Examples

### A.1 Gemini Country Generation Response
```
国名（英語）: France
国名（日本語）: フランス
国旗URL: https://flagcdn.com/w640/fr.png
```

### A.2 Gemini Question Validation Response
```
Yes
```

### A.3 Gemini Question Answer Response
```
No
```

### A.4 Gemini Hint Response
```
主食として小麦が多く使われ、パンやパスタが食卓の中心となっています。チーズとワインも食文化の重要な部分を占めています。
```

## Appendix B: Error Handling Matrix

| Error Type | User Message | System Action | Recovery |
|------------|--------------|---------------|----------|
| API Failure | "サービスが一時的に利用できません" | Use fallback responses | Retry on next request |
| Invalid Question | "質問はYes/No形式で答えられる内容にしてください" | Show error message | Allow new question |
| No Questions Left | "質問回数が残っていません" | Disable question form | Game continues |
| No Hints Left | "ヒントは3回まで使用できます" | Disable hint buttons | Game continues |
| No Answers Left | "回答回数が残っていません" | Show game over | Offer new game |
| Session Timeout | "セッションが切れました" | Clear game state | Redirect to start |

---

*This requirements document serves as the comprehensive specification for the Flag Quiz Game Java Spring Boot application. It should be updated as the project evolves and new requirements are identified.*