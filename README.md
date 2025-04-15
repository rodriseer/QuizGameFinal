# Quiz Game

# Project Description
QuizGameFinal is a simple quiz game application built using Java Swing for the GUI and MySQL for the database. Users can create their own quizzes, select categories, and answer questions. With this project I hope I can help fellow students to study for their upcoming exams/quizzes by letting the user define their own questions and answers.

# Features
- Create Quizzes
- Play Quizzes
- Score and chances Tracking
- Delete database data to start fresh
- Database Integration

# Installation
Java Development Kit (JDK) 8
MySQL Server
- Easy steps for installation:
  1. Download repo
  2. Download the required SQL connector, which can be found here: https://dev.mysql.com/downloads/file/?id=530070
  3. Ensure MySQL is running on your machine.
  4. Create a database named quiz_gui_db.
  5. Import the provided SQL schema and data (if any) to set up your tables.
  6. Update the JDBC class in the database package with your MySQL credentials.
  7. Open the project in your preferred IDE (I've used IntelliJ IDEA to develop this project).
  8. Compile and run the App.java file to start the application.

# Running the Application
Launch the Application:
Upon running the App.java class, and assuming you have all the neccessary files, the title screen will appear.

Create a Quiz:
Click "Create a Question" to create a new quiz.
Enter your question, answers, and category.
Submit the question to save it to the database.

Play a Quiz:
From the title screen, choose a category and the number of questions.
Answer the questions as they appear and see your score at the end.

# Technologies Used
Java: For core programming logic and GUI development.
Swing: For building the graphical user interface.
MySQL: For database management.
Git for update and documentations control
