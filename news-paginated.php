<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
header('Access-Control-Allow-Methods: POST, GET, DELETE, UPDATE');
header('Content-Type: application/json; charset=utf-8');
include('../db.credentials.php');

$method = $_SERVER['REQUEST_METHOD'];
$Received_JSON = file_get_contents('php://input');
$obj = json_decode($Received_JSON, true);

switch ($method) {
    case 'GET':
        try {
            $bdd = new PDO("mysql:host=$servername;dbname=$dbname; charset=utf8mb4", $username, $password, array(PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION));

            if (isset($_GET['cle']) && is_numeric($_GET['cle'])) {
                //get news by cle
                $req = $bdd->prepare('SELECT rubrique,image_id,titre,titre_ar,date,cle,contenu,contenu_ar FROM actualite WHERE cle=?');
                $req->execute(array(
                    $_GET['cle'],
                ));
                $newsData = $req->fetch(PDO::FETCH_ASSOC);
                $newsData['image_id'] = "https://bneder.dz/" . $newsData['image_id'];

                echo json_encode(array("reponse" => "true", "newsData" => $newsData));
            } else {
                $page = isset($_GET['page']) ? max(1, (int)$_GET['page']) : 1;
                $pageSize = isset($_GET['pageSize']) ? min(100, max(1, (int)$_GET['pageSize'])) : 10;
                $offset = ($page - 1) * $pageSize;
                $search = isset($_GET['search']) ? trim($_GET['search']) : '';

                $baseSql = 'FROM actualite a';
                $params = [];
                $whereParts = [];

                if ($search !== '') {
                    $whereParts[] = '(a.titre LIKE :search OR a.titre_ar LIKE :search)';
                    $params[':search'] = "%$search%";
                }

                $whereClause = '';
                if (!empty($whereParts)) {
                    $whereClause = ' WHERE ' . implode(' AND ', $whereParts);
                }

                $countStmt = $bdd->prepare("SELECT COUNT(*) $baseSql $whereClause");
                foreach ($params as $key => $value) {
                    $countStmt->bindValue($key, $value, PDO::PARAM_STR);
                }
                $countStmt->execute();
                $totalItems = (int)$countStmt->fetchColumn();

                $sql = "
                    SELECT
                        a.rubrique,
                        a.image_id,
                        a.titre,
                        a.titre_ar,
                        DATE_FORMAT(a.date,'%d/%m/%Y') AS date,
                        a.cle
                    $baseSql
                    $whereClause
                    ORDER BY a.cle DESC
                    LIMIT :offset, :limit
                ";

                $stmt = $bdd->prepare($sql);
                foreach ($params as $key => $value) {
                    $stmt->bindValue($key, $value, PDO::PARAM_STR);
                }
                $stmt->bindValue(':offset', $offset, PDO::PARAM_INT);
                $stmt->bindValue(':limit', $pageSize, PDO::PARAM_INT);
                $stmt->execute();

                $newsList = $stmt->fetchAll(PDO::FETCH_ASSOC);

                foreach ($newsList as &$news) {
                    if (!empty($news['image_id'])) {
                        $news['pic_path'] = 'https://bneder.dz/' . ltrim($news['image_id'], '/');
                    } else {
                        $news['pic_path'] = null;
                    }
                    unset($news['image_id']);
                    $news['cle'] = (int)$news['cle'];
                }
                unset($news);

                $totalPages = max(1, (int)ceil($totalItems / $pageSize));

                echo json_encode([
                    'reponse' => 'true',
                    'newsList' => $newsList,
                    'data' => $newsList,
                    'pagination' => [
                        'currentPage' => $page,
                        'pageSize' => $pageSize,
                        'totalItems' => $totalItems,
                        'totalPages' => $totalPages,
                        'hasNext' => $page < $totalPages,
                        'hasPrevious' => $page > 1,
                    ],
                ], JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
            }
        } catch (Exception $e) {
            $msg = $e->getMessage();
            echo json_encode(array("reponse" => "false", "place" => "tc", "message" => $msg, "type" => "danger", "icon" => "nc-icon nc-bell-55", "autoDismiss" => 0));
        }
        break;

    case 'POST':
        $form = $obj['form'];
        if (!isset($form['titre']) || !isset($form['contenu']) || !isset($form['rubrique']) && isset($form['date'])) {
            echo json_encode(array("reponse" => "false", "place" => "tc", "message" => "Certains champs sont manquants", "type" => "danger", "icon" => "nc-icon nc-bell-55", "autoDismiss" => 2));
        } else {
            try {
                $bdd = new PDO("mysql:host=$servername;dbname=$dbname; charset=utf8mb4", $username, $password, array(PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION));

                $req = $bdd->prepare('INSERT INTO actualite SET titre=?,titre_ar=?,rubrique=?,contenu=?,contenu_ar=?,date=?');
                $req->execute(array(
                    $form['titre'],
                    $form['titre_ar'],
                    $form['rubrique'],
                    $form['contenu'],
                    $form['contenu_ar'],
                    $form['date']
                ));

                $req = $bdd->query('SELECT cle FROM actualite ORDER BY cle DESC');
                $res = $req->fetch(PDO::FETCH_ASSOC);
                $cle = $res['cle'];
                echo json_encode(array("reponse" => "true", "cle" => $cle, "place" => "tc", "message" => "Article ajouté!", "type" => "success", "icon" => "nc-icon nc-bell-55", "autoDismiss" => 2));
            } catch (Exception $e) {
                $msg = $e->getMessage();
                echo json_encode(array("reponse" => "false", "place" => "tc", "message" => $msg, "type" => "danger", "icon" => "nc-icon nc-bell-55", "autoDismiss" => 0));
            }
        }
        break;
    case 'DELETE':
        if (isset($obj['cle']) && $obj['cle'] != "") {

            try {
                $bdd = new PDO("mysql:host=$servername;dbname=$dbname; charset=utf8mb4", $username, $password, array(PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION));
                $req = $bdd->prepare('DELETE FROM actualite WHERE cle=?');
                $req->execute(array(
                    $obj['cle']
                ));
                $bdd = new PDO("mysql:host=$servername;dbname=$dbname; charset=utf8mb4", $username, $password, array(PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION));
                $req = $bdd->prepare('DELETE FROM actualite_photos WHERE id_news=?');
                $req->execute(array(
                    $obj['cle']
                ));

                //delete news picture folder
                array_map('unlink', glob("../../images/actus/" . $obj['cle'] . "/*.*"));
                rmdir('../../images/actus/' . $obj['cle']);

                echo json_encode(array("reponse" => "true", "place" => "tc", "message" => "Article supprimé!", "type" => "success", "icon" => "nc-icon nc-bell-55", "autoDismiss" => 2));
            } catch (Exception $e) {
                $msg = $e->getMessage();
                echo json_encode(array("reponse" => "false", "place" => "tc", "message" => $msg, "type" => "danger", "icon" => "nc-icon nc-bell-55", "autoDismiss" => 0));
            }
        } else {
            echo json_encode(array("reponse" => "false", "place" => "tc", "message" => "Impossible de supprimer l'article", "type" => "danger", "icon" => "nc-icon nc-bell-55", "autoDismiss" => 0));
        }
        break;
    case 'UPDATE':
        $form = $obj['form'];
        if (!isset($form['titre']) || !isset($form['contenu']) || !isset($form['rubrique']) && isset($form['date'])) {
            echo json_encode(array("reponse" => "false", "place" => "tc", "message" => "Certains champs sont manquants", "type" => "danger", "icon" => "nc-icon nc-bell-55", "autoDismiss" => 2));
        } else {
            try {
                $bdd = new PDO("mysql:host=$servername;dbname=$dbname; charset=utf8mb4", $username, $password, array(PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION));

                $req = $bdd->prepare('UPDATE actualite SET titre=?,titre_ar=?,rubrique=?,contenu=?,contenu_ar=?,date=? WHERE cle=?');
                $req->execute(array(
                    $form['titre'],
                    $form['titre_ar'],
                    $form['rubrique'],
                    $form['contenu'],
                    $form['contenu_ar'],
                    $form['date'],
                    $form['cle']
                ));

                echo json_encode(array("reponse" => "true", "place" => "tc", "message" => "Article modifié!", "type" => "success", "icon" => "nc-icon nc-bell-55", "autoDismiss" => 2));
            } catch (Exception $e) {
                $msg = $e->getMessage();
                echo json_encode(array("reponse" => "false", "place" => "tc", "message" => $msg, "type" => "danger", "icon" => "nc-icon nc-bell-55", "autoDismiss" => 0));
            }
        }

        break;
}
