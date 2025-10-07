<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json");

// Connect to MySQL
$conn = new mysqli("localhost", "root", "", "renalguard", 3306);
if ($conn->connect_error) {
    echo json_encode(["success" => false, "message" => "Database connection failed"]);
    exit();
}

// Get raw POST body
$rawData = file_get_contents("php://input");
$data = json_decode($rawData, true);

// Get email from JSON
$email = $data['email'] ?? '';

if (empty($email)) {
    echo json_encode(["success" => false, "message" => "Email is required in JSON body"]);
    $conn->close();
    exit();
}

// Fetch patient details, ensure role is 'patient'
$stmt = $conn->prepare("SELECT name, email, phone, password FROM users WHERE email = ? ");
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    $row = $result->fetch_assoc();
    echo json_encode(["success" => true, "data" => $row]);
} else {
    echo json_encode(["success" => false, "message" => "Patient not found"]);
}

$stmt->close();
$conn->close();
?>
