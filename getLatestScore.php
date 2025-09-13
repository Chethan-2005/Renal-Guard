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

// First try to read patient_id from GET/POST
$patient_id = $_GET['patient_id'] ?? $_POST['patient_id'] ?? null;

// If not found, try to decode JSON body
if (!$patient_id) {
    $input = json_decode(file_get_contents("php://input"), true);
    $patient_id = $input['patient_id'] ?? null;
}

if (!$patient_id) {
    echo json_encode(["success" => false, "message" => "Missing patient_id"]);
    $conn->close();
    exit();
}

// Fetch latest score for the patient
$sql = "SELECT score, created_at 
        FROM patient_assessments 
        WHERE patient_id = ? 
        ORDER BY created_at DESC 
        LIMIT 1";

$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $patient_id);
$stmt->execute();
$result = $stmt->get_result();

if ($row = $result->fetch_assoc()) {
    echo json_encode([
        "success"    => true,
        "score"      => (int)$row['score'],
        "created_at" => $row['created_at']
    ]);
} else {
    echo json_encode([
        "success" => false,
        "message" => "No score found for this patient"
    ]);
}

$stmt->close();
$conn->close();
?>
