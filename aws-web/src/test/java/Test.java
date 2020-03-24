public class Test {

  public static void  main(String[] args){
    Test test = new Test();
    String fileName = "2.doc";

    String newFileName =  fileName.substring(0,fileName.lastIndexOf("."))
        + "_" + System.currentTimeMillis()
        + fileName.substring(fileName.lastIndexOf("."));

    System.out.println(newFileName);
  }
}
