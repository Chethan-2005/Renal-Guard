<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
header("Content-Type: application/json");

$conn = new mysqli("localhost", "root", "", "renalguard", 3306);
if ($conn->connect_error) {
    echo json_encode(["success" => false, "message" => "Database connection failed"]);
    exit();
}

$input = json_decode(file_get_contents("php://input"), true);
$email = $_POST['email'] ?? $input['email'] ?? null;
$password = $_POST['password'] ?? $input['password'] ?? null;

if (!$email) {
    echo json_encode(["success" => false, "message" => "Missing email"]);
    $conn->close();
    exit();
}

if (!empty($password)) {
    // Store password as plain text
    $sql = "UPDATE admin SET password = ? WHERE username = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ss", $password, $email);

    if ($stmt->execute()) {
        echo json_encode(["success" => true, "message" => "Password updated successfully"]);
    } else {
        echo json_encode(["success" => false, "message" => "Password update failed"]);
    }

    $stmt->close();
} else {
    echo json_encode(["success" => false, "message" => "No password provided"]);
}

$conn->close();
?>
