<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
header("Content-Type: application/json");

$data = json_decode(file_get_contents("php://input"));
$conn = new mysqli("localhost", "root", "", "renalguard", 3306);

$doctor_email = $conn->real_escape_string($data->doctor_email ?? '');
if (!$doctor_email) {
    echo json_encode(["success"=>false,"message"=>"doctor_email required"]);
    exit();
}

// get doctor_id from email
$resDoc = $conn->query("SELECT doctor_id FROM doctors WHERE email='$doctor_email' LIMIT 1");
if (!$resDoc || $resDoc->num_rows == 0) {
    echo json_encode(["success"=>false,"message"=>"Doctor not found"]);
    exit();
}
$rowDoc = $resDoc->fetch_assoc();
$doctor_id = $rowDoc['doctor_id'];

// join doctor_schedule + appointments + users
$sql = "SELECT a.appointment_id, a.schedule_id, a.patient_email, a.slot_time, a.status AS appointment_status, a.booking_date,
               s.available_date, s.start_time AS schedule_start, s.end_time AS schedule_end,
               u.name AS patient_name, u.phone AS patient_phone
        FROM appointments a
        JOIN doctor_schedule s ON a.schedule_id = s.schedule_id
        JOIN users u ON a.patient_email = u.email
        WHERE s.doctor_id = '$doctor_id'
        ORDER BY s.available_date, a.slot_time";

$res = $conn->query($sql);
if (!$res) {
    echo json_encode(["success"=>false,"message"=>$conn->error]);
    exit();
}

$appointments = [];
while ($row = $res->fetch_assoc()) {
    $appointments[] = [
        "appointment_id"     => $row['appointment_id'],
        "schedule_id"        => $row['schedule_id'],
        "date"               => $row['available_date'],
        "slot_time"          => $row['slot_time'],
        "appointment_status" => $row['appointment_status'],
        "booking_date"       => $row['booking_date'],
        "patient_email"      => $row['patient_email'],
        "patient_name"       => $row['patient_name'],
        "patient_phone"      => $row['patient_phone'],
        "schedule_start"     => $row['schedule_start'],
        "schedule_end"       => $row['schedule_end']
    ];
}

echo json_encode([
    "success"=>true,
    "doctor_email"=>$doctor_email,
    "doctor_id"=>$doctor_id,
    "appointments"=>$appointments
]);

$conn->close();
?>
