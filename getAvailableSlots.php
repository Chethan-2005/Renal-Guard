<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
header("Content-Type: application/json");

$data = json_decode(file_get_contents("php://input"));
$conn = new mysqli("localhost", "root", "", "renalguard", 3306);

$schedule_id = intval($data->schedule_id ?? 0);
if (!$schedule_id) {
    echo json_encode(["success"=>false,"message"=>"schedule_id required"]);
    exit();
}

$res = $conn->query("SELECT * FROM doctor_schedule WHERE schedule_id=$schedule_id");
if (!$res || $res->num_rows == 0) {
    echo json_encode(["success"=>false,"message"=>"Schedule not found"]);
    exit();
}
$schedule = $res->fetch_assoc();

$slots = [];
$start = strtotime($schedule['start_time']);
$end = strtotime($schedule['end_time']);
$interval = $schedule['slot_duration'] * 60; // convert minutes to seconds

for ($t = $start; $t < $end; $t += $interval) {
    $slot_time = date("H:i:s",$t);

    // check if slot already booked
    $chk = $conn->query("SELECT COUNT(*) AS cnt FROM appointments 
                         WHERE schedule_id=$schedule_id AND slot_time='$slot_time'");
    $row = $chk->fetch_assoc();
    if ($row['cnt'] == 0) {
        $slots[] = $slot_time;
    }
}

echo json_encode([
    "success"=>true,
    "date"=>$schedule['available_date'],
    "slots"=>$slots
]);

$conn->close();
?>
