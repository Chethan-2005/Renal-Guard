<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
header("Content-Type: application/json");

// Decode input
$data = json_decode(file_get_contents("php://input"));

$conn = new mysqli("localhost", "root", "", "renalguard", 3306);
if ($conn->connect_error) {
    echo json_encode(["success" => false, "message" => "Database connection failed"]);
    exit();
}

// Read and sanitize data
$email = $conn->real_escape_string($data->email ?? '');
$password = $conn->real_escape_string($data->password ?? '');
$role = $conn->real_escape_string($data->role ?? '');

// Validate input
if (!$email || !$password || !$role) {
    echo json_encode(["success" => false, "message" => "All fields are required"]);
    $conn->close();
    exit();
}

if ($role === "admin") {
    // Admin login (username = email)
    $sql = "SELECT * FROM admin WHERE username = '$email'";
    $result = $conn->query($sql);

    if ($result && $result->num_rows === 1) {
        $admin = $result->fetch_assoc();

        if ($password === $admin['password']) {
            echo json_encode([
                "success" => true,
                "message" => "Admin login successful",
                "user" => [
                    "email" => $admin["username"],
                    "role" => "admin"
                ]
            ]);
            $conn->close();
            exit();
        } else {
            echo json_encode(["success" => false, "message" => "Incorrect password"]);
            $conn->close();
            exit();
        }
    } else {
        echo json_encode(["success" => false, "message" => "Admin not found"]);
        $conn->close();
        exit();
    }
} else {
    // Doctor or Patient login from users table
    $sql = "SELECT * FROM users WHERE email = '$email' AND role = '$role'";
    $result = $conn->query($sql);

    if ($result && $result->num_rows === 1) {
        $user = $result->fetch_assoc();

        if ($password === $user['password']) {
            echo json_encode([
                "success" => true,
                "message" => "Login successful",
                "user" => [
                    "id" => $user["id"],
                    "name" => $user["name"],
                    "email" => $user["email"],
                    "phone" => $user["phone"],
                    "role" => $user["role"]
                ]
            ]);
            $conn->close();
            exit();
        } else {
            echo json_encode(["success" => false, "message" => "Incorrect password"]);
            $conn->close();
            exit();
        }
    } else {
        echo json_encode(["success" => false, "message" => "User not found or role mismatch"]);
        $conn->close();
        exit();
    }
}
?>
