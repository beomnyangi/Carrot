<?php

    $headers = apache_request_headers(); // 클라이언트에서 넘어온 header배열을 저장

    // 배열로 된 헤더 정보 출력
    foreach ($headers as $header => $value) {
        //echo "$header : $value \n <br/>";

        // 헤더의 키 값이 request일때만 실행
        if($header == request){

            //특정 계정의 게시글 좋아요 여부를 확인하기 위함
            if($value == select){

                $Account_pn = $_POST[Account_pn];
                $Post_id = $_POST[Post_id];

                
                $host_name = 'ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com'; // 연결 할 db서버 주소
                $user_name = 'beom'; // db에 접속 할 계정 이름
                $password = 'Gozldgkwlak!724'; // db에 접속 할 계정 비밀번호
                $dbName = 'test_schema'; // 접속 할 db 이름
                $tbName = 'LikedPosts_Table'; // 접속 할 table 이름
                
                $conn = mysqli_connect($host_name, $user_name, $password, $dbName) or die ("mysql 접속 실패 \n <br/>"); // mysql에 연결
                //echo "mysql 접속 성공 \n <br/>";
                
                mysqli_select_db($conn, $dbName) or die ("db 접속 실패 $e"); // 데이터베이스 선택
                //echo "db 접속 성공 \n <br/>";

                $query = "SELECT LikePosts_id FROM $tbName where Post_id = $Post_id and Account_pn = $Account_pn";

                $result = mysqli_query($conn, $query)  or die ("쿼리 전송 실패");// 쿼리 전송
                //echo "쿼리 전송 성공 \n <br/>";
                
                mysqli_close($conn) or die ("mysql 연결 종료 실패");// mysql 연결 종료
                //echo "mysql 연결 종료 성공 \n <br/>";

                //$result = "phone_number:".$phone_number."\n address_name:".$town_name."\n x:".$x."\n y:".$y;
                //echo $result;
                $board = mysqli_fetch_array($result);
                echo $board['LikePosts_id'];
                //echo $result
            }

            //게시글의 좋아요 갯수를 찾기 위함
            else if($value == select_num){
                $Post_id = $_POST[Post_id];
                
                $host_name = 'ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com'; // 연결 할 db서버 주소
                $user_name = 'beom'; // db에 접속 할 계정 이름
                $password = 'Gozldgkwlak!724'; // db에 접속 할 계정 비밀번호
                $dbName = 'test_schema'; // 접속 할 db 이름
                $tbName = 'LikedPosts_Table'; // 접속 할 table 이름
                
                $conn = mysqli_connect($host_name, $user_name, $password, $dbName) or die ("mysql 접속 실패 \n <br/>"); // mysql에 연결
                //echo "mysql 접속 성공 \n <br/>";
                
                mysqli_select_db($conn, $dbName) or die ("db 접속 실패 $e"); // 데이터베이스 선택
                //echo "db 접속 성공 \n <br/>";

                //$query = "SELECT LikePosts_id FROM $tbName where Post_id = $Post_id and Account_pn = $Account_pn";
                $query = "SELECT COUNT(*) FROM $tbName where Post_id = $Post_id";

                $result = mysqli_query($conn, $query)  or die ("쿼리 전송 실패");// 쿼리 전송
                //echo "쿼리 전송 성공 \n <br/>";
                
                mysqli_close($conn) or die ("mysql 연결 종료 실패");// mysql 연결 종료
                //echo "mysql 연결 종료 성공 \n <br/>";

                //$result = "phone_number:".$phone_number."\n address_name:".$town_name."\n x:".$x."\n y:".$y;
                //echo $result;
                $board = mysqli_fetch_array($result);
                echo $board['COUNT(*)'];
            }
            
            //관심목록 리스트를 보여주기 위해 특정 계정의 좋아요 한 게시글 검색(페이징 적용 됨)
            else if($value == select_WatchList){
                $phone_number = $_POST[phone_number];
                $LIMIT = $_POST[LIMIT];
                $OFFSET = $_POST[OFFSET];
                
                $host_name = 'ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com'; // 연결 할 db서버 주소
                $user_name = 'beom'; // db에 접속 할 계정 이름
                $password = 'Gozldgkwlak!724'; // db에 접속 할 계정 비밀번호
                $dbName = 'test_schema'; // 접속 할 db 이름
                $tbName = 'LikedPosts_Table'; // 접속 할 table 이름
                
                $conn = mysqli_connect($host_name, $user_name, $password, $dbName) or die ("mysql 접속 실패 \n <br/>"); // mysql에 연결
                //echo "mysql 접속 성공 \n <br/>";
                
                mysqli_select_db($conn, $dbName) or die ("db 접속 실패 $e"); // 데이터베이스 선택
                //echo "db 접속 성공 \n <br/>";

                $query = "SELECT * FROM $tbName INNER JOIN HomePost_Table ON $tbName.Post_id = HomePost_Table.post_id where Account_pn = '$phone_number' ORDER BY LikePosts_id DESC LIMIT $LIMIT OFFSET $OFFSET";
                // $query = "SELECT * FROM $tbName ORDER BY post_id DESC LIMIT $LIMIT OFFSET $OFFSET";
                //$query = "SELECT LikePosts_id FROM $tbName where Account_pn = $phone_number";

                $result = mysqli_query($conn, $query)  or die ("쿼리 전송 실패");// 쿼리 전송
                //echo "쿼리 전송 성공 \n <br/>";
                
                mysqli_close($conn) or die ("mysql 연결 종료 실패");// mysql 연결 종료
                //echo "mysql 연결 종료 성공 \n <br/>";

                //$board = mysqli_fetch_array($result);
                //echo $board['LikePosts_id'];

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

            //관심목록 리스트 페이징을 위해 특정 계정의 좋아요 한 게시글 개수 검색
            else if($value == select_WatchList_count){
                $phone_number = $_POST[phone_number];
                
                $host_name = 'ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com'; // 연결 할 db서버 주소
                $user_name = 'beom'; // db에 접속 할 계정 이름
                $password = 'Gozldgkwlak!724'; // db에 접속 할 계정 비밀번호
                $dbName = 'test_schema'; // 접속 할 db 이름
                $tbName = 'LikedPosts_Table'; // 접속 할 table 이름
                
                $conn = mysqli_connect($host_name, $user_name, $password, $dbName) or die ("mysql 접속 실패 \n <br/>"); // mysql에 연결
                //echo "mysql 접속 성공 \n <br/>";
                
                mysqli_select_db($conn, $dbName) or die ("db 접속 실패 $e"); // 데이터베이스 선택
                //echo "db 접속 성공 \n <br/>";

                // $query = "SELECT * FROM $tbName INNER JOIN HomePost_Table ON $tbName.Post_id = HomePost_Table.post_id where Account_pn = '$phone_number' ORDER BY LikePosts_id DESC";
                $query = "SELECT COUNT(*) FROM $tbName WHERE Account_pn = $phone_number";
                //$query = "SELECT LikePosts_id FROM $tbName where Account_pn = $phone_number";

                $result = mysqli_query($conn, $query)  or die ("쿼리 전송 실패");// 쿼리 전송
                //echo "쿼리 전송 성공 \n <br/>";
                
                mysqli_close($conn) or die ("mysql 연결 종료 실패");// mysql 연결 종료
                //echo "mysql 연결 종료 성공 \n <br/>";

                //$board = mysqli_fetch_array($result);
                //echo $board['LikePosts_id'];

                $board = mysqli_fetch_array($result);
                echo $board['COUNT(*)'];
            }

            else{
                echo "header의 request키값의 value가 올바르지 않습니다. \n <br/>";
            }
        }
    }
    
?>