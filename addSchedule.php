<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
header("Content-Type: application/json");

$data = json_decode(file_get_contents("php://input"));
$conn = new mysqli("localhost", "root", "", "renalguard", 3306);

if ($conn->connect_error) {
    echo json_encode(["success"=>false,"message"=>"DB connection failed"]);
    exit();
}

$doctor_email   = $conn->real_escape_string($data->doctor_email ?? '');
$available_date = $conn->real_escape_string($data->available_date ?? '');
$start_time     = $conn->real_escape_string($data->start_time ?? '');
$end_time       = $conn->real_escape_string($data->end_time ?? '');
$slot_duration  = intval($data->slot_duration ?? 0);
$max_patients   = intval($data->max_patients ?? 0);

if (!$doctor_email || !$available_date || !$start_time || !$end_time || !$slot_duration || !$max_patients) {
    echo json_encode(["success"=>false,"message"=>"All fields are required"]);
    exit();
}

// 🔹 get doctor_id from email
$resDoc = $conn->query("SELECT doctor_id FROM doctors WHERE email='$doctor_email' LIMIT 1");
if (!$resDoc || $resDoc->num_rows == 0) {
    echo json_encode(["success"=>false,"message"=>"Doctor not found"]);
    exit();
}
$rowDoc = $resDoc->fetch_assoc();
$doctor_id = $rowDoc['doctor_id'];

// 🔹 Convert input times (any format) to 24-hour HH:MM:SS
function to24Hour($time) {
    $t = strtotime($time);
    return $t ? date("H:i:s", $t) : null;
}

$start_time = to24Hour($start_time);
$end_time   = to24Hour($end_time);

if (!$start_time || !$end_time) {
    echo json_encode(["success"=>false,"message"=>"Invalid time format"]);
    exit();
}

// insert schedule
$sql = "INSERT INTO doctor_schedule 
        (doctor_id, available_date, start_time, end_time, slot_duration, max_patients, booked_count, status)
        VALUES ('$doctor_id','$available_date','$start_time','$end_time',$slot_duration,$max_patients,0,'active')";

if ($conn->query($sql)) {
    echo json_encode(["success"=>true,"message"=>"Schedule added successfully"]);
} else {
    echo json_encode(["success"=>false,"message"=>$conn->error]);
}

$conn->close();
?>
