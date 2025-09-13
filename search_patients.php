<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json");

// Connect to MySQL
$conn = new mysqli("localhost", "root", "", "renalguard", 3306);

// Check connection
if ($conn->connect_error) {
    echo json_encode(["error" => "Connection failed: " . $conn->connect_error]);
    exit();
}

// Get raw POST body
$rawData = file_get_contents("php://input");
$data = json_decode($rawData, true);

// Get doctor_email and query from JSON
$doctor_email = $data['doctor_email'] ?? '';
$search_query = $data['query'] ?? '';

// Validate input
if (empty($doctor_email)) {
    echo json_encode(["error" => "doctor_email is required in JSON body"]);
    $conn->close();
    exit();
}

// Use a prepared statement with wildcards to prevent SQL injection
$search_param = "%$search_query%";
$sql = "SELECT id, name, age, gender, patient_id FROM patients WHERE doctor_email = ? AND (name LIKE ? OR patient_id LIKE ?)";
$stmt = $conn->prepare($sql);

if (!$stmt) {
    echo json_encode(["error" => "Failed to prepare statement"]);
    $conn->close();
    exit();
}

// Bind parameters and execute
$stmt->bind_param("sss", $doctor_email, $search_param, $search_param);
$stmt->execute();
$result = $stmt->get_result();

$patients = [];
while ($row = $result->fetch_assoc()) {
    $patients[] = $row;
}

// Return JSON response
echo json_encode($patients);

// Close connections
$stmt->close();
$conn->close();
exit();
?>x