<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
header('Access-Control-Allow-Methods: GET, POST, DELETE');
header('Content-Type: application/json; charset=utf-8');

$method = $_SERVER['REQUEST_METHOD'];
$Received_JSON = file_get_contents('php://input');
$obj = json_decode($Received_JSON, true);

$baseUrl = 'https://bneder.dz/';
$documentRoot = rtrim($_SERVER['DOCUMENT_ROOT'] ?? dirname(__DIR__), '/');

function buildAlbumPicturePayload(string $relativePath): array
{
    global $baseUrl, $documentRoot;

    $normalized = ltrim($relativePath, '/');
    $fileName = basename($normalized);
    $fileUrl = rtrim($baseUrl, '/') . '/' . $normalized;

    $thumbnailRelative = preg_replace('#/+#', '/', dirname($normalized) . '/thumbnail/' . $fileName);
    $thumbnailPath = rtrim($documentRoot, '/') . '/' . $thumbnailRelative;
    $thumbnailUrl = file_exists($thumbnailPath)
        ? rtrim($baseUrl, '/') . '/' . $thumbnailRelative
        : null;

    return [
        'fileName'     => $fileName,
        'relativePath' => $normalized,
        'fileUrl'      => $fileUrl,
        'thumbnailUrl' => $thumbnailUrl,
    ];
}

function listAlbumPictures(string $relativeDir): array
{
    global $documentRoot;

    $fullDir = rtrim($documentRoot, '/') . '/' . trim($relativeDir, '/');
    if (!is_dir($fullDir)) {
        return [];
    }

    $files = array_diff(scandir($fullDir), ['.', '..', 'thumbnail']);
    $pictures = [];

    foreach ($files as $file) {
        $filePath = trim($relativeDir, '/') . '/' . $file;
        $pictures[] = buildAlbumPicturePayload($filePath);
    }

    return $pictures;
}

switch (strtoupper($method)) {
    case 'GET':
    case 'POST':
        $cle = $method === 'GET' ? ($_GET['cle'] ?? null) : ($obj['cle'] ?? null);

        if ($cle === null || $cle === '') {
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

        $dir = 'images/album/' . $cle;
        $pictures = listAlbumPictures($dir);

        echo json_encode([
            'reponse' => 'true',
            'newsPictureAlbum' => $pictures,
        ], JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
        break;

    case 'DELETE':
        $cle = $obj['cle'] ?? null;
        $picture = $obj['picture'] ?? null;

        if ($cle === null || $cle === '' || $picture === null || $picture === '') {
            echo json_encode([
                'reponse' => 'false',
                'place'   => 'tc',
                'message' => 'Clé ou photo manquante',
                'type'    => 'danger',
                'icon'    => 'nc-icon nc-bell-55',
                'autoDismiss' => 0,
            ]);
            break;
        }

        $dir = 'images/album/' . $cle;
        $normalizedPicture = ltrim($picture, '/');
        if (strpos($normalizedPicture, $dir) !== 0) {
            $normalizedPicture = trim($dir, '/') . '/' . basename($normalizedPicture);
        }

        $fullPath = rtrim($documentRoot, '/') . '/' . $normalizedPicture;
        $deleted = false;
        if (file_exists($fullPath)) {
            $deleted = unlink($fullPath);
        }

        $thumbDir = dirname($fullPath) . '/thumbnail';
        $thumbPath = $thumbDir . '/' . basename($fullPath);
        if (is_dir($thumbDir) && file_exists($thumbPath)) {
            unlink($thumbPath);
        }

        if ($deleted) {
            echo json_encode([
                'reponse' => 'true',
                'place'   => 'tc',
                'message' => 'Photo supprimée',
                'type'    => 'success',
                'icon'    => 'nc-icon nc-bell-55',
                'autoDismiss' => 2,
            ]);
        } else {
            echo json_encode([
                'reponse' => 'false',
                'place'   => 'tc',
                'message' => 'Photo introuvable',
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
