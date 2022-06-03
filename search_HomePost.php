<?php

    $headers = apache_request_headers(); // 클라이언트에서 넘어온 header배열을 저장
    
    // 배열로 된 헤더 정보 출력
    foreach ($headers as $header => $value) {
        //echo "$header : $value \n <br/>";

        // 헤더의 키 값이 request일때만 실행
        if($header == request){
            
            // 전체 게시물을 보여주기 위한 쿼리
            if($value == select){

                $phone_number = $_POST[phone_number];
                $LIMIT = $_POST[LIMIT];
                $OFFSET = $_POST[OFFSET];
                
                $host_name = 'ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com'; // 연결 할 db서버 주소
                $user_name = 'beom'; // db에 접속 할 계정 이름
                $password = 'Gozldgkwlak!724'; // db에 접속 할 계정 비밀번호
                $dbName = 'test_schema'; // 접속 할 db 이름
                $tbName = 'HomePost_Table'; // 접속 할 table 이름
                
                header('Content-Type: application/json; charset=UTF-8'); // JSON 형태로 데이터 출력

                $conn = mysqli_connect($host_name, $user_name, $password, $dbName) or die ("mysql 접속 실패 \n <br/>"); // mysql에 연결
                //echo "mysql 접속 성공 \n <br/>";
                
                mysqli_select_db($conn, $dbName) or die ("db 접속 실패 $e"); // 데이터베이스 선택
                //echo "db 접속 성공 \n <br/>";
                
                $query = "SELECT * FROM $tbName ORDER BY post_id DESC LIMIT $LIMIT OFFSET $OFFSET";

                $result = mysqli_query($conn, $query)  or die ("쿼리 전송 실패");// 쿼리 전송
                //echo "쿼리 전송 성공 \n <br/>";
                
                mysqli_close($conn) or die ("mysql 연결 종료 실패");// mysql 연결 종료
                //echo "mysql 연결 종료 성공 \n <br/>";
                
                $main_array = array();
                $output = array(); // 응답값으로 보낼 값

                $main_array['message'] = "성공";

                if (mysqli_num_rows($result) > 0) { // 쿼리 결과로 1행 이상 존재한다면
                    while ($row = mysqli_fetch_assoc($result)) { // 행별로 유저의 정보 output에 넣어주기
                        array_push($output,
                            array('post_id' => $row['post_id'],
                            'Account_ID' => $row['Account_ID'],
                            'image' => $row['image'],
                            'title' => $row['title'],
                            'TownName' => $row['TownName'],
                            'uploaded_time' => $row['uploaded_time'],
                            'upload_count' => $row['upload_count'],
                            'price' => $row['price'],
                            'chatting_count' => $row['chatting_count'],
                            'Like_count' => $row['Like_count'],
                            'PriceOffer_check' => $row['PriceOffer_check'],
                            'views' => $row['views'],
                            'SalesStatus' => $row['SalesStatus']
                            )
                        );
                        $main_array['contents'] = $output;
                    }
                    
                } else { // 쿼리 결과가 없다면 메시지 보내주기
                $output = array('message' => '쿼리 결과 없음');
                }

                $main_array =  json_encode($main_array, JSON_UNESCAPED_UNICODE);
                echo $main_array; // array를 json형태로 변환하여 출력

            }

            // 홈화면을 페이징 하기위해 전체 게시물 갯수를 구하는 쿼리
            else if($value == count){
                
                $host_name = 'ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com'; // 연결 할 db서버 주소
                $user_name = 'beom'; // db에 접속 할 계정 이름
                $password = 'Gozldgkwlak!724'; // db에 접속 할 계정 비밀번호
                $dbName = 'test_schema'; // 접속 할 db 이름
                $tbName = 'HomePost_Table'; // 접속 할 table 이름

                $conn = mysqli_connect($host_name, $user_name, $password, $dbName) or die ("mysql 접속 실패 \n <br/>"); // mysql에 연결
                //echo "mysql 접속 성공 \n <br/>";
                
                mysqli_select_db($conn, $dbName) or die ("db 접속 실패 $e"); // 데이터베이스 선택
                //echo "db 접속 성공 \n <br/>";

                $query = "SELECT COUNT(*) FROM $tbName";

                $result = mysqli_query($conn, $query)  or die ("쿼리 전송 실패");// 쿼리 전송
                //echo "쿼리 전송 성공 \n <br/>";
                
                mysqli_close($conn) or die ("mysql 연결 종료 실패");// mysql 연결 종료
                //echo "mysql 연결 종료 성공 \n <br/>";

                $board = mysqli_fetch_array($result);
                echo $board['COUNT(*)'];

            }

            // 검색어로 게시물을 검색하기 위한 쿼리(페이징 적용 됨)
            else if($value == filter){
                $key_word = $_POST[key_word];
                $LIMIT = $_POST[LIMIT];
                $OFFSET = $_POST[OFFSET];
                
                $host_name = 'ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com'; // 연결 할 db서버 주소
                $user_name = 'beom'; // db에 접속 할 계정 이름
                $password = 'Gozldgkwlak!724'; // db에 접속 할 계정 비밀번호
                $dbName = 'test_schema'; // 접속 할 db 이름
                $tbName = 'HomePost_Table'; // 접속 할 table 이름
                
                header('Content-Type: application/json; charset=UTF-8'); // JSON 형태로 데이터 출력

                $conn = mysqli_connect($host_name, $user_name, $password, $dbName) or die ("mysql 접속 실패 \n <br/>"); // mysql에 연결
                //echo "mysql 접속 성공 \n <br/>";
                
                mysqli_select_db($conn, $dbName) or die ("db 접속 실패 $e"); // 데이터베이스 선택
                //echo "db 접속 성공 \n <br/>";

                $query = "SELECT * FROM $tbName WHERE title LIKE '%$key_word%' OR contents LIKE '%$key_word%' ORDER BY post_id DESC LIMIT $LIMIT OFFSET $OFFSET";
                
                $result = mysqli_query($conn, $query)  or die ("쿼리 전송 실패");// 쿼리 전송
                //echo "쿼리 전송 성공 \n <br/>";
                
                mysqli_close($conn) or die ("mysql 연결 종료 실패");// mysql 연결 종료
                //echo "mysql 연결 종료 성공 \n <br/>";
                
                $main_array = array();
                $output = array(); // 응답값으로 보낼 값

                $main_array['message'] = "성공";

                if (mysqli_num_rows($result) > 0) { // 쿼리 결과로 1행 이상 존재한다면
                    while ($row = mysqli_fetch_assoc($result)) { // 행별로 유저의 정보 output에 넣어주기
                        array_push($output,
                            array('post_id' => $row['post_id'],
                            'Account_ID' => $row['Account_ID'],
                            'image' => $row['image'],
                            'title' => $row['title'],
                            'TownName' => $row['TownName'],
                            'uploaded_time' => $row['uploaded_time'],
                            'upload_count' => $row['upload_count'],
                            'price' => $row['price'],
                            'chatting_count' => $row['chatting_count'],
                            'Like_count' => $row['Like_count'],
                            'PriceOffer_check' => $row['PriceOffer_check'],
                            'views' => $row['views'],
                            'SalesStatus' => $row['SalesStatus']
                            )
                        );
                        $main_array['contents'] = $output;
                    }
                    
                } else { // 쿼리 결과가 없다면 메시지 보내주기
                $output = array('message' => '쿼리 결과 없음');
                }

                $main_array =  json_encode($main_array, JSON_UNESCAPED_UNICODE);
                echo $main_array; // array를 json형태로 변환하여 출력

            }

            // 게시물 검색화면을 페이징 하기위해 검색 한 데이터 갯수를 구하는 쿼리
            else if($value == filter_count){
                $key_word = $_POST[key_word];
                
                $host_name = 'ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com'; // 연결 할 db서버 주소
                $user_name = 'beom'; // db에 접속 할 계정 이름
                $password = 'Gozldgkwlak!724'; // db에 접속 할 계정 비밀번호
                $dbName = 'test_schema'; // 접속 할 db 이름
                $tbName = 'HomePost_Table'; // 접속 할 table 이름
                
                header('Content-Type: application/json; charset=UTF-8'); // JSON 형태로 데이터 출력

                $conn = mysqli_connect($host_name, $user_name, $password, $dbName) or die ("mysql 접속 실패 \n <br/>"); // mysql에 연결
                //echo "mysql 접속 성공 \n <br/>";
                
                mysqli_select_db($conn, $dbName) or die ("db 접속 실패 $e"); // 데이터베이스 선택
                //echo "db 접속 성공 \n <br/>";

                $query = "SELECT COUNT(*) FROM $tbName WHERE title LIKE '%$key_word%' OR contents LIKE '%$key_word%'";
                
                $result = mysqli_query($conn, $query)  or die ("쿼리 전송 실패");// 쿼리 전송
                //echo "쿼리 전송 성공 \n <br/>";
                
                mysqli_close($conn) or die ("mysql 연결 종료 실패");// mysql 연결 종료
                //echo "mysql 연결 종료 성공 \n <br/>";
                
                $board = mysqli_fetch_array($result);
                echo $board['COUNT(*)'];

            }

            // 프로필 정보에 특정 사용자가 작성한 전체 게시글 갯수를 구하는 쿼리 & 판매 상품 보기 화면에서 전체 부분을 페이징 하기위해 전체 게시물 갯수를 구하는 쿼리
            else if($value == HomePost_count){
                $phone_number = $_POST[phone_number];
                
                $host_name = 'ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com'; // 연결 할 db서버 주소
                $user_name = 'beom'; // db에 접속 할 계정 이름
                $password = 'Gozldgkwlak!724'; // db에 접속 할 계정 비밀번호
                $dbName = 'test_schema'; // 접속 할 db 이름
                $tbName = 'HomePost_Table'; // 접속 할 table 이름
                
                header('Content-Type: application/json; charset=UTF-8'); // JSON 형태로 데이터 출력

                $conn = mysqli_connect($host_name, $user_name, $password, $dbName) or die ("mysql 접속 실패 \n <br/>"); // mysql에 연결
                //echo "mysql 접속 성공 \n <br/>";
                
                mysqli_select_db($conn, $dbName) or die ("db 접속 실패 $e"); // 데이터베이스 선택
                //echo "db 접속 성공 \n <br/>";
                
                $query = "SELECT COUNT(*) FROM $tbName WHERE PhoneNumber = $phone_number";

                // SELECT * FROM test_schema.HomePost_Table WHERE title LIKE '%%' OR contents LIKE '%%';

                $result = mysqli_query($conn, $query)  or die ("쿼리 전송 실패");// 쿼리 전송
                //echo "쿼리 전송 성공 \n <br/>";
                
                mysqli_close($conn) or die ("mysql 연결 종료 실패");// mysql 연결 종료
                //echo "mysql 연결 종료 성공 \n <br/>";
                
                $board = mysqli_fetch_array($result);
                echo $board['COUNT(*)'];

            }

            // 판매 상품 보기 화면에서 거래상태에 따라 다르게 페이징 하기위해 전체 게시물 갯수를 구하는 쿼리
            else if($value == HomePostStatus_count){
                $phone_number = $_POST[phone_number];
                $SalesStatus = $_POST[SalesStatus];

                $host_name = 'ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com'; // 연결 할 db서버 주소
                $user_name = 'beom'; // db에 접속 할 계정 이름
                $password = 'Gozldgkwlak!724'; // db에 접속 할 계정 비밀번호
                $dbName = 'test_schema'; // 접속 할 db 이름
                $tbName = 'HomePost_Table'; // 접속 할 table 이름
                
                header('Content-Type: application/json; charset=UTF-8'); // JSON 형태로 데이터 출력

                $conn = mysqli_connect($host_name, $user_name, $password, $dbName) or die ("mysql 접속 실패 \n <br/>"); // mysql에 연결
                //echo "mysql 접속 성공 \n <br/>";
                
                mysqli_select_db($conn, $dbName) or die ("db 접속 실패 $e"); // 데이터베이스 선택
                //echo "db 접속 성공 \n <br/>";
                
                if($SalesStatus == "판매중"){
                    $query = "SELECT COUNT(*) FROM $tbName WHERE PhoneNumber = '$phone_number' AND SalesStatus = '판매중' OR SalesStatus = '예약중'";
                }
                else if($SalesStatus == "거래완료"){
                    $query = "SELECT COUNT(*) FROM $tbName WHERE PhoneNumber = '$phone_number' AND SalesStatus = '거래완료'";
                }
                // $query = "SELECT COUNT(*) FROM $tbName WHERE PhoneNumber = '$phone_number' AND SalesStatus = '$SalesStatus'";

                // SELECT * FROM test_schema.HomePost_Table WHERE title LIKE '%%' OR contents LIKE '%%';

                $result = mysqli_query($conn, $query)  or die ("쿼리 전송 실패");// 쿼리 전송
                //echo "쿼리 전송 성공 \n <br/>";
                
                mysqli_close($conn) or die ("mysql 연결 종료 실패");// mysql 연결 종료
                //echo "mysql 연결 종료 성공 \n <br/>";
                
                $board = mysqli_fetch_array($result);
                echo $board['COUNT(*)'];

            }

            // 특정 사용자의 전체 게시글만 보여주기 위해 게시글 검색
            else if($value == select_UserHomePost){
                $phone_number = $_POST[phone_number];
                $LIMIT = $_POST[LIMIT];
                $OFFSET = $_POST[OFFSET];
                
                $host_name = 'ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com'; // 연결 할 db서버 주소
                $user_name = 'beom'; // db에 접속 할 계정 이름
                $password = 'Gozldgkwlak!724'; // db에 접속 할 계정 비밀번호
                $dbName = 'test_schema'; // 접속 할 db 이름
                $tbName = 'HomePost_Table'; // 접속 할 table 이름
                
                header('Content-Type: application/json; charset=UTF-8'); // JSON 형태로 데이터 출력

                $conn = mysqli_connect($host_name, $user_name, $password, $dbName) or die ("mysql 접속 실패 \n <br/>"); // mysql에 연결
                //echo "mysql 접속 성공 \n <br/>";
                
                mysqli_select_db($conn, $dbName) or die ("db 접속 실패 $e"); // 데이터베이스 선택
                //echo "db 접속 성공 \n <br/>";
                
                $query = "SELECT * FROM $tbName WHERE PhoneNumber = $phone_number ORDER BY post_id DESC LIMIT $LIMIT OFFSET $OFFSET";

                $result = mysqli_query($conn, $query)  or die ("쿼리 전송 실패");// 쿼리 전송
                //echo "쿼리 전송 성공 \n <br/>";
                
                mysqli_close($conn) or die ("mysql 연결 종료 실패");// mysql 연결 종료
                //echo "mysql 연결 종료 성공 \n <br/>";
                
                $main_array = array();
                $output = array(); // 응답값으로 보낼 값

                $main_array['message'] = "성공";

                if (mysqli_num_rows($result) > 0) { // 쿼리 결과로 1행 이상 존재한다면
                    while ($row = mysqli_fetch_assoc($result)) { // 행별로 유저의 정보 output에 넣어주기
                        array_push($output,
                            array('post_id' => $row['post_id'],
                            'Account_ID' => $row['Account_ID'],
                            'image' => $row['image'],
                            'title' => $row['title'],
                            'TownName' => $row['TownName'],
                            'uploaded_time' => $row['uploaded_time'],
                            'upload_count' => $row['upload_count'],
                            'price' => $row['price'],
                            'chatting_count' => $row['chatting_count'],
                            'Like_count' => $row['Like_count'],
                            'PriceOffer_check' => $row['PriceOffer_check'],
                            'views' => $row['views'],
                            'SalesStatus' => $row['SalesStatus']
                            )
                        );
                        $main_array['contents'] = $output;
                    }
                    
                } else { // 쿼리 결과가 없다면 메시지 보내주기
                $output = array('message' => '쿼리 결과 없음');
                }

                $main_array =  json_encode($main_array, JSON_UNESCAPED_UNICODE);
                echo $main_array; // array를 json형태로 변환하여 출력

            }

            // 특정 사용자의 거래상태에 따라 다르게 게시글을 보여주기 위해 게시글 검색
            else if($value == select_UserHomePostStatus){
                $phone_number = $_POST[phone_number];
                $SalesStatus = $_POST[SalesStatus];
                $LIMIT = $_POST[LIMIT];
                $OFFSET = $_POST[OFFSET];
                
                $host_name = 'ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com'; // 연결 할 db서버 주소
                $user_name = 'beom'; // db에 접속 할 계정 이름
                $password = 'Gozldgkwlak!724'; // db에 접속 할 계정 비밀번호
                $dbName = 'test_schema'; // 접속 할 db 이름
                $tbName = 'HomePost_Table'; // 접속 할 table 이름
                
                header('Content-Type: application/json; charset=UTF-8'); // JSON 형태로 데이터 출력

                $conn = mysqli_connect($host_name, $user_name, $password, $dbName) or die ("mysql 접속 실패 \n <br/>"); // mysql에 연결
                //echo "mysql 접속 성공 \n <br/>";
                
                mysqli_select_db($conn, $dbName) or die ("db 접속 실패 $e"); // 데이터베이스 선택
                //echo "db 접속 성공 \n <br/>";
                
                if($SalesStatus == "판매중"){

                    $query = "SELECT * FROM $tbName WHERE PhoneNumber = '$phone_number' AND SalesStatus = '판매중' OR SalesStatus = '예약중' ORDER BY post_id DESC LIMIT $LIMIT OFFSET $OFFSET";
                }
                else if($SalesStatus == "거래완료"){
                    $query = "SELECT * FROM $tbName WHERE PhoneNumber = '$phone_number' AND SalesStatus = '거래완료' ORDER BY post_id DESC LIMIT $LIMIT OFFSET $OFFSET";
                }

                // $query = "SELECT * FROM $tbName WHERE PhoneNumber = '$phone_number' AND SalesStatus = '$SalesStatus' ORDER BY post_id DESC LIMIT $LIMIT OFFSET $OFFSET";

                $result = mysqli_query($conn, $query)  or die ("쿼리 전송 실패");// 쿼리 전송
                //echo "쿼리 전송 성공 \n <br/>";
                
                mysqli_close($conn) or die ("mysql 연결 종료 실패");// mysql 연결 종료
                //echo "mysql 연결 종료 성공 \n <br/>";
                
                $main_array = array();
                $output = array(); // 응답값으로 보낼 값

                $main_array['message'] = "성공";

                if (mysqli_num_rows($result) > 0) { // 쿼리 결과로 1행 이상 존재한다면
                    while ($row = mysqli_fetch_assoc($result)) { // 행별로 유저의 정보 output에 넣어주기
                        array_push($output,
                            array('post_id' => $row['post_id'],
                            'Account_ID' => $row['Account_ID'],
                            'image' => $row['image'],
                            'title' => $row['title'],
                            'TownName' => $row['TownName'],
                            'uploaded_time' => $row['uploaded_time'],
                            'upload_count' => $row['upload_count'],
                            'price' => $row['price'],
                            'chatting_count' => $row['chatting_count'],
                            'Like_count' => $row['Like_count'],
                            'PriceOffer_check' => $row['PriceOffer_check'],
                            'views' => $row['views'],
                            'SalesStatus' => $row['SalesStatus']
                            )
                        );
                        $main_array['contents'] = $output;
                    }
                    
                } else { // 쿼리 결과가 없다면 메시지 보내주기
                $output = array('message' => '쿼리 결과 없음');
                }

                $main_array =  json_encode($main_array, JSON_UNESCAPED_UNICODE);
                echo $main_array; // array를 json형태로 변환하여 출력

            }

            else{
                echo "header의 request키값의 value가 올바르지 않습니다. \n <br/>";
            }

        }
    }
    
?>