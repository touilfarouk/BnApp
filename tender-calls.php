<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
header('Access-Control-Allow-Methods: POST, GET, DELETE, UPDATE');
include('../db.credentials.php');

$method = $_SERVER['REQUEST_METHOD'];
$Received_JSON = file_get_contents('php://input');
$obj = json_decode($Received_JSON, true);
$year = date('Y');
switch ($method) {

        ////////////////////////////////
        //LOAD TENDER DATA FROM DATABASE
        ////////////////////////////////

    case 'GET':
        try {
            $bdd = new PDO("mysql:host=$servername;dbname=$dbname; charset=utf8mb4", $username, $password, array(PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION));

            if (isset($_GET['cle_appel_consultation']) && is_numeric($_GET['cle_appel_consultation'])) {
                //get tender by ID
                $req = $bdd->prepare('SELECT *,DATE_FORMAT(date_depot,"%Y-%m-%d") AS date_depot FROM appel_consultation WHERE cle_appel_consultation=?');
                $req->execute(array(
                    $_GET['cle_appel_consultation'],
                ));
                $tenderData = $req->fetch(PDO::FETCH_ASSOC);

                echo json_encode($tenderData);
            } else {
                //get all tenders
                $req = $bdd->query('SELECT nom_appel_consultation,DATE_FORMAT(date_depot,"%d/%m/%Y") AS date_depot,cle_appel_consultation FROM appel_consultation ORDER BY cle_appel_consultation DESC');
                $tendersList = [];
                while ($res = $req->fetch(PDO::FETCH_ASSOC)) {
                    $tendersList[] = $res;
                }
                echo json_encode($tendersList);
            }
        } catch (Exception $e) {
            $msg = $e->getMessage();
            echo json_encode(array("reponse" => "false", "place" => "tc", "message" => $msg, "type" => "danger", "icon" => "nc-icon nc-bell-55", "autoDismiss" => 0));
        }
        break;

        ////////////////////////////////
        //ADD TENDER TO DATABASE
        ////////////////////////////////
    case 'POST':
        $form = $obj['form'];
        if (!isset($form['nom_appel_consultation']) || !isset($form['date_depot']) || !isset($form['jour_depot']) || !isset($form['num_tender'])) {
            die(json_encode(array("reponse" => "false", "place" => "tc", "message" => "Certains champs sont manquants", "type" => "danger", "icon" => "nc-icon nc-bell-55", "autoDismiss" => 2)));
        }
        if ($form['num_tender'] < 0) {
            die(json_encode(array("reponse" => "false", "place" => "tc", "message" => "Le numéro de la lettre doit être un nombre positif!", "type" => "danger", "icon" => "nc-icon nc-bell-55", "autoDismiss" => 2)));
        }
        try {
            $bdd = new PDO("mysql:host=$servername;dbname=$dbname; charset=utf8mb4", $username, $password, array(PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION));

            $nom_appel_consultation = "LETTRE DE CONSULTATION N° " . $form['num_tender'] . '/' . $year . ' (' . $form['nom_appel_consultation'] . ')';

            $req = $bdd->prepare('INSERT INTO appel_consultation SET nom_appel_consultation=?,date_depot=?,jour_depot=?,num_tender=?');
            $req->execute(array(
                $nom_appel_consultation,
                $form['date_depot'],
                $form['jour_depot'],
                $form['num_tender']
            ));

            $req = $bdd->query('SELECT cle_appel_consultation FROM appel_consultation ORDER BY cle_appel_consultation DESC');
            $res = $req->fetch(PDO::FETCH_ASSOC);
            $cle_appel_consultation = $res['cle_appel_consultation'];
            echo json_encode(array("reponse" => "true", "cle_appel_consultation" => $cle_appel_consultation, "place" => "tc", "message" => "Appel à consultation ajouté!", "type" => "success", "icon" => "nc-icon nc-bell-55", "autoDismiss" => 2));
        } catch (Exception $e) {
            $msg = $e->getMessage();
            echo json_encode(array("reponse" => "false", "place" => "tc", "message" => $msg, "type" => "danger", "icon" => "nc-icon nc-bell-55", "autoDismiss" => 0));
        }

        break;

        ////////////////////////////////
        //DELETE TENDER FROM DATABASE
        ////////////////////////////////
    case 'DELETE':
        if (isset($obj['cle_appel_consultation']) && $obj['cle_appel_consultation'] != "") {

            try {
                $bdd = new PDO("mysql:host=$servername;dbname=$dbname; charset=utf8mb4", $username, $password, array(PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION));

                $req = $bdd->prepare('DELETE FROM appel_consultation WHERE cle_appel_consultation=?');
                $req->execute(array(
                    $obj['cle_appel_consultation']
                ));

                //delete tender files
                $req = $bdd->prepare('SELECT * FROM appel_documents WHERE cle_appel_consultation=?');
                $req->execute(array(
                    $obj['cle_appel_consultation']
                ));

                while ($res = $req->fetch(PDO::FETCH_ASSOC)) {
                    $path = $res['files'];
                    unlink('../../' . $path);
                }
                //delete tender from database
                $req = $bdd->prepare('DELETE FROM appel_documents WHERE cle_appel_consultation=?');
                $req->execute(array(
                    $obj['cle_appel_consultation']
                ));

                echo json_encode(array("reponse" => "true", "place" => "tc", "message" => "Appel à consultation supprimé!", "type" => "success", "icon" => "nc-icon nc-bell-55", "autoDismiss" => 2));
            } catch (Exception $e) {
                $msg = $e->getMessage();
                echo json_encode(array("reponse" => "false", "place" => "tc", "message" => $msg, "type" => "danger", "icon" => "nc-icon nc-bell-55", "autoDismiss" => 0));
            }
        } else {
            echo json_encode(array("reponse" => "false", "place" => "tc", "message" => "Impossible de supprimer l'appel", "type" => "danger", "icon" => "nc-icon nc-bell-55", "autoDismiss" => 0));
        }
        break;

        ////////////////////////////////
        //UPDATE TENDER IN DATABASE
        ////////////////////////////////
    case 'UPDATE':
        $form = $obj['form'];
        if (!isset($form['nom_appel_consultation']) || !isset($form['date_depot']) || !isset($form['jour_depot']) || !isset($form['num_tender'])) {
            echo json_encode(array("reponse" => "false", "place" => "tc", "message" => "Certains champs sont manquants", "type" => "danger", "icon" => "nc-icon nc-bell-55", "autoDismiss" => 2));
        } else {
            try {
                $bdd = new PDO("mysql:host=$servername;dbname=$dbname; charset=utf8mb4", $username, $password, array(PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION));

                $attribution = $form['attribution'] === "null" ? null : $form['attribution'];

                $req = $bdd->prepare('UPDATE appel_consultation SET nom_appel_consultation=?,date_depot=?,jour_depot=?,date_fin_evaluation=?,attribution=?,num_tender=? WHERE cle_appel_consultation=?');
                $req->execute(array(
                    $form['nom_appel_consultation'],
                    $form['date_depot'],
                    $form['jour_depot'],
                    $form['date_fin_evaluation'],
                    $attribution,
                    $form['num_tender'],
                    $form['cle_appel_consultation']
                ));

                echo json_encode(array("reponse" => "true", "place" => "tc", "message" => "Appel à consultation modifié!", "type" => "success", "icon" => "nc-icon nc-bell-55", "autoDismiss" => 2));
            } catch (Exception $e) {
                $msg = $e->getMessage();
                echo json_encode(array("reponse" => "false", "place" => "tc", "message" => $msg, "type" => "danger", "icon" => "nc-icon nc-bell-55", "autoDismiss" => 0));
            }
        }

        break;
}
