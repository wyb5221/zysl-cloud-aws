//package com.zysl.cloud.aws.biz.componet;
//
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.stereotype.Component;
//
//@Aspect
//@Component
//@Slf4j
//public class DataAuthAspect {
//
//	@Pointcut("execution(public * com.zysl.cloud.aws.biz.service.s3.*.*(..))  && @annotation(com.zysl.cloud.aws.biz.componet.DataAuthAnnotation)")
//	public void addAdvice(){}
//
//	@Around("addAdvice()")
//	public Object Interceptor(ProceedingJoinPoint pjp){
//		Object result = null;
//		Object[] args = pjp.getArgs();
//		log.info("----数据权限切面测试----");
//
//		try {
//			result =pjp.proceed();
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//		return result;
//	}
//
//}
