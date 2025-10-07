<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
header("Content-Type: application/json");

$conn = new mysqli("localhost", "root", "", "renalguard", 3306);
if ($conn->connect_error) {
    echo json_encode(["success"=>false,"message"=>"Database connection failed"]);
    exit();
}

$sql = "SELECT doctor_id, name, email, phone, specialization, education, location FROM doctors";
$result = $conn->query($sql);

$doctors = [];
if ($result && $result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $doctors[] = $row;
    }
}

echo json_encode([
    "success" => true,
    "doctors" => $doctors
]);

$conn->close();
?>
