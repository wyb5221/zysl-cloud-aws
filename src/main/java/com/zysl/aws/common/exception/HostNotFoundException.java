package com.zysl.aws.common.exception;

public class HostNotFoundException extends RuntimeException {

  public HostNotFoundException(){
    super();
  }

  public HostNotFoundException(String msg){
    super((msg));
  }
}
