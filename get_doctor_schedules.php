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

// doctor_email should come from Android
$doctor_email = $conn->real_escape_string($data->doctor_email ?? '');
if (!$doctor_email) {
    echo json_encode(["success"=>false,"message"=>"doctor email required"]);
    exit();
}

// get doctor_id
$resDoc = $conn->query("SELECT doctor_id FROM doctors WHERE email='$doctor_email' LIMIT 1");
if (!$resDoc || $resDoc->num_rows == 0) {
    echo json_encode(["success"=>false,"message"=>"Doctor not found"]);
    exit();
}
$rowDoc = $resDoc->fetch_assoc();
$doctor_id = $rowDoc['doctor_id'];

// fetch schedules with dynamic booked_count
$sql = "SELECT ds.schedule_id, ds.available_date, ds.start_time, ds.end_time, ds.slot_duration,
               ds.max_patients, ds.status,
               (SELECT COUNT(*) FROM appointments a WHERE a.schedule_id = ds.schedule_id) AS booked_count
        FROM doctor_schedule ds
        WHERE ds.doctor_id='$doctor_id'
        ORDER BY ds.available_date, ds.start_time";

$res = $conn->query($sql);
if (!$res) {
    echo json_encode(["success"=>false,"message"=>$conn->error]);
    exit();
}

$schedules = [];
while ($row = $res->fetch_assoc()) {
    $remaining = intval($row['max_patients']) - intval($row['booked_count']);
    if ($remaining < 0) $remaining = 0;

    // fetch patients who booked this schedule
    $patients = [];
    $resApp = $conn->query("SELECT a.appointment_id, a.slot_time, a.status,
                                   p.name, p.email, p.phone
                            FROM appointments a
                            JOIN patients p ON a.patient_email = p.email
                            WHERE a.schedule_id=" . intval($row['schedule_id']));

    if ($resApp) {
        while ($p = $resApp->fetch_assoc()) {
            $patients[] = [
                "appointment_id" => $p['appointment_id'],
                "slot_time"      => $p['slot_time'],
                "status"         => $p['status'],
                "patient_name"   => $p['name'],
                "patient_email"  => $p['email'],
                "patient_phone"  => $p['phone']
            ];
        }
    }

    $schedules[] = [
        "schedule_id"   => $row['schedule_id'],
        "available_date"=> $row['available_date'],
        "start_time"    => $row['start_time'],
        "end_time"      => $row['end_time'],
        "slot_duration" => $row['slot_duration'],
        "max_patients"  => $row['max_patients'],
        "booked_count"  => $row['booked_count'],
        "remaining"     => $remaining,
        "status"        => $row['status'],
        "patients"      => $patients
    ];
}

echo json_encode([
    "success"=>true,
    "doctor_email"=>$doctor_email,
    "schedules"=>$schedules
]);

$conn->close();
?>
