<?php
$servername = "localhost";
$username = "root";      // your DB username
$password = "";          // your DB password
$dbname = "Renal Guard";
$port=3307;
// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}
?>
