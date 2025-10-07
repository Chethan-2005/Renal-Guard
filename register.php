<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
header("Content-Type: application/json");

$data = json_decode(file_get_contents("php://input"));

$conn = new mysqli("localhost", "root", "", "renalguard", 3306);
if ($conn->connect_error) {
    die(json_encode(["success" => false, "message" => "Database connection failed"]));
}

// Extract and sanitize input
$name = $conn->real_escape_string($data->name ?? '');
$email = $conn->real_escape_string($data->email ?? '');
$phone = $conn->real_escape_string($data->phone ?? '');
$password = $conn->real_escape_string($data->password ?? '');
$role = $conn->real_escape_string($data->role ?? '');

// 1. Validate email format
if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    echo json_encode(["success" => false, "message" => "Invalid email format"]);
    exit();
}

// 2. Check for empty fields
if (!$name || !$email || !$phone || !$password || !$role) {
    echo json_encode(["success" => false, "message" => "All fields are required"]);
    exit();
}

// 3. Check if email already exists
$checkEmail = $conn->query("SELECT id FROM users WHERE email = '$email'");
if ($checkEmail->num_rows > 0) {
    echo json_encode(["success" => false, "message" => "Account already exists, please login"]);
    exit();
}

// 4. Get next available ID (reuse deleted ones)
$result = $conn->query("
    SELECT MIN(t1.id + 1) AS next_id
    FROM users t1
    LEFT JOIN users t2 ON t1.id + 1 = t2.id
    WHERE t2.id IS NULL
");
$row = $result->fetch_assoc();
$nextId = $row['next_id'] ?? 1;

if (!$nextId || $nextId == 0) {
    $nextId = 1;
}

// 5. Insert the new user
$sql = "INSERT INTO users (id, name, email, phone, password, role)
        VALUES ('$nextId', '$name', '$email', '$phone', '$password', '$role')";

if ($conn->query($sql) === TRUE) {
    echo json_encode(["success" => true, "message" => "Registration successful"]);
} else {
    echo json_encode(["success" => false, "message" => "Error: " . $conn->error]);
}

$conn->close();
?>
