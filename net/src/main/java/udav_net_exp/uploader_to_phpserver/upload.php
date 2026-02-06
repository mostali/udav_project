<?PHP
  if(!empty($_FILES['uploaded_file']))
  {
    $headers=getallheaders();

    $path ='.';

    if(!empty($headers['parent'])){
        $path = $headers['parent'];
        $path = $path.'/';
        if ( !empty($path) && !file_exists($path) ) {
            mkdir($path, 0777, true);
        }
    }


    $path = $path . basename( $_FILES['uploaded_file']['name']);

    if(move_uploaded_file($_FILES['uploaded_file']['tmp_name'], $path)) {
     echo 'ok';
     // echo "The file ".  basename( $_FILES['uploaded_file']['name']).
     // " has been uploaded";
    } else{
        echo "There was an error uploading the file, please try again!";
    }
  }
?>