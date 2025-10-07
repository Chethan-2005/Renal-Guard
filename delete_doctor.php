<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
header("Content-Type: application/json");

$data = json_decode(file_get_contents("php://input"));
$doctor_id = $data->doctor_id ?? '';

if (!$doctor_id) {
    echo json_encode(["success"=>false,"message"=>"Doctor ID is required"]);
    exit();
}

$conn = new mysqli("localhost", "root", "", "renalguard", 3306);
if ($conn->connect_error) {
    echo json_encode(["success"=>false,"message"=>"Database connection failed"]);
    exit();
}

// get doctor email first
$res = $conn->query("SELECT email FROM doctors WHERE doctor_id='$doctor_id' LIMIT 1");
if (!$res || $res->num_rows === 0) {
    echo json_encode(["success"=>false,"message"=>"Doctor not found"]);
    $conn->close();
    exit();
}
$row = $res->fetch_assoc();
$email = $row['email'];

// start transaction
$conn->begin_transaction();

try {
    // delete from doctors
    $sql1 = "DELETE FROM doctors WHERE doctor_id='$doctor_id'";
    if (!$conn->query($sql1)) {
        throw new Exception("Failed to delete from doctors: " . $conn->error);
    }

    // delete from users
    $sql2 = "DELETE FROM users WHERE email='$email'";
    if (!$conn->query($sql2)) {
        throw new Exception("Failed to delete from users: " . $conn->error);
    }

    $conn->commit();
    echo json_encode(["success"=>true,"message"=>"Doctor deleted successfully"]);

} catch (Exception $e) {
    $conn->rollback();
    echo json_encode(["success"=>false,"message"=>$e->getMessage()]);
}

$conn->close();
?>
