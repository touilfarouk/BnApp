<?php
header('Content-Type: application/json; charset=utf-8');

$servername = '127.0.0.1';          // or localhost
$dbname     = 'order_app';
$username   = 'phpmyadmin';
$password   = 'root'; // replace with the real one

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
        'response' => 'false',
        'place'    => 'db',
        'message'  => 'Connexion impossible: ' . $e->getMessage(),
        'type'     => 'danger'
    ]);
    exit;
}

$raw = file_get_contents('php://input');
$data = json_decode($raw, true);
$action = $data['action'] ?? $_GET['action'] ?? null;

if (!$action) {
    http_response_code(400);
    echo json_encode([
        'response' => 'false',
        'message'  => 'Paramètre "action" manquant'
    ]);
    exit;
}

switch ($action) {
    case 'LIST_STRUCTURES':
        $stmt = $pdo->query("SELECT id, name, address FROM structures ORDER BY name");
        echo json_encode(['response' => 'true', 'structures' => $stmt->fetchAll(PDO::FETCH_ASSOC)]);
        break;

    case 'LIST_PERSONNEL':
        $structureId = $data['structure_id'] ?? null;
        if ($structureId) {
            $stmt = $pdo->prepare("
                SELECT id, full_name, structure_id, role
                FROM personnel
                WHERE structure_id = :structure_id
                ORDER BY full_name
            ");
            $stmt->execute([':structure_id' => $structureId]);
        } else {
            $stmt = $pdo->query("
                SELECT id, full_name, structure_id, role
                FROM personnel
                ORDER BY full_name
            ");
        }
        echo json_encode(['response' => 'true', 'personnel' => $stmt->fetchAll(PDO::FETCH_ASSOC)]);
        break;

    case 'LIST_PRODUCTS':
        $structureId = $data['structure_id'] ?? null;
        $personnelId = $data['personnel_id'] ?? null;
        $query = "
            SELECT p.*, s.name AS structure_name, pr.full_name AS personnel_name
            FROM products p
            LEFT JOIN structures s ON p.structure_id = s.id
            LEFT JOIN personnel pr ON p.assigned_personnel_id = pr.id
            WHERE 1=1
        ";
        $params = [];
        if ($structureId) {
            $query .= " AND p.structure_id = :structure_id";
            $params[':structure_id'] = $structureId;
        }
        if ($personnelId) {
            $query .= " AND p.assigned_personnel_id = :personnel_id";
            $params[':personnel_id'] = $personnelId;
        }

        $query .= " ORDER BY p.name";
        $stmt = $pdo->prepare($query);
        $stmt->execute($params);

        echo json_encode(['response' => 'true', 'products' => $stmt->fetchAll(PDO::FETCH_ASSOC)]);
        break;

    case 'CREATE_PRODUCT':
        $form = $data['form'] ?? [];
        if (empty($form['name'])) {
            echo json_encode(['response' => 'false', 'message' => 'Nom obligatoire', 'type' => 'danger']);
            break;
        }

        $stmt = $pdo->prepare("
            INSERT INTO products (
                name,
                label,
                structure_id,
                structure_name,
                assigned_personnel_id,
                assigned_personnel_name,
                accessories
            )
            VALUES (
                :name,
                :label,
                :structure_id,
                :structure_name,
                :personnel_id,
                :personnel_name,
                :accessories
            )
        ");

        $accessoriesPayload = null;
        if (!empty($form['accessories']) && is_array($form['accessories'])) {
            $accessoriesPayload = json_encode($form['accessories'], JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
        }

        $stmt->execute([
            ':name'          => $form['name'],
            ':label'         => $form['label'] ?? null,
            ':structure_id'  => $form['structure_id'] ?? $form['structure'] ?? null,
            ':structure_name'=> $form['structure_name'] ?? null,
            ':personnel_id'  => $form['assigned_personnel_id'] ?? $form['assigned_personnel'] ?? null,
            ':personnel_name'=> $form['assigned_personnel_name'] ?? null,
            ':accessories'   => $accessoriesPayload,
        ]);

        echo json_encode([
            'response' => 'true',
            'message'  => 'Produit ajouté',
            'id'       => $pdo->lastInsertId(),
        ]);
        break;

    case 'UPDATE_PRODUCT':
        $form = $data['form'] ?? [];
        if (empty($form['id']) || empty($form['name'])) {
            echo json_encode(['response' => 'false', 'message' => 'Identifiant ou nom manquant', 'type' => 'danger']);
            break;
        }

        $stmt = $pdo->prepare("
            UPDATE products
            SET name = :name,
                label = :label,
                structure_id = :structure_id,
                structure_name = :structure_name,
                assigned_personnel_id = :personnel_id,
                assigned_personnel_name = :personnel_name,
                accessories = :accessories
            WHERE id = :id
        ");
        $stmt->execute([
            ':name'          => $form['name'],
            ':label'         => $form['label'] ?? null,
            ':structure_id'  => $form['structure_id'] ?? null,
            ':structure_name'=> $form['structure_name'] ?? null,
            ':personnel_id'  => $form['assigned_personnel_id'] ?? null,
            ':personnel_name'=> $form['assigned_personnel_name'] ?? null,
            ':accessories'   => !empty($form['accessories']) && is_array($form['accessories'])
                ? json_encode($form['accessories'], JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES)
                : null,
            ':id'            => $form['id'],
        ]);

        echo json_encode(['response' => 'true', 'message' => 'Produit modifié']);
        break;

    case 'DELETE_PRODUCT':
        $id = $data['id'] ?? null;
        if (!$id) {
            echo json_encode(['response' => 'false', 'message' => 'ID produit manquant', 'type' => 'danger']);
            break;
        }
        $stmt = $pdo->prepare('DELETE FROM products WHERE id = :id');
        $stmt->execute([':id' => $id]);
        echo json_encode(['response' => 'true', 'message' => 'Produit supprimé']);
        break;

    case 'LIST_AFFECTATIONS':
        $stmt = $pdo->query("
            SELECT a.*, p.name AS product_name, pr.full_name AS personnel_name, s.name AS structure_name
            FROM affectations a
            LEFT JOIN products p ON a.product_id = p.id
            LEFT JOIN personnel pr ON a.personnel_id = pr.id
            LEFT JOIN structures s ON a.structure_id = s.id
            ORDER BY a.affectation_date DESC
        ");
        echo json_encode(['response' => 'true', 'affectations' => $stmt->fetchAll(PDO::FETCH_ASSOC)]);
        break;

    case 'CREATE_AFFECTATION':
        $form = $data['form'] ?? [];
        if (empty($form['product_id']) || empty($form['structure_id'])) {
            echo json_encode(['response' => 'false', 'message' => 'Produit et structure obligatoires', 'type' => 'danger']);
            break;
        }
        $stmt = $pdo->prepare("
            INSERT INTO affectations (product_id, personnel_id, structure_id, affectation_date, notes)
            VALUES (:product_id, :personnel_id, :structure_id, :affectation_date, :notes)
        ");
        $stmt->execute([
            ':product_id'       => $form['product_id'],
            ':personnel_id'     => $form['personnel_id'] ?? null,
            ':structure_id'     => $form['structure_id'],
            ':affectation_date' => $form['affectation_date'] ?? date('Y-m-d H:i:s'),
            ':notes'            => $form['notes'] ?? null,
        ]);
        echo json_encode(['response' => 'true', 'message' => 'Affectation enregistrée', 'id' => $pdo->lastInsertId()]);
        break;

    case 'DELETE_AFFECTATION':
        $id = $data['id'] ?? null;
        if (!$id) {
            echo json_encode(['response' => 'false', 'message' => 'ID affectation manquant', 'type' => 'danger']);
            break;
        }
        $stmt = $pdo->prepare('DELETE FROM affectations WHERE id = :id');
        $stmt->execute([':id' => $id]);
        echo json_encode(['response' => 'true', 'message' => 'Affectation supprimée']);
        break;

    default:
        http_response_code(404);
        echo json_encode(['response' => 'false', 'message' => 'Action inconnue: ' . $action]);
}