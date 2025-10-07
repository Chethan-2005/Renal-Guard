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

// Get POST fields (matches Retrofit @FormUrlEncoded)
$patient_id = $conn->real_escape_string($_POST['patient_id'] ?? '');
$name       = $conn->real_escape_string($_POST['name'] ?? '');
$age        = $conn->real_escape_string($_POST['age'] ?? '');
$gender     = $conn->real_escape_string($_POST['gender'] ?? '');
$email      = $conn->real_escape_string($_POST['email'] ?? '');
$mobile     = $conn->real_escape_string($_POST['mobile'] ?? '');

// Validate required fields
if (!$patient_id || !$name || !$age || !$gender || !$email || !$mobile) {
    echo json_encode(["success" => false, "message" => "All fields are required"]);
    $conn->close();
    exit();
}

// Validate email
if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    echo json_encode(["success" => false, "message" => "Invalid email format"]);
    $conn->close();
    exit();
}

// Update patient in database
$sql = "UPDATE patients 
        SET name='$name', age='$age', gender='$gender', email='$email', phone='$mobile'
        WHERE patient_id='$patient_id'";

if ($conn->query($sql) === TRUE) {
    echo json_encode(["success" => true, "message" => "Patient updated successfully"]);
} else {
    echo json_encode(["success" => false, "message" => "Update failed: " . $conn->error]);
}

$conn->close();
?>
