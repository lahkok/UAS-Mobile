package main

import (
	"database/sql"
	"errors"
	"fmt"
	"log"

	_ "github.com/go-sql-driver/mysql"
	"golang.org/x/crypto/bcrypt"
)

type User struct {
	UserID   int    `json:"user_id"`
	Username string `json:"username"`
	Password string `json:"password"`
	Name     string `json:"name"`
	Role     string `json:"role"`
	Email    string `json:"email"`
}

type Workout struct {
	Id       int    `json:"id"`
	UserId   int    `json:"user_id"`
	Type     string `json:"type"`
	Duration int    `json:"duration"`
	Notes    string `json:"notes"`
	Date     string `json:"date"`
}

var db *sql.DB

func initDB() {
	var err error
	// Replace the connection details with your MySQL server information
	db, err = sql.Open("mysql", "root:admin@/mencoba")
	if err != nil {
		fmt.Println("Failed to connect to MySQL:", err)
		return
	}

	// defer db.Close()

	fmt.Println("Connected to MySQL!")
}

func createUser(user User) error {
	log.Printf("Creating user: %v", user)
	if db == nil {
		return errors.New("database connection not established")
	}

	// Test the connection
	err := db.Ping()
	if err != nil {
		fmt.Println("Failed to ping MySQL:", err)
		return err
	}

	if user.Username == "" {
		return errors.New("username is empty")
	} else if user.Password == "" {
		return errors.New("password is empty")
	} else if user.Name == "" {
		return errors.New("name is empty")
	} else if user.Email == "" {
		return errors.New("email is empty")
	}

	var existingUser User
	err = db.QueryRow("SELECT * FROM users WHERE username = ?", user.Username).Scan(&existingUser.UserID, &existingUser.Username, &existingUser.Password, &existingUser.Name, &existingUser.Role)
	if err != sql.ErrNoRows {
		if err != nil {
			log.Printf("Error checking for existing user: %v", err)
			return err
		}
		// If there's no error, that means a user with the given username already exists
		return errors.New("user already exists")
	}

	// Hash the password
	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(user.Password), bcrypt.DefaultCost)
	if err != nil {
		log.Printf("Error hashing password: %v", err)
		return err
	}

	// Prepare the SQL statement
	stmt, err := db.Prepare("INSERT INTO users (username, password, name, role, email) VALUES (?, ?, ?, ?, ?)")
	if err != nil {
		log.Printf("Error preparing SQL statement: %v", err)
		return err
	}
	defer stmt.Close()

	// Execute the SQL statement
	_, err = stmt.Exec(user.Username, string(hashedPassword), user.Name, "user", user.Email)
	if err != nil {
		log.Printf("Error executing SQL statement: %v", err)
		return err
	}

	return nil
}

func getUser(username string) (User, error) {
	var user User

	// Prepare the SQL statement
	stmt, err := db.Prepare("SELECT * FROM users WHERE username = ?")
	if err != nil {
		return user, err
	}
	defer stmt.Close()

	// Execute the SQL statement and scan the result into the user struct
	err = stmt.QueryRow(username).Scan(&user.UserID, &user.Username, &user.Password, &user.Name, &user.Role, &user.Email)
	if err != nil {
		return user, err
	}

	return user, nil
}

func CreateWorkout(userId int, workoutType string, duration int, notes string, date string) (Workout, error) {
	tx, err := db.Begin()
	if err != nil {
		return Workout{}, err
	}

	defer func() {
		if p := recover(); p != nil {
			tx.Rollback()
			panic(p) // re-throw panic after Rollback
		} else if err != nil {
			tx.Rollback()
		} else {
			err = tx.Commit()
		}
	}()

	result, err := db.Exec("INSERT INTO workouts (user_id, type, duration, notes, date) VALUES (?, ?, ?, ?, ?)", userId, workoutType, duration, notes, date)
	if err != nil {
		log.Printf("Error executing SQL query: %v", err)
		return Workout{}, err
	}

	rowsAffected, err := result.RowsAffected()
	if err != nil {
		log.Printf("Error getting RowsAffected: %v", err)
		return Workout{}, err
	}
	if rowsAffected == 0 {
		log.Println("No rows were inserted")
		return Workout{}, errors.New("no rows were inserted")
	}

	// Get the ID of the last inserted row
	id, err := result.LastInsertId()
	if err != nil {
		log.Printf("Error getting LastInsertId: %v", err)
		return Workout{}, err
	}

	log.Printf("Inserted workout with ID: %d", id)

	return Workout{
		Id:       int(id),
		UserId:   userId,
		Type:     workoutType,
		Duration: duration,
		Notes:    notes,
		Date:     date,
	}, nil
}

func GetWorkouts(userId int) ([]Workout, error) {
	rows, err := db.Query("SELECT * FROM workouts WHERE user_id = ?", userId)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	workouts := []Workout{}
	for rows.Next() {
		var w Workout
		if err := rows.Scan(&w.Id, &w.UserId, &w.Type, &w.Duration, &w.Notes, &w.Date); err != nil {
			return nil, err
		}
		workouts = append(workouts, w)
	}

	return workouts, nil
}

func GetWorkoutById(Id int) (Workout, error) {
	row := db.QueryRow("SELECT * FROM workouts WHERE id = ?", Id)

	var w Workout
	if err := row.Scan(&w.Id, &w.UserId, &w.Type, &w.Duration, &w.Notes, &w.Date); err != nil {
		if err == sql.ErrNoRows {
			// There were no rows, but otherwise no error occurred
			return Workout{}, nil
		}
		return Workout{}, err
	}

	return w, nil
}

func UpdateWorkout(id int, workoutType string, duration int, notes string, date string) error {
	_, err := db.Exec("UPDATE workouts SET type = ?, duration = ?, notes = ?, date = ? WHERE id = ?", workoutType, duration, notes, date, id)
	return err
}

func DeleteWorkout(id int) error {
	_, err := db.Exec("DELETE FROM workouts WHERE id = ?", id)
	return err
}
