package main

import (
	"encoding/json"
	"errors"
	"log"
	"net/http"
	"net/mail"
	"strconv"
	"strings"
	"time"

	"github.com/dgrijalva/jwt-go"
	"golang.org/x/crypto/bcrypt"
)

type LoginResponse struct {
	Token  string `json:"token"`
	UserId int    `json:"userId"`
}

type RegisterResponse struct {
	Message string `json:"message"`
	UserID  int    `json:"user_id"`
}

func validateEmail(email string) error {
	_, err := mail.ParseAddress(email)
	return err
}

func validateWorkoutData(workout Workout) error {
	if workout.UserId <= 0 {
		return errors.New("invalid user ID")
	}

	// Add more validation as needed for other fields

	return nil
}

func generateToken(UserID int, role string) (string, error) {
	// Create a new token object
	token := jwt.New(jwt.SigningMethodHS256)

	// Create a map to store our claims
	claims := token.Claims.(jwt.MapClaims)

	// Set token claims
	claims["userId"] = strconv.Itoa(UserID)
	claims["role"] = role
	claims["exp"] = time.Now().Add(time.Hour * 72).Unix()

	// Sign the token with our secret
	tokenString, err := token.SignedString([]byte("sungguh_sangat_rahasia"))

	if err != nil {
		return "", err
	}

	return tokenString, nil
}

func handleRequest() {
	// Create a new serve mux and register the handlers
	mux := http.NewServeMux()

	// Register the handler functions for each route
	mux.HandleFunc("/login", loginHandler)
	mux.HandleFunc("/register", registerHandler)
	mux.HandleFunc("/profile", profileHandler)
	mux.HandleFunc("/logout", logoutHandler)
	mux.HandleFunc("/create_workout", createWorkoutsHandler)
	mux.HandleFunc("/get_workouts/", getWorkoutsHandler)
	mux.HandleFunc("/get_single_workout/", getSingleWorkoutHandler)
	mux.HandleFunc("/update_workout/", updateWorkoutsHandler)
	mux.HandleFunc("/delete_workout/", deleteWorkoutsHandler)

	// Start the server on port 8080
	http.ListenAndServe(":8080", mux)
}

func loginHandler(w http.ResponseWriter, r *http.Request) {
	var user User

	// Decode the request body into the user struct
	err := json.NewDecoder(r.Body).Decode(&user)
	if err != nil {
		http.Error(w, "Bad Request", http.StatusBadRequest)
		return
	}

	// Get the user from the database
	dbUser, err := getUser(user.Username)
	if err != nil {
		http.Error(w, err.Error(), http.StatusNotFound)
		return
	}

	// Compare the hashed password in the database with the password from the request
	err = bcrypt.CompareHashAndPassword([]byte(dbUser.Password), []byte(user.Password))
	if err != nil {
		http.Error(w, "Invalid password", http.StatusUnauthorized)
		return
	}

	// If the passwords match, the login is successful
	token, err := generateToken(user.UserID, user.Role)
	if err != nil {
		http.Error(w, "Error generating token", http.StatusInternalServerError)
		return
	}

	// Create a LoginResponse object
	loginResponse := LoginResponse{Token: token, UserId: dbUser.UserID}

	// Convert the LoginResponse object to JSON
	jsonResponse, err := json.Marshal(loginResponse)
	if err != nil {
		http.Error(w, "Error creating JSON response", http.StatusInternalServerError)
		return
	}

	// Set the content type to application/json
	w.Header().Set("Content-Type", "application/json")

	// Write the JSON response
	w.Write(jsonResponse)
}

func registerHandler(w http.ResponseWriter, r *http.Request) {
	log.Printf("Handling register request")
	var user User

	// Decode the JSON request body into the user struct
	err := json.NewDecoder(r.Body).Decode(&user)
	if err != nil {
		log.Printf("Error decoding request body: %v", err)
		// If there is something wrong with the request body, return a 400 status
		w.WriteHeader(http.StatusBadRequest)
		return
	}

	// Validate the email
	if err := validateEmail(user.Email); err != nil {
		errorResponse := map[string]string{"error": "Invalid email format"}
		jsonResponse, _ := json.Marshal(errorResponse)
		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusBadRequest)
		w.Write(jsonResponse)
		return
	}

	// Create the user in the database
	err = createUser(user)
	if err != nil {
		var status int
		var message string

		switch err.Error() {
		case "user already exists":
			status = http.StatusConflict
			message = "User already exists"
		case "username is empty":
			status = http.StatusBadRequest
			message = "Username is empty"
		case "password is empty":
			status = http.StatusBadRequest
			message = "Password is empty"
		case "name is empty":
			status = http.StatusBadRequest
			message = "Name is empty"
		case "email is empty":
			status = http.StatusBadRequest
			message = "Email is empty"
		default:
			status = http.StatusInternalServerError
			message = "Internal server error"
		}

		// Create an error response object
		errorResponse := map[string]string{"error": message}

		// Convert the error response object to JSON
		jsonResponse, err := json.Marshal(errorResponse)
		if err != nil {
			http.Error(w, "Error creating JSON response", http.StatusInternalServerError)
			return
		}

		// Set the content type to application/json
		w.Header().Set("Content-Type", "application/json")

		// Set the status code
		w.WriteHeader(status)

		// Write the JSON response
		w.Write(jsonResponse)

		return
	}

	// If we've reached this point, that means the user has been successfully created
	registerResponse := RegisterResponse{Message: "Successfully registered!", UserID: user.UserID} // Replace userId with the actual user's ID

	jsonResponse, err := json.Marshal(registerResponse)
	if err != nil {
		http.Error(w, "Error creating JSON response", http.StatusInternalServerError)
		return
	}

	// Set the content type to application/json
	w.Header().Set("Content-Type", "application/json")

	// Write the JSON response
	w.Write(jsonResponse)
}

