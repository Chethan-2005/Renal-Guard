<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
header("Content-Type: application/json");

$data = json_decode(file_get_contents("php://input"));

// DB connect
$conn = new mysqli("localhost", "root", "", "renalguard", 3306);
if ($conn->connect_error) {
    echo json_encode(["success"=>false,"message"=>"Database connection failed"]);
    exit();
}

// sanitize
$name = $conn->real_escape_string($data->name ?? '');
$email = $conn->real_escape_string($data->email ?? '');
$phone = $conn->real_escape_string($data->phone ?? '');
$password = $conn->real_escape_string($data->password ?? '');
$specialization = $conn->real_escape_string($data->specialization ?? '');
$education = $conn->real_escape_string($data->education ?? '');
$location = $conn->real_escape_string($data->location ?? '');
$role = "doctor";

if (!$name || !$email || !$phone || !$password) {
    echo json_encode(["success"=>false,"message"=>"All fields are required"]);
    $conn->close();
    exit();
}

// check existing email in users
$chk = $conn->query("SELECT id FROM users WHERE email='$email' LIMIT 1");
if ($chk && $chk->num_rows > 0) {
    echo json_encode(["success"=>false,"message"=>"Email already exists"]);
    $conn->close();
    exit();
}

// generate doctor_id using max value
$sel = $conn->query("SELECT MAX(CAST(SUBSTRING(doctor_id,4) AS UNSIGNED)) AS mx FROM doctors");
$lastNum = 0;
if ($sel && $row = $sel->fetch_assoc()) {
    $lastNum = intval($row['mx']);
}
$newNum = $lastNum + 1;
$doctor_id = "DOC" . str_pad($newNum, 2, "0", STR_PAD_LEFT);

// start transaction
$conn->begin_transaction();

try {
    // insert into doctors table
    $sql1 = "INSERT INTO doctors (doctor_id, name, email, phone, password, specialization, education, location)
             VALUES ('$doctor_id', '$name', '$email', '$phone', '$password', '$specialization', '$education', '$location')";
    if (!$conn->query($sql1)) {
        throw new Exception("Failed to insert into doctors: ".$conn->error);
    }

    // insert into users table
    $sql2 = "INSERT INTO users (name, email, phone, password, role, specialization, education, location)
             VALUES ('$name', '$email', '$phone', '$password', '$role', '$specialization', '$education', '$location')";
    if (!$conn->query($sql2)) {
        throw new Exception("Failed to insert into users: ".$conn->error);
    }

    $conn->commit();

    echo json_encode([
        "success" => true,
        "message" => "Doctor added successfully",
        "doctor" => [
            "doctor_id" => $doctor_id,
            "name" => $name,
            "email" => $email,
            "phone" => $phone,
            "role" => $role
        ]
    ]);

} catch (Exception $e) {
    $conn->rollback();
    echo json_encode(["success"=>false,"message"=>$e->getMessage()]);
}

$conn->close();
?>
