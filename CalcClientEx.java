package Clock;

import java.io.*;
import java.net.*;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CalcClientEx {
	public static void main(String[] args) {
		BufferedReader in = null; //입출력 변수 선언 및 초기화
		BufferedWriter out = null; //입출력 변수 선언 및 초기화
		Socket socket = null; //소켓 선언 및 초기화
		Scanner scanner = new Scanner(System.in); //키보드 입력 스캐너 선언
		
        String serverInfo = "localhost 1234"; //서버정보(ip, port) 기본값 지정
        
        try {
			BufferedReader fileReader = new BufferedReader(new FileReader("C:\\Myprogram\\HW1_src_김진형\\src\\Clock\\server_info.dat")); // 주소에서 데이터파일 가져오기
			String line; //해당 정보를 저장할 임시 변수 생성
			if ((line = fileReader.readLine()) != null) {
				serverInfo = line; // 파일에서 읽은 정보가 있을 때 서버 정보 변경
			}
			fileReader.close();
		} catch (FileNotFoundException e) {
			// 파일이 없을 때
			System.out.println("server_info.data 파일이 없습니다. 기본값을 사용합니다."); //값 변경 없이 기본값 사용 
		} catch (IOException e) {
			e.printStackTrace(); //예외 처리
		}
        
        String[] serverInfoArray = serverInfo.split(" "); //빈칸 기준으로 함수 배열 나누기
		String serverIP = serverInfoArray[0];//첫 번째 문장 IP로 설정
		int serverPort = Integer.parseInt(serverInfoArray[1]);//두 번째 문장 port로 설정
		
		try {
			socket = new Socket(serverIP, serverPort); //IP, port를 미리 설정한 값으로 변경
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			while (true) { //서버와의 통신 대기를 위한 무한 반복문
				System.out.print("계산식(빈칸으로 띄어 입력,예:24 + 42)>>"); // 메세지 출력
				String outputMessage = scanner.nextLine(); // 키보드에서 수식 읽기
				if (outputMessage.equalsIgnoreCase("종료")) {
					out.write(outputMessage + "\n"); // 사용자가 "종료" 입력 시
					out.flush(); //버퍼 메세지 모두 전송
					break; // 사용자가 "종료"를 입력한 경우 서버로 전송 후 연결 종료
				}
				out.write(outputMessage + "\n"); // 키보드에서 읽은 수식 문자열 전송
				out.flush(); //버퍼 메세지 모두 전송
				String inputMessage = in.readLine(); // 서버로부터 계산 결과 수신
				System.out.println("계산 결과: " + inputMessage); // 가져온 계산 결과 표시
			}
		} catch (IOException e) {
			System.out.println(e.getMessage()); //예외 처리
		} finally {
			try {
				scanner.close(); //스캐너 종료
				if (socket != null)
					socket.close(); // 클라이언트 소켓 닫기
			} catch (IOException e) {
				System.out.println("서버와 채팅 중 오류가 발생했습니다."); //예외 오류 발생 메세지
			}
		}
	}
}