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

$email          = $input['email'] ?? null;
$name           = $input['name'] ?? null;
$phone          = $input['phone'] ?? null;
$specialization = $input['specialization'] ?? null;
$education      = $input['education'] ?? null;
$location       = $input['location'] ?? null;
$password       = $input['password'] ?? null;

if (!$email) {
    echo json_encode(["success" => false, "message" => "Missing email"]);
    $conn->close();
    exit();
}

// Start transaction
$conn->begin_transaction();

try {
    if ($password) {
        // Update users
        $sql1 = "UPDATE users 
                 SET name = ?, phone = ?, specialization = ?, education = ?, location = ?, password = ?
                 WHERE email = ?";
        $stmt1 = $conn->prepare($sql1);
        $stmt1->bind_param("sssssss", $name, $phone, $specialization, $education, $location, $password, $email);
        if (!$stmt1->execute()) throw new Exception("Users update failed: " . $stmt1->error);
        $stmt1->close();

        // Update doctors
        $sql2 = "UPDATE doctors 
                 SET name = ?, phone = ?, specialization = ?, education = ?, location = ?, password = ?
                 WHERE email = ?";
        $stmt2 = $conn->prepare($sql2);
        $stmt2->bind_param("sssssss", $name, $phone, $specialization, $education, $location, $password, $email);
        if (!$stmt2->execute()) throw new Exception("Doctors update failed: " . $stmt2->error);
        $stmt2->close();

    } else {
        // Update users
        $sql1 = "UPDATE users 
                 SET name = ?, phone = ?, specialization = ?, education = ?, location = ?
                 WHERE email = ?";
        $stmt1 = $conn->prepare($sql1);
        $stmt1->bind_param("ssssss", $name, $phone, $specialization, $education, $location, $email);
        if (!$stmt1->execute()) throw new Exception("Users update failed: " . $stmt1->error);
        $stmt1->close();

        // Update doctors
        $sql2 = "UPDATE doctors 
                 SET name = ?, phone = ?, specialization = ?, education = ?, location = ?
                 WHERE email = ?";
        $stmt2 = $conn->prepare($sql2);
        $stmt2->bind_param("ssssss", $name, $phone, $specialization, $education, $location, $email);
        if (!$stmt2->execute()) throw new Exception("Doctors update failed: " . $stmt2->error);
        $stmt2->close();
    }

    $conn->commit();
    echo json_encode(["success" => true, "message" => "Profile updated successfully"]);

} catch (Exception $e) {
    $conn->rollback();
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}

$conn->close();
?>