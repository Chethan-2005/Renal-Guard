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

// Fetch patient scores
$sql = "SELECT score, created_at 
        FROM patient_assessments 
        WHERE patient_email = ? 
        ORDER BY created_at DESC";

$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();

$scores = [];
while ($row = $result->fetch_assoc()) {
    $scores[] = [
        "score" => (int)$row['score'],
        "created_at" => $row['created_at']
    ];
}

if (count($scores) > 0) {
    echo json_encode([
        "success" => true,
        "scores" => $scores
    ]);
} else {
    echo json_encode([
        "success" => false,
        "message" => "No scores found"
    ]);
}

$stmt->close();
$conn->close();
?>
