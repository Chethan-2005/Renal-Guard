<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
header("Content-Type: application/json");

// Connect to MySQL
$conn = new mysqli("localhost", "root", "", "renalguard", 3306);
if ($conn->connect_error) {
    echo json_encode(["success" => false, "message" => "Database connection failed"]);
    exit();
}

// First try to read email from GET/POST
$email = $_GET['email'] ?? $_POST['email'] ?? null;

// If not found, try to decode JSON body
if (!$email) {
    $input = json_decode(file_get_contents("php://input"), true);
    $email = $input['email'] ?? null;
}

if (!$email) {
    echo json_encode(["success" => false, "message" => "Missing email"]);
    $conn->close();
    exit();
}

// Fetch doctor details
$sql = "SELECT name, phone, email, specialization, education, location 
        FROM users 
        WHERE email = ? 
        LIMIT 1";

$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();

if ($row = $result->fetch_assoc()) {
    echo json_encode([
        "success" => true,
        "data" => [
            "name"           => $row['name'],
            "phone"         => $row['phone'],
            "email"          => $row['email'],
            "specialization" => $row['specialization'],
            "education"      => $row['education'],
            "location"       => $row['location']
        ]
    ]);
} else {
    echo json_encode([
        "success" => false,
        "message" => "Doctor not found"
    ]);
}

$stmt->close();
$conn->close();
?>
