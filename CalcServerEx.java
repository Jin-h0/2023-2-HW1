package Clock;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CalcServerEx {
	public static String calc(String exp) {
		StringTokenizer st = new StringTokenizer(exp, " ");
		if (st.countTokens() != 3) //토큰이 3개가 아닐 때
			return "error 101, Incorrect formula"; // 연산자 형태 에러
		String res = "";
		int op1 = Integer.parseInt(st.nextToken()); //첫 번째 토큰 (첫 번째 숫자)지정
		String opcode = st.nextToken(); //두 번째 토큰(연산자) 지정
		int op2 = Integer.parseInt(st.nextToken()); //세 번째 토큰 (두 번째 숫자)지정
		switch (opcode) {
		case "+": //더하기
			res = Integer.toString(op1 + op2);
			break;

		case "-": //빼기
			res = Integer.toString(op1 - op2);
			break;

		case "*": //곱하기
			res = Integer.toString(op1 * op2);
			break;

		case "/": //나누기
			if (op2 == 0) { //두 번째 숫자가 0일 때
				res = "error 102, divided by zero"; // 에러 출력
				break;
			}
			res = Integer.toString(op1 / op2); //0이 아니면 그대로 나누기
			break;

		default:
			res = "error 002, unknown error"; //이 외 다른 경우 에러 출력
		}
		return res; //최종 결과 반환
	}

	public static void main(String[] args) throws Exception {
		ServerSocket listener = null; // 리스너 초기화

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
			listener = new ServerSocket(serverPort); // 서버 소켓 생성
			System.out.println("연결을 기다리고 있습니다.....");

			ExecutorService pool = Executors.newFixedThreadPool(20); //쓰레드풀 20만큼 생성
			while (true) { //연결 대기를 위한 무한 반복문
				Socket socket = listener.accept(); // 클라이언트로부터 연결 요청 대기
				pool.execute(new Capitalizer(socket)); //클라이언트 연결 성공 시 새로운 스레드 생성
				System.out.println("연결되었습니다.");
			}
		} catch (IOException e) {
			System.out.println(e.getMessage()); //예외 출력 메세지
		} finally {
			if (listener != null) {
				listener.close(); // listener가 null이 아닐 때 서버 소켓 닫기
			}
		}
	}

	private static class Capitalizer implements Runnable {
		private Socket socket;

		Capitalizer(Socket socket) {
			this.socket = socket; //소켓 생성 후 초기화
		}

		@Override
		public void run() { //runnable 실행 메서드
			BufferedReader in = null; //입출력 변수 선언 및 초기화
			BufferedWriter out = null; //입출력 변수 선언 및 초기화
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //입력스트림
				out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); //출력스트림
				while (true) { //클라이언트와의 통신대기를 위한 무한 반복문
					String inputMessage = in.readLine(); //클라이언트로부터 메세지 한 줄 가져오기
					if (inputMessage == null || inputMessage.equalsIgnoreCase("종료")) {
						System.out.println("클라이언트에서 연결을 종료하였음"); //받아온 메세지가 null/종료일 경우 통신 종료
						break;
					}
					System.out.println(inputMessage); // 받은 메시지를 화면에 출력
					String res = calc(inputMessage); // 계산. 계산 결과는 res에 저장
					out.write(res + "\n"); // 계산 결과 문자열 전송
					out.flush(); //버퍼에 있는 메세지 모두 클라이언트로 전송
				}
			} catch (Exception e) { //예외 처리
				System.out.println("Error:" + socket);//에러 출력
			} finally {
				try {
					if (socket != null) //소켓이 null이 아닐 때
						socket.close(); // 통신용 소켓 닫기
				} catch (IOException e) {
					System.out.println("클라이언트와 채팅 중 오류가 발생했습니다."); //예외 처리
				}
			}
		}
	}
}