<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
header("Content-Type: application/json");

$data = json_decode(file_get_contents("php://input"));

$conn = new mysqli("localhost", "root", "", "renalguard", 3306);
if ($conn->connect_error) {
    echo json_encode(["success" => false, "message" => "Database connection failed"]);
    exit();
}

$patient_id     = $conn->real_escape_string($data->patient_id ?? '');
$doctor_email   = $conn->real_escape_string($data->doctor_email ?? '');
$patient_email  = $conn->real_escape_string($data->patient_email ?? '');
$score          = $conn->real_escape_string($data->score ?? '');
$line_duration  = $conn->real_escape_string($data->line_duration ?? '');
$stage          = $conn->real_escape_string($data->stage ?? '');

if (!$patient_id || !$doctor_email || !$patient_email || !$score || !$line_duration || !$stage) {
    echo json_encode(["success" => false, "message" => "All fields are required"]);
    $conn->close();
    exit();
}

$stmt = $conn->prepare("INSERT INTO patient_assessments 
    (patient_id, doctor_email, patient_email, score, line_duration, stage, created_at) 
    VALUES (?, ?, ?, ?, ?, ?, CURDATE())");
$stmt->bind_param("sssiss", $patient_id, $doctor_email, $patient_email, $score, $line_duration, $stage);

if ($stmt->execute()) {
    echo json_encode(["success" => true, "message" => "Assessment saved"]);
} else {
    echo json_encode(["success" => false, "message" => "Database error: " . $stmt->error]);
}

$stmt->close();
$conn->close();
?>
