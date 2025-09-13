<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: Content-Type, Authorization");
header("Access-Control-Allow-Methods: POST, GET, OPTIONS");
header("Content-Type: application/json");

// Connect to MySQL
$conn = new mysqli("localhost", "root", "", "renalguard", 3306);

if ($conn->connect_error) {
    echo json_encode(["success" => false, "message" => "Connection failed: " . $conn->connect_error]);
    exit();
}

// Get raw POST body
$rawData = file_get_contents("php://input");
$data = json_decode($rawData, true);

// Get patientId
$patientId = $data['patientId'] ?? '';

if (empty($patientId)) {
    echo json_encode(["success" => false, "message" => "patientId is required"]);
    $conn->close();
    exit();
}

// Prepare SQL
$sql = "SELECT patient_id, name, age, gender, email, phone FROM patients WHERE patient_id = ?";
$stmt = $conn->prepare($sql);

if (!$stmt) {
    echo json_encode(["success" => false, "message" => "Failed to prepare statement"]);
    $conn->close();
    exit();
}

$stmt->bind_param("s", $patientId);
$stmt->execute();
$result = $stmt->get_result();

if ($row = $result->fetch_assoc()) {
    echo json_encode([
        "success" => true,
        "data" => [
            "patientId" => $row["patient_id"],
            "name" => $row["name"],
            "age" => (int)$row["age"],
            "gender" => $row["gender"],
            "email" => $row["email"],
            "phone" => $row["phone"]
        ]
    ]);
} else {
    echo json_encode(["success" => false, "message" => "No patient found"]);
}

$stmt->close();
$conn->close();
?>
