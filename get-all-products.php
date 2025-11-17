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

    // Decode accessories JSON and translate accessory codes to French labels
    $accessoryLabels = [
        'MOUSE' => 'Souris',
        'KEYBOARD' => 'Clavier',
        'UPS' => 'Onduleur',
        'MOUNT' => 'Support',
        'ETHERNET_CABLE' => 'Câble Ethernet',
        'PRINTER' => 'Imprimante',
        'CHAIR' => 'Chaise',
        'DESK' => 'Bureau',
        'SCANNER_HEAD' => 'Tête de scanner',
        'SCANNER' => 'Scanner',
        'POWER_SUPPLY' => 'Bloc d’alimentation',
        'MONITOR' => 'Écran',
        'HEADSET' => 'Casque',
        'WEBCAM' => 'Webcam',
        'BARCODE_SCANNER' => 'Lecteur de codes-barres',
        'LABEL_PRINTER' => 'Imprimante d’étiquettes',
        'DOCKING_STATION' => 'Station d’accueil',
    ];

    foreach ($products as &$product) {
        if (!empty($product['accessories'])) {
            $decoded = json_decode($product['accessories'], true);
            if (is_array($decoded)) {
                $product['accessories'] = array_map(function ($code) use ($accessoryLabels) {
                    $key = strtoupper(str_replace(' ', '_', (string) $code));
                    if (isset($accessoryLabels[$key])) {
                        return $accessoryLabels[$key];
                    }

                    $humanReadable = ucwords(strtolower(str_replace('_', ' ', $key)));
                    return $humanReadable;
                }, $decoded);
            } else {
                $product['accessories'] = [];
            }
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
