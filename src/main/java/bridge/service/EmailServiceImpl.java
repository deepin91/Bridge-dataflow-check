package bridge.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
 
@Service
public class EmailServiceImpl implements EmailService{
 
    @Autowired
    JavaMailSender emailSender;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
 
//    public static final String ePw = createKey();// 여기에 넣으면 사용자마다 같은 인증번호 보내질 가능성 있음
    											 // 같은 인증코드 보내지는 거 수정 필요함
 
    private MimeMessage createMessage(String to, String ePw)throws Exception{
        
    	System.out.println("보내는 대상 : "+ to);
        System.out.println("인증 번호 : "+ ePw);
        
        MimeMessage  message = emailSender.createMimeMessage();
 
        message.addRecipients(RecipientType.TO, to);//보내는 대상
        message.setSubject("이메일 인증 테스트");//제목
 
        String msgg="";
        msgg+= "<div style='margin:20px;'>";
        msgg+= "<h1> 안녕하세요 bridge입니다. </h1>";
        msgg+= "<br>";
        msgg+= "<p>아래 코드를 복사해 입력해주세요<p>";
        msgg+= "<br>";
        msgg+= "<p>감사합니다.<p>";
        msgg+= "<br>";
        msgg+= "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg+= "<h3 style='color:blue;'>인증 코드입니다.</h3>";
        msgg+= "<div style='font-size:130%'>";
        msgg+= "CODE : <strong>";
        msgg+= ePw+"</strong><div><br/> ";
        msgg+= "</div>";
        message.setText(msgg, "utf-8", "html");//내용
//      message.setFrom(new InternetAddress("bridge.project0605@gmail.com","bridge"));//보내는 사람
        message.setFrom(new InternetAddress("deepin3809@gmail.com","deepin"));//보내는 사람 250415 SMTP 오류, 내 계정으로 임시 수정
        return message;
    }
 
    public static String createKey() {
    	return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
//        StringBuffer key = new StringBuffer();
//        Random rnd = new Random();
// 
//        for (int i = 0; i < 8; i++) { // 인증코드 8자리
//            int index = rnd.nextInt(3); // 0~2 까지 랜덤
// 
//            switch (index) {
//                case 0:
//                    key.append((char) ((int) (rnd.nextInt(26)) + 97));
//                    //  a~z  (ex. 1+97=98 => (char)98 = 'b')
//                    break;
//                case 1:
//                    key.append((char) ((int) (rnd.nextInt(26)) + 65));
//                    //  A~Z
//                    break;
//                case 2:
//                    key.append((rnd.nextInt(10)));
//                    // 0~9
//                    break;
//            }
//        }
//        return key.toString(); // 주석 여기까지 부분 random > UUID.random 방식으로 변경 
    }
    
    @Override
    public String sendSimpleMessage(String to)throws Exception {
    	String ePw = createKey();// 20250415 추가한 부분(동일코드 부분은 해결가능하지만 다른 오류 발생)
        MimeMessage message = createMessage(to, ePw);
        try{//예외처리
            emailSender.send(message);
        }catch(MailException es){
            es.printStackTrace();
            throw new IllegalArgumentException();
        }
        
        redisTemplate.opsForValue().set(to, ePw, 5, TimeUnit.MINUTES);
        return ePw;
    }
}