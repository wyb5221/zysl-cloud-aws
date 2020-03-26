import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Test {

  public static void  main(String[] args){
    Test test = new Test();
    Calendar cal1 = Calendar.getInstance();
    cal1.setTimeInMillis(1585045458180L);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    System.out.println(sdf.format(cal1.getTime()));
  }
}