func profileHandler(w http.ResponseWriter, r *http.Request) {
}

func logoutHandler(w http.ResponseWriter, r *http.Request) {
}

func createWorkoutsHandler(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		http.Error(w, "Invalid request method", http.StatusMethodNotAllowed)
		return
	}

	var workout Workout

	if err := json.NewDecoder(r.Body).Decode(&workout); err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	// Validate the input data
	if err := validateWorkoutData(workout); err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	// Log the received data
	log.Printf("Received Workout Data: %+v\n", workout)

	// Attempt to create the workout
	createdWorkout, err := CreateWorkout(workout.UserId, workout.Type, workout.Duration, workout.Notes, workout.Date)
	if err != nil {
		log.Printf("Error creating workout: %v\n", err)
		http.Error(w, "Internal Server Error", http.StatusInternalServerError)
		return
	}

	log.Printf("Created Workout: %+v\n", createdWorkout)

	w.WriteHeader(http.StatusCreated)
	json.NewEncoder(w).Encode(createdWorkout)
}

func getWorkoutsHandler(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodGet {
		http.Error(w, "Invalid request method", http.StatusMethodNotAllowed)
		return
	}

	pathSegments := strings.Split(r.URL.Path, "/")
	if len(pathSegments) != 3 {
		http.Error(w, "Invalid URL path", http.StatusBadRequest)
		return
	}

	userId, err := strconv.Atoi(pathSegments[2])
	if err != nil {
		http.Error(w, "Invalid user ID", http.StatusBadRequest)
		return
	}

	workouts, err := GetWorkouts(userId)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	json.NewEncoder(w).Encode(workouts)
}

func getSingleWorkoutHandler(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodGet {
		http.Error(w, "Invalid request method", http.StatusMethodNotAllowed)
		return
	}

	pathSegments := strings.Split(r.URL.Path, "/")
	if len(pathSegments) != 3 {
		http.Error(w, "Invalid URL path", http.StatusBadRequest)
		return
	}

	workoutId, err := strconv.Atoi(pathSegments[2])
	if err != nil {
		http.Error(w, "Invalid workout ID", http.StatusBadRequest)
		return
	}

	workout, err := GetWorkoutById(workoutId)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	json.NewEncoder(w).Encode(workout)
}

func updateWorkoutsHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Received a request to update a workout")

	if r.Method != http.MethodPut {
		http.Error(w, "Invalid request method", http.StatusMethodNotAllowed)
		return
	}

	pathSegments := strings.Split(r.URL.Path, "/")
	if len(pathSegments) != 3 {
		http.Error(w, "Invalid URL path", http.StatusBadRequest)
		return
	}

	id, err := strconv.Atoi(pathSegments[2])
	if err != nil {
		http.Error(w, "Invalid workout ID", http.StatusBadRequest)
		return
	}

	var workout Workout
	if err := json.NewDecoder(r.Body).Decode(&workout); err != nil {
		log.Printf("Failed to decode the request body: %v", err)
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	log.Printf("Updating workout with ID %d: %+v", id, workout)

	if err := UpdateWorkout(id, workout.Type, workout.Duration, workout.Notes, workout.Date); err != nil {
		log.Printf("Failed to update the workout: %v", err)
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	log.Println("Workout updated successfully")

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	json.NewEncoder(w).Encode(map[string]string{"message": "Workout updated successfully"})
}

func deleteWorkoutsHandler(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodDelete {
		http.Error(w, "Invalid request method", http.StatusMethodNotAllowed)
		return
	}

	pathSegments := strings.Split(r.URL.Path, "/")
	if len(pathSegments) != 3 {
		http.Error(w, "Invalid URL path", http.StatusBadRequest)
		return
	}

	id, err := strconv.Atoi(pathSegments[2])
	if err != nil {
		http.Error(w, "Invalid workout ID", http.StatusBadRequest)
		return
	}

	err = DeleteWorkout(id)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	w.WriteHeader(http.StatusOK)
	w.Write([]byte("Workout deleted successfully"))
}
