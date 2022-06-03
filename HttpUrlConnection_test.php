<?php

    $headers = apache_request_headers(); // 클라이언트에서 넘어온 header배열을 저장

    // 배열로 된 헤더 정보 출력
    foreach ($headers as $header => $value) {
        echo "$header : $value \n <br/>";

        // 헤더의 키 값이 request일때만 실행
        if($header == request){
            // 헤더의 request의 value가 save일때만 실행
            if($value == insert){
                $town_name = $_POST[address_name];
                $phone_number = $_POST[phone_number];
                $x = $_POST[x];
                $y = $_POST[y];
                
                $host_name = 'ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com'; // 연결 할 db서버 주소
                $user_name = 'beom'; // db에 접속 할 계정 이름
                $password = 'Gozldgkwlak!724'; // db에 접속 할 계정 비밀번호
                $dbName = 'test_schema'; // 접속 할 db 이름
                $tbName = 'userdata_test'; // 접속 할 table 이름
                
                $conn = mysqli_connect($host_name, $user_name, $password, $dbName) or die ("mysql 접속 실패 \n <br/>");; // mysql에 연결
                echo "mysql 접속 성공 \n <br/>";
                
                mysqli_select_db($conn, $dbName) or die ("db 접속 실패 $e"); // 데이터베이스 선택
                echo "db 접속 성공 \n <br/>";

                $query = "INSERT INTO $tbName (`PhoneNumber`, `TownName_1`, `X_1`, `Y_1`) VALUES ('$phone_number', '$town_name', '$x', '$y')";

                mysqli_query($conn, $query)  or die ("쿼리 전송 실패");// 쿼리 전송
                echo "쿼리 전송 성공 \n <br/>";
                
                mysqli_close($conn) or die ("mysql 연결 종료 실패");// mysql 연결 종료
                echo "mysql 연결 종료 성공 \n <br/>";

                $result = "phone_number:".$phone_number."\n address_name:".$town_name."\n x:".$x."\n y:".$y;
                echo $result;
            }
            else if($value == select){
                $phone_number = $_POST[phone_number];
                
                $host_name = 'ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com'; // 연결 할 db서버 주소
                $user_name = 'beom'; // db에 접속 할 계정 이름
                $password = 'Gozldgkwlak!724'; // db에 접속 할 계정 비밀번호
                $dbName = 'test_schema'; // 접속 할 db 이름
                $tbName = 'userdata_test'; // 접속 할 table 이름
                
                $conn = mysqli_connect($host_name, $user_name, $password, $dbName) or die ("mysql 접속 실패 \n <br/>");; // mysql에 연결
                echo "mysql 접속 성공 \n <br/>";
                
                mysqli_select_db($conn, $dbName) or die ("db 접속 실패 $e"); // 데이터베이스 선택
                echo "db 접속 성공 \n <br/>";

                $query = "SELECT TownName_1 FROM $tbName where PhoneNumber = $phone_number";

                $result = mysqli_query($conn, $query)  or die ("쿼리 전송 실패");// 쿼리 전송
                echo "쿼리 전송 성공 \n <br/>";
                
                mysqli_close($conn) or die ("mysql 연결 종료 실패");// mysql 연결 종료
                echo "mysql 연결 종료 성공 \n <br/>";

                // $result = "phone_number:".$phone_number."\n address_name:".$town_name."\n x:".$x."\n y:".$y;
                echo $result;
            }
            else{
                echo "header의 request키값의 value가 올바르지 않습니다. \n <br/>";
            }
        }
    }
    
?>