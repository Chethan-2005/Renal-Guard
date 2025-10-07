<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
header("Content-Type: application/json");

$data = json_decode(file_get_contents("php://input"));
$conn = new mysqli("localhost", "root", "", "renalguard", 3306);

$schedule_id   = intval($data->schedule_id ?? 0);
$patient_email = $conn->real_escape_string($data->patient_email ?? '');
$slot_time     = $conn->real_escape_string($data->slot_time ?? '');

if (!$schedule_id || !$patient_email || !$slot_time) {
    echo json_encode(["success"=>false,"message"=>"All fields are required"]);
    exit();
}

// verify slot still free
$chk = $conn->query("SELECT COUNT(*) AS cnt FROM appointments 
                     WHERE schedule_id=$schedule_id AND slot_time='$slot_time'");
$row = $chk->fetch_assoc();
if ($row['cnt'] > 0) {
    echo json_encode(["success"=>false,"message"=>"Slot already booked"]);
    exit();
}

// insert appointment
$sql = "INSERT INTO appointments (schedule_id, patient_email, slot_time)
        VALUES ($schedule_id, '$patient_email', '$slot_time')";
if ($conn->query($sql)) {
    // update booked count
    $conn->query("UPDATE doctor_schedule SET booked_count=booked_count+1 WHERE schedule_id=$schedule_id");
    echo json_encode(["success"=>true,"message"=>"Appointment booked"]);
} else {
    echo json_encode(["success"=>false,"message"=>$conn->error]);
}

$conn->close();
?>
