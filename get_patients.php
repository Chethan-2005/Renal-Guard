<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
header("Content-Type: application/json");

// Connect to MySQL
$conn = new mysqli("localhost", "root", "", "renalguard", 3306);

// Initialize patients array
$patients = [];

// Check connection
if ($conn->connect_error) {
    echo json_encode(["error" => "Database connection failed: " . $conn->connect_error]);
    exit();
}

// Get raw POST body
$rawData = file_get_contents("php://input");
$data = json_decode($rawData, true);

// Debug: check what is received
// file_put_contents("debug_doctor.txt", print_r($data, true));

$doctor_email = $data['doctor_email'] ?? '';

if (empty($doctor_email)) {
    echo json_encode(["error" => "Doctor email not provided"]);
    exit();
}

// Use prepared statement to prevent SQL injection
$sql = "SELECT name, age, gender, patient_id, email FROM patients WHERE doctor_email = ?";
$stmt = $conn->prepare($sql);

if (!$stmt) {
    echo json_encode(["error" => "SQL prepare failed: " . $conn->error]);
    exit();
}

// Bind parameter and execute
$stmt->bind_param("s", $doctor_email);
$stmt->execute();
$result = $stmt->get_result();

// Fetch patients
while ($row = $result->fetch_assoc()) {
    $patients[] = $row;
}

// Optional: log result for debugging
// file_put_contents("debug_patients.txt", print_r($patients, true));

// Return JSON array
echo json_encode($patients);

// Close statement and connection
$stmt->close();
$conn->close();
exit();
?>
