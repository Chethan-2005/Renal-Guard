<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
header("Content-Type: application/json");

// Get JSON input
$data = json_decode(file_get_contents("php://input"));

// Connect to MySQL
$conn = new mysqli("localhost", "root", "", "renalguard", 3306);
if ($conn->connect_error) {
    echo json_encode(["success" => false, "message" => "Database connection failed"]);
    exit();
}

// Sanitize input
$name = $conn->real_escape_string($data->name ?? '');
$age = (int)($data->age ?? 0);
$email = $conn->real_escape_string($data->email ?? '');
$phone = $conn->real_escape_string($data->phone ?? '');
$doctor_email = $conn->real_escape_string($data->doctor_email ?? '');
$gender = $conn->real_escape_string($data->gender ?? '');

// Validate inputs
if (!$name || !$age || !$email || !$phone || !$doctor_email || !$gender) {
    echo json_encode(["success" => false, "message" => "All fields are required"]);
    $conn->close();
    exit();
}

if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    echo json_encode(["success" => false, "message" => "Invalid email format"]);
    $conn->close();
    exit();
}

// Get reusable smallest missing ID
$idResult = $conn->query("
    SELECT COALESCE(MIN(t1.id + 1), 1) AS next_id
    FROM patients t1
    LEFT JOIN patients t2 ON t1.id + 1 = t2.id
    WHERE t2.id IS NULL
");
$row = $idResult->fetch_assoc();
$nextId = $row['next_id'] ?? 1;

// Generate unique patient_id like PAT001, PAT002, etc.
$patient_id = 'PAT' . str_pad($nextId, 3, '0', STR_PAD_LEFT);

// Insert new patient including gender
$sql = "INSERT INTO patients (id, name, age, email, phone, patient_id, doctor_email, gender)
        VALUES ('$nextId', '$name', '$age', '$email', '$phone', '$patient_id', '$doctor_email', '$gender')";

if ($conn->query($sql)) {
    echo json_encode(["success" => true, "message" => "Patient added successfully", "patient_id" => $patient_id]);
} else {
    echo json_encode(["success" => false, "message" => "Error: " . $conn->error]);
}

$conn->close();
?>
