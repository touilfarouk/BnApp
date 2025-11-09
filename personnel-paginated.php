<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
header('Access-Control-Allow-Methods: GET');
header('Content-Type: application/json; charset=utf-8');

error_reporting(E_ALL);
ini_set('display_errors', 1);

$servername = "";
$dbname = "";
$username = "";
$password = "";

try {
    $bdd = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8", $username, $password, [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
        PDO::ATTR_EMULATE_PREPARES => false
    ]);

    $page = isset($_GET['page']) ? max(1, (int)$_GET['page']) : 1;
    $pageSize = isset($_GET['pageSize']) ? min(100, max(1, (int)$_GET['pageSize'])) : 20;
    $offset = ($page - 1) * $pageSize;
    $search = isset($_GET['search']) ? trim($_GET['search']) : '';

    $baseSql = "FROM mailbox m";
    $where = "";
    $params = [];

    if ($search !== '') {
        $where = " WHERE m.username LIKE :search OR m.nom LIKE :search OR m.prenom LIKE :search OR m.structure LIKE :search OR m.fonction LIKE :search";
        $params[':search'] = "%$search%";
    }

    $countStmt = $bdd->prepare("SELECT COUNT(*) $baseSql $where");
    $countStmt->execute($params);
    $total = (int)$countStmt->fetchColumn();

    $sql = "
        SELECT
            m.id,
            TRIM(m.username) AS username,
            m.password,
            TRIM(m.name) AS name,
            TRIM(m.nom) AS nom,
            TRIM(m.prenom) AS prenom,
            TRIM(m.fonction) AS fonction,
            NULLIF(TRIM(m.structure), '') AS structure,
            TRIM(m.maildir) AS maildir,
            TRIM(m.quota) AS quota,
            TRIM(m.local_part) AS localPart,
            TRIM(m.domain) AS domain,
            m.matricule,
            m.active,
            m.mdp_changed AS mdpChanged,
            m.date_register AS dateRegister,
            m.modified
        $baseSql
        $where
        ORDER BY m.structure ASC, m.nom ASC, m.prenom ASC
        LIMIT :offset, :limit
    ";

    $stmt = $bdd->prepare($sql);
    foreach ($params as $key => $value) {
        $stmt->bindValue($key, $value, PDO::PARAM_STR);
    }
    $stmt->bindValue(':offset', $offset, PDO::PARAM_INT);
    $stmt->bindValue(':limit', $pageSize, PDO::PARAM_INT);
    $stmt->execute();

    $rows = $stmt->fetchAll();

    foreach ($rows as &$row) {
        $row['id'] = isset($row['id']) ? (int)$row['id'] : null;
        $row['active'] = isset($row['active']) ? (int)$row['active'] : null;
        $row['mdpChanged'] = isset($row['mdpChanged']) ? (int)$row['mdpChanged'] : null;
        $row['matricule'] = isset($row['matricule']) ? (int)$row['matricule'] : null;
    }

    $totalPages = max(1, ceil($total / $pageSize));

    $response = [
        'data' => $rows,
        'pagination' => [
            'currentPage' => $page,
            'pageSize' => $pageSize,
            'totalItems' => $total,
            'totalPages' => $totalPages,
            'hasNext' => $page < $totalPages,
            'hasPrevious' => $page > 1
        ]
    ];

    echo json_encode($response, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
    exit;

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Database Error: ' . $e->getMessage()]);
    exit;
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Server Error: ' . $e->getMessage()]);
    exit;
}

