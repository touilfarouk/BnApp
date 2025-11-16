<?php
header('Content-Type: application/json; charset=utf-8');

$servername = '127.0.0.1';
$dbname     = 'order_app';
$username   = 'phpmyadmin';
$password   = 'root';

try {
    $pdo = new PDO(
        "mysql:host=$servername;dbname=$dbname;charset=utf8mb4",
        $username,
        $password,
        [PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION]
    );
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Database connection failed',
        'error'   => $e->getMessage(),
    ]);
    exit;
}

$structureId = $_GET['structure_id'] ?? null;
$personnelId = $_GET['personnel_id'] ?? null;

$query = "
    SELECT
        p.id,
        p.name,
        p.label,
        p.structure_id,
        p.structure_name,
        p.assigned_personnel_id,
        p.assigned_personnel_name,
        p.accessories,
        p.created_at,
        p.updated_at
    FROM products p
    WHERE 1 = 1
";

$params = [];
if (!empty($structureId)) {
    $query .= ' AND p.structure_id = :structure_id';
    $params[':structure_id'] = $structureId;
}

if (!empty($personnelId)) {
    $query .= ' AND p.assigned_personnel_id = :personnel_id';
    $params[':personnel_id'] = $personnelId;
}

$query .= ' ORDER BY p.name';

try {
    $stmt = $pdo->prepare($query);
    $stmt->execute($params);
    $products = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Decode accessories JSON for consumers that expect an array
    foreach ($products as &$product) {
        if (!empty($product['accessories'])) {
            $decoded = json_decode($product['accessories'], true);
            $product['accessories'] = is_array($decoded) ? $decoded : [];
        } else {
            $product['accessories'] = [];
        }
    }

    echo json_encode([
        'success'  => true,
        'count'    => count($products),
        'products' => $products,
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Failed to fetch products',
        'error'   => $e->getMessage(),
    ]);
}
