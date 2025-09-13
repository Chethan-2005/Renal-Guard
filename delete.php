<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json");

// Connect to MySQL
$conn = new mysqli("localhost", "root", "", "renalguard", 3306);

// Check connection
if ($conn->connect_error) {
    echo json_encode(["success" => false, "message" => "Database connection failed"]);
    exit();
}

// Get raw POST body
$rawData = file_get_contents("php://input");
$data = json_decode($rawData, true);

// Get patient_id from JSON
$patient_id = $data['patient_id'] ?? '';

// Validate input
if (empty($patient_id)) {
    echo json_encode(["success" => false, "message" => "Patient ID is required in JSON body"]);
    $conn->close();
    exit();
}

// Prepare and execute the SQL statement to prevent SQL injection
$stmt = $conn->prepare("DELETE FROM patients WHERE patient_id = ?");
$stmt->bind_param("s", $patient_id); // 's' for string

if ($stmt->execute()) {
    if ($stmt->affected_rows > 0) {
        echo json_encode(["success" => true, "message" => "Patient deleted successfully"]);
    } else {
        echo json_encode(["success" => false, "message" => "Patient not found"]);
    }
} else {
    echo json_encode(["success" => false, "message" => "Error deleting patient: " . $stmt->error]);
}

$stmt->close();
$conn->close();
exit();
?>