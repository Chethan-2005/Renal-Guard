<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
header("Content-Type: application/json");

$conn = new mysqli("localhost", "root", "", "renalguard", 3306);
if ($conn->connect_error) {
    echo json_encode(["success" => false, "message" => "Database connection failed"]);
    exit();
}

$patient_email = $_GET['email'] ?? $_POST['email'] ?? null;

if (!$patient_email) {
    $input = json_decode(file_get_contents("php://input"), true);
    $patient_email = $input['email'] ?? null;
}

if (!$patient_email) {
    echo json_encode(["success" => false, "message" => "Missing patient email"]);
    $conn->close();
    exit();
}

$sql = "SELECT u.name, u.specialization, u.education, u.location, u.phone, u.email 
        FROM users u
        JOIN patients p ON p.doctor_email = u.email
        WHERE p.email = ?
        LIMIT 1";

$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $patient_email);
$stmt->execute();
$result = $stmt->get_result();

if ($row = $result->fetch_assoc()) {
    echo json_encode([
        "success" => true,
        "doctor" => [
            "name" => $row['name'],
            "specialization" => $row['specialization'],
            "education" => $row['education'],
            "location" => $row['location'],
            "phone" => $row['phone'],
            "email" => $row['email']
        ]
    ]);
} else {
    echo json_encode(["success" => false, "message" => "No doctor assigned"]);
}

$stmt->close();
$conn->close();
?>
