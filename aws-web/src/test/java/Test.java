import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.utils.StringUtils;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Test {

  public static void  main(String[] args){
    Test test = new Test();
    S3ObjectBO bo = new S3ObjectBO();
    bo.setPath("111");

    S3ObjectBO bo1 = bo;
    bo1.setPath("222");

    System.out.println(bo1.getPath());
    System.out.println(bo.getPath());
  }
}
