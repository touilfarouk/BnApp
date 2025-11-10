<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
header('Access-Control-Allow-Methods: GET, POST, DELETE');
header('Content-Type: application/json; charset=utf-8');
include('../db.credentials.php');

$method = $_SERVER['REQUEST_METHOD'];
$Received_JSON = file_get_contents('php://input');
$obj = json_decode($Received_JSON, true);

$baseUrl = 'https://bneder.dz/';
$documentRoot = rtrim($_SERVER['DOCUMENT_ROOT'] ?? dirname(__DIR__), '/');

function normalizeRelativePath(?string $path): string
{
    if ($path === null) {
        return '';
    }
    $trimmed = trim($path);
    if ($trimmed === '') {
        return '';
    }

    $trimmed = preg_replace('#^https?://[^/]+/#i', '', $trimmed);
    return ltrim($trimmed, '/');
}

function buildPicturePayload(?string $relativePath, ?int $idPic = null, array $extra = []): array
{
    global $baseUrl, $documentRoot;

    $normalized = normalizeRelativePath($relativePath);
    $payload = [
        'id'           => $idPic !== null ? (int)$idPic : null,
        'fileName'     => null,
        'relativePath' => null,
        'fileUrl'      => null,
        'thumbnailUrl' => null,
    ];

    if ($normalized !== '') {
        $payload['fileName'] = basename($normalized);
        $payload['relativePath'] = $normalized;
        $payload['fileUrl'] = rtrim($baseUrl, '/') . '/' . $normalized;

        $directory = dirname($normalized);
        if ($directory === '.' || $directory === '') {
            $directory = '';
        }

        $thumbnailRelative = $directory !== ''
            ? $directory . '/thumbnail/' . $payload['fileName']
            : 'thumbnail/' . $payload['fileName'];
        $thumbnailPath = rtrim($documentRoot, '/') . '/' . $thumbnailRelative;
        if (file_exists($thumbnailPath)) {
            $payload['thumbnailUrl'] = rtrim($baseUrl, '/') . '/' . $thumbnailRelative;
        }
    }

    return array_merge($payload, $extra);
}

function getPicturesFromFilesystem(string $relativeDir): array
{
    global $documentRoot;
    $pictures = [];
    $fullDir = rtrim($documentRoot, '/') . '/' . trim($relativeDir, '/');

    if (!is_dir($fullDir)) {
        return [];
    }

    $files = array_diff(scandir($fullDir), ['.', '..', 'thumbnail']);
    foreach ($files as $file) {
        $filePath = trim($relativeDir, '/') . '/' . $file;
        $pictures[] = buildPicturePayload($filePath);
    }

    return $pictures;
}

switch (strtoupper($method)) {
    case 'GET':
    case 'POST':
        $cle = $method === 'GET' ? ($_GET['cle'] ?? null) : ($obj['cle'] ?? null);

        if ($cle === null || $cle === '' || !is_numeric($cle)) {
            echo json_encode([
                'reponse' => 'false',
                'place'   => 'tc',
                'message' => 'Clé d’actualité invalide',
                'type'    => 'danger',
                'icon'    => 'nc-icon nc-bell-55',
                'autoDismiss' => 0,
            ]);
            break;
        }

        try {
            $bdd = new PDO(
                "mysql:host=$servername;dbname=$dbname; charset=utf8mb4",
                $username,
                $password,
                [PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION]
            );

            $stmt = $bdd->prepare('SELECT id_pic, pic_path, pic_title, legend FROM actualite_photos WHERE id_news = :cle ORDER BY id_pic ASC');
            $stmt->bindValue(':cle', (int)$cle, PDO::PARAM_INT);
            $stmt->execute();

            $pictures = [];
            $pathsSeen = [];

            while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
                $extra = [];
                if (isset($row['pic_title'])) {
                    $extra['title'] = $row['pic_title'];
                }
                if (isset($row['legend'])) {
                    $extra['legend'] = $row['legend'];
                }

                $payload = buildPicturePayload($row['pic_path'] ?? '', (int)$row['id_pic'], $extra);
                $pictures[] = $payload;

                if (!empty($payload['relativePath'])) {
                    $pathsSeen[$payload['relativePath']] = true;
                }
            }

            $filesystemDir = 'images/actus/' . (int)$cle;
            foreach (getPicturesFromFilesystem($filesystemDir) as $fsPicture) {
                if ($fsPicture['relativePath'] === null) {
                    continue;
                }
                if (!isset($pathsSeen[$fsPicture['relativePath']])) {
                    $pictures[] = $fsPicture;
                }
            }

            echo json_encode([
                'reponse' => 'true',
                'newsPicture' => $pictures,
            ], JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
        } catch (Exception $e) {
            echo json_encode([
                'reponse' => 'false',
                'place'   => 'tc',
                'message' => $e->getMessage(),
                'type'    => 'danger',
                'icon'    => 'nc-icon nc-bell-55',
                'autoDismiss' => 0,
            ]);
        }
        break;

    case 'DELETE':
        $id_pic = $obj['id_pic'] ?? null;
        $path = $obj['path'] ?? null;

        if ($id_pic === null && $path === null) {
            echo json_encode([
                'reponse' => 'false',
                'place'   => 'tc',
                'message' => 'Identifiant de photo ou chemin requis',
                'type'    => 'danger',
                'icon'    => 'nc-icon nc-bell-55',
                'autoDismiss' => 0,
            ]);
            break;
        }

        try {
            $bdd = new PDO(
                "mysql:host=$servername;dbname=$dbname; charset=utf8mb4",
                $username,
                $password,
                [PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION]
            );

            $relativePath = null;

            if ($id_pic !== null) {
                $stmt = $bdd->prepare('SELECT pic_path FROM actualite_photos WHERE id_pic = :id');
                $stmt->bindValue(':id', (int)$id_pic, PDO::PARAM_INT);
                $stmt->execute();
                $row = $stmt->fetch(PDO::FETCH_ASSOC);
                if ($row) {
                    $relativePath = $row['pic_path'];
                }
            }

            if ($relativePath === null && $path !== null) {
                $relativePath = $path;
            }

            $normalized = normalizeRelativePath($relativePath);
            if ($normalized === '') {
                throw new RuntimeException('Chemin de fichier introuvable');
            }

            $fullPath = rtrim($documentRoot, '/') . '/' . $normalized;
            if (file_exists($fullPath)) {
                unlink($fullPath);
            }

            if (is_dir(dirname($fullPath) . '/thumbnail')) {
                $thumb = dirname($fullPath) . '/thumbnail/' . basename($fullPath);
                if (file_exists($thumb)) {
                    unlink($thumb);
                }
            }

            if ($id_pic !== null) {
                $deleteStmt = $bdd->prepare('DELETE FROM actualite_photos WHERE id_pic = :id');
                $deleteStmt->bindValue(':id', (int)$id_pic, PDO::PARAM_INT);
                $deleteStmt->execute();
            }

            echo json_encode([
                'reponse' => 'true',
                'place'   => 'tc',
                'message' => 'Photo supprimée',
                'type'    => 'success',
                'icon'    => 'nc-icon nc-bell-55',
                'autoDismiss' => 2,
            ]);
        } catch (Exception $e) {
            echo json_encode([
                'reponse' => 'false',
                'place'   => 'tc',
                'message' => $e->getMessage(),
                'type'    => 'danger',
                'icon'    => 'nc-icon nc-bell-55',
                'autoDismiss' => 0,
            ]);
        }
        break;

    default:
        http_response_code(405);
        echo json_encode([
            'reponse' => 'false',
            'message' => 'Méthode non autorisée',
        ]);
        break;
}
