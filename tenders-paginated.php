<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
header('Access-Control-Allow-Methods: GET');
header('Content-Type: application/json; charset=utf-8');

// Enable full error reporting
error_reporting(E_ALL);
ini_set('display_errors', 1);

include(__DIR__ . '/../db.credentials.php');

try {
    // Database connection
    $bdd = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8mb4", $username, $password, [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
        PDO::ATTR_EMULATE_PREPARES => false
    ]);

    // Pagination
    $page = isset($_GET['page']) ? max(1, (int)$_GET['page']) : 1;
    $pageSize = isset($_GET['pageSize']) ? min(100, max(1, (int)$_GET['pageSize'])) : 10;
    $offset = ($page - 1) * $pageSize;
    $search = isset($_GET['search']) ? trim($_GET['search']) : '';

    // Base query
    $baseSql = "FROM appel_consultation ac";
    $where = "";
    $params = [];

    if ($search !== '') {
        $where = " WHERE ac.nom_appel_consultation LIKE :search";
        $params[':search'] = "%$search%";
    }

    // Total count
    $countStmt = $bdd->prepare("SELECT COUNT(*) as total $baseSql $where");
    $countStmt->execute($params);
    $total = (int)$countStmt->fetchColumn();

    // Data query
    $sql = "
        SELECT 
            ac.cle_appel_consultation AS id,
            ac.nom_appel_consultation AS title,
            DATE(ac.date_depot) AS depositDate,
            ac.jour_depot AS dayOfWeek,
            DATE(ac.date_fin_evaluation) AS evaluationEndDate,
            ac.attribution,
            ac.num_tender AS tenderNumber,
            ac.download_count AS downloadCount,
            ac.code
        $baseSql
        $where
        ORDER BY ac.cle_appel_consultation DESC
        LIMIT :offset, :limit
    ";

    $stmt = $bdd->prepare($sql);
    foreach ($params as $k => $v) $stmt->bindValue($k, $v, PDO::PARAM_STR);
    $stmt->bindValue(':offset', $offset, PDO::PARAM_INT);
    $stmt->bindValue(':limit', $pageSize, PDO::PARAM_INT);
    $stmt->execute();

    $tenders = $stmt->fetchAll();

    // Get document links dynamically for all years
    $basePath = $_SERVER['DOCUMENT_ROOT'] . '/documents/';
    $baseUrl  = 'https://bneder.dz/documents/';

    foreach ($tenders as &$tender) {
        $tenderNumber = $tender['tenderNumber'];
        $tender['documents'] = [];

        // Detect all lcYYYY folders
        $years = glob($basePath . 'lc*', GLOB_ONLYDIR);
        $documentsByYear = [];

        foreach ($years as $yearDir) {
            $yearFolder = basename($yearDir);
            $yearNumber = preg_replace('/\D+/', '', $yearFolder);
            if ($yearNumber === '') {
                continue;
            }

            $pattern = sprintf('%s/%s/lc-*.pdf', $basePath, $yearFolder);
            $files = glob($pattern);

            if (empty($files)) {
                continue;
            }

            $tenderNumberPattern = '/lc-0*' . preg_quote((string)$tenderNumber, '/') . '-/i';

            foreach ($files as $file) {
                $fileName = basename($file);
                if (!preg_match($tenderNumberPattern, $fileName)) {
                    continue;
                }

                $documentsByYear[$yearNumber][] = [
                    'year' => $yearNumber,
                    'fileName' => $fileName,
                    'fileUrl' => $baseUrl . $yearFolder . '/' . $fileName
                ];
            }
        }

        if (!empty($documentsByYear)) {
            $depositYear = null;
            if (!empty($tender['depositDate'])) {
                $depositYear = (int)date('Y', strtotime($tender['depositDate']));
            }

            if ($depositYear !== null && isset($documentsByYear[(string)$depositYear])) {
                $tender['documents'] = array_values($documentsByYear[(string)$depositYear]);
            } elseif ($depositYear === null) {
                $yearKeys = array_keys($documentsByYear);
                rsort($yearKeys, SORT_NUMERIC);
                $latestYearKey = $yearKeys[0];
                $tender['documents'] = array_values($documentsByYear[$latestYearKey]);
            }
        }

        // Integer cast for consistency
        $tender['id'] = (int)$tender['id'];
        $tender['tenderNumber'] = (int)$tender['tenderNumber'];
        $tender['downloadCount'] = (int)$tender['downloadCount'];
        $tender['code'] = (int)$tender['code'];
    }

    // Pagination info
    $totalPages = max(1, ceil($total / $pageSize));
    $response = [
        'data' => $tenders,
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
