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

// First try to read from GET/POST
$email          = $_GET['email'] ?? $_POST['email'] ?? null;
$name           = $_GET['name'] ?? $_POST['name'] ?? null;
$phone          = $_GET['phone'] ?? $_POST['phone'] ?? null;
$specialization = $_GET['specialization'] ?? $_POST['specialization'] ?? null;
$education      = $_GET['education'] ?? $_POST['education'] ?? null;
$location       = $_GET['location'] ?? $_POST['location'] ?? null;

// If not found, try JSON body
if (!$email) {
    $input = json_decode(file_get_contents("php://input"), true);
    $email          = $input['email'] ?? null;
    $name           = $input['name'] ?? null;
    $phone          = $input['phone'] ?? null;
    $specialization = $input['specialization'] ?? null;
    $education      = $input['education'] ?? null;
    $location       = $input['location'] ?? null;
}

if (!$email) {
    echo json_encode(["success" => false, "message" => "Missing email"]);
    $conn->close();
    exit();
}

// Update doctor profile
$sql = "UPDATE users 
        SET name = ?, phone = ?, specialization = ?, education = ?, location = ? 
        WHERE email = ?";

$stmt = $conn->prepare($sql);
$stmt->bind_param("ssssss", $name, $phone, $specialization, $education, $location, $email);

if ($stmt->execute()) {
    echo json_encode(["success" => true, "message" => "Profile updated successfully"]);

} else {
    echo json_encode(["success" => false, "message" => "Update failed"]);
}

$stmt->close();
$conn->close();
?>
